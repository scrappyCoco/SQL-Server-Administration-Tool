package ru.coding4fun.intellij.database.ui.form

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.util.*
import javax.swing.*

class UiDependencyManager private constructor(rules: List<UiDependencyRule>) {
	private val triggerComponents: HashMap<JComponent, LinkedList<UiDependencyRule>> = hashMapOf()
	private val textFieldHandler: TextFieldHandler
	private val commonActionListener: CommonActionListener
	private val inAction: HashSet<JComponent> = HashSet()

	private inner class TextFieldHandler : KeyListener {
		override fun keyTyped(e: KeyEvent?) {}
		override fun keyPressed(e: KeyEvent?) {}

		override fun keyReleased(e: KeyEvent?) {
			val sourceComponent = e!!.source as JComponent
			updateState(sourceComponent)
		}
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
		textFieldHandler = TextFieldHandler()
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
					is JTextField -> eventSource.addKeyListener(textFieldHandler)
					is JRadioButton -> eventSource.addActionListener(commonActionListener)
					is JCheckBox -> eventSource.addActionListener(commonActionListener)
					is JComboBox<*> -> eventSource.addActionListener(commonActionListener)
				}
			}
		}
	}
}