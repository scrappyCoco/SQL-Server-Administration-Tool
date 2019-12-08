package ru.coding4fun.intellij.database.model.property.security

import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.common.Named
import ru.coding4fun.intellij.database.ui.form.common.ModelModification

class MsCredentialModel : Named {
	lateinit var credential: ModelModification<MsCredential>
	lateinit var cryptographicProviders: List<BasicIdentity>
	override var name: String
		get() = (credential.new ?: credential.old)!!.name
		set(_) {}
}