package ru.coding4fun.intellij.database.generation.agent


import ru.coding4fun.intellij.database.extension.appendJbLn

object AgentCategoryUtil {
	enum class Kind {
		Job,
		Alert,
		Operator
	}

	fun getAddScript(kind: Kind): String {
		val type = if (kind == Kind.Job) "LOCAL" else "NONE"

		return StringBuilder()
			.append("EXEC msdb.dbo.sp_add_category  ").appendJbLn()
			.append(" @class = N'", kind.toString().toUpperCase(), "',").appendJbLn()
			.append(" @type = '", type, "',").appendJbLn()
			.append(" @name = N'...';").appendJbLn()
			.toString()
	}
}