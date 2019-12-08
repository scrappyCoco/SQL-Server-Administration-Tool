DECLARE @loginId INT = ??loginId??

SELECT id        = ClassDesc + ':' + CAST(ObjectId AS VARCHAR(100)),
       name      = ObjectName,
       kind      = ObjectClass,
       classDesc = ClassDesc,
       isExists  = CAST(IsExists AS BIT),
       majorId   = CAST(ObjectId AS VARCHAR(10)),
       grantor   = Grantor
FROM (
    -- Login to Servers.
    SELECT ObjectId    = servers.server_id,
           ObjectName  = servers.name,
           ObjectClass = 'SERVER',
           ClassDesc   = 'SERVER',
           IsExists    = IIF(permission.major_id IS NOT NULL, 1, 0),
           Grantor     = Grantor.name
    FROM sys.server_permissions AS permission
    INNER JOIN sys.server_principals AS source_principal
               ON source_principal.principal_id = permission.grantee_principal_id
                   AND permission.class_desc = 'SERVER'
                   AND source_principal.principal_id = @loginId
    INNER JOIN sys.server_principals AS Grantor ON Grantor.principal_id = permission.grantor_principal_id
    FULL JOIN sys.servers ON servers.server_id = permission.major_id
    UNION
    -- Login to [Login, Server Role].
    SELECT ObjectId    = target_principal.principal_id,
           ObjectName  = target_principal.name,
           ObjectClass = target_principal.type_desc,
           ClassDesc   = IIF(target_principal.type_desc = 'SERVER_ROLE', 'SERVER ROLE', 'LOGIN'),
           IsExists    = IIF(permission.major_id IS NOT NULL, 1, 0),
           Grantor     = Grantor.name
    FROM sys.server_permissions AS permission
    INNER JOIN sys.server_principals AS source_principal
               ON source_principal.principal_id = permission.grantee_principal_id
                   AND permission.class_desc = 'SERVER_PRINCIPAL'
                   AND source_principal.principal_id = @loginId
    INNER JOIN sys.server_principals AS Grantor ON Grantor.principal_id = permission.grantor_principal_id
    FULL JOIN sys.server_principals AS target_principal ON target_principal.principal_id = permission.major_id
    UNION
    -- Login to Endpoint.
    SELECT ObjectId    = endpoints.endpoint_id,
           ObjectName  = endpoints.name,
           ObjectClass = 'ENDPOINT',
           ClassDesc   = 'ENDPOINT',
           IsExists    = IIF(permission.major_id IS NOT NULL, 1, 0),
           Grantor     = Grantor.name
    FROM sys.server_permissions AS permission
    INNER JOIN sys.server_principals AS source_principal
               ON source_principal.principal_id = permission.grantee_principal_id
                   AND permission.class_desc = 'ENDPOINT'
                   AND source_principal.principal_id = @loginId
    INNER JOIN sys.server_principals AS Grantor ON Grantor.principal_id = permission.grantor_principal_id
    FULL JOIN sys.endpoints ON endpoints.endpoint_id = permission.major_id
     ) AS AllPermissions
ORDER BY ClassDesc,
         ObjectClass,
         ObjectName;