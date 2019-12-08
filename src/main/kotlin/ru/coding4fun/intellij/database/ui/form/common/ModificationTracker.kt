package ru.coding4fun.intellij.database.ui.form.common

import ru.coding4fun.intellij.database.model.common.Copyable
import ru.coding4fun.intellij.database.model.common.Identity

interface ModificationTracker<Model> where Model : Identity,
										   Model : Copyable<Model> {
	fun getModifications(): List<ModelModification<Model>>
}