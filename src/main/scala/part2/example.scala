package part2

import java.nio.file.Paths
import java.util.concurrent._

import cats.effect._
import fs2._
import fs2.io.file
// import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.global

import org.http4s.client.{Client, JavaNetClientBuilder}
import org.http4s.{Request, UrlForm, Uri}
import org.http4s.Uri.uri
import org.http4s.Method.{GET, POST}


object Part2 extends IOApp {
  def run(args: List[String]) : IO[ExitCode] = {
        readFileExample()
  }

  def readFileExample[F[_]: ContextShift]() : IO[ExitCode] = {
    Stream.resource(Blocker[IO]).flatMap { blocker =>
      file.readAll[IO](Paths.get("./myfile.txt"), blocker, 4096)
        .through(text.utf8Decode)
        .through(text.lines)
        .map(line => println(line))
    }
    .compile
    .drain
    .as(ExitCode.Success)
  }

}