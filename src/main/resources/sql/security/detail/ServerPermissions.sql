SELECT class_desc   = server_permissions.class_desc,
       major_id     = server_permissions.major_id,
       minor_id     = server_permissions.minor_id,
       grantee_name = Grantee.name,
       grantee_id   = Grantee.principal_id,
       grantor_name = Grantor.name,
       grantor_id   = Grantor.principal_id,
       type         = server_permissions.type,
       state_desc   = server_permissions.state_desc,
       id           = 'NOT USED',
       name         = 'NOT USED'
FROM sys.server_permissions
       LEFT JOIN sys.server_principals AS Grantee ON Grantee.principal_id = server_permissions.grantee_principal_id
       LEFT JOIN sys.server_principals AS Grantor ON Grantor.principal_id = server_permissions.grantor_principal_id;