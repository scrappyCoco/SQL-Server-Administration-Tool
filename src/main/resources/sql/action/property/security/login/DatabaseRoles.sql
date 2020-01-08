--DECLARE @ServerPrincipalId INT = ??serverPrincipalId??;

DECLARE @ROLE_TEMPLATE NVARCHAR(MAX) = N'UNION ALL
SELECT id           = ''$$DB_NAME$$:'' + CAST(Roles.principal_id AS VARCHAR(100)),
       name         = CAST(Roles.name COLLATE DATABASE_DEFAULT AS NVARCHAR(MAX)),
       isSelected   = CAST(MAX(IIF(Users.principal_id IS NULL, 0, 1)) AS BIT),
       databaseName = ''$$DB_NAME$$'',
       principalId  = CAST(server_principals.principal_id AS VARCHAR(100))
FROM [$$DB_NAME$$].sys.database_principals AS Roles
CROSS APPLY master.sys.server_principals
LEFT JOIN [$$DB_NAME$$].sys.database_role_members ON database_role_members.role_principal_id = Roles.principal_id
LEFT JOIN [$$DB_NAME$$].sys.database_principals AS Users ON Users.principal_id = database_role_members.member_principal_id AND server_principals.sid = Users.sid
WHERE Roles.type_desc = ''DATABASE_ROLE''
GROUP BY Roles.principal_id, Roles.name, server_principals.principal_id
UNION ALL
SELECT id           = ''$$DB_NAME$$:'' + CAST(Roles.principal_id AS VARCHAR(100)),
       name         = CAST(Roles.name COLLATE DATABASE_DEFAULT AS NVARCHAR(MAX)),
       isSelected   = CAST(0 AS BIT),
       databaseName = ''$$DB_NAME$$'',
       principalId  = ''''
FROM [$$DB_NAME$$].sys.database_principals AS Roles
WHERE Roles.type_desc = ''DATABASE_ROLE''
';

DECLARE @roleSql NVARCHAR(MAX) = N'';

SELECT @roleSql += REPLACE(@ROLE_TEMPLATE, '$$DB_NAME$$', databases.name)
FROM sys.databases
WHERE databases.state_desc = 'ONLINE';

SET @roleSql = '
SELECT *
FROM (' + STUFF(@roleSql, 1, LEN('UNION ALL'), '') + ') AS Roles
ORDER BY principalId,
         IIF(name = ''public'', 0, 1),
         databaseName,
         name;
';

EXEC sys.sp_executesql @roleSql--, N'@ServerPrincipalId INT', @ServerPrincipalId = @ServerPrincipalId;