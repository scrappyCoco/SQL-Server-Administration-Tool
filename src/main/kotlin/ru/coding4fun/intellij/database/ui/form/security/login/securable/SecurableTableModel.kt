package ru.coding4fun.intellij.database.ui.form.security.login.securable

import ru.coding4fun.intellij.database.model.property.security.login.MsSecurable
import ru.coding4fun.intellij.database.ui.form.common.MutableTableModel
import ru.coding4fun.intellij.database.ui.form.common.TableColumn
import ru.coding4fun.intellij.database.ui.form.common.TableColumnModel

class SecurableTableModel : MutableTableModel<MsSecurable>(
	arrayListOf(),
	SecurableTableColumnModel
) {
	override var rows: List<MsSecurable>
		get() = super.rows
		set(value) {
			super.rows = value.sortedWith(SecurableComparator)
		}

	private object SecurableTableColumnModel : TableColumnModel<MsSecurable> {
		override val columns: Array<TableColumn<MsSecurable>>
			get() = arrayOf(name, type, hasAny)

		private val name = TableColumn<MsSecurable>(
			"Name",
			String::class.javaObjectType,
			get = { model -> model.name }
		)

		private val type = TableColumn<MsSecurable>(
			"Type",
			String::class.javaObjectType,
			get = { model -> model.kind.toString() },
			size = 250
		)

		private val hasAny = TableColumn<MsSecurable>(
			"Has any",
			Boolean::class.javaObjectType,
			get = { model -> model.isExists },
			size = 70
		)
	}

	private object SecurableComparator : Comparator<MsSecurable> {
		override fun compare(o1: MsSecurable?, o2: MsSecurable?): Int {
			val compareResult = o1!!.kind.toString().compareTo(o2!!.kind.toString())

			if (compareResult != 0) {
				return compareResult
			}

			return o1.name.compareTo(o2.name)
		}
	}
}