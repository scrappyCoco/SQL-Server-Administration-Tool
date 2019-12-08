package ru.coding4fun.intellij.database.model.property.agent.schedule

import ru.coding4fun.intellij.database.model.common.Named
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import ru.coding4fun.intellij.database.ui.form.common.Modifications
import ru.coding4fun.intellij.database.ui.form.common.toModificationList

class MsScheduleModel : Named {
	lateinit var schedule: ModelModification<MsSchedule>
	lateinit var jobs: List<MsScheduleJob>
	var jobModifications: Modifications<MsScheduleJob> = emptyList<MsScheduleJob>().toModificationList()
	override var name: String
		get() = (schedule.new ?: schedule.old)!!.name
		set(_) {}
}