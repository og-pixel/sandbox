package com.miloszjakubanis.filebrowser

import cats.effect.Async
import org.http4s.{HttpRoutes, StaticFile}
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.{OptionalQueryParamDecoderMatcher, QueryParamDecoderMatcher}
import cats.implicits.*
import com.miloszjakubanis.filebrowser.fs.Filesystem
import org.http4s.circe.CirceEntityEncoder.*
import com.miloszjakubanis.filebrowser.model.{DirectoryNode, FileNode, User}

import java.io.IOException
import io.circe.generic.auto.*
import io.circe.syntax.*

import java.nio.file.Paths

object Routes {

  object FileQueryParamMatcher
      extends QueryParamDecoderMatcher[String]("filepath")

  private object SourceQueryParamMatcher
      extends QueryParamDecoderMatcher[String]("src")

  private object DestinationQueryParamMatcher
      extends QueryParamDecoderMatcher[String]("dest")

  private object UsernameQueryParamMatcher
      extends QueryParamDecoderMatcher[String]("username")

  // TODO PASSWORD SHOULD ABSOLUTELY NOT BE PASSED IN QUERY PARAMS
  private object PasswordQueryParamMatcher
      extends QueryParamDecoderMatcher[String]("password")

  private object OptionalForceQueryParamMatcher
      extends OptionalQueryParamDecoderMatcher[Boolean]("force")

  private object OptionalRecursiveQueryParamMatcher
      extends OptionalQueryParamDecoderMatcher[Boolean]("recursive")

  def routes[F[_]: Async](routesImp: RoutesImp[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl.*
    val API_ROUTES_V1 = Root / "api" / "v1"

    HttpRoutes.of[F] {
      case GET -> Root =>
        Ok("")
      case GET -> API_ROUTES_V1 / "users" / "create"
          :?
          UsernameQueryParamMatcher(username) +&
          PasswordQueryParamMatcher(password) =>
        println("Creating user")
        routesImp.UserRoutes.usersExists(username).flatMap {
          case true =>
            BadRequest(s"User $username already exists")
          case false =>
            routesImp.UserRoutes
              .putUser(username, password)
              .attempt
              .flatMap {
                case Left(ex) =>
                  InternalServerError(
                    s"Failed to create user: ${ex.getMessage}"
                  )
                case Right(_) =>
                  Ok(s"Created user: $username successfully!")
              }
        }

      case GET -> API_ROUTES_V1 / "users" / "list" =>
        routesImp.UserRoutes
          .listUsers(None)
          .flatMap { users =>
            // TODO improve
            Ok(users)
          }
      case GET -> API_ROUTES_V1 / "users" / "find" :?
          UsernameQueryParamMatcher(username) =>
        routesImp.UserRoutes.findUser(username).flatMap {
          case Some(user) => Ok(user)
          case None       => NotFound(s"User $username not found")
        }
      case request @ GET -> API_ROUTES_V1 / "download" :? FileQueryParamMatcher(
            filepath
          ) =>
        val path = fs2.io.file.Path(filepath)
        // Serve the file from the filesystem
        StaticFile
          .fromPath(path, Some(request))
          .getOrElseF(NotFound())
      case GET -> API_ROUTES_V1 / "cat" :? FileQueryParamMatcher(filepath) =>
        routesImp.FsRoutes.catString(filepath).flatMap {
          case Left(ex)   => NotFound(ex.getMessage)
          case Right(str) => Ok(str)
        }
      case GET -> API_ROUTES_V1 / "ls" :? FileQueryParamMatcher(filepath) =>
        val a = Filesystem.listDirectory2(Paths.get(filepath))
          .through(Filesystem.pathToNodePipe)
          .compile
          .toList
          .map(e =>
            e.map(a =>
              a.absolutePath
            ).mkString("\n")
          )


        a.flatMap(Ok(_))

//        Ok(a)
//        routesImp.FsRoutes
//          .ls(filepath)
//          .flatMap {
//            case Some(v) =>
//              Ok(
//                v.map(e =>
//                  (
//                    e.absolutePath,
//                    e match
//                      case FileNode(absolutePath, size) => "file"
//                      case DirectoryNode(absolutePath, contents) =>
//                        contents.map(a => a.absolutePath)
//                  )
//                ).mkString("\n")
//              )
//            case None => NotFound()
//          }
      case GET -> API_ROUTES_V1 / "cp" :?
          SourceQueryParamMatcher(src) +&
          DestinationQueryParamMatcher(dest) +&
          OptionalForceQueryParamMatcher(force) +&
          OptionalRecursiveQueryParamMatcher(recursive) =>
        routesImp.FsRoutes
          .copy(
            src,
            dest,
            force.getOrElse(false),
            recursive.getOrElse(false)
          )
          .flatMap {
            case Right(_) =>
              Ok(s"File $src copied to $dest")

            case Left(
                  ex: IOException
                ) /* if ex.getMessage.contains("already exists") */ =>
              // Handle file already exists case (e.g., if force is false)
              Conflict(
                s"File $dest already exists. Use force=true to overwrite. ${ex.getMessage}"
              )
            case Left(ex: IOException) =>
              // Handle other IO exceptions, such as permission issues, etc.
              BadRequest(s"File copy failed: ${ex.getMessage}")
            case Left(ex) =>
              // For any other exceptions, handle them with a generic 500 Internal Server Error
              InternalServerError(s"Unexpected error: ${ex.getMessage}")
          }
      case GET -> API_ROUTES_V1 / "mkdir" =>
        Ok()
      case GET -> API_ROUTES_V1 / "touch" =>
        Ok()

    }
  }

}
