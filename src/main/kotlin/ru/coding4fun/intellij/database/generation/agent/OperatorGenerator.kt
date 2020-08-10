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

package ru.coding4fun.intellij.database.generation.agent


import ru.coding4fun.intellij.database.data.property.DbUtils
import ru.coding4fun.intellij.database.extension.addSeparatorScope
import ru.coding4fun.intellij.database.extension.appendJbLn
import ru.coding4fun.intellij.database.extension.appendLnIfAbsent
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase
import ru.coding4fun.intellij.database.model.property.agent.MsOperator
import ru.coding4fun.intellij.database.model.property.agent.operator.MsOperatorAlert
import ru.coding4fun.intellij.database.model.property.agent.operator.MsOperatorJob
import ru.coding4fun.intellij.database.model.property.agent.operator.MsOperatorModel
import ru.coding4fun.intellij.database.ui.form.common.ModList

object OperatorGenerator : ScriptGeneratorBase<MsOperatorModel>() {
	override fun getCreatePart(model: MsOperatorModel, scriptBuilder: StringBuilder): StringBuilder {
		val operator = model.operator.new ?: model.operator.old
		scriptBuilder.append("EXEC msdb.dbo.sp_add_operator").appendJbLn()
			.append("  @name = ", DbUtils.getQuotedOrNull(operator.name), ",").appendJbLn()
			.append("  @enabled = ", if (operator.isEnabled) "1" else "0", ",").appendJbLn()
			.append("  @email_address = ", DbUtils.getQuotedOrNull(operator.eMail))


		if (!operator.categoryName.isNullOrBlank()) {
			scriptBuilder.appendJbLn(",").append("  @category_name = ", DbUtils.getQuotedOrNull(operator.categoryName))
		}
		scriptBuilder.append(";")

		modifyJobs(scriptBuilder, operator, model.jobModList)

		return scriptBuilder
	}

	override fun getDropPart(model: MsOperatorModel, scriptBuilder: StringBuilder): StringBuilder {
		return scriptBuilder.appendLnIfAbsent()
			.append(
				"EXEC msdb.dbo.sp_delete_operator",
				" @name = ", DbUtils.getQuotedOrNull(model.operator.old.name), ";"
			)
	}

	override fun getAlterPart(model: MsOperatorModel): String? {
		val scriptBuilder = StringBuilder()
		val newOperator = model.operator.new!!
		val oldOperator = model.operator.old
		if (model.operator.isModified) {
			scriptBuilder.addSeparatorScope("EXEC msdb.dbo.sp_update_operator @name = '" + oldOperator.name + "',\n  ") {
				scriptBuilder.append(",").appendJbLn().append("  ")
			}.also { scope ->
				scope.invokeIf(oldOperator.name != newOperator.name) {
					scriptBuilder.append("  @new_name = ", DbUtils.getQuotedOrNull(newOperator.name))
				}
				scope.invokeIf(oldOperator.isEnabled != newOperator.isEnabled) {
					scriptBuilder.append("  @enabled = ", if (newOperator.isEnabled) "1" else "0")
				}
				scope.invokeIf(oldOperator.eMail != newOperator.eMail) {
					scriptBuilder.append("  @email_address = ", DbUtils.getQuotedOrNull(newOperator.eMail))
				}
				scope.invokeIf(oldOperator.categoryName != newOperator.categoryName) {
					scriptBuilder.append("  @category_name = ", DbUtils.getQuotedOrNull(newOperator.categoryName))
				}
			}
		}

		modifyAlerts(scriptBuilder, newOperator, model.alertModList.map { it.new!! }.toList())
		modifyJobs(scriptBuilder, newOperator, model.jobModList)

		return scriptBuilder.toString()
	}

	private fun modifyAlerts(
		scriptBuilder: StringBuilder,
		operator: MsOperator,
		alerts: List<MsOperatorAlert>
	) {
		for (alert in alerts) {
			if (alert.sendToMail) {
				scriptBuilder.appendLnIfAbsent()
					.append("EXEC msdb.dbo.sp_add_notification").appendJbLn()
					.append("  @alert_name = ", DbUtils.getQuotedOrNull(alert.name), ",").appendJbLn()
					.append("  @operator_name = ", DbUtils.getQuotedOrNull(operator.name), ",").appendJbLn()
					.append("  @notification_method = 1;").appendJbLn()
			} else {
				scriptBuilder.appendLnIfAbsent()
					.append("EXEC msdb.dbo.sp_delete_notification").appendJbLn()
					.append("  @alert_name = ", DbUtils.getQuotedOrNull(alert.name), ",").appendJbLn()
					.append("  @operator_name = ", DbUtils.getQuotedOrNull(operator.name), ";").appendJbLn()
			}
		}
	}

	private fun modifyJobs(
		scriptBuilder: StringBuilder,
		operator: MsOperator,
		jobs: ModList<MsOperatorJob>
	) {
		for (job in jobs) {
			scriptBuilder.appendLnIfAbsent()
				.append("EXEC msdb.dbo.sp_update_job @job_id = ", DbUtils.getQuotedOrNull(job.new!!.id), ",").appendJbLn()
				.append(" @notify_email_operator_name = ", DbUtils.getQuotedOrNull(operator.name), ",").appendJbLn()
				.append(" @notify_level_email = ", job.new!!.mailNotifyLevel.id).appendJbLn(";")
		}
	}
}