package ru.coding4fun.intellij.database.model.property.security

//@Serializable
//class MssqlUserMapping(
//	var database: String,
//	var map: Boolean,
//	@Optional var user: String? = null,
//	@Optional var defaultSchema: String? = null,
//	@Optional var roles: List<MssqlDatabaseRole>? = null) {
//	fun getCopy(): MssqlUserMapping {
//		var rolesCopy: List<MssqlDatabaseRole>? = null
//		if (roles != null) {
//			rolesCopy = ArrayList(roles!!.size)
//			for (role in roles!!) {
//				val roleCopy = role.getCopy()
//				rolesCopy.add(roleCopy)
//			}
//		}
//
//		return MssqlUserMapping(
//			database,
//			map,
//			user,
//			defaultSchema,
//			rolesCopy
//		)
//	}
//}