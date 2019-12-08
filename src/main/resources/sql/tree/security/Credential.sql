SELECT id   = CAST(credentials.credential_id AS NVARCHAR(50)),
       name = credentials.name,
       kind = 'CREDENTIAL'
FROM sys.credentials
ORDER BY credentials.name;