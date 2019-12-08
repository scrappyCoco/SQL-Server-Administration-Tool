package ru.coding4fun.intellij.database.message

import com.intellij.AbstractBundle
import org.jetbrains.annotations.PropertyKey

object DataProviderMessages : AbstractBundle("messages/data-provider") {
	fun message(@PropertyKey(resourceBundle = "messages.data-provider") key: String, vararg params: String): String {
		return getMessage(key, *params)
	}
}