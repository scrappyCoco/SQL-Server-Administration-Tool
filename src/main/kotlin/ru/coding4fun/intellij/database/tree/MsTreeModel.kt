package ru.coding4fun.intellij.database.tree

import ru.coding4fun.intellij.database.extension.getLeafNodes
import ru.coding4fun.intellij.database.extension.treeLabel
import ru.coding4fun.intellij.database.model.tree.MsKind
import ru.coding4fun.intellij.database.model.tree.TreeLabel
import ru.coding4fun.intellij.database.tree.folder.FolderBuilder
import ru.coding4fun.intellij.database.tree.folder.GroupFolderBuilder
import ru.coding4fun.intellij.database.tree.folder.LoginFolderBuilder
import ru.coding4fun.intellij.database.tree.folder.SimpleFolderBuilder
import ru.coding4fun.intellij.database.view.MsViewOptions
import javax.swing.event.TreeModelEvent
import javax.swing.event.TreeModelListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeModel
import javax.swing.tree.TreePath

class MsTreeModel(viewOptions: MsViewOptions) : TreeModel {
	private val folderBuilders: MutableMap<MsKind, () -> FolderBuilder> = mutableMapOf()
	private val listeners: MutableList<TreeModelListener> = mutableListOf()
	private val root: DefaultMutableTreeNode

	fun getFolderBuilder(kind: MsKind): FolderBuilder {
		return folderBuilders[kind]!!()
	}

	private var isGroupByLogin: Boolean = false
	private var serverPrincipalParentNode: DefaultMutableTreeNode? = null

	var groupByLogin: Boolean
		get() = isGroupByLogin
		set(isGroup) {
			isGroupByLogin = isGroup
			if (isGroup) {
				folderBuilders[MsKind.SERVER_PRINCIPAL_FOLDER] =
					{ LoginFolderBuilder(serverPrincipalParentNode!!, this) }
			} else {
				folderBuilders[MsKind.SERVER_PRINCIPAL_FOLDER] =
					{ SimpleFolderBuilder(serverPrincipalParentNode!!, this) }
			}
		}

	private var isGroupByDb: Boolean = false
	private var asymKeyNode: DefaultMutableTreeNode? = null
	private var symKeyNode: DefaultMutableTreeNode? = null
	private var certNode: DefaultMutableTreeNode? = null
	var groupByDb: Boolean
		get() = isGroupByDb
		set(isGroup) {
			isGroupByDb = isGroup
			folderBuilders[MsKind.ASYMMETRIC_KEY_FOLDER] =
				{
					if (isGroup) GroupFolderBuilder(
						asymKeyNode!!,
						this,
						MsKind.ASYMMETRIC_KEY_GROUP_FOLDER
					) else SimpleFolderBuilder(asymKeyNode!!, this)
				}
			folderBuilders[MsKind.SYMMETRIC_KEY_FOLDER] =
				{
					if (isGroup) GroupFolderBuilder(
						symKeyNode!!,
						this,
						MsKind.SYMMETRIC_KEY_GROUP_FOLDER
					) else SimpleFolderBuilder(symKeyNode!!, this)
				}
			folderBuilders[MsKind.CERTIFICATE_FOLDER] =
				{
					if (isGroup) GroupFolderBuilder(
						certNode!!,
						this,
						MsKind.CERTIFICATE_GROUP_FOLDER
					) else SimpleFolderBuilder(certNode!!, this)
				}
		}


	private var isGroupByAgent: Boolean = false
	private var jobParentNode: DefaultMutableTreeNode? = null
	private var operatorParentNode: DefaultMutableTreeNode? = null
	private var alertParentNode: DefaultMutableTreeNode? = null

	var groupByAgent: Boolean
		get() = isGroupByAgent
		set(isGroup) {
			isGroupByAgent = isGroup
			folderBuilders[MsKind.JOB_FOLDER] =
				{
					if (isGroup) GroupFolderBuilder(
						jobParentNode!!,
						this,
						MsKind.JOB_GROUP_FOLDER
					) else SimpleFolderBuilder(jobParentNode!!, this)
				}
			folderBuilders[MsKind.OPERATOR_FOLDER] =
				{
					if (isGroup) GroupFolderBuilder(
						operatorParentNode!!,
						this,
						MsKind.OPERATOR_GROUP_FOLDER
					) else SimpleFolderBuilder(operatorParentNode!!, this)
				}
			folderBuilders[MsKind.ALERT_FOLDER] =
				{
					if (isGroup) GroupFolderBuilder(
						alertParentNode!!,
						this,
						MsKind.ALERT_GROUP_FOLDER
					) else SimpleFolderBuilder(alertParentNode!!, this)
				}
		}

	fun toggleGroupForDb() {
		val dbFolders = listOf(
			Pair(asymKeyNode!!, MsKind.ASYMMETRIC_KEY_FOLDER),
			Pair(symKeyNode!!, MsKind.SYMMETRIC_KEY_FOLDER),
			Pair(certNode!!, MsKind.CERTIFICATE_FOLDER)
		)
		toggle(dbFolders)
	}

