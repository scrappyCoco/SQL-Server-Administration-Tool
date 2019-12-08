package ru.coding4fun.intellij.database.model.property.security

import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.common.Named
import ru.coding4fun.intellij.database.ui.form.common.ModelModification

class MsCertificateModel : Named {
	lateinit var certificate: ModelModification<MsCertificate>
	lateinit var databases: List<BasicIdentity>
	override var name: String
		get() = (certificate.new ?: certificate.old)!!.name
		set(_) {}
}