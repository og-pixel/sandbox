package com.miloszjakubanis.filebrowser.fs

import cats.effect.{Async, IO, Resource, Sync}

import java.io.{File, FileInputStream, IOException}
import java.nio.file.{Files, LinkOption, Path, StandardCopyOption}
import scala.jdk.CollectionConverters.*
import cats.syntax.all.*
import com.miloszjakubanis.filebrowser.model.{DirectoryNode, FileNode, Node}
import fs2.{Pipe, Stream}
import fs2.io.file.Path as fs2Path

import scala.util.{Failure, Success, Try}

object Filesystem {


  def pathToNodePipe[F[_]: Async]: Pipe[F, Path, Node] = { pathStream =>
    pathStream.flatMap {
      case path if Files.isDirectory(path) =>
        Stream.emit(DirectoryNode(path.toAbsolutePath.toString, List.empty))
      case path if Files.isRegularFile(path) =>
        Stream.emit(FileNode(path.toAbsolutePath.toString, Files.size(path).toInt))
    }
  }

  def listDirectory2[F[_]: Async](path: Path): Stream[F, Path] = {
    Stream.bracket(
        Async[F].delay(Files.newDirectoryStream(path)) // Open the directory
      )(stream => Async[F].delay(stream.close()))    // Ensure it is closed after use
      .flatMap { dirStream =>
        Stream
          .fromIterator[F](dirStream.iterator().asScala, 4096) // Create a stream from the directory contents
          .flatMap {
            case subPath if Files.isDirectory(subPath) => // Recursively process subdirectories
              Stream.emit(subPath) ++ listDirectory2(subPath) // List the subdirectory and recurse
            case filePath =>
              Stream.emit(filePath) // Emit regular files
          }
      }
  }

  def listDirectory[F[_]: Sync](directory: String): F[Option[List[Node]]] = {
    val path: Path = Try(
      Path.of(directory)
    ) match
      case Failure(exception) =>
        return Sync[F].pure(None)
      case Success(value) => value

    if !Files.exists(path) || !Files.isDirectory(path) then
      Sync[F].pure(None)
    else
      Files
        .list(path)
        .iterator
        .asScala
        .toList
        .traverse {
          case path if Files.isRegularFile(path) =>
            Sync[F].delay(
              FileNode(
                path.toAbsolutePath.toString,
                Files.size(path).toInt
              )
            )
          case path if Files.isDirectory(path) =>
            listDirectory(path.toAbsolutePath.toString).map ( e =>
              DirectoryNode(
                path.toAbsolutePath.toString,
                e.getOrElse(List.empty)
              )
            )
        }.map(e => Option(e))

  }

  def readFile[F[_]: Sync](path: String): Resource[F, FileInputStream] =
    val filepath = new File(path)
    Resource.make {
      Sync[F]
        .blocking(new FileInputStream(filepath))
        .attempt
        .flatMap {
          case Left(ex) =>
            Sync[F].raiseError(
              new Exception(
                s"Error opening a file: ${filepath.getAbsolutePath}",
                ex
              )
            )
          case Right(value) => Sync[F].pure(value)
        }
    } { inStream =>
      Sync[F]
        .blocking(inStream.close())
        .handleErrorWith(_ => Sync[F].unit) // release
    }

  def copyDirectory[F[_]: Async](
      src: Path,
      dest: Path,
      force: Boolean,
      recursive: Boolean
  ): F[Either[Throwable, Unit]] = ???

  def copyFile[F[_]: Async](
      src: Path,
      dest: Path,
      force: Boolean,
      recursive: Boolean
  ): F[Either[Throwable, Unit]] = Sync[F].delay {
    val parentDir = dest.getParent

    def copy: Either[Throwable, Unit] = {
      try {
        if (force) {
          Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING)
          Right(())
        } else {
          Files.copy(src, dest)
          Right(())
        }
      } catch {
        case ex: IOException =>
          Left(ex)
        case ex: Throwable =>
          Left(ex)
      }
    }

    // Ensure the parent directory exists if needed
    if (!Files.exists(parentDir) && recursive) {
      Files.createDirectories(parentDir)
    }

    // If parentDir exists or recursive is true, perform the copy, otherwise return an error
    if (Files.exists(parentDir) || recursive) {
      copy
    } else {
      Left(new Exception("No parent dir and recursive option is false"))
    }
  }

  def copyNode[F[_]: Async](
      src: String,
      dest: String,
      force: Boolean,
      recursive: Boolean
  ): F[Either[Throwable, Unit]] = {
    val source = Path.of(src)
    val destination = Path.of(dest)

    if (Files.exists(source) && Files.isRegularFile(source)) {
      copyFile(source, destination, force, recursive)
    } else if (Files.exists(source) && Files.isDirectory(source)) {
      copyDirectory(source, destination, force, recursive)
    } else {
      throw new IllegalArgumentException(
        s"Source node does not exist or is not a file or a directory: $source"
      )
    }
  }

}
