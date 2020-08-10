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

package ru.coding4fun.intellij.database.generation

import org.apache.commons.lang.text.StrSubstitutor
import ru.coding4fun.intellij.database.client.MsResourceUtil
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

abstract class ResourceGenerator<Model : Any>(private val sqlResource: String) : ScriptGeneratorBase<Model>() {
	protected open fun mapModelToParams(model: Model): Map<String, String> {
		val propertyMap = HashMap<String, String>(10)
		for (memberProperty in model::class.memberProperties) {
			@Suppress("UNCHECKED_CAST")
			val propertyValue = (memberProperty as KProperty1<Any, *>).get(model)?.toString() ?: ""
			propertyMap[memberProperty.name] = propertyValue
		}
		return propertyMap
	}

	override fun getCreatePart(model: Model, scriptBuilder: StringBuilder): StringBuilder {
		val queryParameters = mapModelToParams(model)
		var sql = MsResourceUtil.readQuery(sqlResource)
		if (queryParameters.any()) {
			val stringSubstitutor = StrSubstitutor(queryParameters, "??", "??")
			sql = stringSubstitutor.replace(sql)
		}
		return scriptBuilder.append(sql)
	}

	override fun getDropPart(model: Model, scriptBuilder: StringBuilder): StringBuilder = scriptBuilder
	override fun getAlterPart(model: Model): String? = null
}