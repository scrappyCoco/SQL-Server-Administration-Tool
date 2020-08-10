/*
 * Copyright [2020] Coding4fun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
LEFT JOIN @currentNames AS CurrentActions
                        ON CurrentActions.Name = AllAvailableActions.Name
                       AND CurrentActions.SpecificationId = Specifications.server_specification_id
UNION ALL
SELECT id              = AllAvailableActions.Name,
       name            = AllAvailableActions.Name,
       isSelected      = CAST(0 AS BIT),
       specificationId = '-1'
FROM @availableNames AS AllAvailableActions;