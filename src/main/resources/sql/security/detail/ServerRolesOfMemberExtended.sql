DECLARE @serverPrincipalId INT = ??serverPrincipalId??;

SELECT id         = CAST(server_principals.principal_id AS VARCHAR(10)),
       name       = server_principals.name,
       isSelected = CAST(IIF(server_role_members.member_principal_id IS NULL, 0, 1) AS BIT),
       kind       = server_principals.type_desc
FROM sys.server_principals
LEFT JOIN sys.server_role_members ON server_role_members.role_principal_id = server_principals.principal_id
        AND server_role_members.member_principal_id = @serverPrincipalId
WHERE server_principals.type = 'R';