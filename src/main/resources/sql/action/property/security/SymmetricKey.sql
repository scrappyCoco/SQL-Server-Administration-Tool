DECLARE @sql NVARCHAR(MAX) = N'
SELECT id   = N''??db??'' + N'':^%^:'' + CAST(symmetric_keys.symmetric_key_id AS NVARCHAR(MAX)),
       name = symmetric_keys.name,
       db   = N''??db??''
FROM [??db??].sys.symmetric_keys
WHERE @keyId = symmetric_key_id;
';

EXEC sys.sp_executesql @sql, N'@keyId INT', @keyId = ??keyId??;