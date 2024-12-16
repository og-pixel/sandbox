package com.miloszjakubanis.filebrowser

import cats.effect.*

object Main extends IOApp.Simple {

  val server = new Server()
  
  val run: IO[Unit] = {
    val unusedVal = true
    val unsuedVal2 = "unused"
    val unsuedVal3 = "unused"
    val unsuedVal4 = "unused"
    val unsuedVal5 = "unused"
    val unsuedVal6 = "unused"
    val unsuedVal7 = "unused" //Another 3 lines added
    val unsuedVal8 = "unused"
    val unsuedVal9 = "unused"
    server.runServer[IO]
  }

}
