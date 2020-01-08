package ru.coding4fun.intellij.database.action.security

import ru.coding4fun.intellij.database.action.KindPaths
import ru.coding4fun.intellij.database.action.common.NewModelAction
import ru.coding4fun.intellij.database.data.property.security.SecurityDataProviders
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditModel
import ru.coding4fun.intellij.database.ui.form.security.ServerAuditDialog

class NewAuditAction : NewModelAction<MsServerAuditModel, ServerAuditDialog>(
    KindPaths.audits,
    { ServerAuditDialog() },
    SecurityDataProviders::getServerAuditProvider
)