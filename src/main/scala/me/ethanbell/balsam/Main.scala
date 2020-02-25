package me.ethanbell.balsam

import zio.{UIO, ZIO}

object Main extends zio.App {

  /**
   * Run the program, as from the command line
   * @param args the arguments with which the program was called
   * @return An exit code
   */
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] =
    UIO[Int](0)
}
