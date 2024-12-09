package com.miloszjakubanis.filebrowser.model

import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf
import slick.lifted.Rep
import slick.jdbc.H2Profile.api.*

import java.time.LocalDate

case class User(id: Long, name: String, password: String)

object User {
  
//  given Encoder[List[User]] = new Encoder[List[User]] {
//    
//    final def apply(a: List[User]): Json = Json.arr(
//        a.map(user => Json.obj(
//            (
//            "id",
//            Json.fromLong(user.id)
//            ),
//            (
//            "user",
//            Json.fromString(user.name)
//            ),
//            (
//            "password",
//            Json.fromString(user.password)
//            )
//        )): _*
//    )
//  }

  given Encoder[User] = new Encoder[User] {
    final def apply(a: User): Json = Json.obj(
      (
        "id",
        Json.fromLong(a.id)
      ),
      (
        "user",
        Json.fromString(a.name)
      ),
      (
        "password",
        Json.fromString(a.password)
      )
    )
  }

  given [F[_]]: EntityEncoder[F, User] =
    jsonEncoderOf[F, User]

}
