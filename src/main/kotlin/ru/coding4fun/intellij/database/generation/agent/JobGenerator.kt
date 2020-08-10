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
import ru.coding4fun.intellij.database.extension.addCommaWithNewLineScope
import ru.coding4fun.intellij.database.extension.addSeparatorScope
import ru.coding4fun.intellij.database.extension.appendJbLn
import ru.coding4fun.intellij.database.extension.appendLnIfAbsent
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase
import ru.coding4fun.intellij.database.model.property.agent.MsNotifyLevels
import ru.coding4fun.intellij.database.model.property.agent.job.*
import ru.coding4fun.intellij.database.ui.form.common.ModList
import ru.coding4fun.intellij.database.ui.form.common.ModelModification

object JobGenerator : ScriptGeneratorBase<MsJobModel>() {
	override fun getCreatePart(model: MsJobModel, scriptBuilder: StringBuilder): StringBuilder {
		val jobName = model.job.new?.name ?: model.job.old.name
		val isNowCreated = model.job.old.id == DbUtils.defaultId
		val steps = model.steps.associateBy { it.number }

		appendJobScript(model.job, steps, scriptBuilder)
		appendAlerts(model.alertMods, scriptBuilder, jobName)
		appendSchedules(model.scheduleMods, scriptBuilder, jobName)
		appendJobSteps(model.stepMods, scriptBuilder, jobName, isNowCreated)
		return scriptBuilder
	}

	override fun getDropPart(model: MsJobModel, scriptBuilder: StringBuilder): StringBuilder {
		val jobName = model.job.old.name
		scriptBuilder.append("EXEC msdb.dbo.sp_delete_job").appendJbLn()
			.append("  @job_name = ", DbUtils.getQuotedOrNull(jobName), ",").appendJbLn()
			.append("  @delete_history = 1,").appendJbLn()
			.append("  @delete_unused_schedule = 1;").appendJbLn()

		return scriptBuilder
	}

	override fun getAlterPart(model: MsJobModel): String? {
		val isNowCreated = DbUtils.isGuid(model.job.old.id)
		val scriptBuilder = StringBuilder()
		val steps = model.steps.associateBy { it.number }

		appendJobScript(model.job, steps, scriptBuilder)
		appendAlerts(model.alertMods, scriptBuilder, model.job.new!!.name)
		appendSchedules(model.scheduleMods, scriptBuilder, model.job.new!!.name)
		appendJobSteps(model.stepMods, scriptBuilder, model.job.new!!.name, isNowCreated)
		return wrapToTryCatch(scriptBuilder)
	}

	private fun wrapToTryCatch(scriptBuilder: StringBuilder): String {
		if (scriptBuilder.isEmpty()) return ""

		val wrapperSb = StringBuilder()
		wrapperSb.appendJbLn("SET XACT_ABORT ON;")
		wrapperSb.appendJbLn("BEGIN TRANSACTION;")
		wrapperSb.appendJbLn("BEGIN TRY")

		wrapperSb.appendJbLn(scriptBuilder.toString())

		wrapperSb.appendJbLn("COMMIT TRANSACTION;")
		wrapperSb.appendJbLn("END TRY")
		wrapperSb.appendJbLn("BEGIN CATCH")
		wrapperSb.appendJbLn("	ROLLBACK TRANSACTION;")
		wrapperSb.appendJbLn("END CATCH")

		return wrapperSb.toString()
	}

