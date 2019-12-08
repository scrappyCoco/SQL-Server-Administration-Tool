DECLARE @roleId INT = ??roleId??;

SELECT id         = CAST(AllRoles.principal_id AS VARCHAR(10)),
       name       = AllRoles.name,
       isSelected = CAST(IIF(CurrentRole.principal_id IS NOT NULL, 1, 0) AS BIT),
       kind       = 'SERVER_ROLE'
FROM master.sys.server_principals AS AllRoles
LEFT JOIN master.sys.server_role_members ON AllRoles.principal_id = server_role_members.role_principal_id
        AND server_role_members.member_principal_id = @roleId
LEFT JOIN master.sys.server_principals AS CurrentRole
          ON CurrentRole.principal_id = server_role_members.member_principal_id
WHERE AllRoles.type_desc = 'SERVER_ROLE';