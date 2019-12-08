package ru.coding4fun.intellij.database.generation.security

import ru.coding4fun.intellij.database.extension.addCommaWithNewLineScope
import ru.coding4fun.intellij.database.extension.appendJbLn
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditDestination
import ru.coding4fun.intellij.database.model.property.security.MsServerAuditModel

object ServerAuditGenerator : ScriptGeneratorBase<MsServerAuditModel>() {
	private fun generateScript(model: MsServerAuditModel, scriptBuilder: StringBuilder) {
		val auditName = (model.audit.old?.name ?: model.audit.new?.name)
		val audit = model.audit.new!!

		scriptBuilder.append(if (model.audit.old != null) "ALTER " else "CREATE ")
			.append("SERVER AUDIT [", auditName, "]").appendJbLn()

		if (model.audit.old?.name != null && model.audit.old!!.name != model.audit.new!!.name) {
			scriptBuilder.append("  MODIFY NAME = [", model.audit.new!!.name, "]")
			return
		}

		scriptBuilder.append("  TO ", audit.auditDestination, " ")

		// <file_options>::=
		if (MsServerAuditDestination.FILE == audit.auditDestination) {
			scriptBuilder.addCommaWithNewLineScope().also { separatorScope ->
				scriptBuilder.appendJbLn().append("  (").appendJbLn()

				separatorScope.invokeIf(!audit.filePath.isNullOrBlank()) {
					scriptBuilder.append("    FILEPATH = N'", audit.filePath, "'")
				}

				separatorScope.invoke {
					scriptBuilder.append("    MAXSIZE = ")
					if (audit.maxSize != null) {
						if (audit.maxSize == -1L) {
							scriptBuilder.append("UNLIMITED")
						} else {
							scriptBuilder.append(audit.maxSize, " ", audit.maxSizeUnit)
						}
					}
				}

				separatorScope.invokeIf(audit.maxRolloverFiles != null) {
					scriptBuilder.append(
						"    MAX_ROLLOVER_FILES = ",
						if (audit.maxRolloverFiles == -1) "UNLIMITED" else audit.maxRolloverFiles
					)
				}.invokeElseIf(audit.maxFiles != null) {
					scriptBuilder.append("    MAX_FILES = ", if (audit.maxFiles == -1) "UNLIMITED" else audit.maxFiles)
				}

				separatorScope.invokeIf(audit.reserveDiskSpace != null) {
					scriptBuilder.append("    RESERVE_DISK_SPACE = ", if (audit.reserveDiskSpace!!) "ON" else "OFF")
				}

				scriptBuilder.appendJbLn().append("  )").appendJbLn()
			}
		}

		// <audit_options>::=
		scriptBuilder.append("  WITH (").appendJbLn().append("    ON_FAILURE = ", audit.onAuditLogFailure)
		if (audit.queueDelay != null) {
			scriptBuilder.append(",").appendJbLn().append("    QUEUE_DELAY = ", audit.queueDelay).appendJbLn()
		}
		scriptBuilder.append("  )").appendJbLn()
	}

	override fun getCreatePart(
		model: MsServerAuditModel,
		scriptBuilder: StringBuilder,
		reverse: Boolean
	): StringBuilder {
		if (reverse) model.audit.reverse()
		generateScript(model, scriptBuilder)
		return scriptBuilder
	}

	override fun getDropPart(model: MsServerAuditModel, scriptBuilder: StringBuilder): StringBuilder {
		return scriptBuilder.append("DROP SERVER AUDIT [", model.audit.old!!.name, "]")
	}

	override fun getAlterPart(model: MsServerAuditModel): String? {
		val scriptBuilder = StringBuilder()
		generateScript(model, scriptBuilder)
		return scriptBuilder.toString()
	}
}