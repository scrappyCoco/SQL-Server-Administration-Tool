SELECT DISTINCT id         = CONVERT(NVARCHAR(MAX), containing_group_name),
                name       = CONVERT(NVARCHAR(MAX), containing_group_name),
                isSelected = CAST(0 AS BIT)
FROM sys.dm_audit_actions
WHERE class_desc = N'SERVER'