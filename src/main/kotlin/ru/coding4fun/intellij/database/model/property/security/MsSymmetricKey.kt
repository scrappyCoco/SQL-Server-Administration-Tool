package ru.coding4fun.intellij.database.model.property.security


import ru.coding4fun.intellij.database.model.common.Named


class MsSymmetricKey(
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
) : Named {
	override fun toString(): String {
		return name
	}
}