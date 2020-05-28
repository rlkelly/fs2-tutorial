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


object Part1 extends IOApp {
  def run(args: List[String]) : IO[ExitCode] = {
        merge()
  }

  def merge[F[_]: ContextShift](): IO[ExitCode] = {
    fs2.Stream
      .emits('A' to 'E')
      .map(letter =>
        Stream.emits(1 to 10)
          .map(index => s"$letter$index")
      )
      .flatten
      .flatMap(x => Stream.eval(IO(println(x))))
      .take(5)
      .compile
      .drain
      .as(ExitCode.Success)
  }

}
