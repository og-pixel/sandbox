package com.miloszjakubanis.filebrowser.model

import java.time.LocalDate

object FSCache


case class FSCache(id: Long, command: String, lastTimeUsed: LocalDate, result: String)
