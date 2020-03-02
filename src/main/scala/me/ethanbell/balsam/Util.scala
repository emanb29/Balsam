package me.ethanbell.balsam

import zio.{DefaultRuntime, IO}

private[balsam] object Util {
  implicit class unsafeRunOps[E, A](io: IO[E, A]) {

    /**
     * Unsafely run this `IO` in a [[DefaultRuntime]]
     * @return
     */
    def unsafeRun: A = new DefaultRuntime {}.unsafeRun(io)
  }
}
