package ru.coding4fun.intellij.database.ui.form.agent.schedule

import org.jetbrains.annotations.Contract
import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.ui.form.state.ComboBoxGetter
import javax.swing.JComboBox

class ScheduleType(private val comboBox: JComboBox<BasicIdentity>) {
	companion object {
		val agentStarts = BasicIdentity("64", "Start automatically when SQL Server Agent starts")
		val idle = BasicIdentity("128", "Start whenever the CPUs become idle")
		val recurring = BasicIdentity("-1", "Recurring")
		val oneTime = BasicIdentity("1", "One time")

		val all = listOf(agentStarts, idle, recurring, oneTime)

		@Contract("null -> null")
		fun get(id: Int?): BasicIdentity? {
			if (id == null) return null
			return all.firstOrNull{it.id == id.toString()} ?: recurring
		}
	}

	private val selected: BasicIdentity?
		get() = ComboBoxGetter.getSelected(comboBox) { it }

	val isOneTime: Boolean
		get() = oneTime == selected

	val isIdle: Boolean
		get() = idle == selected

	val isAgentStarts: Boolean
		get() = agentStarts == selected

	val isRecurring: Boolean
		get() = recurring == selected
}