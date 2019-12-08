package ru.coding4fun.intellij.database.ui.form.agent.schedule

import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.ui.form.state.ComboBoxGetter
import javax.swing.JComboBox

class WeekType(private val comboBox: JComboBox<BasicIdentity>) {
	companion object {
		private val first = BasicIdentity("1", "First")
		private val second = BasicIdentity("2", "Second")
		private val third = BasicIdentity("4", "Third")
		private val fourth = BasicIdentity("8", "Fourth")
		private val last = BasicIdentity("16", "Last")

		val all = listOf(first, second, third, fourth, last)

		fun get(id: Int?): BasicIdentity? {
			if (id == null) return null
			return all.firstOrNull { it.id == id.toString() }
		}
	}

	fun getFreqRelativeInterval(): Int {
		val selectedOccurs = ComboBoxGetter.getSelected(comboBox) { it } ?: return 0
		return Integer.parseInt(selectedOccurs.id)
	}
}