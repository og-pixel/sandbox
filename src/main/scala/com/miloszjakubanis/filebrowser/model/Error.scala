package com.miloszjakubanis.filebrowser.model

import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf


object Error {

  final case class Error(errorMessage: String)

  object User {
    given Encoder[Error] = new Encoder[Error]:
      final def apply(a: Error): Json = Json.obj(
        (
          "errorMessage", Json.fromString(a.errorMessage),
        ),
      )

    given [F[_]]: EntityEncoder[F, Error] =
      jsonEncoderOf[F, Error]
  }
}
