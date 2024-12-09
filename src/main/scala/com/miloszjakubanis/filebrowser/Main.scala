package com.miloszjakubanis.filebrowser

import cats.effect.*

object Main extends IOApp.Simple {

  val server = new Server()
  
  val run: IO[Unit] = {
    server.runServer[IO]
  }

}
