package me.ethanbell.balsam

import zio._

import scala.collection.immutable.SortedSet
import scala.io.Source

/**
 * A list of precisely 2048 words, standardized by the BIP39 spec
 */
trait WordList extends Iterable[String] {

  /**
   * @note this has to be a `def` or we get ExceptionInInitializerError caused by java.lang.NullPointerException
   * @return an alphabetically-sorted list of words
   */
  def words: SortedSet[String] // This should always be 2048 words
  protected def indexedWords: IndexedSeq[String] = words.toIndexedSeq
  override def iterator: Iterator[String]        = indexedWords.iterator

  def apply(index: Int): IO[IndexOutOfBoundsException, String] =
    IO.effect(indexedWords(index)).refineToOrDie
}
object WordList {
  object English extends WordList {
    private val wordSet = {
      // preferred over Source.fromResource("english.wordlist") for the leading /, which seems idiomatic
      // I allow the "english.wordlist" string because it's statically safe
      val resource = Source.fromURL(getClass.getResource("/english.wordlist")) // TODO load this using an effect
      val words    = resource.getLines().filterNot(_.isEmpty).toList
      resource.close()
      SortedSet(words: _*)
    }

    override def words: SortedSet[String] = wordSet
  }
}
