DECLARE @availableNames TABLE (Name NVARCHAR(200));

DECLARE @currentNames TABLE (
    Name NVARCHAR(200),
    SpecificationId INT
);

INSERT INTO @availableNames (Name)
SELECT DISTINCT containing_group_name = CONVERT(NVARCHAR(MAX), containing_group_name)
FROM sys.dm_audit_actions
WHERE class_desc = N'SERVER';

INSERT INTO @currentNames (Name, SpecificationId)
SELECT Name            = CONVERT(NVARCHAR(MAX), server_audit_specification_details.audit_action_name),
       SpecificationId = server_specification_id
FROM sys.server_audit_specification_details;

SELECT id              = AllAvailableActions.Name,
       name            = AllAvailableActions.Name,
       isSelected      = CAST(IIF(CurrentActions.Name IS NOT NULL, 1, 0) AS BIT),
       specificationId = CAST(Specifications.server_specification_id AS VARCHAR(10))
FROM @availableNames AS AllAvailableActions
CROSS APPLY sys.server_audit_specifications AS Specifications
LEFT JOIN @currentNames AS CurrentActions ON CurrentActions.Name = AllAvailableActions.Name
    AND CurrentActions.SpecificationId = Specifications.server_specification_id
UNION ALL
SELECT id              = AllAvailableActions.Name,
       name            = AllAvailableActions.Name,
       isSelected      = CAST(0 AS BIT),
       specificationId = ''
FROM @availableNames AS AllAvailableActions;