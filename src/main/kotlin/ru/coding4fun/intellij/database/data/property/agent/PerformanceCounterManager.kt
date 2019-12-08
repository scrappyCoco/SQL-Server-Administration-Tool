package ru.coding4fun.intellij.database.data.property.agent

import ru.coding4fun.intellij.database.data.property.agent.PerformanceCounterManager.PerformanceTree.Companion.buildTree
import ru.coding4fun.intellij.database.model.property.agent.alert.PerformanceCounterRow
import java.util.*

class PerformanceCounterManager(performanceCounters: List<PerformanceCounterRow>) {
	private val root = buildTree(performanceCounters)

	fun getChildren(vararg pathParts: String?): List<String> {
		var currentTree = root
		for (pathPart in pathParts) {
			if (pathPart.isNullOrEmpty()) return emptyList()
			currentTree = currentTree[pathPart]!!
		}
		return currentTree.keys.toList()
	}

	private class PerformanceTree : TreeMap<String, PerformanceTree?>() {
		companion object {
			fun buildTree(performanceCounters: List<PerformanceCounterRow>): PerformanceTree {
				val objectTree = PerformanceTree()
				for (performanceCounter in performanceCounters) {
					val objectNode = objectTree.getOrPut(performanceCounter.objectName) { PerformanceTree() }
					val counterNode = objectNode!!.getOrPut(performanceCounter.counterName) { PerformanceTree() }
					if (performanceCounter.instanceName.isNullOrEmpty()) continue
					counterNode!![performanceCounter.instanceName] = null
				}
				return objectTree
			}
		}
	}
}