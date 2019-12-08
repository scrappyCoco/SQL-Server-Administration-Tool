package ru.coding4fun.intellij.database.action.agent

//import ru.coding4fun.intellij.database.action.common.openSqlDocument
//import ru.coding4fun.intellij.database.action.common.updateVisibility
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.coding4fun.intellij.database.action.KindPaths
import ru.coding4fun.intellij.database.action.common.ScriptActionUtil.openSqlDocument
import ru.coding4fun.intellij.database.action.common.ScriptActionUtil.updateVisibility
import ru.coding4fun.intellij.database.generation.agent.AgentCategoryUtil

class NewOperatorCategoryAction : AnAction() {
	override fun actionPerformed(e: AnActionEvent) {
		val script = AgentCategoryUtil.getAddScript(AgentCategoryUtil.Kind.Operator)
		openSqlDocument(script, e.project!!)
	}

	override fun update(e: AnActionEvent) = updateVisibility(e, KindPaths.operator)
}