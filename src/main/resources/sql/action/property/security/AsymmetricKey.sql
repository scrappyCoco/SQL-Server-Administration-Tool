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

DECLARE @ASYM_KEY_TEMPLATE NVARCHAR(MAX) = N'UNION ALL
SELECT id        = N''$$DB_NAME$$'' + N'':^%^:'' + CAST(asymmetric_key_id AS NVARCHAR(MAX)),
       name      = name COLLATE DATABASE_DEFAULT,
       algorithm = algorithm_desc,
       db        = N''$$DB_NAME$$''
FROM [$$DB_NAME$$].sys.asymmetric_keys
';

DECLARE @asymmetricKeysSql NVARCHAR(MAX) = N'';

SELECT @asymmetricKeysSql += REPLACE(@ASYM_KEY_TEMPLATE, '$$DB_NAME$$', databases.name)
FROM sys.databases
WHERE databases.state_desc = 'ONLINE';

SET @asymmetricKeysSql = N'
SELECT id        = N''-1'',
       name      = N''My Asymmetric Key'',
       algorithm = N''RSA_4096'',
       db        = ''master''
' + @asymmetricKeysSql

EXEC sys.sp_executesql @asymmetricKeysSql;