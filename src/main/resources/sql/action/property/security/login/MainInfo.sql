--DECLARE @principalId INT = ??principalId??;

SELECT id                  = CAST(p.principal_id AS VARCHAR(10)),
       name                = p.name,
       principalKind       = p.type_desc,
       defaultDatabase     = p.default_database_name,
       defaultLanguage     = sql_logins.default_language_name,
       loginPasswordHashed = CONVERT(VARCHAR(512), LOGINPROPERTY(p.name, 'PasswordHash'), 1),
       sid                 = CONVERT(VARCHAR(512), p.sid, 1),
       isPolicyChecked     = CAST(ISNULL(sql_logins.is_policy_checked, 0) AS BIT),
       isExpirationChecked = CAST(ISNULL(sql_logins.is_expiration_checked, 0) AS BIT),
       denyLogin           = CAST(ISNULL(l.denylogin, 0) AS BIT),
       isDisabled          = CAST(ISNULL(p.is_disabled, 0) AS BIT),
       mustChange          = CAST(0 AS BIT),
       credential          = credentials.name
FROM sys.server_principals p
LEFT JOIN sys.syslogins l ON l.name = p.name
LEFT JOIN sys.sql_logins ON sql_logins.name = l.name
LEFT JOIN sys.server_principal_credentials ON server_principal_credentials.principal_id = sql_logins.principal_id
LEFT JOIN sys.credentials ON credentials.credential_id = server_principal_credentials.credential_id
--WHERE p.principal_id = @principalId;