package ru.coding4fun.intellij.database.generation.security

import ru.coding4fun.intellij.database.extension.*
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase
import ru.coding4fun.intellij.database.model.property.security.MsCertificate
import ru.coding4fun.intellij.database.model.property.security.MsCertificateModel

object CertificateGenerator : ScriptGeneratorBase<MsCertificateModel>() {
	override fun getCreatePart(
		model: MsCertificateModel,
		scriptBuilder: StringBuilder,
		reverse: Boolean
	): StringBuilder {
		if (reverse) model.certificate.reverse()
		val certificate = model.certificate.new!!
		scriptBuilder
			.append("USE [", certificate.db, "]").appendGo()
			.append("CREATE CERTIFICATE [", certificate.name, "]")

		if (!certificate.userName.isNullOrBlank()) {
			scriptBuilder.appendJbLn().append("  AUTHORIZATION [", certificate.userName, "]")
		}

		if (!certificate.assemblyName.isNullOrBlank()) {
			scriptBuilder.appendJbLn().append("  FROM ASSEMBLY [", certificate.assemblyName, "]")
		} else if (!certificate.assemblyPath.isNullOrBlank()) {
			scriptBuilder.appendJbLn().append("  FROM EXECUTABLE FILE = '", certificate.assemblyPath, "'")
			appendPrivateKeyOptions(certificate, scriptBuilder)
		} else if (!certificate.asn.isNullOrBlank()) {
			scriptBuilder.appendJbLn().append("  FROM BINARY = ", certificate.asn)
			appendPrivateKeyOptions(certificate, scriptBuilder)
		} else if (!certificate.subject.isNullOrBlank()) {
			if (!certificate.encryptionPassword.isNullOrBlank()) {
				scriptBuilder.appendLnIfAbsent().append("  ENCRYPTION BY PASSWORD = '", certificate.encryptionPassword, "'")
			}

			scriptBuilder.appendLnIfAbsent()
				.addCommaWithNewLineScope().also { separateScope ->
					separateScope.invoke {
						scriptBuilder.append("  WITH SUBJECT = '", certificate.subject, "'")
					}
					separateScope.invokeIf(!certificate.startDate.isNullOrBlank()) {
						scriptBuilder.append("  START_DATE = '", certificate.startDate, "'")
					}
					separateScope.invokeIf(!certificate.expiryDate.isNullOrBlank()) {
						scriptBuilder.append("  EXPIRY_DATE = '", certificate.expiryDate, "'")
					}
				}
		}

		scriptBuilder.appendJbLn()
		scriptBuilder.append("  ACTIVE FOR BEGIN_DIALOG = ", if (certificate.beginDialog) "ON" else "OFF")
		scriptBuilder.append(";")

		return scriptBuilder
	}

	private fun appendPrivateKeyOptions(certificate: MsCertificate, scriptBuilder: StringBuilder) {
		val isEncryptionPasswordSet = !certificate.encryptionPassword.isNullOrBlank()
		val isDecryptionPasswordSet = !certificate.decryptionPassword.isNullOrBlank()
		val isPrivateKeyPathSet = !certificate.privateKeyPath.isNullOrBlank()
		val isPrivateKeyBitsSet = !certificate.privateKeyBits.isNullOrBlank()

		if (isEncryptionPasswordSet
			|| isDecryptionPasswordSet
			|| isPrivateKeyPathSet
			|| isPrivateKeyBitsSet
		) {
			scriptBuilder
				.appendLnIfAbsent()
				.append("  WITH PRIVATE KEY (")
				.addSeparatorScope { stringBuilder -> stringBuilder.append(", ") }.also { separateScope ->
					separateScope.invokeIf(isPrivateKeyPathSet) {
						scriptBuilder.append("FILE = '", certificate.privateKeyPath, "'")
					}.invokeElseIf(isPrivateKeyBitsSet) {
						scriptBuilder.append("BINARY = ", certificate.privateKeyBits)
					}

					separateScope.invokeIf(isEncryptionPasswordSet) {
						scriptBuilder.append("ENCRYPTION BY PASSWORD = '", certificate.encryptionPassword, "'")
					}
					separateScope.invokeIf(isDecryptionPasswordSet) {
						scriptBuilder.append("DECRYPTION BY PASSWORD = '", certificate.decryptionPassword, "'")
					}
				}
			scriptBuilder.append(")")
		}
	}

	override fun getDropPart(model: MsCertificateModel, scriptBuilder: StringBuilder): StringBuilder {
		val certificate = model.certificate.old!!
		return scriptBuilder.append("USE [", certificate.db, "]").appendGo()
			.append("DROP CERTIFICATE [", certificate.name, "];")
	}

	override fun getAlterPart(model: MsCertificateModel): String? {
		return ""
	}
}