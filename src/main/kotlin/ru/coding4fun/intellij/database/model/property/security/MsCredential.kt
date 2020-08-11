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

import ru.coding4fun.intellij.database.model.common.Identity

data class MsCredential(
	override var name: String,
	var identityName: String,
	override var id: String,
	var password: String? = null,
	var providerId: String? = null,
	var providerName: String? = null
) : Identity {
	override fun toString(): String = name
}