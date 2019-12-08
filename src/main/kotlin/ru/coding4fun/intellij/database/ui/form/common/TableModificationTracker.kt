package ru.coding4fun.intellij.database.ui.form.common

import ru.coding4fun.intellij.database.model.common.Copyable
import ru.coding4fun.intellij.database.model.common.Identity

class TableModificationTracker<Model>(tableModel: MutableTableModel<Model>) :
	ModificationTracker<Model> where Model : Copyable<Model>,
									 Model : Identity {
	private val mods = hashMapOf<String, ModelModification<Model>>()

	init {
		tableModel.addOnValueChangeAction(this::handle)
	}

	private fun handle(modification: ModelModification<Model>) {
		if (!modification.isModified) mods.remove(modification.old!!.id)
		mods[modification.old!!.id] = modification
	}

	override fun getModifications() = mods.values.toList()
}