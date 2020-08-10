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

package ru.coding4fun.intellij.database.data.property.agent.impl

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.client.MsClient
import ru.coding4fun.intellij.database.client.QueryDefinition
import ru.coding4fun.intellij.database.data.property.DbUtils
import ru.coding4fun.intellij.database.data.property.agent.ScheduleDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.property.agent.schedule.MsSchedule
import ru.coding4fun.intellij.database.model.property.agent.schedule.MsScheduleJob
import ru.coding4fun.intellij.database.model.property.agent.schedule.MsScheduleModel
import ru.coding4fun.intellij.database.ui.form.common.toMod
import java.util.function.Consumer

class ScheduleDataProviderImpl(project: Project) : MsClient(project), ScheduleDataProvider {

    override fun getModels(
        objectIds: Array<String>?,
        successConsumer: Consumer<Map<String, MsScheduleModel>>,
        errorConsumer: Consumer<Exception>
    ) {
        var jobs: List<MsScheduleJob> = emptyList()
        var schedules: List<MsSchedule> = emptyList()

        val models: HashMap<String, MsScheduleModel> =
            objectIds?.associateTo(HashMap(), { it to MsScheduleModel() }) ?: HashMap()

        val queries = arrayListOf(
            QueryDefinition(
                "sql/action/property/agent/schedule/Jobs.sql",
                DataProviderMessages.message("agent.schedule.progress.job"),
                Consumer { jobs = it.getObjects() }
            ), QueryDefinition(
                "sql/action/property/agent/schedule/Schedule.sql",
                DataProviderMessages.message("agent.schedule.progress.schedule"),
                Consumer { schedules = it.getObjects() }
            )
        )

        if (objectIds == null) models[DbUtils.defaultId] = MsScheduleModel()

        invokeComposite(
            DataProviderMessages.message("agent.schedule.progress.task"),
            queries,
            Consumer {
                val jobMap = jobs.groupBy { it.scheduleId }
                val scheduleMap = schedules.associateBy { it.id }

                for (modelEntry in models) {
                    val scheduleId = modelEntry.key
                    val model = modelEntry.value
                    model.schedule = (scheduleMap[scheduleId] ?: error("Unable to find schedule with id $scheduleId")).toMod()
                    model.jobs = jobMap[scheduleId] ?: emptyList()
                }
                successConsumer.accept(models)
            }, errorConsumer
        )
    }
}