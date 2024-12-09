package com.miloszjakubanis.filebrowser

import cats.effect.Async
import com.comcast.ip4s.{ipv4, port}
import com.miloszjakubanis.filebrowser.fs.Filesystem
import fs2.io.net.Network
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.Logger

class Server {

//  val filesystem = new Filesystem()

  def runServer[F[_] : Async : Network]: F[Nothing] = {
    val routesImp = new RoutesImp()
    
    for {
      client <- EmberClientBuilder
        .default[F]
        .build

      httpApp = Routes.routes(routesImp).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      _ <-
        EmberServerBuilder
          .default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(finalHttpApp)
          .build
    } yield ()
  }.useForever
}
