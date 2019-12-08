package ru.coding4fun.intellij.database.data.property.security

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project

object SecurityDataProviders {
	fun getLogin(project: Project): LoginDataProvider =
		ServiceManager.getService(project, LoginDataProvider::class.java)

	fun getCertificate(project: Project): CertificateDataProvider =
		ServiceManager.getService(project, CertificateDataProvider::class.java)

	fun getAsymmetricKey(project: Project): AsymmetricKeyDataProvider =
		ServiceManager.getService(project, AsymmetricKeyDataProvider::class.java)

	fun getCredential(project: Project): CredentialDataProvider =
		ServiceManager.getService(project, CredentialDataProvider::class.java)

	fun getSymmetricKey(project: Project): SymmetricKeyDataProvider =
		ServiceManager.getService(project, SymmetricKeyDataProvider::class.java)

	fun getCryptographicProvider(project: Project): CryptographicDataProvider =
		ServiceManager.getService(project, CryptographicDataProvider::class.java)

	fun getServerAuditSpecificationProvider(project: Project): ServerAuditSpecificationDataProvider =
		ServiceManager.getService(project, ServerAuditSpecificationDataProvider::class.java)

	fun getServerAuditProvider(project: Project): ServerAuditDataProvider =
		ServiceManager.getService(project, ServerAuditDataProvider::class.java)

	fun getServerRole(project: Project): ServerRoleDataProvider =
		ServiceManager.getService(project, ServerRoleDataProvider::class.java)
}