package ru.coding4fun.intellij.database.model.property.security.role


import ru.coding4fun.intellij.database.model.common.Identity
import ru.coding4fun.intellij.database.model.common.Named

class ServerRole(
	override var id: String,
	override var name: String,
	var auth: String?
) : Identity, Named