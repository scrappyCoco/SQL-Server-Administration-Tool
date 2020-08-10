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

SELECT id           = CAST(sysoperators.id AS VARCHAR(10)),
       name         = sysoperators.name,
       isEnabled    = CAST(sysoperators.enabled AS BIT),
       eMail        = sysoperators.email_address,
       categoryId   = CAST(syscategories.category_id AS VARCHAR(10)),
       categoryName = syscategories.name
FROM msdb.dbo.sysoperators
LEFT JOIN msdb.dbo.syscategories ON syscategories.category_id = sysoperators.category_id
UNION ALL
SELECT id           = '-1',
       name         = 'My Operator',
       isEnabled    = CAST(1 AS BIT),
       eMail        = 'my@mail.com',
       categoryId   = NULL,
       categoryName = NULL