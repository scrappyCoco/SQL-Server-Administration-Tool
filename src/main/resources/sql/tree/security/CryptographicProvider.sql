SELECT id        = CAST(provider_id AS NVARCHAR(50)),
       name      = name,
       isEnabled = is_enabled,
       kind      = 'CRYPTOGRAPHIC_PROVIDER'
FROM sys.cryptographic_providers
ORDER BY name;