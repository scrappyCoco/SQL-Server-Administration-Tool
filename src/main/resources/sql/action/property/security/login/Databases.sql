DECLARE @ServerPrincipalId INT = ??serverPrincipalId??;

DECLARE @searchQuery NVARCHAR(MAX) = N'';

SELECT @searchQuery += N'UNION ALL
SELECT
  defaultSchema = database_principals.default_schema_name COLLATE DATABASE_DEFAULT,
  id            = CAST(' + CAST(databases.database_id AS NVARCHAR(MAX)) + N' AS VARCHAR(10)),
  name          = ''' + databases.name COLLATE DATABASE_DEFAULT + N''',
  [user]        = database_principals.name COLLATE DATABASE_DEFAULT,
  isSelected    = CAST(IIF(database_principals.sid IS NOT NULL, 1, 0) AS BIT)
FROM sys.server_principals
LEFT JOIN [' + databases.name + N'].sys.database_principals ON database_principals.sid = server_principals.sid
WHERE @ServerPrincipalId = server_principals.principal_id
'
FROM sys.databases
WHERE databases.state_desc = 'ONLINE';

SET @searchQuery = '
SELECT *
FROM (' + STUFF(@searchQuery, 1, LEN(N'UNION ALL'), '') + ') AS Databases
ORDER BY name;';

EXEC sys.sp_executesql @searchQuery, N'@ServerPrincipalId INT', @ServerPrincipalId = @ServerPrincipalId;