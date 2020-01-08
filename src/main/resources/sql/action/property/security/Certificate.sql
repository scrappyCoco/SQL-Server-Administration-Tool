DECLARE @CERTIFICATE_TEMPLATE NVARCHAR(MAX) = N'UNION ALL
SELECT beginDialog = is_active_for_begin_dialog,
       startDate   = CAST(start_date AS VARCHAR(100)),
       expiryDate  = CAST(expiry_date AS VARCHAR(100)),
       subject     = subject COLLATE DATABASE_DEFAULT,
       name        = name COLLATE DATABASE_DEFAULT,
       id          = N''$$DB_NAME$$'' + N'':^%^:'' + CAST(certificate_id AS NVARCHAR(MAX)),
       db          = N''$$DB_NAME$$''
FROM [$$DB_NAME$$].sys.certificates
';

DECLARE @certificateSql NVARCHAR(MAX) = N'';

SELECT @certificateSql += REPLACE(@CERTIFICATE_TEMPLATE, '$$DB_NAME$$', databases.name)
FROM sys.databases
WHERE databases.state_desc = 'ONLINE';

SET @certificateSql = '
SELECT *
FROM (' + STUFF(@certificateSql, 1, LEN('UNION ALL'), '') + ') AS Certificates;
';


EXEC sys.sp_executesql @certificateSql;