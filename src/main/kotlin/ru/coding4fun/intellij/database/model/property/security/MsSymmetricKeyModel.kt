package ru.coding4fun.intellij.database.model.property.security

import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.common.Named
import ru.coding4fun.intellij.database.ui.form.common.ModelModification

class MsSymmetricKeyModel: Named {
	lateinit var key: ModelModification<MsSymmetricKey>
	lateinit var databases: List<BasicIdentity>
	override var name: String
		get() = (key.new ?: key.old)!!.name
		set(_) {}
}