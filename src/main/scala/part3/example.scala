package part2

import cats.effect._
import fs2._


object Part3 extends IOApp {
  def run(args: List[String]) : IO[ExitCode] = {
    pipeExample()
      .through(asyncPipeTap)
      .through(asyncPipeMap)
      .compile
      .drain
      .as(ExitCode.Success)
  }

  def asyncPipeTap[F[_] : Sync: LiftIO]: Stream[F, String] => Stream[F, String] =
    // Shift gives the chance to use another thread
    _.evalTap { value =>
      (IO.shift *> IO(println(s"Processing $value by ${Thread.currentThread().getName}"))).runAsync(
        _ => IO.unit).to[F]
    }

  def asyncPipeMap[F[_] : Sync: LiftIO]: Stream[F, String] => Stream[F, Unit] =
    _.evalMap { value =>
      (IO.shift *> IO(println(s"Processing $value by ${Thread.currentThread().getName}"))).runAsync(
        _ => IO.unit).to[F]
    }

  def change[F[_]]: Pipe[F, Int, String] = {
    _.flatMap(s => Stream(s.toString + "...."))
  }

  def pipeExample() : Stream[IO, String] = {
    fs2.Stream
      .emits(1 to 10)
        .through(change)
  }
}
