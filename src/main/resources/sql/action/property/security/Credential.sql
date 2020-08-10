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

WITH AllCredentials AS (
    SELECT name         = credentials.name,
           identityName = credentials.credential_identity,
           id           = CAST(credentials.credential_id AS VARCHAR(10)),
           providerId   = CAST(cryptographic_providers.provider_id AS VARCHAR(10)),
           providerName = cryptographic_providers.name
    FROM sys.credentials
    LEFT JOIN sys.cryptographic_providers ON cryptographic_providers.provider_id = credentials.target_id
                           AND credentials.target_type = 'CRYPTOGRAPHIC PROVIDER'
    UNION ALL
    SELECT name         = 'My Credential',
           identityName = 'My Identity',
           id           = '-1',
           providerId   = NULL,
           providerName = NULL
)
SELECT *
FROM AllCredentials