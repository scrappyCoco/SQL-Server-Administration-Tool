package ru.coding4fun.intellij.database.model.property.security

import ru.coding4fun.intellij.database.model.common.Named
import ru.coding4fun.intellij.database.ui.form.common.ModelModification

class MsCryptographicProviderModel : Named {
	lateinit var provider: ModelModification<MsCryptographicProvider>
	override var name: String
		get() = (provider.new ?: provider.old)!!.name
		set(_) {}
}