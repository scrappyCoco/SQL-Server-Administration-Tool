package ru.coding4fun.intellij.database.client

import com.intellij.database.console.client.DatabaseSessionClient
import com.intellij.database.psi.DbDataSource

object MsConnectionManager {
	var client: DatabaseSessionClient? = null
	var dbDataSource: DbDataSource? = null
}