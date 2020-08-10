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


import ru.coding4fun.intellij.database.extension.appendGo
import ru.coding4fun.intellij.database.extension.appendJbLn
import ru.coding4fun.intellij.database.extension.appendLnIfAbsent
import ru.coding4fun.intellij.database.model.common.Named

abstract class ScriptGeneratorBase<Model> {
	fun getCreateScript(model: Model): String {
		val scriptBuilder = StringBuilder()

		getPrePart(model, scriptBuilder)
		getCreatePart(model, scriptBuilder).appendGo()
		getPostPart(model, scriptBuilder)

		return scriptBuilder.toString()
	}

	fun getDropScript(model: Model): String {
		val scriptBuilder = StringBuilder()

		getPrePart(model, scriptBuilder)
		getDropPart(model, scriptBuilder).appendGo()
		getPostPart(model, scriptBuilder)

		return scriptBuilder.toString()
	}

	fun getDropAndCreateScript(model: Model): String {
		val scriptBuilder = StringBuilder()

		getPrePart(model, scriptBuilder)
		getDropPart(model, scriptBuilder).appendGo()
		getCreatePart(model, scriptBuilder).appendGo()
		getPostPart(model, scriptBuilder)

		return scriptBuilder.toString()
	}

	fun getAlterScript(model: Model): String {
		return getAlterPart(model).toString()
	}

	private fun getPrePart(model: Model, scriptBuilder: StringBuilder): StringBuilder {
		if (model is Named) {
			scriptBuilder.appendLnIfAbsent().append("-- region ", model.name)
		}
		return scriptBuilder.appendJbLn()
	}

	private fun getPostPart(model: Model, scriptBuilder: StringBuilder): StringBuilder {
		scriptBuilder.appendLnIfAbsent()
		if (model is Named) {
			scriptBuilder.append("-- endregion ")
		}
		return scriptBuilder.appendJbLn()
	}

	protected abstract fun getCreatePart(model: Model, scriptBuilder: StringBuilder): StringBuilder
	protected abstract fun getDropPart(model: Model, scriptBuilder: StringBuilder): StringBuilder
	protected abstract fun getAlterPart(model: Model): String?
}