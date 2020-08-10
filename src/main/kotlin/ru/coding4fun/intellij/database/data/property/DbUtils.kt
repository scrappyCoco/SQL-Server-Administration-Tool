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

package ru.coding4fun.intellij.database.data.property

import ru.coding4fun.intellij.database.model.common.Identity
import ru.coding4fun.intellij.database.ui.form.common.ModelModification

object DbUtils {
    const val defaultId = "-1"

    fun <Model> getNewName(mod: ModelModification<Model>): String? where Model : Identity {
        if (mod.old.id != defaultId) {
            if (mod.new?.name != null && mod.old.name != mod.new!!.name) return mod.new!!.name
            return null
        }
        return mod.new!!.name
    }

    fun getQuotedOrNull(text: String?): String = if (text.isNullOrBlank()) "null" else "'${text.replace("'", "''")}'"
    fun getIntOrNull(text: String?): String = if (text.isNullOrBlank()) "null" else text
    fun getBoolOrNull(bool: Boolean?): String = when {
        bool == null -> "null"
        bool -> "1"
        else -> "0"
    }

    private val guidRegex = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$".toRegex()
    fun isGuid(text: String) = guidRegex.matches(text)
}