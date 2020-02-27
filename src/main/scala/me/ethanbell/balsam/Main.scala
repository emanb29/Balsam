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
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] =
    runHandlingErrors(
      ZIO
        .fromTry(Try {
          BitChunk.fromHexString(args.head)
        })
        .flatMap(Pure.getMnemonicForBits(_))
        .flatMap(putStrLn),
    )

  def runHandlingErrors[R, E](program: ZIO[R, E, Unit]): URIO[Console with R, Int] =
    program
      .mapError {
        case e: Throwable => s"Execution failed: $e"
        case o            => s"Execution failed, citing a non-throwable error: $o"
      }
      .flatMapError(putStrLn)
      .fold(_ => 1, _ => 0)
}
