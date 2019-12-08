DECLARE @serverAuditSpecificationId INT = ??specId??;

SELECT id        = CAST(server_specification_id AS VARCHAR(20)),
       name      = server_audit_specifications.name,
       auditName = server_audits.name,
       isEnabled = server_audit_specifications.is_state_enabled
FROM sys.server_audit_specifications
LEFT JOIN sys.server_audits ON server_audits.audit_guid = server_audit_specifications.audit_guid
WHERE @serverAuditSpecificationId = server_audit_specifications.server_specification_id;