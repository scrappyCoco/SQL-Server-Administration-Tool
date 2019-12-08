SELECT id        = CAST(server_specification_id AS NVARCHAR(50)),
       name      = name,
       isEnabled = server_audit_specifications.is_state_enabled,
       kind      = 'SERVER_AUDIT_SPECIFICATION'
FROM sys.server_audit_specifications
ORDER BY name;