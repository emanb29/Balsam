package me.ethanbell.balsam

import me.ethanbell.bitchunk.BitChunk
import zio.{IO, ZIO}

/**
 * Exposes pure interfaces to useful functions of this library
 */
object Pure {
  def getMnemonicForEntropy(
    entropy: Entropy,
    wordList: WordList = WordList.English
  ): IO[IndexOutOfBoundsException, String] =
    ZIO.foreach(entropy.wordIndices)(wordList.apply).map(_.mkString(" "))
  def getMnemonicForBits(
    entropy: BitChunk,
    wordList: WordList = WordList.English
  ): IO[RuntimeException, String] =
    Entropy.fromBitChunk(entropy).flatMap(getMnemonicForEntropy(_, wordList))
  def getMnemonicFor32BitChunks(
    entropy: Seq[Int],
    wordList: WordList = WordList.English
  ): IO[IndexOutOfBoundsException, String] =
    getMnemonicForBits(
      entropy.map(BitChunk.apply).reduce(_ ++ _),
      wordList
    ).orDieWith(cause =>
      new RuntimeException("Failed to create Entropy from provided sequence of 32-bit ints", cause)
    )
}
