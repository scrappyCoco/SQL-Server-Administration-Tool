package ru.coding4fun.intellij.database.model.property.security.login

import ru.coding4fun.intellij.database.model.common.Identity

data class MsLogin(
	override var id: String = "",
	override var name: String,
	var principalKind: String,
	var defaultDatabase: String? = null,
	var defaultLanguage: String? = null,
	var loginPassword: String? = null,
	var loginPasswordHashed: String? = null,
	var sid: String? = null,
	var isPolicyChecked: Boolean = false,
	var isExpirationChecked: Boolean = false,
	var denyLogin: Boolean = false,
	var isDisabled: Boolean = false,
	var mustChange: Boolean = false,
	var credential: String? = null,
	var certificate: String? = null,
	var asymmetricKey: String? = null
) : Identity