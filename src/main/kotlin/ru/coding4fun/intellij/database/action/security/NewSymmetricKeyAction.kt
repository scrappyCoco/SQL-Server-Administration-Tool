package ru.coding4fun.intellij.database.action.security

import ru.coding4fun.intellij.database.action.KindPaths
import ru.coding4fun.intellij.database.action.common.NewModelAction
import ru.coding4fun.intellij.database.data.property.security.SecurityDataProviders
import ru.coding4fun.intellij.database.model.property.security.MsSymmetricKeyModel
import ru.coding4fun.intellij.database.ui.form.security.SymmetricKeyDialog

class NewSymmetricKeyAction : NewModelAction<MsSymmetricKeyModel, SymmetricKeyDialog>(
    KindPaths.symmetricKey,
    { SymmetricKeyDialog() },
    SecurityDataProviders::getSymmetricKey
)