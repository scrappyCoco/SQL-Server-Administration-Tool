package ru.coding4fun.intellij.database.model.property.agent.job

import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.common.Named
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import ru.coding4fun.intellij.database.ui.form.common.Modifications
import ru.coding4fun.intellij.database.ui.form.common.toModificationList

class MsJobModel : Named {
	lateinit var job: ModelModification<MsJob>
	lateinit var categories: List<BasicIdentity>
	lateinit var operaotrs: List<BasicIdentity>
	lateinit var steps: List<MsJobStep>
	lateinit var schedules: List<MsSchedule>
	lateinit var alerts: List<MsAlert>
	lateinit var subSystems: List<BasicIdentity>
	lateinit var databases: List<BasicIdentity>

	var alertMods: Modifications<MsAlert> = emptyList<MsAlert>().toModificationList()
	var scheduleMods: Modifications<MsSchedule> = emptyList<MsSchedule>().toModificationList()
	override var name: String
		get() = (job.new ?: job.old)!!.name
		set(_) {}
}