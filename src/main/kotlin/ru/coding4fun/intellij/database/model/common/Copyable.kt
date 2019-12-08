package ru.coding4fun.intellij.database.model.common

interface Copyable<Model> {
	fun getCopy(): Model
}