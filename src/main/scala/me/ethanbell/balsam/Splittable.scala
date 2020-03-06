package me.ethanbell.balsam

import zio.{IO, ZIO}
import cats._
import cats.implicits._

object Splittable {
  type Share[A] = List[Option[A]]
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
   * Given some lists of shares, merge them down to a single share containing all their values
   * @note impure, therefore private -- will fail when shares is empty or shares are not the same length, and will be incorrect when shares are not disjoint
   * @example mergeShares({[Some(3), None, None],[None, Some(5), None]}) == [Some(3), Some(5), None]
   * @param shares the shares to merge
   * @return the merged shares
   */
  private def mergeShares(shares: Seq[Share[A]]): Share[A] = shares.reduce { (ls, rs) =>
    (ls zip rs).map {
      case (Some(shareFromLeft), None)  => Some(shareFromLeft)
      case (None, Some(shareFromRight)) => Some(shareFromRight)
      case (None, None)                 => None
      case (Some(_), Some(_)) =>
        throw new RuntimeException("Attempted to merge non-disjoint shares") // TODO lift into pure IO?
    }
  }

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
        es.zip((0 until es.length).map(_ % count)) // label each element with the modulo group it should go in
          .map { // assemble a list of n-vectors of the form (None, ... Some, ... None) where Some is at the index of the modulo group
            case (elem, idx) =>
              Vector.fill(count)(None).updated(idx, Some(elem))
          }
          .toList
          .sequence // "unzip" the n-vectors into n lists
      }
  def split23(): IO[IllegalArgumentException, Vector[Share[A]]] = mutuallyExclusiveShares(3).map {
    shares =>
      for {
        notPrivyToWhich <- shares.indices.toVector
        before = shares.take(notPrivyToWhich)
        after  = shares.drop(notPrivyToWhich + 1)
      } yield mergeShares(before ++ after)
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
