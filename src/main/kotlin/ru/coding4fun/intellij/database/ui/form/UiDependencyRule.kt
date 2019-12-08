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
	private var myTargetComponents: List<JComponent> = emptyList()

	init {
		myTargetComponents = arrayListOf(*targetComponents)
	}

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