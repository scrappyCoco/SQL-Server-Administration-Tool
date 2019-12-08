package ru.coding4fun.intellij.database.client

import com.intellij.util.ResourceUtil

object MsResourceUtil {
	fun readQuery(resourcePath: String): String {
		val resourceUrl = ResourceUtil.getResource(MsResourceUtil::class.java, "/", resourcePath)
		return ResourceUtil.loadText(resourceUrl)
	}
}