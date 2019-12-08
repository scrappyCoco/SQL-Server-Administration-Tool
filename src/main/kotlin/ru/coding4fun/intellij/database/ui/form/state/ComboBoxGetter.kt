package ru.coding4fun.intellij.database.ui.form.state

import java.util.function.Function
import javax.swing.JComboBox
import javax.swing.JComponent

object ComboBoxGetter : Function<JComponent, Boolean> {
	override fun apply(t: JComponent): Boolean {
		val comboBox = (t as JComboBox<*>)
		return comboBox.isEnabled
	}

	fun getText(comboBox: JComboBox<*>): String? {
		return if (!apply(comboBox) || comboBox.selectedItem == null) null else comboBox.selectedItem!!.toString()
	}

	@Suppress("UNCHECKED_CAST")
	fun <Source, Result> getSelected(comboBox: JComboBox<Source>, map: (Source) -> Result): Result? {
		return if (!apply(comboBox) || comboBox.selectedItem == null) null else map(comboBox.selectedItem as Source)
	}
}