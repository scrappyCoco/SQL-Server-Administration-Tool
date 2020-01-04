package ru.coding4fun.intellij.database.tree.impl

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.MsView
import ru.coding4fun.intellij.database.client.MsClient
import ru.coding4fun.intellij.database.client.MsConnectionManager
import ru.coding4fun.intellij.database.client.QueryDefinition
import ru.coding4fun.intellij.database.extension.treeLabel
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.tree.MsKind
import ru.coding4fun.intellij.database.model.tree.isRoot
import ru.coding4fun.intellij.database.tree.MsTreeManager
import ru.coding4fun.intellij.database.tree.MsTreeModel
import ru.coding4fun.intellij.database.tree.folder.FolderBuilder
import java.util.*
import java.util.function.Consumer
import javax.swing.tree.DefaultMutableTreeNode

class MsTreeManagerImpl(project: Project) : MsTreeManager, MsClient(project) {
	private class TreeDirectoryDescription(
		resPath: String,
		progressDesc: String,
		folderBuilder: FolderBuilder
	) : QueryDefinition(resPath, progressDesc, Consumer {
		folderBuilder.distribute(it.getObjects())
		folderBuilder.reDraw()
	}, null)

	private val descriptions = hashMapOf(
		//region Agent
		MsKind.JOB_FOLDER to Pair(
			"sql/tree/agent/Job.sql",
			DataProviderMessages.message("tree.agent.job")
		),
		MsKind.SCHEDULE_FOLDER to Pair(
			"sql/tree/agent/Schedule.sql",
			DataProviderMessages.message("tree.agent.schedule")
		),
		MsKind.ALERT_FOLDER to Pair(
			"sql/tree/agent/Alert.sql",
			DataProviderMessages.message("tree.agent.alert")
		),
		MsKind.OPERATOR_FOLDER to Pair(
			"sql/tree/agent/Operator.sql",
			DataProviderMessages.message("tree.agent.operator")
		),
		MsKind.AGENT_ERROR_LOG_FOLDER to Pair(
			"sql/tree/agent/ErrorLog.sql",
			DataProviderMessages.message("tree.agent.log")
		),
		//endregion
		//region Security
		MsKind.SERVER_PRINCIPAL_FOLDER to Pair(
			"sql/tree/security/ServerPrincipal.sql",
			DataProviderMessages.message("tree.security.login")
		),
		MsKind.CREDENTIAL_FOLDER to Pair(
			"sql/tree/security/Credential.sql",
			DataProviderMessages.message("tree.security.credential")
		),
		MsKind.CRYPTOGRAPHIC_PROVIDER_FOLDER to Pair(
			"sql/tree/security/CryptographicProvider.sql",
			DataProviderMessages.message("tree.security.crypto")
		),
		MsKind.AUDIT_FOLDER to Pair(
			"sql/tree/security/Audit.sql",
			DataProviderMessages.message("tree.security.server.audit")
		),
		MsKind.SERVER_AUDIT_SPECIFICATION_FOLDER to Pair(
			"sql/tree/security/AuditSpecification.sql",
			DataProviderMessages.message("tree.security.server.audit.specification")
		),
		MsKind.CERTIFICATE_FOLDER to Pair(
			"sql/tree/security/Certificate.sql",
			DataProviderMessages.message("tree.security.certificate")
		),
		MsKind.SYMMETRIC_KEY_FOLDER to Pair(
			"sql/tree/security/SymmetricKey.sql",
			DataProviderMessages.message("tree.security.symmetric.key")
		),
		MsKind.ASYMMETRIC_KEY_FOLDER to Pair(
			"sql/tree/security/AsymmetricKey.sql",
			DataProviderMessages.message("tree.security.asymmetric.key")
		)
		//endregion
	)

	override fun refreshSingleFolder(
		selectedNode: DefaultMutableTreeNode,
		treeModel: MsTreeModel
	) {
		var directoryNode: DefaultMutableTreeNode? = selectedNode
		var dirDesc: Pair<String, String>? = null

		while (directoryNode != null) {
			dirDesc = descriptions[directoryNode.treeLabel.kind]
			if (dirDesc != null || directoryNode.treeLabel.isRoot) break
			directoryNode = directoryNode.parent as? DefaultMutableTreeNode
		}

		if (dirDesc != null) {
			val folderBuilder = treeModel.getFolderBuilder(directoryNode!!.treeLabel.kind)
			val query = TreeDirectoryDescription(dirDesc.first, dirDesc.second, folderBuilder)
			invokeComposite("Tree Manager", listOf(query), Consumer {})
		} else {
			refreshAll(treeModel)
		}
	}

	override fun refreshAll(treeModel: MsTreeModel) {
		MsView.getInstance(project).setVisible()
		(treeModel.root as DefaultMutableTreeNode).treeLabel.name = MsConnectionManager.dbDataSource!!.name
		val queries = ArrayList<QueryDefinition>()
		for (descriptionEntry in descriptions) {
			val folderBuilder = treeModel.getFolderBuilder(descriptionEntry.key)
			queries.add(
				TreeDirectoryDescription(
					descriptionEntry.value.first,
					descriptionEntry.value.second,
					folderBuilder
				)
			)
		}
		invokeComposite("Tree Manager", queries, Consumer {})
	}
}