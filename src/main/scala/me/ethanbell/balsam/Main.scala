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
        _ <- putStrLn(
          s"""$entropy generated phrase "$phrase""""
        )
        _          <- putStrLn(s""""$phrase" splits into the following 2-3 share cards:""")
        cardShares <- Splittable(phrase.split(" ").toSeq).split23()
        cardPrints <- ZIO.foreach(cardShares) { share =>
          ZIO
            .foreach(share) { wordOpt =>
              ZIO.fromOption(wordOpt).catchAll(_ => ZIO("XXXXX"))
            }
            .map(_.mkString(" "))
        }
        _ <- putStrLn(cardPrints.mkString("\n"))
      } yield ()
    }

  /**
   * Run a ZIO program, logging any errors, throwable or otherwise, and returning an appropriate exit status
   */
  def runHandlingErrors[R, E](program: ZIO[R, E, Unit]): URIO[Console with R, Int] =
    program
      .mapError {
        case e: Throwable => s"Execution failed: $e"
        case o            => s"Execution failed, citing a non-throwable error: $o"
      }
      .flatMapError(putStrLn)
      .fold(_ => 1, _ => 0)
}
