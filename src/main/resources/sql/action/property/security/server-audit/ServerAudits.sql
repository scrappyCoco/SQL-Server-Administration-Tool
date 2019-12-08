SELECT id   = CAST(audit_guid AS VARCHAR(50)),
       name = name
FROM sys.server_audits;