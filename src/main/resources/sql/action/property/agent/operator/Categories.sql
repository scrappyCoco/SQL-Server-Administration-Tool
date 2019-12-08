SELECT name = syscategories.name,
       id   = CAST(syscategories.category_id AS VARCHAR(10))
FROM msdb.dbo.syscategories
where category_class = 3