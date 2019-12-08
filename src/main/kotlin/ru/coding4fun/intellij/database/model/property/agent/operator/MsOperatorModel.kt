package ru.coding4fun.intellij.database.model.property.agent.operator

import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.common.Named
import ru.coding4fun.intellij.database.model.property.agent.MsOperator
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import ru.coding4fun.intellij.database.ui.form.common.Modifications
import ru.coding4fun.intellij.database.ui.form.common.toModificationList

class MsOperatorModel : Named {
	lateinit var operator: ModelModification<MsOperator>
	lateinit var operatorCategories: List<BasicIdentity>
	lateinit var jobs: List<MsOperatorJob>
	lateinit var alerts: List<MsOperatorAlert>
	var alertModifications: Modifications<MsOperatorAlert> = emptyList<MsOperatorAlert>().toModificationList()
	var jobModifications: Modifications<MsOperatorJob> = emptyList<MsOperatorJob>().toModificationList()
	override var name: String
		get() = (operator.new ?: operator.old)!!.name
		set(_) {}
}