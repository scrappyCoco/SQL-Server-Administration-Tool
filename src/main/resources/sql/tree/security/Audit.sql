SELECT id        = CAST(audit_guid AS NVARCHAR(50)),
       name      = name,
       isEnabled = server_audits.is_state_enabled,
       kind      = 'AUDIT'
FROM master.sys.server_audits
ORDER BY name;