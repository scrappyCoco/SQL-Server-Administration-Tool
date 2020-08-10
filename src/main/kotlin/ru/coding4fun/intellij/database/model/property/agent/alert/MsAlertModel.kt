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

package ru.coding4fun.intellij.database.model.property.agent.alert

import ru.coding4fun.intellij.database.data.property.agent.PerformanceCounterManager
import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.common.Named
import ru.coding4fun.intellij.database.ui.form.common.ModList
import ru.coding4fun.intellij.database.ui.form.common.ModelModification

class MsAlertModel : Named {
	lateinit var alert: ModelModification<MsAlert>
	lateinit var databases: List<BasicIdentity>
	lateinit var categories: List<BasicIdentity>
	lateinit var operators: ModList<Operator>
	lateinit var jobs: List<BasicIdentity>
	lateinit var perfCounterManager: PerformanceCounterManager
	override var name: String
		get() = (alert.new ?: alert.old)!!.name
		set(_) {}
}