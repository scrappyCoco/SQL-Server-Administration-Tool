package ru.coding4fun.intellij.database.extension

import java.sql.ResultSet

fun ResultSet.getIntNull(columnName: String): Int? {
	val cell = this.getInt(columnName)
	return if (this.wasNull()) null else cell
}