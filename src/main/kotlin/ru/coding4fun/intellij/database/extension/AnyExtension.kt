package ru.coding4fun.intellij.database.extension

fun <T> T?.alsoIfNotNull(block: (T) -> Unit): T? {
	if (this != null) block(this)
	return this
}