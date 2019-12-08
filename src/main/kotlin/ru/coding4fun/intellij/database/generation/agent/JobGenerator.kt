package ru.coding4fun.intellij.database.generation.agent

import ru.coding4fun.intellij.database.extension.addCommaWithNewLineScope
import ru.coding4fun.intellij.database.extension.appendJbLn
import ru.coding4fun.intellij.database.extension.appendLnIfAbsent
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase
import ru.coding4fun.intellij.database.model.property.agent.job.*
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import ru.coding4fun.intellij.database.ui.form.common.Modifications

object JobGenerator : ScriptGeneratorBase<MsJobModel>() {
	override fun getCreatePart(model: MsJobModel, scriptBuilder: StringBuilder, reverse: Boolean): StringBuilder {
		if (reverse) model.job.reverse()
		val jobName = model.job.new!!.name
		appendJobScript(model.job, scriptBuilder)
		appendAlerts(model.alertMods, scriptBuilder, jobName)
		appendSchedules(model.scheduleMods, scriptBuilder, jobName)
		//appendJobSteps(model.steps, scriptBuilder, jobName)
		return scriptBuilder
	}

	override fun getDropPart(model: MsJobModel, scriptBuilder: StringBuilder): StringBuilder {
		val jobName = model.job.old!!.name
		scriptBuilder.append("EXEC msdb.dbo.sp_delete_job").appendJbLn()
			.append("  @job_name = '", jobName, "',").appendJbLn()
			.append("  @delete_history = 1,").appendJbLn()
			.append("  @delete_unused_schedule = 1;").appendJbLn()

		return scriptBuilder
	}

	override fun getAlterPart(model: MsJobModel): String? {
		val scriptBuilder = StringBuilder()
		appendJobScript(model.job, scriptBuilder)
		appendAlerts(model.alertMods, scriptBuilder, model.job.new!!.name)
		appendSchedules(model.scheduleMods, scriptBuilder, model.job.new!!.name)
		//appendJobSteps(model.steps, scriptBuilder, model.job.new!!.name)
		return scriptBuilder.toString()
	}

	private fun appendJobScript(jobModification: ModelModification<MsJob>, scriptBuilder: StringBuilder) {
		if (!jobModification.isModified) return

		if (jobModification.old == null) {
			scriptBuilder.append("EXEC msdb.dbo.sp_add_job").appendJbLn()
		} else {
			scriptBuilder.append("EXEC msdb.dbo.sp_update_job").appendJbLn()
		}

		val newJob = jobModification.new!!
		scriptBuilder
			.append("  @job_name = '", jobModification.old?.name ?: newJob.name, "',").appendJbLn()
			.addCommaWithNewLineScope().also { scope ->
				scope.invokeIf(jobModification.old != null && newJob.name != jobModification.old!!.name) {
					scriptBuilder.append("  @new_name = '", newJob.name, "'")
				}
				scope.invokeIf(newJob.isEnabled != jobModification.old?.isEnabled) {
					scriptBuilder.append("  @enabled = ", if (newJob.isEnabled) "1" else "0")
				}
				scope.invokeIf(newJob.description != jobModification.old?.description) {
					scriptBuilder.append("  @description = '", newJob.description, "'")
				}
				scope.invokeIf(newJob.startStepId != jobModification.old?.startStepId) {
					scriptBuilder.append("  @start_step_id = ", newJob.startStepId)
				}
				scope.invokeIf(newJob.categoryName != jobModification.old?.categoryName) {
					scriptBuilder.append("  @category_name = '", newJob.categoryName, "'")
				}
				scope.invokeIf(newJob.ownerName != jobModification.old?.ownerName) {
					scriptBuilder.append("  @owner_login_name = '", newJob.ownerName, "'")
				}
				scope.invokeIf(newJob.eventLogLevel != jobModification.old?.eventLogLevel) {
					scriptBuilder.append("  @notify_level_eventlog = ", newJob.eventLogLevel?.id ?: "null")
				}
				scope.invokeIf(newJob.eMailNotifyLevel != jobModification.old?.eMailNotifyLevel) {
					scriptBuilder.append("  @notify_level_email = ", newJob.eMailNotifyLevel?.id ?: "null")
				}
				scope.invokeIf(newJob.eMailOperatorName != jobModification.old?.eMailOperatorName) {
					scriptBuilder.append(
						"  @notify_email_operator_name = '",
						newJob.eMailOperatorName,
						"'"
					)
				}
				scope.invokeIf(newJob.deleteLevel != jobModification.old?.deleteLevel) {
					scriptBuilder.append("  @delete_level = ", newJob.deleteLevel?.id ?: "null")
				}
			}

		scriptBuilder.appendLnIfAbsent()
	}

