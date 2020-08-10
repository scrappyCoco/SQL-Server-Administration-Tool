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

package ru.coding4fun.intellij.database

import com.intellij.openapi.help.WebHelpProvider

class MsWebHelpProvider : WebHelpProvider() {
    override fun getHelpPageUrl(helpTopicId: String): String? {
        return when (helpTopicId) {
            "ru.coding4fun.intellij.database.help.general" -> "https://github.com/scrappyCoco/SQL-Server-Administration-Tool/wiki/Connection"

            //region Security
            "ru.coding4fun.intellij.database.help.security.asymmetric.key" -> "https://docs.microsoft.com/en-US/sql/t-sql/statements/create-asymmetric-key-transact-sql"
            "ru.coding4fun.intellij.database.help.security.certificate" -> "https://docs.microsoft.com/en-US/sql/t-sql/statements/create-certificate-transact-sql"
            "ru.coding4fun.intellij.database.help.security.credential" -> "https://docs.microsoft.com/en-US/sql/t-sql/statements/create-credential-transact-sql"
            "ru.coding4fun.intellij.database.help.security.crypto.provider" -> "https://docs.microsoft.com/en-US/sql/t-sql/statements/create-cryptographic-provider-transact-sql"
            "ru.coding4fun.intellij.database.help.security.login" -> "https://docs.microsoft.com/en-US/sql/t-sql/statements/create-login-transact-sql"
            "ru.coding4fun.intellij.database.help.security.server.audit" -> "https://docs.microsoft.com/en-US/sql/t-sql/statements/create-server-audit-transact-sql"
            "ru.coding4fun.intellij.database.help.security.server.audit.specification" -> "https://docs.microsoft.com/en-US/sql/t-sql/statements/create-server-audit-specification-transact-sql"
            "ru.coding4fun.intellij.database.help.security.server.role" -> "https://docs.microsoft.com/en-US/sql/t-sql/statements/create-server-role-transact-sql"
            "ru.coding4fun.intellij.database.help.security.symmetric.key" -> "https://docs.microsoft.com/en-US/sql/t-sql/statements/create-symmetric-key-transact-sql"
            //endregion
            //region Agent
            "ru.coding4fun.intellij.database.help.agent.alert" -> "https://docs.microsoft.com/en-US/sql/relational-databases/system-stored-procedures/sp-add-alert-transact-sql"
            "ru.coding4fun.intellij.database.help.agent.job" -> "https://docs.microsoft.com/en-US/sql/relational-databases/system-stored-procedures/sp-add-job-transact-sql"
            "ru.coding4fun.intellij.database.help.agent.operator" -> "https://docs.microsoft.com/en-US/sql/relational-databases/system-stored-procedures/sp-add-operator-transact-sql"
            "ru.coding4fun.intellij.database.help.agent.schedule" -> "https://docs.microsoft.com/en-US/sql/relational-databases/system-stored-procedures/sp-add-schedule-transact-sql"
            "ru.coding4fun.intellij.database.help.agent.proxy" -> "https://docs.microsoft.com/en-US/sql/relational-databases/system-stored-procedures/sp-add-proxy-transact-sql"
            //endregion

            else -> null
        }
    }
}