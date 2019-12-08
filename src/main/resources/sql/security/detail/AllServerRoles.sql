SELECT id         = CAST(server_principals.principal_id AS VARCHAR(10)),
       name       = server_principals.name,
       isSelected = CAST(0 AS BIT),
       kind       = server_principals.type_desc
FROM sys.server_principals
WHERE server_principals.type = 'R';