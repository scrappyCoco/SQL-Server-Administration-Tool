package ru.coding4fun.intellij.database.client

import java.util.function.Consumer

open class QueryDefinition(
	val resPath: String,
	val progressDesc: String,
	val consumer: Consumer<MsRequest>,
	val queryParameters: Map<String, String>? = null
)