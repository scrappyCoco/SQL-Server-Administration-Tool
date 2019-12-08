package ru.coding4fun.intellij.database.model.property.agent.alert

import ru.coding4fun.intellij.database.model.common.BasicIdentity

object Severity {
	val Types = arrayListOf(
		BasicIdentity("1", "001 - Miscellaneous System Information"),
		BasicIdentity("2", "002 - Reserved"),
		BasicIdentity("3", "003 - Reserved"),
		BasicIdentity("4", "004 - Reserved"),
		BasicIdentity("5", "005 - Reserved"),
		BasicIdentity("6", "006 - Reserved"),
		BasicIdentity("7", "007 - Notification: Status Information"),
		BasicIdentity("8", "008 - Notification: User Intervention Required"),
		BasicIdentity("9", "009 - User Defined"),
		BasicIdentity("10", "010 - Information"),
		BasicIdentity("11", "011 - Specified Database Object Not Found"),
		BasicIdentity("12", "012 - Unused"),
		BasicIdentity("13", "013 - User Transaction Syntax Error"),
		BasicIdentity("14", "014 - Insufficient Permission"),
		BasicIdentity("15", "015 - Syntax Error in SQL Statements"),
		BasicIdentity("16", "016 - Miscellaneous User Error"),
		BasicIdentity("17", "017 - Insufficient Resource"),
		BasicIdentity("18", "018 - Nonfatal Internal Error"),
		BasicIdentity("19", "019 - Fatal Error in Resource"),
		BasicIdentity("20", "020 - Fatal Error in Current Process"),
		BasicIdentity("21", "021 - Fatal Error in Database Processes"),
		BasicIdentity("22", "022 - Fatal Error: Table Integrity Suspect"),
		BasicIdentity("23", "023 - Fatal Error: Database Integrity Suspect"),
		BasicIdentity("24", "024 - Fatal Error: Hardware Error"),
		BasicIdentity("25", "025 - Fatal Error")
	)
}