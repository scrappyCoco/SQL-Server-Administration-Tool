DECLARE @sql NVARCHAR(MAX) = N'
SELECT beginDialog = is_active_for_begin_dialog,
       startDate   = CAST(start_date AS VARCHAR(100)),
       expiryDate  = CAST(expiry_date AS VARCHAR(100)),
       subject     = subject,
       name        = name,
       id          = N''??db??'' + N'':^%^:'' + CAST(certificate_id AS NVARCHAR(MAX)),
       db          = N''??db??''
FROM [??db??].sys.certificates
WHERE @CertificateId = certificate_id;
';

EXEC sys.sp_executesql @sql, N'@CertificateId INT', @CertificateId = ??certId??;