SELECT id   = class_desc,
       name = permission_name
FROM sys.fn_builtin_permissions(default)
ORDER BY class_desc,
         permission_name;