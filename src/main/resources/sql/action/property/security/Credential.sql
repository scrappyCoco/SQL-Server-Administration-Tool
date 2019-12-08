DECLARE @credentialId INT = ??credentialId??;

SELECT name         = credentials.name,
       identityName = credentials.credential_identity,
       id           = CAST(credentials.credential_id AS VARCHAR(10)),
       providerId   = CAST(cryptographic_providers.provider_id AS VARCHAR(10)),
       providerName = cryptographic_providers.name
FROM sys.credentials
LEFT JOIN sys.cryptographic_providers
          ON cryptographic_providers.provider_id = credentials.target_id
              AND credentials.target_type = 'CRYPTOGRAPHIC PROVIDER'
WHERE @credentialId = credential_id;