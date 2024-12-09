package com.miloszjakubanis.filebrowser.model

sealed trait Node {
  val absolutePath: String
}

case class FileNode(override val absolutePath: String, size: Int)
    extends Node with Product with Serializable

case class DirectoryNode(override val absolutePath: String, contents: List[Node])
    extends Node with Product with Serializable
