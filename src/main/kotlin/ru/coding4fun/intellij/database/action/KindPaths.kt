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

package ru.coding4fun.intellij.database.action

import ru.coding4fun.intellij.database.model.tree.MsKind

object KindPaths {
	val login = setOf(
		MsKind.SECURITY_FOLDER,
		MsKind.SERVER_PRINCIPAL_FOLDER,
		MsKind.SQL_LOGIN,
		MsKind.SQL_LOGIN_FOLDER,
		MsKind.WINDOWS_LOGIN,
		MsKind.WINDOWS_LOGIN_FOLDER,
		MsKind.CERTIFICATE_MAPPED_LOGIN,
		MsKind.CERTIFICATE_MAPPED_LOGIN_FOLDER,
		MsKind.ASYMMETRIC_KEY_MAPPED_LOGIN,
		MsKind.ASYMMETRIC_KEY_MAPPED_LOGIN_FOLDER
	)
	val role = setOf(
		MsKind.SERVER_ROLE,
		MsKind.SERVER_ROLE_FOLDER,
		MsKind.SERVER_PRINCIPAL_FOLDER,
		MsKind.SECURITY_FOLDER
	)
	val asymmetricKey = setOf(MsKind.ASYMMETRIC_KEY, MsKind.ASYMMETRIC_KEY_FOLDER, MsKind.SECURITY_FOLDER)
	val certificate = setOf(MsKind.CERTIFICATE, MsKind.CERTIFICATE_FOLDER, MsKind.SECURITY_FOLDER)
	val credential = setOf(MsKind.SECURITY_FOLDER, MsKind.CREDENTIAL_FOLDER, MsKind.CREDENTIAL)
	val cryptographicProvider =
		setOf(MsKind.CRYPTOGRAPHIC_PROVIDER, MsKind.CRYPTOGRAPHIC_PROVIDER_FOLDER, MsKind.SECURITY_FOLDER)
	val symmetricKey = setOf(MsKind.SYMMETRIC_KEY, MsKind.SYMMETRIC_KEY_FOLDER, MsKind.SECURITY_FOLDER)
	val auditSpecification =
		setOf(MsKind.SERVER_AUDIT_SPECIFICATION, MsKind.SERVER_AUDIT_SPECIFICATION_FOLDER, MsKind.SECURITY_FOLDER)
	val audits = setOf(MsKind.AUDIT, MsKind.AUDIT_FOLDER, MsKind.SECURITY_FOLDER)

	val job = setOf(MsKind.JOB, MsKind.JOB_FOLDER, MsKind.JOB_GROUP_FOLDER, MsKind.AGENT_FOLDER)
	val alert = setOf(MsKind.AGENT_FOLDER, MsKind.ALERT, MsKind.ALERT_GROUP_FOLDER, MsKind.ALERT_FOLDER)
	val operator = setOf(MsKind.AGENT_FOLDER, MsKind.OPERATOR_FOLDER, MsKind.OPERATOR_GROUP_FOLDER, MsKind.OPERATOR)
	val schedule = setOf(MsKind.SCHEDULE, MsKind.SCHEDULE_FOLDER, MsKind.AGENT_FOLDER)
	val proxy = setOf(MsKind.PROXY, MsKind.PROXY_FOLDER, MsKind.AGENT_FOLDER)
}