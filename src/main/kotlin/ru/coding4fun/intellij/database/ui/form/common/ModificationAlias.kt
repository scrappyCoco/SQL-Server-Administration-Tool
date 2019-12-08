package ru.coding4fun.intellij.database.ui.form.common

typealias OnRowChangeAction<Model> = (modification: ModelModification<Model>) -> Unit
typealias Modifications<Model> = List<ModelModification<Model>>

fun <Model> Collection<Model>.toModificationList(): List<ModelModification<Model>>
		where Model : Any {
	return this.map { ModelModification(null, it) }.toList()
}