package ru.coding4fun.intellij.database.ui.form.render

import javax.swing.SwingConstants
import javax.swing.table.DefaultTableCellRenderer

object LeftTableCellRenderer : DefaultTableCellRenderer() {
	init {
		horizontalAlignment = SwingConstants.LEFT
	}
}