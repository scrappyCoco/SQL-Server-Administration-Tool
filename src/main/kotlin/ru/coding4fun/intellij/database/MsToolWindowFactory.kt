package ru.coding4fun.intellij.database

import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ex.ToolWindowEx


class MsToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        toolWindow.setAvailable(true, null)
        toolWindow.isToHideOnEmptyContent = true
        toolWindow.title = "MS SQL"
        toolWindow.icon = AllIcons.General.ExternalTools
        toolWindow.helpId = "ru.coding4fun.intellij.database.help.general"

        val view = MsView.getInstance(project)
        view.setupToolWindow(toolWindow as ToolWindowEx)

        val toolWindowContentManager = toolWindow.contentManager
        val treeViewComponent = view.component

        val treeViewContent = toolWindowContentManager.factory.createContent(treeViewComponent, null, false)
		treeViewContent.disposer = view
        treeViewContent.isCloseable = false
        treeViewContent.preferredFocusableComponent = treeViewComponent
        toolWindowContentManager.addContent(treeViewContent)
        toolWindowContentManager.setSelectedContent(treeViewContent, true)
    }
}