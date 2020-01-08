package ru.coding4fun.intellij.database.action.security

import ru.coding4fun.intellij.database.action.KindPaths
import ru.coding4fun.intellij.database.action.common.NewModelAction
import ru.coding4fun.intellij.database.data.property.security.SecurityDataProviders
import ru.coding4fun.intellij.database.model.property.security.MsCryptographicProviderModel
import ru.coding4fun.intellij.database.ui.form.security.CryptographicProviderDialog

class NewCryptographicProviderAction : NewModelAction<MsCryptographicProviderModel, CryptographicProviderDialog>(
    KindPaths.cryptographicProvider,
    { CryptographicProviderDialog() },
    SecurityDataProviders::getCryptographicProvider
)