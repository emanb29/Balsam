package me.ethanbell.balsam

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.io.Source

class WordListTest extends AnyFunSuite with Matchers {
  test("WordTree should not reorder already-sorted english wordlist") {

    val src      = Source.fromURL(getClass.getResource("/english.wordlist"))
    val wordList = src.getLines().toList
    src.close()

    (WordList.English.size) should equal(wordList.size)
    assert(
      wordList.toSet === WordList.English.words,
      "The elements in the wordList were different than the elements in the English WordList object",
    )
    assert(
      wordList.sameElements(WordList.English),
      "The elements in the wordlist were different than the elements in the English WordList object",
    )
  }
}
