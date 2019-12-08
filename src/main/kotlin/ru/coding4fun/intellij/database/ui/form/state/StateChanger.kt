package ru.coding4fun.intellij.database.ui.form.state

import javax.swing.JComponent

object StateChanger {
	@JvmStatic
	fun enable(targetComponent: JComponent, state: Boolean): Unit? {
		targetComponent.isEnabled = state
		return null
	}

	@JvmStatic
	fun disable(targetComponent: JComponent, state: Boolean): Unit? {
		targetComponent.isEnabled = !state
		return null
	}

	@JvmStatic
	fun enableRecurse(targetComponent: JComponent, state: Boolean): Unit? {
		traverse(targetComponent) {
			it.isEnabled = state
		}
		return null
	}

	@JvmStatic
	fun visibleRecurse(targetComponent: JComponent, state: Boolean): Unit? {
		traverse(targetComponent) {
			it.isVisible = state
		}
		return null
	}

	@JvmStatic
	private fun traverse(targetComponent: JComponent, action: ((component: JComponent) -> Unit)): Unit? {
		action(targetComponent)
		for (child in targetComponent.components) {
			val jChild = child as? JComponent ?: continue
			traverse(jChild, action)
		}

		return null
	}
}