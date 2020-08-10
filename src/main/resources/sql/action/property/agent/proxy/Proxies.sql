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

SELECT id             = CAST(sysproxies.proxy_id AS VARCHAR(10)),
       name           = sysproxies.name,
       credentialId   = CAST(sysproxies.credential_id AS VARCHAR(10)),
       credentialName = credentials.name,
       description    = sysproxies.description,
       enabled        = CAST(sysproxies.enabled AS BIT)
FROM msdb.dbo.sysproxies
LEFT JOIN msdb.sys.credentials ON credentials.credential_id = sysproxies.credential_id
UNION ALL
SELECT id             = '-1',
       name           = 'My Proxy',
       credentialId   = NULL,
       credentialName = NULL,
       description    = 'My Description',
       enabled        = CAST(1 AS BIT)