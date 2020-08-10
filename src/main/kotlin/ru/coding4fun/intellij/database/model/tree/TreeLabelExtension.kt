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

val TreeLabel.isLogin: Boolean
    get() = when (this.kind) {
        MsKind.SQL_LOGIN -> true
        MsKind.WINDOWS_LOGIN -> true
        MsKind.ASYMMETRIC_KEY_MAPPED_LOGIN -> true
        MsKind.CERTIFICATE_MAPPED_LOGIN -> true
        else -> false
    }

val TreeLabel.isCredential: Boolean get() = MsKind.CREDENTIAL == this.kind

val TreeLabel.isAsymmetricKey: Boolean get() = MsKind.ASYMMETRIC_KEY == this.kind

val TreeLabel.isCertificate: Boolean get() = MsKind.CERTIFICATE == this.kind

val TreeLabel.isSymmetricKey: Boolean get() = MsKind.SYMMETRIC_KEY == this.kind

val TreeLabel.isCryptographicProvider: Boolean get() = MsKind.CRYPTOGRAPHIC_PROVIDER == this.kind

val TreeLabel.isServerAuditSpecification: Boolean get() = MsKind.SERVER_AUDIT_SPECIFICATION == this.kind

val TreeLabel.isServerAudit: Boolean get() = MsKind.AUDIT == this.kind

val TreeLabel.isServerRole: Boolean get() = MsKind.SERVER_ROLE == this.kind

val TreeLabel.isJob: Boolean get() = MsKind.JOB == this.kind

val TreeLabel.isProxy: Boolean get() = MsKind.PROXY == this.kind

val TreeLabel.isOperator: Boolean get() = MsKind.OPERATOR == this.kind

val TreeLabel.isSchedule: Boolean get() = MsKind.SCHEDULE == this.kind

val TreeLabel.isAlert: Boolean get() = MsKind.ALERT == this.kind

val TreeLabel.isGroup: Boolean get() = GROUP_KINDS.contains(this.kind)

private val GROUP_KINDS = hashSetOf(
    MsKind.JOB_GROUP_FOLDER, MsKind.OPERATOR_GROUP_FOLDER, MsKind.ALERT_GROUP_FOLDER,
    MsKind.ASYMMETRIC_KEY_GROUP_FOLDER, MsKind.SYMMETRIC_KEY_GROUP_FOLDER, MsKind.CERTIFICATE_GROUP_FOLDER
)

val TreeLabel.isReadOnly: Boolean
    get() = arrayListOf(
        !this.isLogin,
        !this.isCredential,
        !this.isAsymmetricKey,
        !this.isCertificate,
        !this.isSymmetricKey,
        !this.isCryptographicProvider,
        !this.isServerAuditSpecification,
        !this.isServerAudit,
        !this.isServerRole,
        !this.isJob,
        !this.isOperator,
        !this.isSchedule,
        !this.isProxy
    ).all { it }

val TreeLabel.isRoot: Boolean
    get() = MsKind.ROOT_FOLDER == this.kind