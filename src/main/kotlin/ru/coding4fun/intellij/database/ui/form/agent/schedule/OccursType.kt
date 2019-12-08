package ru.coding4fun.intellij.database.ui.form.agent.schedule

import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.ui.form.state.ComboBoxGetter
import ru.coding4fun.intellij.database.ui.form.state.TextFieldGetter
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JTextField

class OccursType(
	private val comboBox: JComboBox<BasicIdentity>,
	private val scheduleType: ScheduleType,
	private val weekToCheckBoxMap: Map<Int, JCheckBox>,
	private val occursEveryTextField: JTextField,
	private val monthNumberTextField: JTextField,
	private val everyMonthNumberTextField: JTextField
) {
	companion object {
		val daily = BasicIdentity("4", "Daily")
		val weekly = BasicIdentity("8", "Weekly")
		val monthly = BasicIdentity("16", "Monthly")
		val monthlyRelative = BasicIdentity("32", "Monthly relative")

		val all = listOf(daily, weekly, monthly, monthlyRelative)

		fun get(id: Int?): BasicIdentity? {
			if (id == null) return null
			return all.firstOrNull{it.id == id.toString()}
		}
	}

	private val selected: BasicIdentity?
		get() {
			if (!scheduleType.isRecurring) return null
			return ComboBoxGetter.getSelected(comboBox) { it }
		}

	val isDailySelected: Boolean
		get() = daily == selected

	val isWeeklySelected: Boolean
		get() = weekly == selected

	val isMonthlySelected: Boolean
		get() = monthly == selected

	val isMonthlyRelativeSelected: Boolean
		get() = monthlyRelative == selected

	fun getFreqRecurrenceFactor(): Int {
		when {
			isWeeklySelected -> {
				var accumulator = 0

				for ((dayCode, dayCheckBox) in weekToCheckBoxMap) {
					val isDaySelected = dayCheckBox.isSelected
					accumulator += if (isDaySelected) dayCode else 0
				}

				return accumulator

			}
			isDailySelected -> return TextFieldGetter.getIntOrCompute(occursEveryTextField) { 0 }
			isMonthlySelected -> return TextFieldGetter.getIntOrCompute(monthNumberTextField) { 0 }
			isMonthlyRelativeSelected -> return TextFieldGetter.getIntOrCompute(everyMonthNumberTextField) { 0 }
			else -> return 0
		}
	}
}