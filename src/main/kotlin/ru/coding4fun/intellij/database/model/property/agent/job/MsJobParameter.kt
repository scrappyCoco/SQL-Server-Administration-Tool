package ru.coding4fun.intellij.database.model.property.agent.job

import ru.coding4fun.intellij.database.ui.form.common.Modifications

class MsJobParameter(
	val alerts: Modifications<MsAlert>,
	val schedules: Modifications<MsSchedule>,
	val steps: Modifications<MsJobStep>
)