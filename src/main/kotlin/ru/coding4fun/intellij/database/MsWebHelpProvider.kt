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
            //endregion

            else -> null
        }
    }
}