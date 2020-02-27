package me.ethanbell.balsam

import me.ethanbell.bitchunk.BitChunk
import zio.DefaultRuntime

/**
 * "Impure" functions exposing practical (ie, useful) wrappers for pure behaviors of this library
 */
protected[balsam] trait Impure {
  def getMnemonicForEntropy(entropy: Entropy, wordList: WordList): String =
    (new DefaultRuntime {}).unsafeRun(Mnemonic(wordList, entropy).phrase.orDie)
  def getMnemonicForEntropy(entropy: BitChunk, wordList: WordList): String =
    getMnemonicForEntropy(Entropy(entropy), wordList) // TODO fix this one -- it shouldn't even be able to access this constructor
  def getMnemonicForEntropy(entropy: Seq[Int], wordList: WordList = WordList.English): String =
    getMnemonicForEntropy(entropy.map(BitChunk.apply).reduce(_ ++ _), wordList)
}
