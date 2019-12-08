package ru.coding4fun.intellij.database.ui.form.agent.schedule

import ru.coding4fun.intellij.database.model.common.BasicIdentity
import javax.swing.JComboBox

class DayOfWeek(private val comboBox: JComboBox<BasicIdentity>) {
	companion object {
		private val sunday = BasicIdentity("1", "Sunday")
		private val monday = BasicIdentity("2", "Monday")
		private val tuesday = BasicIdentity("3", "Tuesday")
		private val wednesday = BasicIdentity("4", "Wednesday")
		private val thursday = BasicIdentity("5", "Thursday")
		private val friday = BasicIdentity("6", "Friday")
		private val saturday = BasicIdentity("7", "Saturday")
		private val day = BasicIdentity("8", "Day")
		private val weekday = BasicIdentity("9", "Weekday")
		private val weekendDay = BasicIdentity("10", "Weekend day")

		val all = listOf(sunday, monday, tuesday, wednesday, thursday, friday, saturday,
			day, weekday, weekendDay)

		fun get(id: Int?): BasicIdentity? {
			if (id == null) return null
			return TimeType.all.firstOrNull{it.id == id.toString()}
		}
	}
}