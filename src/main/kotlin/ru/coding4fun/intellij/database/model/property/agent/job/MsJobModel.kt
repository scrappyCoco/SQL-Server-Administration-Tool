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

import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.common.Named
import ru.coding4fun.intellij.database.ui.form.common.ModList
import ru.coding4fun.intellij.database.ui.form.common.ModelModification

class MsJobModel : Named {
	lateinit var job: ModelModification<MsJob>
	lateinit var categories: List<BasicIdentity>
	lateinit var operators: List<BasicIdentity>
	lateinit var steps: List<MsJobStep>
	lateinit var schedules: List<MsSchedule>
	lateinit var alerts: List<MsAlert>
	lateinit var subSystems: List<BasicIdentity>
	lateinit var databases: List<BasicIdentity>
	lateinit var proxies: List<BasicIdentity>

	var stepMods: ModList<MsJobStep> = emptyList()
	var alertMods: ModList<MsAlert> = emptyList()
	var scheduleMods: ModList<MsSchedule> = emptyList()
	override var name: String
		get() = (job.new ?: job.old)!!.name
		set(_) {}
}