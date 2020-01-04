DECLARE @ASYM_KEY_TEMPLATE NVARCHAR(MAX) = N'UNION ALL
SELECT id        = N''$$DB_NAME$$'' + N'':^%^:'' + CAST(asymmetric_key_id AS NVARCHAR(MAX)),
       name      = name,
       algorithm = algorithm_desc,
       db        = N''$$DB_NAME$$''
FROM [$$DB_NAME$$].sys.asymmetric_keys
';

DECLARE @asymmetricKeysSql NVARCHAR(MAX) = N'';

SELECT @asymmetricKeysSql += REPLACE(@ASYM_KEY_TEMPLATE, '$$DB_NAME$$', databases.name)
FROM sys.databases
WHERE databases.state_desc = 'ONLINE';

SET @asymmetricKeysSql = '
SELECT *
FROM (' + STUFF(@asymmetricKeysSql, 1, LEN('UNION ALL'), '') + ') AS AsymmetricKeys;
';

EXEC sys.sp_executesql @asymmetricKeysSql