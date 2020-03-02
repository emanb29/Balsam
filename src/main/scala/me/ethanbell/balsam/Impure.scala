package me.ethanbell.balsam

import me.ethanbell.bitchunk.BitChunk
import zio.{DefaultRuntime, IO}

/**
 * "Impure" functions exposing practical (ie, useful) wrappers for pure behaviors of this library
 */
protected[balsam] trait Impure {
  def getMnemonicForEntropy(entropy: Entropy, wordList: WordList = WordList.English): String =
    new DefaultRuntime {}
      .unsafeRun(Mnemonic.fromEntropy(entropy, wordList).phrase())
  def getMnemonicForBits(entropy: BitChunk, wordList: WordList = WordList.English): String = {
    val phrase: IO[RuntimeException, String] =
      Entropy.fromBitChunk(entropy).flatMap(Mnemonic.fromEntropy(_, wordList).phrase())
    new DefaultRuntime {}.unsafeRun(phrase)
  }
  def getMnemonicFor32BitChunks(entropy: Seq[Int], wordList: WordList = WordList.English): String =
    getMnemonicForEntropy(Entropy(entropy), wordList)
}
