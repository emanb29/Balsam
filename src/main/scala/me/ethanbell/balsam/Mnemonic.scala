package me.ethanbell.balsam

import java.security.MessageDigest

import me.ethanbell.bitchunk.BitChunk
import zio._
object Mnemonic {
  def fromEntropy(
    entropy: Entropy,
    wordList: WordList = WordList.English
  ): Mnemonic = Mnemonic(entropy, wordList)
}
case class Mnemonic(entropy: Entropy, wordList: WordList = WordList.English) {
  def phrase(): IO[IndexOutOfBoundsException, String] =
    IO.foreach(entropy.wordIndices)(wordList.apply).map(_.mkString(" "))

  /**
   * Generate a mnemonic phrase of a specified length.
   * Note that regardless of the length of the generated phrase, this does not increase the entropy (security) of the key
   * Uses the hash, based on https://github.com/iancoleman/bip39/blob/90f7a5ec9309c755dd06a534190067fa6f1f97fe/src/js/index.js#L1620-L1642
   * @param length the number of words to generate
   * @return
   */
  def phrase(length: Int): IO[RuntimeException, String] =
    if (length > 24 || length < 3)
      IO.fail(
        new IllegalArgumentException("Can't make a phrase of more than 24 words or less than 3")
      )
    else if (length % 3 != 0)
      IO.fail(new IllegalArgumentException("Can't make a phrase that is not a multiple of 3 words"))
    else {
      Entropy
        .fromBitChunk {
          // Interestingly, the hash from ian coleman hashes the ASCII representation of the bytes of the
          // base entropy, not the bytes themselves
          val baseEntropyStr: String = entropy.bits.asHexBytes.drop(2) // drop 0x
          // convert hash into a BitChunk
          MessageDigest
            .getInstance("SHA-256")
            .digest(baseEntropyStr.map(_.toByte).toArray) // hash ENT
            .map(BitChunk.apply)
            .reduce(_ ++ _)
            .take(32 * length / 3)
        }
        .map(Mnemonic.apply(_, wordList))
        .flatMap(_.phrase())
    }
}
