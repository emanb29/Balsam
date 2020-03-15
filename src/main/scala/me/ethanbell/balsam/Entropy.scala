package me.ethanbell.balsam

import java.security.MessageDigest

import zio._
import me.ethanbell.bitchunk.BitChunk

object Entropy {

  /**
   * Creates an Entropy from a BitChunk, or returns an IllegalArgumentException if bs.n does not divide 32
   * @param bs
   * @return
   */
  def fromBitChunk(bs: BitChunk): IO[IllegalArgumentException, Entropy] =
    if (bs.n % 32 != 0)
      IO.fail(
        new IllegalArgumentException(
          "Cannot generate seeds for entropy whose length does not divide 32"
        )
      )
    else if (bs.n > 256)
      IO.fail(
        new IllegalArgumentException("Cannot generate seeds for entropy longer than 256 bits")
      )
    else IO.succeed(Entropy(bs))
  def apply(values: Seq[Int]): Entropy =
    if (values.isEmpty) empty
    else Entropy(values.map(BitChunk.apply).reduce(_ ++ _))

  val empty = Entropy(BitChunk.empty)
}

/**
 * A seed Entropy from which a BIP39 phrase may be generated
 * @param bits the bits representing this entropy directly
 */
case class Entropy private[Entropy] (bits: BitChunk) {
  // Technically, these require()s are impure. I would have enforced these using refined types, but for now I just
  // attempt to make the constructor private and use a fatal error to enforce correctness
  require(
    bits.n % 32 == 0,
    s"Entropy must be constructed with multiples of 32 bits. The provided BitChunk was $bits"
  )
  // TODO can I revoke this constraint?
  require(
    bits.n <= 256,
    s"Entropy must be constructed with no more than 256 bits. The provided BitChunk was $bits"
  )
  /**
   * The SHA-256 hash of this Entropy
   */
  lazy val hash: BitChunk =
    MessageDigest
      .getInstance("SHA-256")
      .digest(bits.toBytes().toArray) // hash ENT
      .map(BitChunk.apply)
      .reduce(_ ++ _) // convert hash into a BitChunk

  private lazy val concatenatedBits = bits ++ hash.take(bits.n / 32)
  /**
   * The word indices cooresponding to this Entropy
   */
  lazy val wordIndices: Seq[Int] = concatenatedBits
    .groupedLeftPadded(11) // group into 11 bit chunks. These will be numbers in the range [0, 2047]
    .map(_.toUnsignedBigInt().toInt)
}
