package ru.coding4fun.intellij.database.ui.form.security.login.database

import ru.coding4fun.intellij.database.model.property.security.login.MsDatabaseOfLogin
import ru.coding4fun.intellij.database.ui.form.common.MutableTableModel
import ru.coding4fun.intellij.database.ui.form.common.TableColumn
import ru.coding4fun.intellij.database.ui.form.common.TableColumnModel

class DatabaseTableModel : MutableTableModel<MsDatabaseOfLogin>(
	arrayListOf(),
	DatabaseTableColumnModel
) {

	object DatabaseTableColumnModel : TableColumnModel<MsDatabaseOfLogin> {
		override val columns: Array<TableColumn<MsDatabaseOfLogin>>
			get() = arrayOf(map, database, user, defaultSchema)

		private val map = TableColumn<MsDatabaseOfLogin>(
			"Map",
			Boolean::class.javaObjectType,
			get = { model -> model.isSelected },
			set = { model, aValue -> model.isSelected = aValue as Boolean },
			size = 70
		)

		private val database = TableColumn<MsDatabaseOfLogin>(
			"Database",
			String::class.javaObjectType,
			get = { model -> model.name }
		)

		private val user = TableColumn<MsDatabaseOfLogin>(
			"User",
			String::class.javaObjectType,
			get = { model -> if (model.user != null) model.user!! else "" },
			set = { model, aValue -> model.user = aValue as String }
		)

		private val defaultSchema = TableColumn<MsDatabaseOfLogin>(
			"Default Schema",
			String::class.javaObjectType,
			get = { model -> if (model.defaultSchema != null) model.defaultSchema!! else "" },
			set = { model, aValue -> model.defaultSchema = aValue as String }
		)
	}
}