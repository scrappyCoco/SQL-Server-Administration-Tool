DECLARE @ASYM_KEY_TEMPLATE NVARCHAR(MAX) = N'UNION ALL
SELECT id   = N''$$DB_NAME$$'' + N'':^%^:'' + CAST(symmetric_keys.symmetric_key_id AS NVARCHAR(MAX)),
       name = symmetric_keys.name,
       db   = N''$$DB_NAME$$''
FROM [$$DB_NAME$$].sys.symmetric_keys
';

DECLARE @symmetricKeysSql NVARCHAR(MAX) = N'';

SELECT @symmetricKeysSql += REPLACE(@ASYM_KEY_TEMPLATE, '$$DB_NAME$$', databases.name)
FROM sys.databases
WHERE databases.state_desc = 'ONLINE';

SET @symmetricKeysSql = '
SELECT *
FROM (' + STUFF(@symmetricKeysSql, 1, LEN('UNION ALL'), '') + ') AS SymmetricKeys;
';

EXEC sys.sp_executesql @symmetricKeysSql