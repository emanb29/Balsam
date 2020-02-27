package me.ethanbell.balsam

import zio._

case class Mnemonic(wordList: WordList, entropy: Entropy) {
  def phrase: IO[RuntimeException, String] = ZIO
    .foreach(entropy.wordIndices)(wordList.apply)
    .mapError(err =>
      new RuntimeException(
        "Somehow got an exception generating a wordlist from a valid Entropy",
        err,
      ),
    )
    .map(_.mkString(" "))
}
