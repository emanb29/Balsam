package me.ethanbell.balsam

import zio.IO

object Splittable {
  type Share[A] = List[Option[A]]

  /**
   * Given some lists of shares, merge them down to a single share containing all their values
   * @example mergeShares({[Some(3), None, None],[None, Some(5), None]}) == [Some(3), Some(5), None]
   * @param shares the shares to merge
   * @return the merged shares
   */
  def mergeShares[A](shares: Seq[Share[A]]): IO[IllegalArgumentException, Share[A]] =
    shares
      .foldLeft[IO[IllegalArgumentException, Share[A]]] { // If shares is empty, fail, else seed the fold with the leftmost value (the head)
        shares match {
          case Seq() => IO.fail(new IllegalArgumentException("Attempted to merge no shares"))
          case elems @ (head +: _) if elems.exists(_.length != head.length) =>
            IO.fail(new IllegalArgumentException("Attempted to merge shares of different lengths"))
          case (head +: _) => IO.succeed(List.fill(head.length)(None))
        }
      } { (acc, rightShare: Share[A]) =>
        acc.flatMap { (leftShare: Share[A]) =>
          val zippedIO: List[IO[IllegalArgumentException, Option[A]]] =
            (leftShare zip rightShare).map {
              case (Some(shareFromLeft), None)  => IO.succeed(Some(shareFromLeft))
              case (None, Some(shareFromRight)) => IO.succeed(Some(shareFromRight))
              case (l, r) if l == r             => IO.succeed(l)
              case (Some(shareFromLeft), Some(shareFromRight)) if shareFromLeft != shareFromRight =>
                IO.fail(new IllegalArgumentException("Attempted to merge conflicting shares"))
            }
          IO.collectAll(zippedIO)
        }
      }

}

/**
 * A type for collection types that can be split
 * ("insecure", in the sense that knowing less than "threshold" of "pieces" values
 * may still make it easier to guess the combined value)
 *
 * Example for 2/3 system:
 * enumerate words modulo 3 (0 1 2 0 1 2 0 1 2)
 * give Alice all 0 and 1 words, leaving 2s blank
 * give Bob all 1 and 2 words, leaving 0s blank
 * give Carol all 0 and 2 words, leaving 1s blank
 */
case class Splittable[A](es: Seq[A]) {
  import Splittable.Share

  /**
   * Split a Seq of elements into [[count]] mutually-exclusive shares
   * @param count an integer in the range (0, es.length)
   * @return a vector of the resultant mutually-exclusive shares
   */
  private def mutuallyExclusiveShares(count: Int): IO[IllegalArgumentException, Vector[Share[A]]] =
    if (es.length < count)
      IO.fail(
        new IllegalArgumentException(
          s"Cannot split a group of ${es.length} potential shares in a (k, $count) system for any k, as $count > ${es.length}"
        )
      )
    else if (count <= 0)
      IO.fail(
        new IllegalArgumentException(
          s"Cannot split anything into $count parts"
        )
      )
    else
      IO.succeed {
        es.zip(es.indices.map(_ % count)) // label each element with the modulo group it should go in
          .map { // assemble a list of n-vectors of the form (None, ... Some, ... None) where Some is at the index of the modulo group
            case (elem, idx) =>
              Vector.fill(count)(None).updated(idx, Some(elem))
          }
          .toList
          .transpose
          .toVector // "unzip" the m n-vectors into n m-lists
      }

  /**
   * Split a Seq of elements in a 2-3 secret-sharing scheme (not information-secure)
   * @return
   */
  def split23(): IO[IllegalArgumentException, Vector[Share[A]]] =
    mutuallyExclusiveShares(3).flatMap { shares =>
      val mergedShares = for {
        notPrivyToWhich <- shares.indices
        before = shares.take(notPrivyToWhich)
        after  = shares.drop(notPrivyToWhich + 1)
      } yield Splittable.mergeShares(before ++ after)
      IO.collectAll(mergedShares).map(_.toVector)
    }

//  TODO this is nontrivial while retaining readability of split shares (without re-encoding as a seemingly unrelated string)
//  def thresholdedSplit(
//    threshold: Int,
//    pieces: Int
//  ): IO[IllegalArgumentException, Vector[Share[A]]] = {
//    val shares: IO[IllegalArgumentException, Vector[Share[A]]] = mutuallyExclusiveShares(pieces)
//
//    // the final shares are constructed from [[shares]] such that any [[threshold]] pieces will yield a (Some) in each position
//    ???
//  }

}
