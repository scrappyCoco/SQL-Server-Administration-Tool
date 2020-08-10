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

WITH SourcePrincipals AS (
    SELECT principal_id
    FROM sys.server_principals
    UNION ALL
    SELECT -1
)
SELECT id          = ClassDesc + ':' + CAST(ObjectId AS VARCHAR(100)),
       name        = ObjectName,
       kind        = ObjectClass,
       classDesc   = ClassDesc,
       isExists    = CAST(IsExists AS BIT),
       majorId     = CAST(ObjectId AS VARCHAR(10)),
       grantor     = Grantor,
       principalId = CAST(PrincipalId AS VARCHAR(100))
FROM (
    -- Login to Servers.
    SELECT ObjectId    = servers.server_id,
           ObjectName  = servers.name,
           ObjectClass = 'SERVER',
           ClassDesc   = 'SERVER',
           IsExists    = IIF(permissions.major_id IS NOT NULL, 1, 0),
           Grantor     = Grantor.name,
           PrincipalId = SourcePrincipals.principal_id
    FROM SourcePrincipals
    CROSS JOIN sys.servers
    LEFT JOIN sys.server_permissions AS permissions
                                     ON permissions.grantee_principal_id = SourcePrincipals.principal_id
                                    AND permissions.major_id = servers.server_id
                                    AND permissions.class_desc = 'SERVER'
    LEFT JOIN sys.server_principals AS Grantor
                                    ON Grantor.principal_id = permissions.grantor_principal_id
    UNION ALL
    -- Login to [Login, Server Role].
    SELECT ObjectId    = TargetPrincipals.principal_id,
           ObjectName  = TargetPrincipals.name,
           ObjectClass = TargetPrincipals.type_desc,
           ClassDesc   = IIF(TargetPrincipals.type_desc = 'SERVER_ROLE', 'SERVER ROLE', 'LOGIN'),
           IsExists    = IIF(permissions.major_id IS NOT NULL, 1, 0),
           Grantor     = Grantor.name,
           PrincipalId = SourcePrincipals.principal_id
    FROM SourcePrincipals
    CROSS JOIN sys.server_principals AS TargetPrincipals
    LEFT JOIN sys.server_permissions AS permissions
                                     ON permissions.grantee_principal_id = SourcePrincipals.principal_id
                                    AND permissions.major_id = TargetPrincipals.principal_id
                                    AND permissions.class_desc = 'SERVER_PRINCIPAL'
    LEFT JOIN sys.server_principals AS Grantor
                                    ON Grantor.principal_id = permissions.grantor_principal_id
    UNION ALL
    -- Login to Endpoint.
    SELECT ObjectId    = endpoints.endpoint_id,
           ObjectName  = endpoints.name,
           ObjectClass = 'ENDPOINT',
           ClassDesc   = 'ENDPOINT',
           IsExists    = IIF(permission.major_id IS NOT NULL, 1, 0),
           Grantor     = Grantor.name,
           PrincipalId = SourcePrincipals.principal_id
    FROM SourcePrincipals
    CROSS JOIN sys.endpoints
    LEFT JOIN sys.server_permissions AS permission
                                     ON permission.grantee_principal_id = SourcePrincipals.principal_id
                                    AND permission.class_desc = 'ENDPOINT'
    LEFT JOIN sys.server_principals AS Grantor ON Grantor.principal_id = permission.grantor_principal_id
) AS AllPermissions
ORDER BY PrincipalId,
         ClassDesc,
         ObjectClass,
         ObjectName;