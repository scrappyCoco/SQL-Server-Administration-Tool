SELECT id   = CAST(principal_id AS VARCHAR(200)),
       name = name,
       kind = type_desc
FROM sys.server_principals
WHERE type_desc IN (
                    'SQL_LOGIN',
                    'SERVER_ROLE',
                    'CERTIFICATE_MAPPED_LOGIN',
                    'WINDOWS_LOGIN',
                    'WINDOWS_GROUP',
                    'ASYMMETRIC_KEY_MAPPED_LOGIN'
  )
UNION ALL
SELECT id   = 'SERVER',
       name = 'SERVER',
       kind = 'SERVER';