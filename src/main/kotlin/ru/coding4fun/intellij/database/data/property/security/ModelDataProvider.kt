package ru.coding4fun.intellij.database.data.property.security

import java.util.function.Consumer

interface ModelDataProvider<Model> {
	fun getModel(objectId: String?, consumer: Consumer<Model>)
	fun getModels(
		objectIds: Array<String>?,
		successConsumer: Consumer<Map<String, Model>>,
		errorConsumer: Consumer<Exception>
	)
}