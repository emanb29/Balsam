package me.ethanbell.balsam

import zio.console.{putStrLn, Console}
import zio.{UIO, URIO, ZIO}

object Main extends zio.App {

  /**
   * Run the program, as from the command line
   * @param args the arguments with which the program was called
   * @return An exit code
   */
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] =
    runHandlingErrors(putStrLn("Hello, world!"))

  def runHandlingErrors[R, E](prgm: ZIO[R, E, Unit]): URIO[Console with R, Int] =
    prgm
      .mapError {
        case e: Throwable => s"Execution failed: $e"
        case o            => s"Execution failed, citing a non-throwable error: $o"
      }
      .flatMapError(putStrLn)
      .fold(_ => 1, _ => 0)
}
