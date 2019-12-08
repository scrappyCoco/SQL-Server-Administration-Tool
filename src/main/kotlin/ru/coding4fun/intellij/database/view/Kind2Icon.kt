package ru.coding4fun.intellij.database.view

import com.intellij.icons.AllIcons
import icons.DatabaseIcons
import ru.coding4fun.intellij.database.model.common.Enable
import ru.coding4fun.intellij.database.model.common.Kind
import ru.coding4fun.intellij.database.model.tree.MsKind
import ru.coding4fun.intellij.database.model.tree.TreeLabel
import javax.swing.Icon

object Kind2Icon {
	private val iconMap: Map<MsKind, ((obj: Kind) -> Icon)>

	init {
		iconMap = hashMapOf(
			MsKind.ROOT_FOLDER to { _ -> AllIcons.Providers.SqlServer },
			MsKind.SERVER_ROLE to { _ -> DatabaseIcons.Role },
			MsKind.DATABASE_ROLE to { _ -> DatabaseIcons.Role },
			MsKind.SQL_LOGIN to { o ->
				getIconForEnabled(o, MsIcons.userEnabled, MsIcons.userDisabled, AllIcons.General.User)
			},
			MsKind.WINDOWS_LOGIN to { o ->
				getIconForEnabled(o, MsIcons.microsoftEnabled, MsIcons.microsoftDisabled, AllIcons.Providers.Microsoft)
			},
			MsKind.CERTIFICATE_MAPPED_LOGIN to { o ->
				getIconForEnabled(o, MsIcons.certificateEnabled, MsIcons.certificateDisabled, DatabaseIcons.Rule)
			},
			MsKind.WINDOWS_GROUP to { _ -> AllIcons.Providers.Microsoft },
			MsKind.ASYMMETRIC_KEY_MAPPED_LOGIN to { _ -> DatabaseIcons.Rule },
			MsKind.CERTIFICATE to { _ -> DatabaseIcons.Rule },
			MsKind.SYMMETRIC_KEY to { _ -> DatabaseIcons.Rule },
			MsKind.ASYMMETRIC_KEY to { _ -> DatabaseIcons.Rule },
			MsKind.DATABASE_USER to { _ -> DatabaseIcons.User_mapping },
			MsKind.CRYPTOGRAPHIC_PROVIDER to { o ->
				getIconForEnabled(o, MsIcons.goldKeyEnabled, MsIcons.goldKeyDisabled, DatabaseIcons.GoldKey)
			},
			MsKind.AUDIT to { o ->
				getIconForEnabled(
					o,
					MsIcons.auditEnabled,
					MsIcons.auditDisabled,
					AllIcons.Toolwindows.ToolWindowFind
				)
			},
			MsKind.SERVER_AUDIT_SPECIFICATION to { o ->
				getIconForEnabled(
					o,
					MsIcons.auditSpecificationEnabled,
					MsIcons.auditSpecificationDisabled,
					AllIcons.General.InspectionsEye
				)
			},
			MsKind.CREDENTIAL to { _ -> AllIcons.Ide.HectorOn },
			MsKind.SERVER_PERMISSION_DENY to { _ -> AllIcons.Ide.Readonly },
			MsKind.SERVER_PERMISSION_GRANT to { _ -> AllIcons.Ide.Readwrite },
			MsKind.SERVER_PERMISSION_GRANT_WITH_GRANT to { _ -> AllIcons.Nodes.C_public },
			MsKind.AGENT_ERROR_LOG to { _ -> AllIcons.General.BalloonError },
			MsKind.ALERT to { o ->
				getIconForEnabled(
					o,
					MsIcons.alertEnabled,
					MsIcons.alertDisabled,
					AllIcons.Actions.Lightning
				)
			},
			MsKind.SCHEDULE to { o ->
				getIconForEnabled(
					o,
					AllIcons.Actions.Profile,
					AllIcons.Actions.ProfileRed,
					AllIcons.Actions.Profile
				)
			},
			MsKind.JOB to { o -> getIconForJob(o) },
			MsKind.OPERATOR to { _ -> AllIcons.General.User }
		)
	}

	fun getIcon(obj: Kind): Icon {
		val getIconFun = iconMap[obj.kind]
		return if (getIconFun != null) return getIconFun(obj) else AllIcons.Nodes.Folder
	}

	private fun getIconForEnabled(obj: Kind, enabled: Icon, disabled: Icon, another: Icon? = null): Icon {
		return when {
			(obj as? TreeLabel)?.isEnabled == true -> enabled
			(obj as? TreeLabel)?.isEnabled == false -> disabled
			(obj as? Enable)?.isEnabled == true -> enabled
			(obj as? Enable)?.isEnabled == false -> disabled
			else -> another!!
		}
	}

	private fun getIconForJob(obj: Kind): Icon {
		return when {
			obj is TreeLabel && obj.isEnabled!! && obj.isRunning!! -> MsIcons.jobEnabledRunning
			obj is TreeLabel && obj.isEnabled!! && !obj.isRunning!! -> MsIcons.jobEnabledNotRunning
			obj is TreeLabel && !obj.isEnabled!! && obj.isRunning!! -> MsIcons.jobDisabledRunning
			obj is TreeLabel && !obj.isEnabled!! && !obj.isRunning!! -> MsIcons.jobDisabledNotRunning
			//obj.kind.isFolder -> AllIcons.Nodes.Folder
			else -> AllIcons.Nodes.Folder
		}
	}
}