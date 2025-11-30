/*
 * Fixes.sql
 * Applies necessary database schema corrections for Ninco ERP.
 */

USE Commerce;

-- 1. Fix for 'CompleteEmployeeView' missing 'account_id' column
-- This view is required by EmployeeDAO to properly fetch account details during login.
DROP VIEW IF EXISTS CompleteEmployeeView;

CREATE VIEW CompleteEmployeeView AS
SELECT A.account_id,
       E.employee_id,
       E.email,
       E.name,
       E.last_name,
       S.store_id,
       S.name as store_name,
       A.role,
       A.state,
       E.created_at
FROM Employee E
         JOIN Account A ON E.email = A.email
         LEFT JOIN Store S ON E.store_id = S.store_id;
