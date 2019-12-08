package ru.coding4fun.intellij.database.model.property.security

import ru.coding4fun.intellij.database.model.common.Named

data class MsCredential(
	override var name: String,
	var identityName: String,
	var id: String?,
	var password: String? = null,
	var providerId: String? = null,
	var providerName: String? = null
) : Named {
	override fun toString(): String {
		return name
	}
}