package me.ethanbell.balsam

import zio._

import scala.collection.immutable.SortedSet
import scala.io.Source

/**
 *
 */
trait WordList extends Iterable[String] {

  /**
   * @note this has to be a `def` or we get ExceptionInInitializerError caused by java.lang.NullPointerException
   * @return an alphabetically-sorted list of words
   */
  def words: SortedSet[String]
  protected def indexedWords: IndexedSeq[String] = words.toIndexedSeq
  override def iterator: Iterator[String]        = indexedWords.iterator

  def apply(index: Int): IO[IndexOutOfBoundsException, String] =
    IO.effect(indexedWords(index)).refineToOrDie
}
object WordList {
  object English extends WordList {

    /**
     * I allow the "english.wordlist" string because it's statically safe
     */
    override def words: SortedSet[String] = {

      // preferred over Source.fromResource("english.wordlist") for the leading /, which seems idiomatic
      val resource = Source.fromURL(getClass.getResource("/english.wordlist"))
      val words    = resource.getLines().filterNot(_.isEmpty).toSeq
      resource.close()
      SortedSet(
        words: _*,
      )
    }
  }
}
