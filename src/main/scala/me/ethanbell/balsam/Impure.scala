package me.ethanbell.balsam

import me.ethanbell.bitchunk.BitChunk
import zio.DefaultRuntime

/**
 * "Impure" functions exposing practical (ie, useful) wrappers for pure behaviors of this library
 */
protected[balsam] trait Impure {
  def getMnemonicForEntropy(entropy: Entropy, wordList: WordList): String =
    (new DefaultRuntime {}).unsafeRun(Pure.getMnemonicForEntropy(entropy, wordList))
  def getMnemonicForBits(entropy: BitChunk, wordList: WordList): String =
    (new DefaultRuntime {}).unsafeRun(Pure.getMnemonicForBits(entropy, wordList))
  def getMnemonicFor32BitChunks(entropy: Seq[Int], wordList: WordList = WordList.English): String =
    (new DefaultRuntime {}).unsafeRun(Pure.getMnemonicFor32BitChunks(entropy, wordList))
}
