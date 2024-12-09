package com.miloszjakubanis.filebrowser.model

import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf


object Message {

  final case class Message(message: String)

  object User {
    given Encoder[Message] = new Encoder[Message]:
      final def apply(a: Message): Json = Json.obj(
        (
          "message", Json.fromString(a.message),
        ),
      )

    given [F[_]]: EntityEncoder[F, Message] =
      jsonEncoderOf[F, Message]
  }
}
