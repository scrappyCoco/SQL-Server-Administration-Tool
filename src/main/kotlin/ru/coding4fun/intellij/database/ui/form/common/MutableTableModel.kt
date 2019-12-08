package ru.coding4fun.intellij.database.ui.form.common

import ru.coding4fun.intellij.database.model.common.Copyable
import ru.coding4fun.intellij.database.model.common.Identity
import ru.coding4fun.intellij.database.ui.form.render.LeftTableCellRenderer
import javax.swing.JTable
import javax.swing.event.TableModelEvent
import javax.swing.event.TableModelListener
import javax.swing.table.TableModel

open class MutableTableModel<Model>(
	rows: List<Model>,
	private val columnModel: TableColumnModel<Model>
) : TableModel
		where Model : Copyable<Model>,
			  Model : Identity {
	private val _valueChangeActions: ArrayList<OnRowChangeAction<Model>> = arrayListOf()

	fun addOnValueChangeAction(action: OnRowChangeAction<Model>) {
		_valueChangeActions.add(action)
	}

	fun removeOnValueChangeAction(action: OnRowChangeAction<Model>) {
		_valueChangeActions.remove(action)
	}

	private var _rows = rows
	open var rows: List<Model>
		get() = _rows
		set(value) {
			_rows = value

			fireTableModelChange()
		}
	private val _listeners: ArrayList<TableModelListener> = arrayListOf()

	private fun fireTableModelChange() {
		for (listener in _listeners) {
			listener.tableChanged(TableModelEvent(this))
		}
	}

	private fun fireValueChange(oldRow: Model, newRow: Model) {
		if (!_valueChangeActions.any()) return
		val modelModification = ModelModification(oldRow, newRow)
		for (rowChangeAction in _valueChangeActions) {
			rowChangeAction(modelModification)
		}
	}

	override fun addTableModelListener(listener: TableModelListener) {
		_listeners.add(listener)
	}

	override fun removeTableModelListener(listener: TableModelListener) {
		_listeners.remove(listener)
	}

	override fun getRowCount(): Int {
		return rows.size
	}

	override fun getColumnName(columnIndex: Int): String {
		return columnModel.columns[columnIndex].label
	}

	override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
		return columnModel.columns[columnIndex].isEditable
	}

	override fun getColumnClass(columnIndex: Int): Class<*> {
		return columnModel.columns[columnIndex].clazz
	}

	override fun getColumnCount(): Int {
		return columnModel.columns.size
	}

	override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
		val model = rows[rowIndex]
		val oldModel = model.getCopy()

		val setFunction = columnModel.columns[columnIndex].set
		setFunction?.let {
			it(model, aValue)
		}

		fireValueChange(oldModel, model)
	}

	override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
		val model = rows[rowIndex]
		return columnModel.columns[columnIndex].get(model)
	}

	fun initColumnSettings(table: JTable) {
		for (modelColumn in columnModel.columns) {
			val tableColumn = table.getColumn(modelColumn.label)
			if (modelColumn.size != null) {
				tableColumn.maxWidth = modelColumn.size
				tableColumn.minWidth = modelColumn.size
			}
			if (Boolean::class.javaObjectType != modelColumn.clazz) {
				tableColumn.cellRenderer = modelColumn.cellRenderer ?: LeftTableCellRenderer
			}
			if (modelColumn.cellEditor != null) {
				tableColumn.cellEditor = modelColumn.cellEditor
			}
		}
	}
}