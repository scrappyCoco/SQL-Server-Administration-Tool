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


import ru.coding4fun.intellij.database.extension.appendJbLn
import ru.coding4fun.intellij.database.extension.appendLnIfAbsent
import ru.coding4fun.intellij.database.generation.ScriptGeneratorBase
import ru.coding4fun.intellij.database.model.property.agent.schedule.MsSchedule
import ru.coding4fun.intellij.database.model.property.agent.schedule.MsScheduleJob
import ru.coding4fun.intellij.database.model.property.agent.schedule.MsScheduleModel
import ru.coding4fun.intellij.database.ui.form.common.ModList

object ScheduleGenerator : ScriptGeneratorBase<MsScheduleModel>() {
	override fun getCreatePart(model: MsScheduleModel, scriptBuilder: StringBuilder): StringBuilder {
		scriptBuilder.appendJbLn("EXEC msdb.dbo.sp_add_schedule ")
			.append("@schedule_name = '", model.schedule.new!!.name, "', ").appendJbLn()
		appendScheduleArguments(model.schedule.new!!, scriptBuilder)
		appendJobs(model.jobModList, model.schedule.new!!.id, scriptBuilder)
		return scriptBuilder
	}

	override fun getDropPart(model: MsScheduleModel, scriptBuilder: StringBuilder): StringBuilder {
		return scriptBuilder.append(
			"EXEC msdb.dbo.sp_delete_schedule @schedule_id = ", model.schedule.old.id,
			", @force_delete = 0;"
		)
	}

	override fun getAlterPart(model: MsScheduleModel): String? {
		val scriptBuilder = StringBuilder()
		if (model.schedule.isModified) {
			scriptBuilder.append("EXEC msdb.dbo.sp_update_schedule ")
				.append("@name = '", model.schedule.old.name, "', ").appendJbLn()
			appendScheduleArguments(model.schedule.new!!, scriptBuilder)
		}

		appendJobs(model.jobModList, model.schedule.new!!.id, scriptBuilder)
		return scriptBuilder.toString()
	}

	private fun appendJobs(jobMods: ModList<MsScheduleJob>, scheduleId: String, scriptBuilder: StringBuilder) {
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
			.append("@owner_login_name  = ", if (schedule.ownerLoginName == null) "null" else "'" + schedule.ownerLoginName + "'", ";").appendJbLn()
	}
}