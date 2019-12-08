package ru.coding4fun.intellij.database.generation.agent


import ru.coding4fun.intellij.database.extension.appendJbLn
import ru.coding4fun.intellij.database.extension.appendLnIfAbsent
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase
import ru.coding4fun.intellij.database.model.property.agent.schedule.MsSchedule
import ru.coding4fun.intellij.database.model.property.agent.schedule.MsScheduleJob
import ru.coding4fun.intellij.database.model.property.agent.schedule.MsScheduleModel
import ru.coding4fun.intellij.database.ui.form.common.Modifications

object ScheduleGenerator : ScriptGeneratorBase<MsScheduleModel>() {
	override fun getCreatePart(model: MsScheduleModel, scriptBuilder: StringBuilder, reverse: Boolean): StringBuilder {
		if (reverse) model.schedule.reverse()
		scriptBuilder.append("EXEC msdb.dbo.sp_add_schedule ")
		appendScheduleArguments(model.schedule.new!!, scriptBuilder)
		appendJobs(model.jobModifications, model.schedule.new!!.id, scriptBuilder)
		return scriptBuilder
	}

	override fun getDropPart(model: MsScheduleModel, scriptBuilder: StringBuilder): StringBuilder {
		return scriptBuilder.append(
			"EXEC msdb.dbo.sp_delete_schedule @schedule_id = ", model.schedule.old!!.id,
			", @force_delete = 0;"
		)
	}

	override fun getAlterPart(model: MsScheduleModel): String? {
		val scriptBuilder = StringBuilder()
		if (model.schedule.isModified) {
			scriptBuilder.append("EXEC msdb.dbo.sp_update_schedule ")
			appendScheduleArguments(model.schedule.new!!, scriptBuilder)
		}

		appendJobs(model.jobModifications, model.schedule.new!!.id, scriptBuilder)
		return scriptBuilder.toString()
	}

	private fun appendJobs(jobMods: Modifications<MsScheduleJob>, scheduleId: String, scriptBuilder: StringBuilder) {
		for (job in jobMods) {
			if (job.new!!.isSelected) {
				scriptBuilder.appendLnIfAbsent().append(
					"EXEC msdb.dbo.sp_attach_schedule ",
					"@job_id = '", job.new!!.id, "', ",
					"@schedule_id = ", scheduleId, ";"
				)
			} else {
				scriptBuilder.appendLnIfAbsent().append(
					"EXEC msdb.dbo.sp_detach_schedule ",
					"@job_id = '", job.new!!.id, "', ",
					"@schedule_id = ", scheduleId, ", ",
					"@delete_unused_schedule = 0;"
				)
			}

		}
	}

	private fun appendScheduleArguments(schedule: MsSchedule, scriptBuilder: StringBuilder) {
		scriptBuilder
			.append("@enabled = ", if (schedule.enabled) "1" else "0", ", ").appendJbLn()
			.append("@freq_type = ", schedule.freqType, ", ").appendJbLn()
			.append("@freq_interval = ", schedule.freqInterval, ", ").appendJbLn()
			.append("@freq_subday_type  = ", schedule.freqSubDayType, ", ").appendJbLn()
			.append("@freq_subday_interval  = ", schedule.freqSubDayInterval, ", ").appendJbLn()
			.append("@freq_relative_interval  = ", schedule.freqRelativeInterval, ", ").appendJbLn()
			.append("@freq_recurrence_factor  = ", schedule.freqRecurrenceFactor, ", ").appendJbLn()
			.append("@active_start_date = ", schedule.activeStartDate, ", ").appendJbLn()
			.append("@active_end_date = ", schedule.activeEndDate, ", ").appendJbLn()
			.append("@active_start_time = ", schedule.activeStartTime, ", ").appendJbLn()
			.append("@active_end_time = ", schedule.activeEndTime, ", ").appendJbLn()
			.append("@owner_login_name  = '", schedule.ownerLoginName, "';").appendJbLn()
	}
}