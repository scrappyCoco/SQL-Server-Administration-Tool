package ru.coding4fun.intellij.database.action.security

import ru.coding4fun.intellij.database.action.KindPaths
import ru.coding4fun.intellij.database.action.common.NewModelAction
import ru.coding4fun.intellij.database.data.property.security.SecurityDataProviders
import ru.coding4fun.intellij.database.model.property.security.login.MsLoginModel
import ru.coding4fun.intellij.database.ui.form.security.LoginDialog

class NewLoginAction : NewModelAction<MsLoginModel, LoginDialog>(
    KindPaths.login,
    { LoginDialog() },
    SecurityDataProviders::getLogin
)