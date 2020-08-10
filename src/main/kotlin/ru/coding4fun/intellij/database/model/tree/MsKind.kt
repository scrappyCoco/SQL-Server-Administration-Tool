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

package ru.coding4fun.intellij.database.model.tree

enum class MsKind(val label: String? = null, val isFolder: Boolean = false) {
	UNDEFINED,
	ROOT_FOLDER("Root", true),
	SECURITY_FOLDER("Security", true),
	LOGIN_FOLDER("Logins", true),
	WINDOWS_GROUP,
	WINDOWS_GROUP_FOLDER("Windows group", true),
	LOGIN_KIND("Login Kind"),
	SERVER_ROLE_FOLDER("Roles", true),
	CREDENTIAL_FOLDER("Credentials", true),
	CRYPTOGRAPHIC_PROVIDER_FOLDER("Cryptographic Providers", true),
	AUDIT_FOLDER("Audits", true),
	SERVER_AUDIT_SPECIFICATION_FOLDER("Server Audit Specifications", true),
	AGENT_FOLDER("SQL Server Agent", true),
	JOB_FOLDER("Jobs", true),
	JOB("Job"),
	JOB_CATEGORY,
	ALERT_FOLDER("Alerts", true),
	ALERT_GROUP_FOLDER(null, true),
	ALERT("Alert"),
	OPERATOR_FOLDER("Operators", true),
	OPERATOR_GROUP_FOLDER(null, true),
	OPERATOR("Operator"),
	PROXY("Proxy"),
	PROXY_FOLDER("Proxies", true),
	AGENT_ERROR_LOG,
	AGENT_ERROR_LOG_FOLDER("Error Logs", true),
	CERTIFICATE_FOLDER("Certificates", true),
	CERTIFICATE_GROUP_FOLDER(null, true),
	CERTIFICATE("Certificate"),
	SYMMETRIC_KEY,
	ASYMMETRIC_KEY,
	SQL_LOGIN("Login"),
	WINDOWS_LOGIN("Login"),
	WINDOWS_OTHER,
	CERTIFICATE_MAPPED_LOGIN("Login"),
	ASYMMETRIC_KEY_MAPPED_LOGIN,
	CRYPTOGRAPHIC_PROVIDER("Cryptographic provider"),
	AUDIT("Audit"),
	SERVER_AUDIT_SPECIFICATION("Server audit specification"),
	CREDENTIAL("Credential"),
	SYMMETRIC_KEY_FOLDER("Symmetric keys", true),
	ASYMMETRIC_KEY_FOLDER("Asymmetric keys", true),
	ASYMMETRIC_KEY_GROUP_FOLDER(null, true),
	SYMMETRIC_KEY_GROUP_FOLDER(null, true),
	SERVER_PRINCIPAL_FOLDER("Server principals", true),
	WINDOWS_OTHER_FOLDER("Windows others"),
	SQL_LOGIN_FOLDER("Sql", true),
	SERVER_ROLE("Server role"),
	CERTIFICATE_MAPPED_LOGIN_FOLDER("Certificate", true),
	WINDOWS_LOGIN_FOLDER("Windows", true),
	ASYMMETRIC_KEY_MAPPED_LOGIN_FOLDER("Asymmetric Key", true),
	DATABASE_FOLDER("Databases", true),
	DATABASE_USER,
	LANGUAGE,
	DATABASE,
	DATABASE_ROLE,
	DATABASE_ROLE_FOLDER("Roles", true),
	SERVER_PERMISSION_FOLDER("Permissions", true),
	SERVER_PERMISSION_GRANT,
	SERVER_PERMISSION_GRANT_WITH_GRANT,
	SERVER_PERMISSION_DENY,
	SERVER,
	SCHEDULE("Schedule"),
	SCHEDULE_FOLDER("Schedules", true),
	ENDPOINT,
	JOB_GROUP_FOLDER("", isFolder = true);
}