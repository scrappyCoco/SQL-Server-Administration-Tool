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

package ru.coding4fun.intellij.database.model.property.agent.job

import ru.coding4fun.intellij.database.model.common.Copyable
import ru.coding4fun.intellij.database.model.common.Identity

data class MsJobStep(
    override var id: String,
    override var name: String,
    var number: Int,
    var type: String,
    var onSuccessAction: Short,
    var onFailureAction: Short,
    var command: String,
    var dbName: String?,
    var proxyName: String?,
    var retryAttempts: Int,
    var retryInterval: Int,
    var outputFile: String?,
    var appendFile: Boolean,
    var overrideFile: Boolean,
    var stepHistory: Boolean,
    var jobHistory: Boolean,
    var abortEvent: Boolean,
    var appendTable: Boolean,
    var overrideTable: Boolean,
    var jobId: String
) : Identity, Copyable<MsJobStep> {
    override fun getCopy(): MsJobStep = copy()
    override fun toString(): String = name

    val flags: Int get() = flagsList.sumBy { it.id }

    val flagsList: List<MsJobStepFlags> get() = listOfNotNull(
        if (appendFile) MsJobStepFlags.AppendFile else null,
        if (overrideFile) MsJobStepFlags.OverrideFile else null,
        if (overrideTable) MsJobStepFlags.OverrideTable else null,
        if (appendTable) MsJobStepFlags.AppendTable else null,
        if (jobHistory) MsJobStepFlags.JobHistory else null,
        if (stepHistory) MsJobStepFlags.StepHistory else null,
        if (abortEvent) MsJobStepFlags.AbortEvent else null
    )
}