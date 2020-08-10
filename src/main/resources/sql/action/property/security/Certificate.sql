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

DECLARE @CERTIFICATE_TEMPLATE NVARCHAR(MAX) = N'UNION ALL
SELECT beginDialog = is_active_for_begin_dialog,
       startDate   = CAST(start_date AS VARCHAR(100)),
       expiryDate  = CAST(expiry_date AS VARCHAR(100)),
       subject     = subject COLLATE DATABASE_DEFAULT,
       name        = name COLLATE DATABASE_DEFAULT,
       id          = N''$$DB_NAME$$'' + N'':^%^:'' + CAST(certificate_id AS NVARCHAR(MAX)),
       db          = N''$$DB_NAME$$''
FROM [$$DB_NAME$$].sys.certificates
';

DECLARE @certificateSql NVARCHAR(MAX) = N'';

SELECT @certificateSql += REPLACE(@CERTIFICATE_TEMPLATE, '$$DB_NAME$$', databases.name)
FROM sys.databases
WHERE databases.state_desc = 'ONLINE';

SET @certificateSql = N'
SELECT  beginDialog = CAST(1 AS BIT),
        startDate   = CAST(GETDATE() AS VARCHAR(100)),
        expiryDate  = CAST(DATEADD(YEAR, 1, GETDATE()) AS VARCHAR(100)),
        subject     = N''My Subject'',
        name        = N''My Certificate'',
        id          = N''-1'',
        db          = N''master''
' + @certificateSql

EXEC sys.sp_executesql @certificateSql;