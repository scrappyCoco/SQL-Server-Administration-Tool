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

SELECT *
FROM (
         SELECT id          = CAST(ChildRoles.principal_id AS VARCHAR(10)),
                name        = ChildRoles.name,
                isSelected  = CAST(IIF(server_role_members.member_principal_id IS NOT NULL, 1, 0) AS BIT),
                kind        = 'SERVER_ROLE',
                principalId = CAST(ParentRoles.principal_id AS VARCHAR(10))
         FROM master.sys.server_principals AS ChildRoles
                  CROSS JOIN master.sys.server_principals AS ParentRoles
                  LEFT JOIN master.sys.server_role_members
                            ON server_role_members.member_principal_id = ChildRoles.principal_id
                                AND server_role_members.role_principal_id = ParentRoles.principal_id
         WHERE ChildRoles.type_desc IN ('SERVER_ROLE', 'SQL_LOGIN', 'WINDOWS_LOGIN')
           AND ParentRoles.type_desc = 'SERVER_ROLE'
           AND ChildRoles.principal_id <> ParentRoles.principal_id
         UNION ALL
         SELECT id          = CAST(principal_id AS VARCHAR(10)),
                name        = name,
                isSelected  = CAST(0 AS BIT),
                kind        = 'SERVER_ROLE',
                principalId = '-1'
         FROM master.sys.server_principals
     ) AS Memberships
ORDER BY principalId, name;