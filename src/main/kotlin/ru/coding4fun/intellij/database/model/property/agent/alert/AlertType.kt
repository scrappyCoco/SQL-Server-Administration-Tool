package ru.coding4fun.intellij.database.model.property.agent.alert

enum class AlertType(val id: String, val title: String) {
	SqlEvent("1", "SQL Server event alert"),
	SqlPerformance("2", "SQL Server performance condition alert"),
	WmiEvent("4", "WMI event alert")
}