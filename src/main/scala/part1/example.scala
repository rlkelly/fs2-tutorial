package part2

import cats.effect._
import fs2._


object Part1 extends IOApp {
  def run(args: List[String]) : IO[ExitCode] = {
        merge1()
          .covary[IO]
          .compile
          .drain
          .as(ExitCode.Success)
  }

  def merge1(): Stream[Pure, String] = {
    fs2.Stream
      .emits('A' to 'E')
      .map(letter =>
        Stream.emits(1 to 10)
          .map(index => s"$letter$index")
      )
      .flatten
      .take(5)
  }

  def merge2(): Stream[IO, Unit] = {
    fs2.Stream
      .emits('A' to 'E')
      .map(letter =>
        Stream.emits(1 to 10)
          .map(index => s"$letter$index")
      )
      .flatten
      .flatMap(x => Stream.eval(IO(println(x))))
      .take(5)
  }

}
