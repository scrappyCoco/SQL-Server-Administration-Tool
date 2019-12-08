package ru.coding4fun.intellij.database.model.property.security

import ru.coding4fun.intellij.database.model.common.Identity
import ru.coding4fun.intellij.database.model.common.Named

class MsCertificate(
	override var id: String,
	override var name: String,
	var userName: String? = null,
	var beginDialog: Boolean = false,
	var assemblyName: String? = null,
	var assemblyPath: String? = null,
	var privateKeyPath: String? = null,
	var privateKeyBits: String? = null,
	var encryptionPassword: String? = null,
	var decryptionPassword: String? = null,
	var startDate: String? = null,
	var expiryDate: String? = null,
	var subject: String? = null,
	var asn: String? = null,
	var db: String
) : Identity, Named {
	override fun toString(): String {
		return name
	}
}