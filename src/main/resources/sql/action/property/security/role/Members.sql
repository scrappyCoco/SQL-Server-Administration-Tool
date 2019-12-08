DECLARE @roleId INT = ??roleId??;

SELECT id         = CAST(server_principals.principal_id AS VARCHAR(10)),
       name       = server_principals.name,
       isSelected = CAST(IIF(server_role_members.member_principal_id IS NOT NULL, 1, 0) AS BIT),
       kind       = server_principals.type_desc
FROM master.sys.server_principals
LEFT JOIN master.sys.server_role_members ON server_principals.principal_id = server_role_members.member_principal_id
        AND server_role_members.role_principal_id = @roleId
WHERE server_principals.type_desc IN ('SERVER_ROLE', 'SQL_LOGIN', 'WINDOWS_LOGIN')
ORDER BY server_principals.type_desc,
         server_principals.name;