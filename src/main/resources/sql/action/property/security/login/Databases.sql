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

DECLARE @USER_TEMPLATE NVARCHAR(MAX) = N'UNION ALL
SELECT
  defaultSchema = database_principals.default_schema_name COLLATE DATABASE_DEFAULT,
  id            = CAST($$DB_ID$$ AS VARCHAR(10)),
  name          = N''$$DB_NAME$$'',
  [user]        = database_principals.name COLLATE DATABASE_DEFAULT,
  isSelected    = CAST(IIF(database_principals.sid IS NOT NULL, 1, 0) AS BIT),
  principalId   = CAST(server_principals.principal_id AS VARCHAR(10))
FROM sys.server_principals
LEFT JOIN [$$DB_NAME$$].sys.database_principals ON database_principals.sid = server_principals.sid
UNION ALL
SELECT defaultSchema = NULL,
       id            = CAST($$DB_ID$$ AS VARCHAR(10)),
       name          = N''$$DB_NAME$$'',
       [user]        = NULL,
       isSelected    = CAST(0 AS BIT),
       principalId   = N''-1''
';

DECLARE @searchQuery NVARCHAR(MAX) = N'';

SELECT @searchQuery += REPLACE(REPLACE(@USER_TEMPLATE,
    '$$DB_NAME$$', databases.name COLLATE DATABASE_DEFAULT),
    '$$DB_ID$$', databases.database_id)
FROM sys.databases
WHERE databases.state_desc = 'ONLINE';

SET @searchQuery = STUFF(@searchQuery, 1, LEN(N'UNION ALL'), '');

EXEC sys.sp_executesql @searchQuery;