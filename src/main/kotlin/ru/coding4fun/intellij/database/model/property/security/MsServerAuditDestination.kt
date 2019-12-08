package ru.coding4fun.intellij.database.model.property.security

enum class MsServerAuditDestination(text: String) {
	FILE("File"),
	APPLICATION_LOG("Application Log"),
	SECURITY_LOG("Security Log")
}