package ru.coding4fun.intellij.database.ui.form.common

class ModelModification<Model>(var old: Model?, var new: Model?) {
	val isModified: Boolean
		get() = old?.hashCode() != new?.hashCode()

	fun reverse() {
		val temp = new
		new = old
		old = temp
	}
}