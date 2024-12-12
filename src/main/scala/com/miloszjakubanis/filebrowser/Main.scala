package com.miloszjakubanis.filebrowser

import cats.effect.*

object Main extends IOApp.Simple {

  val server = new Server()
  
  val run: IO[Unit] = {
    val unusedVal = true
    val unsuedVal2 = "unused"
    server.runServer[IO]
  }

}
