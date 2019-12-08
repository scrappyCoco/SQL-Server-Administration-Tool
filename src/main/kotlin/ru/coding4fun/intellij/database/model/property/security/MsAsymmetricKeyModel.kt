package ru.coding4fun.intellij.database.model.property.security

import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.common.Named
import ru.coding4fun.intellij.database.ui.form.common.ModelModification

class MsAsymmetricKeyModel : Named {
	lateinit var asymKey: ModelModification<MsAsymmetricKey>
	lateinit var algorithms: List<BasicIdentity>
	lateinit var creationDispositions: List<BasicIdentity>
	lateinit var databases: List<BasicIdentity>
	override var name: String
		get() = (asymKey.new ?: asymKey.old)!!.name
		set(_) {}
}