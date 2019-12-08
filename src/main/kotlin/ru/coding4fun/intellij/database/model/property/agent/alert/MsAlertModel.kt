package ru.coding4fun.intellij.database.model.property.agent.alert

import ru.coding4fun.intellij.database.data.property.agent.PerformanceCounterManager
import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.common.Named
import ru.coding4fun.intellij.database.ui.form.common.ModelModification

class MsAlertModel : Named {
	lateinit var alert: ModelModification<MsAlert>
	lateinit var databases: List<BasicIdentity>
	lateinit var operators: List<Operator>
	lateinit var jobs: List<BasicIdentity>
	lateinit var perfCounterManager: PerformanceCounterManager
	override var name: String
		get() = (alert.new ?: alert.old)!!.name
		set(_) {}
}