DECLARE @AuditSpecificationId INT = ??specId??;

DECLARE @availableNames TABLE
                        (
                            Name NVARCHAR(200)
                        );

DECLARE @currentNames TABLE
                      (
                          Name NVARCHAR(200)
                      );

INSERT INTO @availableNames (Name)
SELECT DISTINCT containing_group_name = CONVERT(NVARCHAR(MAX), containing_group_name)
FROM sys.dm_audit_actions
WHERE class_desc = N'SERVER'

INSERT INTO @currentNames (Name)
SELECT audit_action_name = CONVERT(NVARCHAR(MAX), server_audit_specification_details.audit_action_name)
FROM sys.server_audit_specification_details
WHERE server_specification_id = @AuditSpecificationId

SELECT id         = AllAvailableActions.Name,
       name       = AllAvailableActions.Name,
       isSelected = CAST(IIF(CurrentActions.Name IS NOT NULL, 1, 0) AS BIT)
FROM @availableNames AS AllAvailableActions
LEFT JOIN @currentNames AS CurrentActions ON CurrentActions.Name = AllAvailableActions.Name;