	private fun appendAlerts(alerts: Modifications<MsAlert>, scriptBuilder: StringBuilder, jobName: String) {
		if (!alerts.any()) return

		for (alert in alerts) {
			scriptBuilder.append("EXEC msdb.dbo.sp_update_alert").appendJbLn()
				.append("  @name = '", alert.new!!.name, "',").appendJbLn()

			if (!alert.old!!.isSelected && alert.new!!.isSelected) {
				scriptBuilder.append("  @job_name = '", jobName, "';").appendJbLn()
			} else {
				scriptBuilder.append("  @job_id = '00000000-0000-0000-0000-000000000000';").appendJbLn()
			}
		}
	}

	private fun appendSchedules(schedules: Modifications<MsSchedule>, scriptBuilder: StringBuilder, jobName: String) {
		if (!schedules.any()) return

		for (schedule in schedules) {
			if (schedule.new == null) continue
			val newSchedule = schedule.new!!
			if (!schedule.old!!.isSelected && newSchedule.isSelected) {
				scriptBuilder.append("EXEC msdb.dbo.sp_attach_schedule").appendJbLn()
					.append("  @job_name = '", jobName, "',").appendJbLn()
					.append("  @schedule_name = '", newSchedule.name, "';").appendJbLn()
			} else {
				scriptBuilder.append("EXEC msdb.dbo.sp_detach_schedule").appendJbLn()
					.append("  @job_name = '", jobName, "',").appendJbLn()
					.append("  @schedule_name = '", newSchedule.name, "',").appendJbLn()
					.append("  @delete_unused_schedule = 1;").appendJbLn()
			}
		}
	}

	private fun appendJobSteps(
		steps: Modifications<MsJobStep>,
		scriptBuilder: java.lang.StringBuilder,
		jobName: String
	) {
		if (!steps.any()) return

		for (step in steps) {
			val newStep = step.new!!
			scriptBuilder.append("EXEC msdb.dbo.sp_add_jobstep").appendJbLn()
				.append("  @job_name = '", jobName, "',").appendJbLn()
				.append("  @step_id = ", newStep.id, ",").appendJbLn()
				.append("  @step_name = '", newStep.name, "',").appendJbLn()
				.append("  @subsystem = '", newStep.type, "',").appendJbLn()
				.append("  @command = '", newStep.command, "',").appendJbLn()
				.append("  @database_name = '", newStep.dbName, "',").appendJbLn()

			scriptBuilder.addCommaWithNewLineScope().also { scope ->
				scope.invokeIf(newStep.retryAttempts != step.old?.retryAttempts) {
					scriptBuilder.append("  @retry_attempts = ", newStep.retryAttempts)
				}
				scope.invokeIf(newStep.retryInterval != step.old?.retryInterval) {
					scriptBuilder.append("  @retry_interval = ", newStep.retryInterval)
				}
				scope.invokeIf(newStep.outputFile != step.old?.outputFile) {
					scriptBuilder.append("  @output_file_name = ", newStep.outputFile ?: "NULL")
				}
				scope.invokeIf(newStep.proxyName != step.old?.proxyName) {
					scriptBuilder.append("  @output_file_name = ", newStep.proxyName ?: "NULL")
				}
			}
		}
	}
}