	private fun appendJobScript(
		jobModification: ModelModification<MsJob>,
		steps: Map<Int, MsJobStep>,
		scriptBuilder: StringBuilder
	) {
		if (!jobModification.isModified) return
		val job = jobModification.new ?: jobModification.old

		val jobName: String
		val createMode = jobModification.old.id == DbUtils.defaultId || jobModification.new == null
		if (createMode) {
			jobName = job.name
			scriptBuilder.append("EXEC msdb.dbo.sp_add_job").appendJbLn()
		} else {
			jobName = job.name
			scriptBuilder.append("EXEC msdb.dbo.sp_update_job").appendJbLn()
		}

		scriptBuilder
			.append("  @job_name = ", DbUtils.getQuotedOrNull(jobName), ",").appendJbLn()
			.addCommaWithNewLineScope().also { scope ->
				scope.invokeIf(!createMode && job.name != jobModification.old.name) {
					scriptBuilder.append("  @new_name = ", DbUtils.getQuotedOrNull(job.name))
				}
				scope.invokeIf(createMode || job.isEnabled != jobModification.old.isEnabled) {
					scriptBuilder.append("  @enabled = ", if (job.isEnabled) "1" else "0")
				}
				scope.invokeIf(createMode || job.description != jobModification.old.description) {
					scriptBuilder.append("  @description = ", DbUtils.getQuotedOrNull(job.description))
				}
				scope.invokeIf(createMode || job.startStepId != jobModification.old.startStepId) {
					val startStep = if (!steps.any()) 0 else steps[job.startStepId?.toInt() ?: 0]?.number
					if (startStep != null) scriptBuilder.append("  @start_step_id = ", startStep)
				}
				scope.invokeIf(createMode || job.categoryName != jobModification.old.categoryName) {
					scriptBuilder.append("  @category_name = ", DbUtils.getQuotedOrNull(job.categoryName))
				}
				scope.invokeIf(createMode || job.ownerName != jobModification.old.ownerName) {
					scriptBuilder.append("  @owner_login_name = ", DbUtils.getQuotedOrNull(job.ownerName))
				}
				scope.invokeIf(createMode || job.eventLogLevel != jobModification.old.eventLogLevel) {
					scriptBuilder.append("  @notify_level_eventlog = ", job.eventLogLevel?.id ?: "null")
				}
				scope.invokeIf(createMode || job.eMailNotifyLevel != jobModification.old.eMailNotifyLevel) {
					scriptBuilder.append("  @notify_level_email = ", job.eMailNotifyLevel?.id ?: "null")
				}
				scope.invokeIf(createMode || job.eMailOperatorName != jobModification.old.eMailOperatorName) {
					scriptBuilder.append("  @notify_email_operator_name = ", DbUtils.getQuotedOrNull(job.eMailOperatorName))
				}
				scope.invokeIf(createMode || job.deleteLevel != jobModification.old.deleteLevel) {
					scriptBuilder.append("  @delete_level = ", job.deleteLevel?.id ?: "null")
				}
			}

		scriptBuilder.appendLnIfAbsent()
	}

	private fun appendAlerts(alerts: ModList<MsAlert>, scriptBuilder: StringBuilder, jobName: String) {
		if (!alerts.any()) return

		for (alert in alerts) {
			scriptBuilder.append("EXEC msdb.dbo.sp_update_alert").appendJbLn()
				.append("  @name = ", DbUtils.getQuotedOrNull(alert.new!!.name), ",").appendJbLn()

			if (!alert.old.isSelected && alert.new!!.isSelected) {
				scriptBuilder.append("  @job_name = ", DbUtils.getQuotedOrNull(jobName), ";").appendJbLn()
			} else {
				scriptBuilder.append("  @job_id = '00000000-0000-0000-0000-000000000000';").appendJbLn()
			}
		}
	}

	private fun appendSchedules(schedules: ModList<MsSchedule>, scriptBuilder: StringBuilder, jobName: String) {
		if (!schedules.any()) return

		for (schedule in schedules) {
			if (schedule.new == null) continue
			val newSchedule = schedule.new!!
			if (!schedule.old.isSelected && newSchedule.isSelected) {
				scriptBuilder.append("EXEC msdb.dbo.sp_attach_schedule").appendJbLn()
					.append("  @job_name = ", DbUtils.getQuotedOrNull(jobName), ",").appendJbLn()
					.append("  @schedule_name = ", DbUtils.getQuotedOrNull(newSchedule.name), ";").appendJbLn()
			} else {
				scriptBuilder.append("EXEC msdb.dbo.sp_detach_schedule").appendJbLn()
					.append("  @job_name = ", DbUtils.getQuotedOrNull(jobName), ",").appendJbLn()
					.append("  @schedule_name = ", DbUtils.getQuotedOrNull(newSchedule.name), ",").appendJbLn()
					.append("  @delete_unused_schedule = 1;").appendJbLn()
			}
		}
	}

