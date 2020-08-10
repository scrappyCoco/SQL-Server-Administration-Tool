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

enum class MsJobStepFlags(var id: Int, var title: String?) {
    OverrideFile(0, "Overwrite output file"),
    AppendFile(2, "Append to output file"),
    StepHistory(4, "Write Transact-SQL job step output to step history"),
    JobHistory(32, "Write all output to job history"),
    AbortEvent(64, "Create a Windows event to use as a signal for the Cmd jobstep to abort"),
    OverrideTable(8, "Overwrite existing history"),
    AppendTable(16, "Append to existing history")
}