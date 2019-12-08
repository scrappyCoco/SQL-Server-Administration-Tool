package ru.coding4fun.intellij.database.generation

import org.apache.commons.lang.text.StrSubstitutor
import ru.coding4fun.intellij.database.client.MsResourceUtil
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

abstract class ResourceGenerator<Model : Any>(private val sqlResource: String) : ScriptGeneratorBase<Model>() {
	protected open fun mapModelToParams(model: Model): Map<String, String> {
		val propertyMap = HashMap<String, String>(10)
		for (memberProperty in model::class.memberProperties) {
			@Suppress("UNCHECKED_CAST")
			val propertyValue = (memberProperty as KProperty1<Any, *>).get(model)?.toString() ?: ""
			propertyMap[memberProperty.name] = propertyValue
		}
		return propertyMap
	}

	override fun getCreatePart(model: Model, scriptBuilder: StringBuilder, reverse: Boolean): StringBuilder {
		val queryParameters = mapModelToParams(model)
		var sql = MsResourceUtil.readQuery(sqlResource)
		if (queryParameters.any()) {
			val stringSubstitutor = StrSubstitutor(queryParameters, "??", "??")
			sql = stringSubstitutor.replace(sql)
		}
		return scriptBuilder.append(sql)
	}

	override fun getDropPart(model: Model, scriptBuilder: StringBuilder): StringBuilder = scriptBuilder
	override fun getAlterPart(model: Model): String? = null
}