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

package ru.coding4fun.intellij.database.ui.form

import com.intellij.util.containers.ContainerUtil
import com.intellij.util.containers.ImmutableList
import javax.swing.JComponent

class UiDependencyRule(
	vararg targetComponents: JComponent
) {
	private val myMustRules: MutableList<() -> Boolean> = arrayListOf()
	private val myDepends: MutableList<JComponent> = arrayListOf()
	private var myStateChanger: (targetComponent: JComponent, state: Boolean) -> Unit = { _, _ -> }
	private var myTargetComponents: List<JComponent> = arrayListOf(*targetComponents)

	val dependencies: ImmutableList<JComponent>
		get() = ContainerUtil.immutableList(myDepends)

	@SafeVarargs
	fun must(vararg mustRules: () -> Boolean): UiDependencyRule {
		mustRules.forEach { myMustRules.add(it) }
		return this
	}

	fun dependOn(vararg components: JComponent): UiDependencyRule {
		components.forEach { myDepends.add(it) }
		return this
	}

	private val isMustMatched: Boolean
		get() {
			var isMatch = false
			for (mustRule in myMustRules) {
				isMatch = mustRule.invoke()
				if (!isMatch) {
					break
				}
			}

			return isMatch
		}

	fun apply() {
		for (targetComponent in myTargetComponents) {
			myStateChanger.invoke(targetComponent, isMustMatched)
		}
	}

	fun setStateChanger(stateChanger: (targetComponent: JComponent, state: Boolean) -> Unit): UiDependencyRule {
		myStateChanger = stateChanger
		return this
	}
}