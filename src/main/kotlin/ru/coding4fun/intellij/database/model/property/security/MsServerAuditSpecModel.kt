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

package ru.coding4fun.intellij.database.model.property.security

import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.common.Named
import ru.coding4fun.intellij.database.ui.form.common.ModList
import ru.coding4fun.intellij.database.ui.form.common.ModelModification
import ru.coding4fun.intellij.database.ui.form.common.toModList

class MsServerAuditSpecModel : Named {
	lateinit var spec: ModelModification<MsServerAuditSpecification>
	lateinit var defaultServerAudits: List<BasicIdentity>
	lateinit var defaultActions: List<MsServerAuditSpecificationAction>
	var actions: ModList<MsServerAuditSpecificationAction> = emptyList<MsServerAuditSpecificationAction>().toModList()
	override var name: String
		get() = (spec.new ?: spec.old)!!.name
		set(_) {}
}