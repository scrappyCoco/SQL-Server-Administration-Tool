package ru.coding4fun.intellij.database.model.property.security

import ru.coding4fun.intellij.database.model.common.Identity
import ru.coding4fun.intellij.database.model.common.Named

class MsAsymmetricKey(
	override var id: String,
	override var name: String,
	var authorization: String? = null,
	var file: String? = null,
	var executableFile: String? = null,
	var assembly: String? = null,
	var provider: String? = null,
	var algorithm: String? = null,
	var providerKeyName: String? = null,
	var creationDisposition: String? = null,
	var password: String? = null,
	var db: String
) : Identity, Named {
	override fun toString(): String {
		return name
	}
}