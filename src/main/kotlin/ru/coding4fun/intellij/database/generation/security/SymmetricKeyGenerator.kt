package ru.coding4fun.intellij.database.generation.security

import ru.coding4fun.intellij.database.extension.addCommaWithNewLineScope
import ru.coding4fun.intellij.database.extension.appendGo
import ru.coding4fun.intellij.database.extension.appendJbLn
import ru.coding4fun.intellij.database.extension.appendLnIfAbsent
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase
import ru.coding4fun.intellij.database.model.property.security.MsSymmetricKeyModel

object SymmetricKeyGenerator : ScriptGeneratorBase<MsSymmetricKeyModel>() {
	override fun getCreatePart(
		model: MsSymmetricKeyModel,
		scriptBuilder: StringBuilder,
		reverse: Boolean
	): StringBuilder {
		if (reverse) model.key.reverse()
		val key = model.key.new!!
		scriptBuilder.append("USE [", key.db, "]").appendGo()
			.append("CREATE SYMMETRIC KEY [", key.name, "]")

		if (!key.authorization.isNullOrBlank()) {
			scriptBuilder.append(" AUTHORIZATION [", key.authorization, "]")
			scriptBuilder.appendJbLn()
		}

		if (!key.providerName.isNullOrBlank()) {
			scriptBuilder.append(" FROM PROVIDER [", key.providerName, "]")
			scriptBuilder.appendJbLn()
		}

		val isKeySourceSet = !key.keySource.isNullOrBlank()
		val isAlgorithmSet = !key.algorithm.isNullOrBlank()
		val isIdentityValueSet = !key.identityValue.isNullOrBlank()
		val isProviderKeyNameSet = !key.providerKeyName.isNullOrBlank()
		val isCreationDispositionSet = !key.creationDisposition.isNullOrBlank()

		if (isKeySourceSet || isAlgorithmSet || isIdentityValueSet || isProviderKeyNameSet || isCreationDispositionSet) {
			scriptBuilder.append("WITH ")
			scriptBuilder.addCommaWithNewLineScope()
				.also { separateScope ->
					separateScope.invokeIf(isKeySourceSet) {
						scriptBuilder.append("KEY_SOURCE = '", key.keySource, "'")
					}
					separateScope.invokeIf(isAlgorithmSet) {
						scriptBuilder.append("ALGORITHM = ", key.algorithm)
					}
					separateScope.invokeIf(isIdentityValueSet) {
						scriptBuilder.append("IDENTITY_VALUE = '", key.identityValue, "'")
					}
					separateScope.invokeIf(isProviderKeyNameSet) {
						scriptBuilder.append("PROVIDER_KEY_NAME = '", key.providerKeyName, "'")
					}
					separateScope.invokeIf(isCreationDispositionSet) {
						scriptBuilder.append("CREATION_DISPOSITION = ", key.creationDisposition)
					}
				}
		}

		val isCertificateSet = !key.certificate.isNullOrBlank()
		val isPasswordSet = !key.password.isNullOrBlank()
		val isSymmetricKeySet = !key.symmetricKey.isNullOrBlank()
		val isAsymmetricKeySet = !key.asymmetricKey.isNullOrBlank()

		if (isCertificateSet || isPasswordSet || isSymmetricKeySet || isAsymmetricKeySet) {
			scriptBuilder.appendLnIfAbsent().append("ENCRYPTION BY ")
			scriptBuilder.addCommaWithNewLineScope()
				.also {separateScope ->
					separateScope.invokeIf(isCertificateSet) {
						scriptBuilder.append("CERTIFICATE [", key.certificate, "]")
					}
					separateScope.invokeIf(isPasswordSet) {
						scriptBuilder.append("PASSWORD = '", key.password, "'")
					}
					separateScope.invokeIf(isAsymmetricKeySet) {
						scriptBuilder.append("ASYMMETRIC KEY [", key.symmetricKey, "]")
					}
					separateScope.invokeIf(isSymmetricKeySet) {
						scriptBuilder.append("SYMMETRIC KEY [", key.asymmetricKey, "]")
					}
				}
		}

		return scriptBuilder
	}

	override fun getDropPart(
		model: MsSymmetricKeyModel,
		scriptBuilder: StringBuilder
	): StringBuilder {
		val key = model.key.old!!
		return scriptBuilder.appendLnIfAbsent()
			.append("USE [", key.db, "]").appendGo()
			.append("DROP SYMMETRIC KEY [", key.name, "]")
	}

	override fun getAlterPart(model: MsSymmetricKeyModel): String? {
		return ""
	}
}