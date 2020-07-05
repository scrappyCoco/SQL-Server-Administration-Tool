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

package ru.coding4fun.intellij.database.data.property

import ru.coding4fun.intellij.database.data.property.security.ModelDataProvider
import ru.coding4fun.intellij.database.model.tool.MsFindJob
import java.util.function.Consumer

class EmptyDataProvider<Model> private constructor(emptyModel: Model) : ModelDataProvider<Model> {
    private val emptyMode: Model = emptyModel

    companion object {
        val findJob = EmptyDataProvider(MsFindJob("%", "%"))
    }

//	override fun getModel(objectId: String?, consumer: Consumer<Model>) {
//		consumer.accept(emptyMode)
//	}

    override fun getModels(
        objectIds: Array<String>?,
        successConsumer: Consumer<Map<String, Model>>,
        errorConsumer: Consumer<Exception>
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}