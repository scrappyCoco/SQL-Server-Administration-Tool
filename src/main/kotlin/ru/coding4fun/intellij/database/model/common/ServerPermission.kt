package ru.coding4fun.intellij.database.model.common

data class ServerPermission(
	val classDesc: String,
	val majorId: String,
	val minorId: String,
	val granteeName: String,
	val granteeId: String,
	val grantorName: String,
	val grantorId: String,
	val type: String,
	val stateDesc: String
)