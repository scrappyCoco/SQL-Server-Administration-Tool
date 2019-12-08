SELECT id        = CONVERT(NVARCHAR(50), server_principals.principal_id, 2),
       name      = server_principals.name,
       kind      = server_principals.type_desc,
       isEnabled = CAST(CASE
                            WHEN type_desc NOT IN ('SQL_LOGIN', 'WINDOWS_LOGIN', 'CERTIFICATE_MAPPED_LOGIN')
                                THEN NULL
                            WHEN server_principals.is_disabled <> 1 THEN 1
                            ELSE 0
           END AS BIT)
FROM sys.server_principals
ORDER BY server_principals.name;