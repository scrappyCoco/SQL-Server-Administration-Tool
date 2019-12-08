package ru.coding4fun.intellij.database.model.property.security.login

import ru.coding4fun.intellij.database.model.common.Copyable
import ru.coding4fun.intellij.database.model.common.Identity
import ru.coding4fun.intellij.database.model.tree.MsKind

data class MsSecurable(
	/**
	 * Examples: ENDPOINT:1, LOGIN:258, SERVER:0, SERVER ROLE:10.
	 */
	override var id: String,
	/**
	 * Examples: sa, BUILTIN\Administrators, processadmin.
	 */
	override var name: String,
	/**
	 * Examples: SERVER, SERVER_ROLE, WINDOWS_LOGIN., CERTIFICATE_MAPPED_LOGIN
	 */
	var kind: MsKind,
	/**
	 * Examples: LOGIN, SERVER ROLE, LOGIN.
	 */
	var classDesc: String,
	var majorId: String,
	var isExists: Boolean
) : Identity, Copyable<MsSecurable> {
	override fun getCopy(): MsSecurable = copy()
}