package ru.coding4fun.intellij.database.ui.form.common

interface TableColumnModel<Model> {
	val columns: Array<TableColumn<Model>>
}