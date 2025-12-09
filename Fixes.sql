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

-- 2. Add missing 'created_at' column to 'Stock' table
-- The StockDTO class expects this column, but it was missing in the original schema.
-- This must be done BEFORE creating CompleteStockView.
ALTER TABLE Stock ADD COLUMN created_at DATETIME(6) NOT NULL DEFAULT NOW(6);

-- 3. Fix for missing 'CompleteStockView'
-- This view is required by StockDAO to display inventory items.
-- It now includes the 'created_at' column added above.
DROP VIEW IF EXISTS CompleteStockView;

CREATE VIEW CompleteStockView AS
SELECT ST.product_id,
       ST.store_id,
       quantity,
       P.name  AS product_name,
       S.name  AS store_name,
       P.price AS price,
       ST.created_at
FROM Stock ST
         JOIN Store S ON S.store_id = ST.store_id
         JOIN Product P on ST.product_id = P.product_id;

-- 4. Fix for 'Sale' table missing 'product_id'
-- The original schema tracked the sale but not WHICH product was sold.
-- This is critical for the checkout process.
ALTER TABLE Sale ADD COLUMN product_id INT NOT NULL AFTER employee_id;
ALTER TABLE Sale ADD CONSTRAINT fk_sale_product FOREIGN KEY (product_id) REFERENCES Product (product_id);
