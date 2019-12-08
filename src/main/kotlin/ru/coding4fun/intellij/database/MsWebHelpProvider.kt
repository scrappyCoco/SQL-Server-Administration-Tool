package ru.coding4fun.intellij.database

import com.intellij.openapi.help.WebHelpProvider

class MsWebHelpProvider : WebHelpProvider() {
	override fun getHelpPageUrl(helpTopicId: String): String? {
		if ("ru.coding4fun.intellij.database.help.general" == helpTopicId) {
			return "https://github.com/scrappyCoco/SQL-Server-Administration-Tool/wiki/Connection";
		}
		return null;
	}
}