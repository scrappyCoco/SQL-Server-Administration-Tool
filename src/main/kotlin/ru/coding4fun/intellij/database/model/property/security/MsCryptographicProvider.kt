package ru.coding4fun.intellij.database.model.property.security


import ru.coding4fun.intellij.database.model.common.Named

class MsCryptographicProvider(
	override var name: String,
	var id: String?,
	var filePath: String,
	var isEnabled: Boolean? = null,
	var isRunning: Boolean? = null
) : Named {
	override fun toString(): String {
		return name
	}
}