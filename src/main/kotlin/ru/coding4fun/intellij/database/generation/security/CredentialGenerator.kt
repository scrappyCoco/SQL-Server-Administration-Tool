package ru.coding4fun.intellij.database.generation.security

import ru.coding4fun.intellij.database.extension.addSeparatorScope

import ru.coding4fun.intellij.database.extension.appendJbLn
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase
import ru.coding4fun.intellij.database.model.property.security.MsCredentialModel

object CredentialGenerator : ScriptGeneratorBase<MsCredentialModel>() {
	override fun getCreatePart(
		model: MsCredentialModel,
		scriptBuilder: StringBuilder,
		reverse: Boolean
	): StringBuilder {
		if (reverse) model.credential.reverse()
		val credential = model.credential.new!!
		scriptBuilder.append("CREATE CREDENTIAL [", credential.name, "]").appendJbLn()
			.addSeparatorScope("  WITH ") { scriptBuilder.appendJbLn().append("  ") }.also {
				it.invoke { scriptBuilder.append("IDENTITY = '", credential.identityName, "'") }
				val hasPassword = !credential.password.isNullOrBlank()
				if (hasPassword) scriptBuilder.append(",")
				it.invokeIf(hasPassword) {
					scriptBuilder.append("SECRET = '", credential.password, "'")
				}
				it.invokeIf(!credential.providerName.isNullOrBlank()) {
					scriptBuilder.append("FOR CRYPTOGRAPHIC PROVIDER [", credential.providerName, "]")
				}
			}
		scriptBuilder.append(";")
		return scriptBuilder
	}

	override fun getDropPart(model: MsCredentialModel, scriptBuilder: StringBuilder): StringBuilder {
		return scriptBuilder.append("DROP CREDENTIAL [", model.credential.old!!.name, "];")
	}

	override fun getAlterPart(model: MsCredentialModel): String? {
		val scriptBuilder = StringBuilder()

		val credential = model.credential.new!!
		scriptBuilder.append("ALTER CREDENTIAL [", credential.name, "]").appendJbLn()
			.append("  WITH IDENTITY = '", credential.identityName, "'")

		if (!credential.password.isNullOrBlank()) {
			scriptBuilder.append(",").appendJbLn()
				.append("  SECRET = '", credential.password, "'")
		}

		return scriptBuilder.append(";").toString()
	}
}