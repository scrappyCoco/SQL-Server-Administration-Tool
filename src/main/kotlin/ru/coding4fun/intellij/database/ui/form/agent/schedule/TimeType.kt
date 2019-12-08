package ru.coding4fun.intellij.database.ui.form.agent.schedule

import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.ui.form.state.ComboBoxGetter
import javax.swing.JComboBox

class TimeType(
	private val comboBox: JComboBox<BasicIdentity>,
	private val scheduleType: ScheduleType
){
	companion object {
		val hour = BasicIdentity("8", "Hour(s)")
		val minute = BasicIdentity("4", "Minute(s)")
		val second = BasicIdentity("2", "Second(s)")

		val all = listOf(hour, minute, second)

		fun get(id: String?): BasicIdentity? {
			if (id == null) return null
			return all.firstOrNull{it.id == id}
		}
	}

	private val selected: BasicIdentity?
		get() {
			if (!scheduleType.isRecurring) return null
			return ComboBoxGetter.getSelected(comboBox) { it }
		}

	fun getFreqSubDayType(): Int {
		val selectedValue = selected ?: return 0
		return Integer.parseInt(selectedValue.id)
	}
}