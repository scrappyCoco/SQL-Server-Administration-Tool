package ru.coding4fun.intellij.database.data.property

import ru.coding4fun.intellij.database.data.property.security.ModelDataProvider
import ru.coding4fun.intellij.database.model.tool.MsFindJob
import java.util.function.Consumer

class EmptyDataProvider<Model> private constructor(emptyModel: Model) : ModelDataProvider<Model> {
	private val emptyMode: Model = emptyModel

	companion object {
		val findJob = EmptyDataProvider(MsFindJob("%", "%"))
	}

	override fun getModel(objectId: String?, consumer: Consumer<Model>) {
		consumer.accept(emptyMode)
	}

	override fun getModels(
        objectIds: Array<String>?,
        successConsumer: Consumer<Map<String, Model>>,
        errorConsumer: Consumer<Exception>
    ) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}