package ru.coding4fun.intellij.database.extension

import java.awt.Component
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

fun Component.onMouseClicked(handleMouseClick: ((MouseEvent?) -> Unit)) {
	this.addMouseListener(object : MouseAdapter() {
		override fun mouseClicked(e: MouseEvent?) {
			handleMouseClick(e)
		}
	})
}

fun Component.onKeyReleased(handleKeyReleased: (KeyEvent?) -> Unit) {
	this.addKeyListener(object : KeyAdapter() {
	override fun keyReleased(e: KeyEvent?){	handleKeyReleased(e) }})
}