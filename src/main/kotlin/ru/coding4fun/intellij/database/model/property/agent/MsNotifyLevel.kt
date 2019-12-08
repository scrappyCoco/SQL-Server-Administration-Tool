package ru.coding4fun.intellij.database.model.property.agent

enum class MsNotifyLevel(val id: Byte, val actionDescription: String, val whenDescription: String) {
	Success(1, "Quit the job reporting success", "When the job fails"),
	Failure(2, "Quit the job reporting failure", "When the job success"),
	Completes(3, "Go to the next step", "When the job completed")
}