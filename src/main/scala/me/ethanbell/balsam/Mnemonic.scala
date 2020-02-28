package me.ethanbell.balsam

import me.ethanbell.bitchunk.BitChunk
import zio._
object Mnemonic {
  def phraseFromEntropy(
    entropy: Entropy,
    wordList: WordList = WordList.English
  ): IO[IndexOutOfBoundsException, String] =
    ZIO.foreach(entropy.wordIndices)(wordList.apply).map(_.mkString(" "))
  def phraseFromBitChunk(
    entropy: BitChunk,
    wordList: WordList = WordList.English
  ): IO[RuntimeException, String] =
    Entropy.fromBitChunk(entropy).flatMap(phraseFromEntropy(_, wordList))
  def phraseFrom32BitInts(
    entropy: Seq[Int],
    wordList: WordList = WordList.English
  ): IO[IndexOutOfBoundsException, String] =
    phraseFromEntropy(
      Entropy(entropy),
      wordList
    ).orDieWith(cause =>
      new RuntimeException("Failed to create Entropy from provided sequence of 32-bit ints", cause)
    )
}
case class Mnemonic(wordList: WordList, entropy: Entropy) {
  def phrase: IO[RuntimeException, String] = ZIO
    .foreach(entropy.wordIndices)(wordList.apply)
    .mapError(err =>
      new RuntimeException(
        "Somehow got an exception generating a wordlist from a valid Entropy",
        err
      )
    )
    .map(_.mkString(" "))
}
