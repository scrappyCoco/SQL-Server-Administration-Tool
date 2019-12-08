DECLARE @sql NVARCHAR(MAX) = N'
SELECT id        = N''??db??'' + N'':^%^:'' + CAST(asymmetric_key_id AS NVARCHAR(MAX)),
       name      = name,
       algorithm = algorithm_desc,
       db        = N''??db??''
FROM [??db??].sys.asymmetric_keys
WHERE asymmetric_key_id = @keyId;
';

EXEC sys.sp_executesql @sql, N'@keyId INT', @keyId = ??keyId??;