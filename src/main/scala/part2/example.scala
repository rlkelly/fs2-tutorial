package part2

import java.nio.file.Paths

import cats.effect._
import fs2._
import fs2.io.file


object Part2 extends IOApp {
  def run(args: List[String]) : IO[ExitCode] = {
        readFileExample()
          .compile
          .drain
          .as(ExitCode.Success)
  }

  def readFileExample[F[_]: ContextShift]() : Stream[IO, Unit] = {
    Stream.resource(Blocker[IO]).flatMap { blocker =>
      file.readAll[IO](Paths.get("./myfile.txt"), blocker, 4096)
        .through(text.utf8Decode)
        .through(text.lines)
        .map(line => println(line))
    }
  }

}
