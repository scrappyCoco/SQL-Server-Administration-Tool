/*
 * Copyright [2020] Coding4fun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.coding4fun.intellij.database.tree.folder

import ru.coding4fun.intellij.database.extension.treeLabel
import ru.coding4fun.intellij.database.model.tree.MsKind
import ru.coding4fun.intellij.database.model.tree.TreeLabel
import ru.coding4fun.intellij.database.tree.MsTreeModel
import javax.swing.tree.DefaultMutableTreeNode

class LoginFolderBuilder(folderNode: DefaultMutableTreeNode, treeModel: MsTreeModel) :
	FolderBuilder(folderNode, treeModel) {
	private val groupKinds: MutableMap<MsKind, DefaultMutableTreeNode?> = mutableMapOf()

	private val labelKindToGroupFolder = mapOf(
		MsKind.WINDOWS_GROUP to MsKind.WINDOWS_GROUP_FOLDER,
		MsKind.SQL_LOGIN to MsKind.SQL_LOGIN_FOLDER,
		MsKind.SERVER_ROLE to MsKind.SERVER_ROLE_FOLDER,
		MsKind.WINDOWS_LOGIN to MsKind.WINDOWS_LOGIN_FOLDER,
		MsKind.CERTIFICATE_MAPPED_LOGIN to MsKind.CERTIFICATE_MAPPED_LOGIN_FOLDER,
		MsKind.ASYMMETRIC_KEY_MAPPED_LOGIN to MsKind.ASYMMETRIC_KEY_MAPPED_LOGIN_FOLDER
	)

	override fun distribute(treeLabels: Collection<TreeLabel>) {
		for (treeLabel in treeLabels) {
			val groupKind = labelKindToGroupFolder[treeLabel.kind]!!
			val subFolder = groupKinds.getOrPut(groupKind) {
				val groupLabel = TreeLabel(groupKind)
				val groupNode = DefaultMutableTreeNode(groupLabel)
				folderNode.add(groupNode)
				groupNode
			}!!

			subFolder.treeLabel.children!!.add(treeLabel)
			subFolder.add(DefaultMutableTreeNode(treeLabel))
		}
	}
}