package me.ethanbell.balsam

import me.ethanbell.bitchunk.BitChunk
import zio.console.{putStrLn, Console}
import zio._

import scala.util.Try

object Main extends zio.App {

  /**
   * Run the program, as from the command line
   * @param args the arguments with which the program was called
   * @return An exit code
   */
  override def run(args: List[String]): URIO[zio.ZEnv, Int] =
    runHandlingErrors[zio.ZEnv, Any] {
      for {
        hexStr <- IO
          .fromOption(args.headOption)
          .mapError(
            _ -> new IllegalArgumentException("Please pass a hex string in the first argument")
          )
        bitchunk <- ZIO.fromTry(Try {
          BitChunk.fromHexString(hexStr)
        })
        entropy <- Entropy.fromBitChunk(bitchunk)
        mnemonic = Mnemonic.fromEntropy(entropy)
        phraseLen = ZIO.fromTry(Try {
          args(1).toInt
        })
        phrase: String <- phraseLen.foldM(_ => mnemonic.phrase(), mnemonic.phrase)
        cards          <- Splittable(phrase.split(" ").toSeq).split23()
        _ <- putStrLn(
          s"Entropy $entropy generated phrase $phrase which splits into the following cards:"
        )
        cardPrints = for {
          card      <- cards
          printable <- card.map(_.getOrElse("XXXXXX"))
        } yield printable.mkString(" ")
        _ <- putStrLn(cardPrints.mkString("\n"))
      } yield ()
    }

  def runHandlingErrors[R, E](program: ZIO[R, E, Unit]): URIO[Console with R, Int] =
    program
      .mapError {
        case e: Throwable => s"Execution failed: $e"
        case o            => s"Execution failed, citing a non-throwable error: $o"
      }
      .flatMapError(putStrLn)
      .fold(_ => 1, _ => 0)
}
