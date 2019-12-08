package ru.coding4fun.intellij.database.ui.form.common

import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

class TableColumn<Model>(
	val label: String,
	val clazz: Class<*>,
	val get: ((model: Model) -> Any),
	val set: ((model: Model, aValue: Any?) -> Unit)? = null,
	val size: Int? = null,
	val cellRenderer: TableCellRenderer? = null,
	val cellEditor: TableCellEditor? = null
) {
	val isEditable: Boolean = set != null
}