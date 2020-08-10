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

package ru.coding4fun.intellij.database.data.property.security.impl

import com.intellij.openapi.project.Project
import ru.coding4fun.intellij.database.client.MsClient
import ru.coding4fun.intellij.database.client.QueryDefinition
import ru.coding4fun.intellij.database.data.property.DbUtils
import ru.coding4fun.intellij.database.data.property.security.CertificateDataProvider
import ru.coding4fun.intellij.database.message.DataProviderMessages
import ru.coding4fun.intellij.database.model.common.BasicIdentity
import ru.coding4fun.intellij.database.model.property.security.MsCertificate
import ru.coding4fun.intellij.database.model.property.security.MsCertificateModel
import ru.coding4fun.intellij.database.ui.form.common.toMod
import java.util.function.Consumer

class CertificateDataProviderImpl(project: Project) : MsClient(project), CertificateDataProvider {
    override fun getModels(
        objectIds: Array<String>?,
        successConsumer: Consumer<Map<String, MsCertificateModel>>,
        errorConsumer: Consumer<Exception>
    ) {
        val models: HashMap<String, MsCertificateModel> =
            objectIds?.associateTo(HashMap(), { it to MsCertificateModel() }) ?: HashMap()
        var certificates: List<MsCertificate> = emptyList()
        var databases: List<BasicIdentity> = emptyList()

        val queries = arrayListOf(QueryDefinition(
            "sql/common/Database.sql",
            DataProviderMessages.message("security.asymmetric.key.progress.database"),
            Consumer { databases = it.getObjects() }
        ), QueryDefinition(
            "sql/action/property/security/Certificate.sql",
            DataProviderMessages.message("security.certificate.progress.main"),
            Consumer { certificates = it.getObjects() }
        ))

        if (objectIds == null) models[DbUtils.defaultId] = MsCertificateModel()

        invokeComposite(
            DataProviderMessages.message("security.certificate.progress.task"),
            queries,
            Consumer {
				val certificateMap = certificates.associateBy { it.id }
				for (modelEntry in models) {
                    val certId = modelEntry.key
                    modelEntry.value.databases = databases
					modelEntry.value.certificate = (certificateMap[certId] ?: error("Unable to find certificate with id $certId")).toMod()
				}
				successConsumer.accept(models)
			},
            errorConsumer
        )
    }
}