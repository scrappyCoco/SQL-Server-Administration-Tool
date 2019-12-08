package ru.coding4fun.intellij.database

import com.intellij.database.actions.DatabaseViewActions
import com.intellij.ide.CommonActionsManager
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.util.treeView.NodeRenderer
import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.wm.ex.ToolWindowEx
import com.intellij.ui.PopupHandler
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.TreeSpeedSearch
import com.intellij.util.xmlb.XmlSerializer
import org.jdom.Element
import ru.coding4fun.intellij.database.action.connection.ConnectionFromDbAction
import ru.coding4fun.intellij.database.model.tree.TreeLabel
import ru.coding4fun.intellij.database.tree.MsTreeModel
import ru.coding4fun.intellij.database.ui.MsTree
import ru.coding4fun.intellij.database.view.MsViewOptions
import ru.coding4fun.intellij.database.view.TreeNodeDescriptor
import java.awt.Component
import javax.swing.JTree
import javax.swing.tree.TreeCellRenderer

class MsView(project: Project) : SimpleToolWindowPanel(true, true), PersistentStateComponent<Element>, Disposable {
    override fun getState(): Element? {
        return XmlSerializer.serialize(viewOptions, null)
    }

    override fun loadState(state: Element) {
        XmlSerializer.deserializeInto(viewOptions, state)
    }

    private val tree: MsTree
    private val viewOptions: MsViewOptions


    val treeModel: MsTreeModel

    init {
        val cellRenderer = MyRenderer()
        viewOptions = MsViewOptions()
        treeModel = MsTreeModel(viewOptions)

        tree = MsTree(project).also {
            it.cellRenderer = cellRenderer
            it.model = treeModel
            it.isVisible = false
        }

        setContent(ScrollPaneFactory.createScrollPane(tree))
        TreeSpeedSearch(tree)

        PopupHandler.installPopupHandler(tree, "MssqlPopupMenuGroup", "Mssql tree")
    }

    fun setVisible() {
        tree.isVisible = true
    }

    companion object {
        private lateinit var INSTANCE: MsView
        private var isInitialized: Boolean = false

        @JvmStatic
        fun getInstance(project: Project): MsView {
            if (!isInitialized) {
                isInitialized = true
                INSTANCE = MsView(project)
            }

            return INSTANCE
        }
    }

    fun setupToolWindow(toolWindow: ToolWindowEx) {
        val group = DefaultActionGroup()

        group.addAction(
            DatabaseViewActions.toggle(
                "Group by Agent Category",
                { viewOptions.groupByAgent },
                { value ->
                    viewOptions.groupByAgent = value
                    treeModel.groupByAgent = value
                },
                { treeModel.toggleGroupForAgent() }
            )
        )

        group.addAction(
            DatabaseViewActions.toggle(
                "Group by Login Type",
                { viewOptions.groupByLogin },
                { value ->
                    viewOptions.groupByLogin = value
                    treeModel.groupByLogin = value
                },
                { treeModel.toggleGroupForServerPrincipal() }
            )
        )

        group.addAction(
            DatabaseViewActions.toggle(
                "Group by DB",
                { viewOptions.groupByDb },
                { value ->
                    viewOptions.groupByDb = value
                    treeModel.groupByDb = value
                },
                { treeModel.toggleGroupForDb() }
            )
        )

        toolWindow.setAdditionalGearActions(group)

        val actionsManager = CommonActionsManager.getInstance()
        val collapseAllAction = actionsManager.createCollapseAllHeaderAction(tree)
        toolWindow.setTitleActions(
            collapseAllAction,
            ConnectionFromDbAction()
        )
    }

    private inner class MyRenderer internal constructor() : TreeCellRenderer {
        internal var presentation: PresentationData = PresentationData()
        internal var nodeRenderer: NodeRenderer

        init {
            this.nodeRenderer = object : NodeRenderer() {
                override fun getPresentation(node: Any?): ItemPresentation? {
                    return this@MyRenderer.getPresentation(node)
                }
            }
        }

        internal fun getPresentation(node: Any?): ItemPresentation? {
            return ReadAction.compute<ItemPresentation, RuntimeException> {
                if (node is TreeLabel) {
                    TreeNodeDescriptor.updatePresentationForLabel(this@MyRenderer.presentation, node, viewOptions)
                    this@MyRenderer.presentation
                } else {
                    if (node is NavigationItem) node.presentation else null
                }
            }
        }

        override fun getTreeCellRendererComponent(
            tree: JTree,
            value: Any?,
            selected: Boolean,
            expanded: Boolean,
            leaf: Boolean,
            row: Int,
            hasFocus: Boolean
        ): Component {
            return nodeRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus)
        }
    }

    override fun dispose() {}
}