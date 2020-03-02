package me.ethanbell.balsam

import me.ethanbell.bitchunk.BitChunk
import zio._
object Mnemonic {
  def fromEntropy(
    entropy: Entropy,
    wordList: WordList = WordList.English
  ): Mnemonic = Mnemonic(entropy.wordIndices, wordList)
}
case class Mnemonic(words: Seq[Int], wordList: WordList = WordList.English) {
  def phrase(): IO[IndexOutOfBoundsException, String] =
    IO.foreach(words)(wordList.apply).map(_.mkString(" "))
  def toEntropy(): IO[IllegalArgumentException, Entropy] = Entropy.fromBitChunk(
    words.map((idx: Int) => BitChunk(idx)).map(_.takeRight(11)).reduce(_ ++ _)
  )
}
