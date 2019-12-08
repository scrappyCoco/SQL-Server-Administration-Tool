package ru.coding4fun.intellij.database.model.property.security

import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.common.Named
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import ru.coding4fun.intellij.database.ui.form.common.Modifications
import ru.coding4fun.intellij.database.ui.form.common.toModificationList

class MsServerAuditSpecModel : Named {
	lateinit var spec: ModelModification<MsServerAuditSpecification>
	lateinit var defaultServerAudits: List<BasicIdentity>
	lateinit var defaultActions: List<MsServerAuditSpecificationAction>
	var actions: Modifications<MsServerAuditSpecificationAction> = emptyList<MsServerAuditSpecificationAction>().toModificationList()
	override var name: String
		get() = (spec.new ?: spec.old)!!.name
		set(_) {}
}