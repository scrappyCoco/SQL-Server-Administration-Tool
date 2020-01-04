SELECT id        = CAST(provider_id AS VARCHAR(10)),
       name      = name,
       isEnabled = cryptographic_providers.is_enabled,
       filePath  = dll_path
FROM sys.cryptographic_providers;