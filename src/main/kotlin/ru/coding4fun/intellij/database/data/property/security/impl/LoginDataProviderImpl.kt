/*
 * Copyright [2020] Coding4fun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.coding4fun.intellij.database.data.property.security.impl

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.client.MsClient
import ru.coding4fun.intellij.database.client.QueryDefinition
import ru.coding4fun.intellij.database.data.property.DbUtils
import ru.coding4fun.intellij.database.data.property.security.LoginDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.common.BuiltinPermission
import ru.coding4fun.intellij.database.model.property.security.login.*
import ru.coding4fun.intellij.database.model.property.security.role.RoleMember
import ru.coding4fun.intellij.database.ui.form.common.toMod
import java.util.function.Consumer

class LoginDataProviderImpl(project: Project) : MsClient(project),
    LoginDataProvider {
    override fun getModels(
        objectIds: Array<String>?,
        successConsumer: Consumer<Map<String, MsLoginModel>>,
        errorConsumer: Consumer<Exception>
    ) {
        val models: HashMap<String, MsLoginModel> =
            objectIds?.associateTo(HashMap(), { it to MsLoginModel() }) ?: HashMap()
        var languages: List<BasicIdentity> = emptyList()
        var certificates: List<BasicIdentity> = emptyList()
        var asymmetricKeys: List<BasicIdentity> = emptyList()
        var credentials: List<BasicIdentity> = emptyList()
        var databases: List<BasicIdentity> = emptyList()
        var serverPermissions: List<MsServerPermission> = emptyList()
        var builtInPermission: List<BuiltinPermission> = emptyList()
        var securables: List<MsSecurable> = emptyList()
        var dbRoles: List<MsDatabaseRoleMembership> = emptyList()
        var logins: List<MsLogin> = emptyList()
        var serverRoles: List<RoleMember> = emptyList()
        var loginDatabases: List<MsDatabaseOfLogin> = emptyList()

        val queries = arrayListOf(QueryDefinition(
            "sql/common/Language.sql",
            DataProviderMessages.message("security.login.progress.lang"),
            Consumer { languages = it.getObjects() }
        ), QueryDefinition(
            "sql/common/Database.sql",
            DataProviderMessages.message("security.login.progress.database"),
            Consumer { databases = it.getObjects() }
        ), QueryDefinition(
            "sql/tree/security/Credential.sql",
            DataProviderMessages.message("security.login.progress.credential"),
            Consumer { credentials = it.getObjects() }
        ), QueryDefinition(
            "sql/tree/security/AsymmetricKey.sql",
            DataProviderMessages.message("security.login.progress.asymmetric.key"),
            Consumer { asymmetricKeys = it.getObjects() }
        ), QueryDefinition(
            "sql/tree/security/Certificate.sql",
            DataProviderMessages.message("security.login.progress.certificate"),
            Consumer { certificates = it.getObjects() }
        ), QueryDefinition(
            "sql/action/property/security/login/ServerPermissions.sql",
            DataProviderMessages.message("security.login.progress.server.permission"),
            Consumer { serverPermissions = it.getObjects() }
        ), QueryDefinition(
            "sql/action/property/security/login/Securables2.sql",
            DataProviderMessages.message("security.login.progress.securable"),
            Consumer { securables = it.getObjects() }
        ), QueryDefinition(
            "sql/security/detail/BuiltinPermissions.sql",
            DataProviderMessages.message("security.login.progress.builtin.permission"),
            Consumer { builtInPermission = it.getObjects() }
        ), QueryDefinition(
            "sql/action/property/security/login/DatabaseRoles.sql",
            DataProviderMessages.message("security.login.progress.database.role"),
            Consumer { dbRoles = it.getObjects() }
        ), QueryDefinition(
            "sql/action/property/security/login/MainInfo.sql",
            DataProviderMessages.message("security.login.progress.main"),
            Consumer { logins = it.getObjects() }
        ), QueryDefinition(
            "sql/security/detail/ServerRolesOfMemberExtended.sql",
            DataProviderMessages.message("security.login.progress.server.role"),
            Consumer { serverRoles = it.getObjects() }
        ), QueryDefinition(
            "sql/action/property/security/login/Databases.sql",
            DataProviderMessages.message("security.login.progress.database"),
            Consumer { loginDatabases = it.getObjects() }
        ))

        if (objectIds == null) models[DbUtils.defaultId] = MsLoginModel()

        invokeComposite(
            DataProviderMessages.message("security.login.progress.task"),
            queries,
            Consumer {
                val serverPermissionMap = serverPermissions.groupBy { it.principalId }
                val securableMap = securables.groupBy { it.principalId }
                val dbRoleMap = dbRoles.groupBy { it.principalId }
                val mainMap = logins.associateBy { it.id }
                val loginDatabaseMap = loginDatabases.groupBy { it.principalId }
                val serverRoleMap = serverRoles.groupBy { it.principalId }

                for (modelEntry in models) {
                    val model = modelEntry.value
                    val loginId = modelEntry.key

                    model.builtInPermission = builtInPermission.sortedBy { it.name }
                    model.languages = languages.sortedBy { it.name }
                    model.certificates = certificates.sortedBy { it.name }
                    model.asymmetricKeys = asymmetricKeys.sortedBy { it.name }
                    model.credentials = credentials.sortedBy { it.name }
                    model.databases = databases.sortedBy { it.name }
                    model.serverPermissions = serverPermissionMap[loginId]?.sortedBy { it.name } ?: emptyList()
                    model.securables = securableMap[loginId] ?: emptyList()
                    model.dbRoles = dbRoleMap[loginId]?.sortedBy { it.name } ?: emptyList()
                    model.login = (mainMap[loginId] ?: error("Unable to find login with id $loginId")).toMod()
                    model.loginDatabases = loginDatabaseMap[loginId]?.sortedBy { it.name } ?: emptyList()
                    model.serverRoles = serverRoleMap[loginId]?.sortedBy { it.name } ?: emptyList()
                }
                successConsumer.accept(models)
            }, errorConsumer
        )
    }
}