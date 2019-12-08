DECLARE @SEPARATOR NVARCHAR(MAX) = N':^%^:';
DECLARE @sql NVARCHAR(MAX) = N'';

SELECT @sql += N'UNION ALL
SELECT id        = N''' + databases.name + @SEPARATOR + N''' + CAST(asymmetric_key_id AS NVARCHAR(MAX)),
       name      = asymmetric_keys.name COLLATE DATABASE_DEFAULT,
       groupName = N''' + databases.name + N''',
       kind      = ''ASYMMETRIC_KEY''
FROM ' + databases.name + N'.sys.asymmetric_keys
'
FROM sys.databases
WHERE state_desc = 'ONLINE';

SET @sql = STUFF(@sql, 1, LEN(N'UNION ALL'), N'');
SET @sql = N'SELECT * FROM (' + @sql + N') AS R ORDER BY name ASC;'

EXEC (@sql);