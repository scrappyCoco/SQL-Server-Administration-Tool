--DECLARE @serverPrincipalId INT = ??serverPrincipalId??;

SELECT id         = CAST(Roles.principal_id AS VARCHAR(10)),
       name       = Roles.name,
       isSelected = CAST(IIF(server_role_members.member_principal_id IS NULL, 0, 1) AS BIT),
       kind       = Roles.type_desc,
       principalId = CAST(Users.principal_id AS VARCHAR(100))
FROM sys.server_principals AS Roles
         CROSS APPLY sys.server_principals AS Users
         LEFT JOIN sys.server_role_members ON server_role_members.role_principal_id = Roles.principal_id
    AND server_role_members.member_principal_id = Users.principal_id
WHERE Roles.type = 'R'
  AND Users.type <> 'R';