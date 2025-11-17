DROP DATABASE IF EXISTS Commerce;

CREATE DATABASE IF NOT EXISTS Commerce
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE Commerce;

CREATE TABLE Account
(
    account_id INT                         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(128)                NOT NULL UNIQUE,
    password   VARCHAR(64)                 NOT NULL,
    role       ENUM ('CASHIER', 'ADMIN')   NOT NULL DEFAULT 'CASHIER',
    state      ENUM ('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME(6)                 NOT NULL DEFAULT NOW(6)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Table: Store
CREATE TABLE Store
(
    store_id   INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(128) NOT NULL,
    address    VARCHAR(256) NOT NULL,
    phone      VARCHAR(15)  NOT NULL UNIQUE,
    created_at DATETIME(6)  NOT NULL DEFAULT NOW(6)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Table: User
CREATE TABLE Employee
(
    employee_id INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    email       VARCHAR(128) NOT NULL UNIQUE,
    name        VARCHAR(64)  NOT NULL,
    last_name   VARCHAR(128) NOT NULL,
    store_id    INT          NULL     DEFAULT NULL,
    created_at  DATETIME(6)  NOT NULL DEFAULT NOW(6),
    FOREIGN KEY (email) REFERENCES Account (email) ON UPDATE CASCADE,
    FOREIGN KEY (store_id) REFERENCES Store (store_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

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

CREATE VIEW CompleteStoreView AS
SELECT S.store_id,
       S.name,
       S.address,
       S.phone,
       S.created_at,
       (SELECT COUNT(*) FROM Employee E WHERE E.store_id = S.store_id) AS employee_count
FROM Store S;

-- Table: Access
CREATE TABLE Access
(
    access_id   INT                      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    employee_id INT                      NOT NULL,
    action      ENUM ('LOGOUT', 'LOGIN') NOT NULL,
    created_at  DATETIME(6)              NOT NULL DEFAULT NOW(6),
    FOREIGN KEY (employee_id) REFERENCES Employee (employee_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Table: Product
CREATE TABLE Product
(
    product_id  INT            NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(128)   NOT NULL,
    description VARCHAR(512)   NOT NULL,
    brand       VARCHAR(64)    NOT NULL,
    price       DECIMAL(10, 2) NOT NULL,
    created_at  DATETIME(6)    NOT NULL DEFAULT NOW(6)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Table: Invoice
CREATE TABLE Invoice
(
    invoice_id  INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    store_id    INT          NOT NULL,
    name_client VARCHAR(128) NOT NULL,
    created_at  DATETIME(6)  NOT NULL DEFAULT NOW(6),
    FOREIGN KEY (store_id) REFERENCES Store (store_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Table: Sale
CREATE TABLE Sale
(
    sale_id     INT            NOT NULL AUTO_INCREMENT PRIMARY KEY,
    invoice_id  INT            NOT NULL,
    store_id    INT            NOT NULL,
    employee_id INT            NOT NULL,
    amount      INT            NOT NULL CHECK (amount > 0),
    price       DECIMAL(10, 2) NOT NULL,
    created_at  DATETIME(6)    NOT NULL DEFAULT NOW(6),
    FOREIGN KEY (invoice_id) REFERENCES Invoice (invoice_id),
    FOREIGN KEY (employee_id) REFERENCES Employee (employee_id),
    FOREIGN KEY (store_id) REFERENCES Store (store_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Table: Stock
CREATE TABLE Stock
(
    product_id INT         NOT NULL,
    store_id   INT         NOT NULL,
    quantity   INT         NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    created_at DATETIME(6) NOT NULL DEFAULT NOW(6),
    PRIMARY KEY (product_id, store_id),
    FOREIGN KEY (product_id) REFERENCES Product (product_id),
    FOREIGN KEY (store_id) REFERENCES Store (store_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

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

DROP VIEW IF EXISTS CompleteProductView;
CREATE VIEW CompleteProductView AS
SELECT P.product_id,
       P.name,
       P.description,
       P.brand,
       P.price,
       COALESCE(((SELECT SUM(quantity) FROM Stock S WHERE S.product_id = P.product_id)), 0) as stock,
       P.created_at
FROM Product P;

-- Table: PendingRegistrations
CREATE TABLE PendingRegistrations
(
    id         INT                       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(128)              NOT NULL UNIQUE,
    pin        VARCHAR(10)               NOT NULL,
    expires_at DATETIME(6)               NOT NULL,
    created_at DATETIME(6)               NOT NULL DEFAULT NOW(6),
    password   TEXT                      NULL,
    role       ENUM ('CASHIER', 'ADMIN') NOT NULL DEFAULT 'CASHIER'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Table: Session
CREATE TABLE Session
(
    token_id    VARCHAR(64) NOT NULL,
    employee_id INT         NOT NULL,
    created_at  DATETIME(6) NOT NULL DEFAULT NOW(6),
    expires_at  DATETIME(6) NOT NULL,
    PRIMARY KEY (token_id),
    FOREIGN KEY (employee_id) REFERENCES Employee (employee_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

DROP USER IF EXISTS 'commerce_admin'@'localhost';
DROP ROLE IF EXISTS commerce_admin;

CREATE USER commerce_admin@localhost IDENTIFIED BY 'ADMIN_COMMERCE';
CREATE ROLE commerce_admin;
GRANT commerce_admin TO commerce_admin@localhost;
GRANT EXECUTE, SELECT, INSERT, UPDATE, DELETE ON Commerce.* TO commerce_admin;
SET DEFAULT ROLE ALL TO commerce_admin@localhost;
