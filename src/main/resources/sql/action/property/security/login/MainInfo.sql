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

SELECT id                  = CAST(p.principal_id AS VARCHAR(10)),
       name                = p.name,
       principalKind       = p.type_desc,
       defaultDatabase     = p.default_database_name,
       defaultLanguage     = sql_logins.default_language_name,
       loginPasswordHashed = CONVERT(VARCHAR(512), LOGINPROPERTY(p.name, 'PasswordHash'), 1),
       sid                 = CONVERT(VARCHAR(512), p.sid, 1),
       isPolicyChecked     = CAST(ISNULL(sql_logins.is_policy_checked, 0) AS BIT),
       isExpirationChecked = CAST(ISNULL(sql_logins.is_expiration_checked, 0) AS BIT),
       denyLogin           = CAST(ISNULL(l.denylogin, 0) AS BIT),
       isDisabled          = CAST(ISNULL(p.is_disabled, 0) AS BIT),
       mustChange          = CAST(0 AS BIT),
       credential          = credentials.name
FROM sys.server_principals p
LEFT JOIN sys.syslogins l ON l.name = p.name
LEFT JOIN sys.sql_logins ON sql_logins.name = l.name
LEFT JOIN sys.server_principal_credentials ON server_principal_credentials.principal_id = sql_logins.principal_id
LEFT JOIN sys.credentials ON credentials.credential_id = server_principal_credentials.credential_id
UNION ALL
SELECT id                  = '-1',
       name                = 'My Login',
       principalKind       = 'SQL_LOGIN',
       defaultDatabase     = 'master',
       defaultLanguage     = 'us_english',
       loginPasswordHashed = NULL,
       sid                 = NULL,
       isPolicyChecked     = CAST(0 AS BIT),
       isExpirationChecked = CAST(0 AS BIT),
       denyLogin           = CAST(0 AS BIT),
       isDisabled          = CAST(0 AS BIT),
       mustChange          = CAST(0 AS BIT),
       credential          = NULL