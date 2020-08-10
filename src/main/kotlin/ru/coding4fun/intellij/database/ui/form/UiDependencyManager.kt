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

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.*
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class UiDependencyManager private constructor(rules: List<UiDependencyRule>) {
	private val triggerComponents: HashMap<JComponent, LinkedList<UiDependencyRule>> = hashMapOf()
	private val commonActionListener: CommonActionListener
	private val inAction: HashSet<JComponent> = HashSet()

	private inner class TextFieldHandler(private val textField: JTextField) : DocumentListener {
		override fun changedUpdate(e: DocumentEvent?) = updateState(textField)
		override fun insertUpdate(p0: DocumentEvent?) = updateState(textField)
		override fun removeUpdate(p0: DocumentEvent?) = updateState(textField)
	}

	private inner class CommonActionListener : ActionListener {
		override fun actionPerformed(e: ActionEvent?) {
			val sourceComponent = e!!.source as JComponent
			updateState(sourceComponent)
		}

	}

	private fun updateState(eventSource: JComponent) {
		// To avoid stack overflow and infinite.
		if (inAction.contains(eventSource)) return
		inAction.add(eventSource)

		val linkedRules = triggerComponents[eventSource] ?: return
		for (linkedRule in linkedRules) {
			linkedRule.apply()
		}

		inAction.remove(eventSource)
	}

	companion object {
		fun register(vararg rules: UiDependencyRule): List<UiDependencyRule> {
			val ruleList = rules.toList()
			UiDependencyManager(ruleList)
			return ruleList
		}
	}

	init {
		commonActionListener = CommonActionListener()

		for (rule in rules) {
			for (eventSource in rule.dependencies) {
				var rulesOfEventSource: LinkedList<UiDependencyRule>? = triggerComponents[eventSource]

				if (rulesOfEventSource == null) {
					rulesOfEventSource = LinkedList()
					triggerComponents[eventSource] = rulesOfEventSource
				}

				rulesOfEventSource.add(rule)

				when (eventSource) {
					is JTextField -> eventSource.document.addDocumentListener(TextFieldHandler(eventSource))
					is JRadioButton -> eventSource.addActionListener(commonActionListener)
					is JCheckBox -> eventSource.addActionListener(commonActionListener)
					is JComboBox<*> -> eventSource.addActionListener(commonActionListener)
				}
			}
		}
	}
}