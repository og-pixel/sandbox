package com.miloszjakubanis.filebrowser

import cats.effect.{Async, Sync}
import com.miloszjakubanis.filebrowser.fs.Filesystem
import cats.effect.*
import cats.implicits.*
import com.miloszjakubanis.filebrowser.model.{Node, User}

class RoutesImp[F[_] : Async] {

  val dbConnection = new DBConnection[F]

  object FsRoutes {
    def catString(
        filepath: String
    ): F[Either[Throwable, String]] = {
      Filesystem
        .readFile(filepath)
        .use { inStream =>
          val arr = inStream.readAllBytes()
          val str = new String(arr)
          Sync[F].pure(str)
        }
        .attempt
    }

    def catBytes(
        filepath: String
    ): F[Either[Throwable, List[Byte]]] = {
      Filesystem
        .readFile(filepath)
        .use { inStream =>
          val arr = inStream.readAllBytes()
          Sync[F].pure(arr.toList)
        }
        .attempt
    }

    def copy(
        src: String,
        dest: String,
        force: Boolean = false,
        recursive: Boolean = false
    ): F[Either[Throwable, Unit]] =
      Filesystem.copyNode(src, dest, force, recursive)

    def ls(directory: String): F[Option[List[Node]]] =
      Filesystem.listDirectory(directory)

  }

  object UserRoutes {

    def usersExists(username: String): F[Boolean] =
      dbConnection.UserOp.getUsers(None).map(_.exists(_.name == username))

    def putUser(username: String, password: String): F[Unit] = {
      val a = dbConnection.UserOp.putUser(username, password)
      a.map(c => {
        println("cc: " + c)
        ()
      })
    }

    def listUsers(limit: Option[Int]): F[List[User]] =
      dbConnection.UserOp.getUsers(limit)

    def findUser(username: String): F[Option[User]] =
      dbConnection.UserOp.getUsers(None).map(_.find(_.name == username))

  }

}
