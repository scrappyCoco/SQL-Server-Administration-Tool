package ru.coding4fun.intellij.database.model.property.security

import ru.coding4fun.intellij.database.model.common.Named
import ru.coding4fun.intellij.database.ui.form.common.ModelModification

class MsServerAuditModel : Named {
	lateinit var audit: ModelModification<MsServerAudit>
	override var name: String
		get() = (audit.new ?: audit.old)!!.name
		set(_) {}
}