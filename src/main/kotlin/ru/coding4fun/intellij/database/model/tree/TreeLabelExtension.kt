package ru.coding4fun.intellij.database.model.tree

val TreeLabel.isLogin: Boolean
	get() = when (this.kind) {
		MsKind.SQL_LOGIN -> true
		MsKind.WINDOWS_LOGIN -> true
		MsKind.ASYMMETRIC_KEY_MAPPED_LOGIN -> true
		MsKind.CERTIFICATE_MAPPED_LOGIN -> true
		else -> false
	}

val TreeLabel.isCredential: Boolean
	get() = MsKind.CREDENTIAL == this.kind

val TreeLabel.isAsymmetricKey: Boolean
	get() = MsKind.ASYMMETRIC_KEY == this.kind

val TreeLabel.isCertificate: Boolean
	get() = MsKind.CERTIFICATE == this.kind

val TreeLabel.isSymmetricKey: Boolean
	get() = MsKind.SYMMETRIC_KEY == this.kind

val TreeLabel.isCryptographicProvider: Boolean
	get() = MsKind.CRYPTOGRAPHIC_PROVIDER == this.kind

val TreeLabel.isServerAuditSpecification: Boolean
	get() = MsKind.SERVER_AUDIT_SPECIFICATION == this.kind

val TreeLabel.isServerAudit: Boolean
	get() = MsKind.AUDIT == this.kind

val TreeLabel.isServerRole: Boolean
	get() = MsKind.SERVER_ROLE == this.kind

val TreeLabel.isJob: Boolean
	get() = MsKind.JOB == this.kind

val TreeLabel.isOperator: Boolean
	get() = MsKind.OPERATOR == this.kind

val TreeLabel.isSchedule: Boolean
	get() = MsKind.SCHEDULE == this.kind

val TreeLabel.isAlert: Boolean
	get() = MsKind.ALERT == this.kind

val TreeLabel.isGroup: Boolean
	get() = GROUP_KINDS.contains(this.kind)

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
		!this.isSchedule
	).all { it }

val TreeLabel.isRoot: Boolean
	get() = MsKind.ROOT_FOLDER == this.kind