	private fun appendJobSteps(
		steps: ModList<MsJobStep>,
		scriptBuilder: java.lang.StringBuilder,
		jobName: String,
		isNowCreated: Boolean
	) {
		if (!steps.any()) return

		for (step in steps) {
			val newStep = step.new!!
			if (!isNowCreated && step.old.number != newStep.number) {
				scriptBuilder.append("EXEC msdb.dbo.sp_delete_jobstep ", "@job_name = '", jobName, "', ", "@step_id = ", step.old.number).appendJbLn()
			}
		}
		
		for (step in steps) {
			val newStep = step.new!!

			val proc = if (step.old.number == newStep.number && !isNowCreated) "EXEC msdb.dbo.sp_update_jobstep" else "EXEC msdb.dbo.sp_add_jobstep"
			scriptBuilder.append(proc).appendJbLn()
			scriptBuilder.addCommaWithNewLineScope().also { scope ->
				scope.invoke { scriptBuilder.append("  @job_name = ", DbUtils.getQuotedOrNull(jobName)) }
				scope.invoke { scriptBuilder.append("  @step_id = ", newStep.number) }
				scope.invoke { scriptBuilder.append("  @step_name = ", DbUtils.getQuotedOrNull(newStep.name)) }
				scope.invoke { scriptBuilder.append("  @subsystem = ", DbUtils.getQuotedOrNull(newStep.type)) }
				scope.invoke { scriptBuilder.append("  @command = ", DbUtils.getQuotedOrNull(newStep.command)) }

				if (listOf("TSQL", "QueueReader").contains(newStep.type)) {
					scope.invoke { scriptBuilder.append("  @database_name = ", DbUtils.getQuotedOrNull(newStep.dbName)) }
				}

				scope.invokeIf(isNowCreated || newStep.flags != step.old.flags) {
					scriptBuilder.append("  @flags = ", newStep.flags)

					scriptBuilder.append("/* ")
					scriptBuilder.addSeparatorScope { scriptBuilder.append(", ") }.also { flagScope ->
						for (stepFlag in newStep.flagsList) {
							flagScope.invoke { scriptBuilder.append(stepFlag.title) }
						}
					}
					scriptBuilder.append("*/")
				}
				scope.invokeIf(isNowCreated || newStep.onSuccessAction != step.old.onSuccessAction) {
					scriptBuilder.append("  @on_success_action = ", newStep.onSuccessAction,
						getLevelComment(newStep.onSuccessAction))
				}
				scope.invokeIf(isNowCreated || newStep.onFailureAction != step.old.onFailureAction) {
					scriptBuilder.append("  @on_fail_action = ", newStep.onFailureAction,
						getLevelComment(newStep.onSuccessAction))
				}
				scope.invokeIf(isNowCreated || newStep.retryAttempts != step.old.retryAttempts) {
					scriptBuilder.append("  @retry_attempts = ", newStep.retryAttempts)
				}
				scope.invokeIf(isNowCreated || newStep.retryInterval != step.old.retryInterval) {
					scriptBuilder.append("  @retry_interval = ", newStep.retryInterval)
				}
				scope.invokeIf(isNowCreated || newStep.outputFile != step.old.outputFile) {
					scriptBuilder.append("  @output_file_name = ", DbUtils.getQuotedOrNull(newStep.outputFile))
				}
				scope.invokeIf(isNowCreated || newStep.proxyName != step.old.proxyName) {
					scriptBuilder.append("  @proxy_name = ", DbUtils.getQuotedOrNull(newStep.proxyName))
				}
			}
			scriptBuilder.appendJbLn()
		}
	}

	private fun getLevelComment(id: Short): String = "/*" + MsNotifyLevels.levels[id.toString()]!!.actionDescription + "*/"
}