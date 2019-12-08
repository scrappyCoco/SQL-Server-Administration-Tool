package ru.coding4fun.intellij.database.model.common

data class BasicIdentity(
	override var id: String,
	override var name: String
): Identity {
	override fun toString(): String = name
	override fun equals(other: Any?): Boolean = id == (other as? BasicIdentity)?.id
	override fun hashCode(): Int = id.hashCode()
}