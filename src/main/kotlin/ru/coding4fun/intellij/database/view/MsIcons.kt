package ru.coding4fun.intellij.database.view

import com.intellij.ui.IconManager
import javax.swing.Icon

object MsIcons {
	private fun load(path: String): Icon {
		return IconManager.getInstance().getIcon(path, MsIcons::class.java)
	}

	val userEnabled = load("/icon/userEnabled.svg")
	val userDisabled = load("/icon/userDisabled.svg")
	val certificateEnabled = load("/icon/ruleEnabled.svg")
	val certificateDisabled = load("/icon/ruleDisabled.svg")
	val microsoftEnabled = load("/icon/microsoftEnabled.svg")
	val microsoftDisabled = load("/icon/microsoftDisabled.svg")
	val goldKeyEnabled = load("/icon/goldKeyEnabled.svg")
	val goldKeyDisabled = load("/icon/goldKeyDisabled.svg")
	val auditEnabled = load("/icon/auditEnabled.svg")
	val auditDisabled = load("/icon/auditDisabled.svg")
	val auditSpecificationDisabled = load("/icon/auditSpecificationDisabled.svg")
	val auditSpecificationEnabled = load("/icon/auditSpecificationEnabled.svg")
	val alertDisabled = load("/icon/alertDisabled.svg")
	val alertEnabled = load("/icon/alertEnabled.svg")

	val jobEnabledRunning = load("/icon/jobEnabledRunning.svg")
	val jobEnabledNotRunning = load("/icon/jobEnabledNotRunning.svg")
	val jobDisabledRunning = load("/icon/jobDisabledRunning.svg")
	val jobDisabledNotRunning = load("/icon/jobDisabledNotRunning.svg")
}