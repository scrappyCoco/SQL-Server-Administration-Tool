--DECLARE @loginId INT = ??loginId??

SELECT id          = ClassDesc + ':' + CAST(MajorId AS VARCHAR(500)) + ':' + PermissionName,
       name        = PermissionName,
       majorId     = CAST(MajorId AS VARCHAR(10)),
       majorName   = Major.name,
       classDesc   = ClassDesc,
       securableId = ClassDesc + ':' + CAST(MajorId AS VARCHAR(500)),
       [grant]     = CAST(IIF(StateDesc = 'GRANT', 1, 0) AS BIT),
       withGrant   = CAST(IIF(StateDesc = 'GRANT_WITH_GRANT_OPTION', 1, 0) AS BIT),
       [deny]      = CAST(IIF(StateDesc = 'DENY', 1, 0) AS BIT),
       grantor     = Grantor,
       principalId = CAST(PrincipalId as VARCHAR(100))
FROM (
    -- Login to Servers.
    SELECT MajorId        = permission.major_id,
           PermissionName = permission.permission_name,
           StateDesc      = permission.state_desc,
           ClassDesc      = 'SERVER',
           Grantor        = Grantor.name,
           PrincipalId    = source_principal.principal_id
    FROM sys.server_permissions AS permission
    INNER JOIN sys.server_principals AS source_principal
               ON source_principal.principal_id = permission.grantee_principal_id
                   AND permission.class_desc = 'SERVER'
                   --AND source_principal.principal_id = @loginId
    INNER JOIN sys.server_principals AS Grantor ON Grantor.principal_id = permission.grantor_principal_id
    UNION
    -- Login to [Login, Server Role].
    SELECT MajorId        = permission.major_id,
           PermissionName = permission.permission_name,
           StateDesc      = permission.state_desc,
           ClassDesc      = IIF(target_principal.type_desc = 'SERVER_ROLE', 'SERVER ROLE', 'LOGIN'),
           Grantor        = Grantor.name,
           PrincipalId    = source_principal.principal_id
    FROM sys.server_permissions AS permission
    INNER JOIN sys.server_principals AS source_principal
               ON source_principal.principal_id = permission.grantee_principal_id
                   AND permission.class_desc = 'SERVER_PRINCIPAL'
                   --AND source_principal.principal_id = @loginId
    INNER JOIN sys.server_principals AS target_principal
               ON target_principal.principal_id = permission.major_id
    INNER JOIN sys.server_principals AS Grantor ON Grantor.principal_id = permission.grantor_principal_id
    UNION
    -- Login to Endpoint.
    SELECT MajorId        = permission.major_id,
           PermissionName = permission.permission_name,
           StateDesc      = permission.state_desc,
           ClassDesc      = 'ENDPOINT',
           Grantor        = Grantor.name,
           PrincipalId    = source_principal.principal_id
    FROM sys.server_permissions AS permission
    INNER JOIN sys.server_principals AS source_principal
               ON source_principal.principal_id = permission.grantee_principal_id
                   AND permission.class_desc = 'ENDPOINT'
                   --AND source_principal.principal_id = @loginId
    INNER JOIN sys.endpoints ON endpoints.endpoint_id = permission.major_id
    INNER JOIN sys.server_principals AS Grantor ON Grantor.principal_id = permission.grantor_principal_id
     ) AS AllPermissions
LEFT JOIN sys.server_principals AS Major ON Major.principal_id = AllPermissions.MajorId