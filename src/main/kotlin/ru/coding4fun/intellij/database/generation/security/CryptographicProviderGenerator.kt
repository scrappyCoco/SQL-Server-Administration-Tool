package ru.coding4fun.intellij.database.generation.security


import ru.coding4fun.intellij.database.extension.appendJbLn
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase
import ru.coding4fun.intellij.database.model.property.security.MsCryptographicProviderModel

object CryptographicProviderGenerator :
	ScriptGeneratorBase<MsCryptographicProviderModel>() {
	override fun getAlterPart(
		model: MsCryptographicProviderModel): String? {
		val provider = model.provider.new!!
		return "ALTER CRYPTOGRAPHIC PROVIDER [" + provider.name + "] FROM FILE = '" + provider.filePath + "';"
	}

	override fun getCreatePart(
		model: MsCryptographicProviderModel,
		scriptBuilder: StringBuilder,
		reverse: Boolean
	): StringBuilder {
		if (reverse) model.provider.reverse()
		val provider = model.provider.new!!
		scriptBuilder.append("CREATE CRYPTOGRAPHIC PROVIDER [", provider.name, "] FROM FILE = '", provider.filePath, "';")
		return scriptBuilder
	}

	override fun getDropPart(
		model: MsCryptographicProviderModel,
		scriptBuilder: StringBuilder
	): StringBuilder {
		val name = model.provider.old!!.name
		scriptBuilder
			.append("ALTER CRYPTOGRAPHIC PROVIDER [", name, "] DISABLE;")
			.appendJbLn()
			.append("DROP CRYPTOGRAPHIC PROVIDER [", name, "];")

		return scriptBuilder
	}
}