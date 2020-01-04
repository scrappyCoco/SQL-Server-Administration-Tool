package ru.coding4fun.intellij.database.model.property.security

import ru.coding4fun.intellij.database.model.common.Identity

class MsSymmetricKey(
	override var id: String,
	override var name: String,
	var authorization: String? = null,
	var providerName: String? = null,
	var keySource: String? = null,
	var algorithm: String? = null,
	var identityValue: String? = null,
	var providerKeyName: String? = null,
	var creationDisposition: String? = null,
	var certificate: String? = null,
	var password: String? = null,
	var symmetricKey: String? = null,
	var asymmetricKey: String? = null,
	var db: String
): Identity {
	override fun toString(): String {
		return name
	}
}