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

package ru.coding4fun.intellij.database.model.property.agent.operator

import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.common.Named
import ru.coding4fun.intellij.database.model.property.agent.MsOperator
import ru.coding4fun.intellij.database.ui.form.common.ModList
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import ru.coding4fun.intellij.database.ui.form.common.toModList

class MsOperatorModel : Named {
	lateinit var operator: ModelModification<MsOperator>
	lateinit var operatorCategories: List<BasicIdentity>
	lateinit var jobs: List<MsOperatorJob>
	lateinit var alerts: List<MsOperatorAlert>
	var alertModList: ModList<MsOperatorAlert> = emptyList<MsOperatorAlert>().toModList()
	var jobModList: ModList<MsOperatorJob> = emptyList<MsOperatorJob>().toModList()
	override var name: String
		get() = (operator.new ?: operator.old)!!.name
		set(_) {}
}