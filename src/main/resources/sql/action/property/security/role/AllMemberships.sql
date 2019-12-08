SELECT id         = CAST(AllRoles.principal_id AS VARCHAR(10)),
       name       = AllRoles.name,
       isSelected = CAST(0 AS BIT),
       kind       = 'SERVER_ROLE'
FROM master.sys.server_principals AS AllRoles
WHERE AllRoles.type_desc = 'SERVER_ROLE';