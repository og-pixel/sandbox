package com.miloszjakubanis.filebrowser

import cats.effect.{Async, IO, Resource}
import com.miloszjakubanis.filebrowser.model.User
import doobie.{ExecutionContexts, Transactor}
import doobie.hikari.HikariTransactor
import doobie.implicits.toSqlInterpolator
import doobie.util.transactor.Transactor
// Very important to deal with arrays
import doobie.postgres._
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor._
import doobie._
import doobie.implicits._
import scala.concurrent.ExecutionContext

class DBConnection[F[_]: Async] {

  val xa: Transactor[F] = Transactor.fromDriverManager[F](
    "org.postgresql.Driver", // JDBC driver
    "jdbc:postgresql://localhost:5432/postgres", // URL to your PostgreSQL instance
    user = "postgres", // username (default in Docker)
    password = "admin", // password
    None
  )

  val postgres: Resource[F, HikariTransactor[F]] = for {
    ce <- ExecutionContexts.fixedThreadPool[F](32)
    xa <- HikariTransactor.newHikariTransactor[F](
      "org.postgresql.Driver",
      "jdbc:postgresql:myimdb",
      "postgres",
      "admin",
      ce
    )
  } yield xa

  object UserOp {
    def getUsers(limit: Option[Int]): F[List[User]] = {
      sql"SELECT id, name, password FROM users"
        .query[User]
        .to[List]
        .transact(xa)
      // Executes the query on the database
    }

    def putUser(
        name: String,
        password: String
    ): F[Int] = {
      sql"INSERT INTO users (name, password) VALUES ($name, $password)"
        .update
        .run
        .transact(xa)
    }
  }

  object CacheOp {}

}
