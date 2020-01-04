-- DECLARE @operatorId INT = ??operatorId??;

SELECT id           = CAST(sysoperators.id AS VARCHAR(10)),
       name         = sysoperators.name,
       isEnabled    = CAST(sysoperators.enabled AS BIT),
       eMail        = sysoperators.email_address,
       categoryId   = CAST(syscategories.category_id AS VARCHAR(10)),
       categoryName = syscategories.name
FROM msdb.dbo.sysoperators
LEFT JOIN msdb.dbo.syscategories ON syscategories.category_id = sysoperators.category_id
-- WHERE id = @operatorId;