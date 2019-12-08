package ru.coding4fun.intellij.database.action.security

import ru.coding4fun.intellij.database.action.KindPaths
import ru.coding4fun.intellij.database.action.common.NewModelAction
import ru.coding4fun.intellij.database.data.property.security.SecurityDataProviders
import ru.coding4fun.intellij.database.model.property.security.MsCertificateModel
import ru.coding4fun.intellij.database.ui.form.security.CertificateDialog

class NewCertificateAction : NewModelAction<MsCertificateModel, CertificateDialog>(
	KindPaths.certificate,
	CertificateDialog(),
	SecurityDataProviders::getCertificate
)