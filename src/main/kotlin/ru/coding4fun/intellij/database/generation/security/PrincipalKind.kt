package ru.coding4fun.intellij.database.generation.security

enum class PrincipalKind {
	SQL_LOGIN,
	WINDOWS_LOGIN,
	CERTIFICATE_MAPPED_LOGIN,
	ASYMMETRIC_KEY_MAPPED_LOGIN
}