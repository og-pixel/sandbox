package com.miloszjakubanis.filebrowser.model
//
//import java.time.LocalDate
//
//object SlickTables {
//
//  import slick.jdbc.PostgresProfile.api._
//
//  class UserTable(tag: Tag) extends Table[User](tag, Some("users"), "User") {
//    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
//    def name = column[String]("name")
//    def password = column[String]("password")
//
//    def test(i: Int): String = i.toString
//
//
//    override def * = (id, name, password).mapTo[User]
//  }
//
//  class FSCacheTable(tag: Tag) extends Table[FSCache](tag, Some("fscache"), "FSCache") {
//    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
//    def command = column[String]("command", O.PrimaryKey, O.AutoInc)
//    def lastTimeUsed = column[LocalDate]("last_time_used")
//    def result = column[String]("result")
//
//    override def * = (id, command, lastTimeUsed, result).mapTo[FSCache]
//  }
//
//  lazy val userTable = TableQuery[UserTable]
//  lazy val fsCacheTable = TableQuery[FSCacheTable]
//}
