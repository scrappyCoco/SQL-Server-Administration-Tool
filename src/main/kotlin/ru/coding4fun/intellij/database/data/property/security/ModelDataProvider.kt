package ru.coding4fun.intellij.database.data.property.security

import java.util.function.Consumer

interface ModelDataProvider<Model> {
	fun getModel(objectId: String?, consumer: Consumer<Model>)
}