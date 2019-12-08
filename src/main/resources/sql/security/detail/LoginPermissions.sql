SELECT id    = grantee_principal_id,
       name  = permission_name,
       state = state
FROM sys.server_permissions;