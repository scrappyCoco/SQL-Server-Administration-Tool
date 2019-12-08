package ru.coding4fun.intellij.database.client

import com.intellij.database.dataSource.connection.DatabaseDepartment
import javax.swing.Icon

object MsDepartment: DatabaseDepartment {
	override val commonName: String = "MSSQL Common name"
	override val departmentName: String = "MSSQL Department name"
	override val icon: Icon? = null
}