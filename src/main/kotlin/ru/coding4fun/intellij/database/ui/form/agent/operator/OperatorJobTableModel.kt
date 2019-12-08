package ru.coding4fun.intellij.database.ui.form.agent.operator

import ru.coding4fun.intellij.database.model.property.agent.operator.MsOperatorJob
import ru.coding4fun.intellij.database.ui.form.common.MutableTableModel
import ru.coding4fun.intellij.database.ui.form.common.TableColumn
import ru.coding4fun.intellij.database.ui.form.common.TableColumnModel

class OperatorJobTableModel : MutableTableModel<MsOperatorJob>(emptyList(), ColumnModel) {
	private object ColumnModel : TableColumnModel<MsOperatorJob> {
		override val columns: Array<TableColumn<MsOperatorJob>>
			get() = arrayOf(isSelected, name, email)

		private val isSelected = TableColumn<MsOperatorJob>(
			"Map",
			Boolean::class.javaObjectType,
			get = { model -> model.isSelected },
			size = 30
		)

		private val name = TableColumn<MsOperatorJob>(
			"Name",
			String::class.javaObjectType,
			get = { model -> model.name }
		)

		private val email = TableColumn<MsOperatorJob>(
			"Send to mail on",
			String::class.javaObjectType,
			get = { model -> model.mailNotifyLevel.toString() }
		)
	}
}