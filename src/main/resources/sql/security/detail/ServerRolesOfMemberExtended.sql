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

SELECT id         = CAST(Roles.principal_id AS VARCHAR(10)),
       name       = Roles.name,
       isSelected = CAST(IIF(server_role_members.member_principal_id IS NULL, 0, 1) AS BIT),
       kind       = Roles.type_desc,
       principalId = CAST(Users.principal_id AS VARCHAR(10))
FROM sys.server_principals AS Roles
         CROSS APPLY sys.server_principals AS Users
         LEFT JOIN sys.server_role_members ON server_role_members.role_principal_id = Roles.principal_id
    AND server_role_members.member_principal_id = Users.principal_id
WHERE Roles.type = 'R'
  AND Users.type <> 'R'
UNION ALL
SELECT id         = CAST(Roles.principal_id AS VARCHAR(10)),
       name       = Roles.name,
       isSelected = CAST(0 AS BIT),
       kind       = Roles.type_desc,
       principalId = '-1'
FROM sys.server_principals AS Roles
WHERE Roles.type = 'R'