	fun toggleGroupForAgent() {
		val agentFolders = listOf(
			Pair(jobParentNode!!, MsKind.JOB_FOLDER),
			Pair(operatorParentNode!!, MsKind.OPERATOR_FOLDER),
			Pair(alertParentNode!!, MsKind.ALERT_FOLDER)
		)
		toggle(agentFolders)
	}

	private fun toggle(typeFolders: List<Pair<DefaultMutableTreeNode, MsKind>>) {
		for (typeFolder in typeFolders) {
			val parentNode = typeFolder.first
			val leafNodes = this.getLeafNodes(parentNode).map { it.treeLabel }.toList()
			val kind = typeFolder.second
			val folderBuilderFabric = folderBuilders[kind]
			val folderBuilder = folderBuilderFabric!!.invoke()
			folderBuilder.distribute(leafNodes)
			folderBuilder.reDraw()
		}
	}

	fun toggleGroupForServerPrincipal() {
		val leafNodes = this.getLeafNodes(serverPrincipalParentNode!!).map { it.treeLabel }.toList()
		val folderBuilderFabric = folderBuilders[MsKind.SERVER_PRINCIPAL_FOLDER]
		val folderBuilder = folderBuilderFabric!!.invoke()
		folderBuilder.distribute(leafNodes)
		folderBuilder.reDraw()
	}

	init {
		val rootBuilder = Builder(MsKind.ROOT_FOLDER).also { root ->
			root + Builder(MsKind.SECURITY_FOLDER).also { security ->
				security + Builder(MsKind.SERVER_PRINCIPAL_FOLDER).also { serverPrincipal ->
					serverPrincipalParentNode = serverPrincipal.treeNode
				}
				security + Builder(MsKind.CREDENTIAL_FOLDER)
				security + Builder(MsKind.CRYPTOGRAPHIC_PROVIDER_FOLDER)
				security + Builder(MsKind.AUDIT_FOLDER)
				security + Builder(MsKind.SERVER_AUDIT_SPECIFICATION_FOLDER)
				security + Builder(MsKind.CERTIFICATE_FOLDER).also { cert -> certNode = cert.treeNode }
				security + Builder(MsKind.SYMMETRIC_KEY_FOLDER).also { key -> symKeyNode = key.treeNode }
				security + Builder(MsKind.ASYMMETRIC_KEY_FOLDER).also { key -> asymKeyNode = key.treeNode }
			}
			root + Builder(MsKind.AGENT_FOLDER).also { agent ->
				agent + Builder(MsKind.JOB_FOLDER).also { job -> jobParentNode = job.treeNode }
				agent + Builder(MsKind.SCHEDULE_FOLDER)
				agent + Builder(MsKind.ALERT_FOLDER).also { alert -> alertParentNode = alert.treeNode }
				agent + Builder(MsKind.OPERATOR_FOLDER).also { operator -> operatorParentNode = operator.treeNode }
				agent + Builder(MsKind.AGENT_ERROR_LOG_FOLDER)
			}
		}

		groupByLogin = viewOptions.groupByLogin
		groupByAgent = viewOptions.groupByAgent
		groupByDb = viewOptions.groupByDb

		root = rootBuilder.treeNode
	}

	override fun getRoot(): Any {
		return root
	}

	override fun isLeaf(node: Any?): Boolean {
		return (node as DefaultMutableTreeNode).childCount == 0
	}

	override fun getChildCount(parent: Any?): Int {
		return (parent as DefaultMutableTreeNode).childCount
	}

	override fun removeTreeModelListener(listener: TreeModelListener?) {
		listeners.remove(listener)
	}

	override fun valueForPathChanged(path: TreePath?, newValue: Any?) {
		if (path == null) return
		(path.lastPathComponent as DefaultMutableTreeNode).userObject = newValue
	}

	override fun getIndexOfChild(parent: Any?, child: Any?): Int {
		return (parent as DefaultMutableTreeNode).children().toList().indexOf(child)
	}

	override fun getChild(parent: Any?, index: Int): Any {
		return (parent as DefaultMutableTreeNode).children().toList()[index]
	}

	override fun addTreeModelListener(listener: TreeModelListener?) {
		listeners.add(listener ?: return)
	}

	fun reload(node: DefaultMutableTreeNode) {
		if (!listeners.any()) return

		for (listener in listeners) {
			val modelEvent = TreeModelEvent(this, node.path)
			listener.treeStructureChanged(modelEvent)
		}
	}

	private inner class Builder(
		kind: MsKind
	) {
		val treeNode: DefaultMutableTreeNode

		init {
			val treeLabel = TreeLabel(kind)
			treeNode = DefaultMutableTreeNode(treeLabel)
			folderBuilders[kind] = { SimpleFolderBuilder(treeNode, this@MsTreeModel) }
		}

		operator fun plus(child: Builder) {
			this.treeNode.add(child.treeNode)
		}
	}
}