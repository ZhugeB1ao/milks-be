IF DB_ID('Milk') IS NULL
BEGIN
    CREATE DATABASE Milk;
END
GO

USE Milk;
GO

DROP TABLE IF EXISTS dbo.OrderDetail;
DROP TABLE IF EXISTS dbo.OrderHeader;
DROP TABLE IF EXISTS dbo.Customer;
DROP TABLE IF EXISTS dbo.Product;
DROP TABLE IF EXISTS dbo.Category;
DROP TABLE IF EXISTS dbo.Account;
GO

CREATE TABLE dbo.Category (
                              id int IDENTITY(1,1) PRIMARY KEY,
                              name varchar(100) NOT NULL
);
GO

CREATE TABLE dbo.Account (
                             id int IDENTITY(1,1) PRIMARY KEY,
                             name nvarchar(100) NOT NULL,
                             address nvarchar(255) NOT NULL,
                             phone varchar(15) NOT NULL,
                             email varchar(100) NOT NULL UNIQUE,
                             password varchar(100) NOT NULL,
                             enabled bit NOT NULL DEFAULT 1,
                             role varchar(30) NOT NULL,
                             CONSTRAINT CK_Account_Role CHECK (role IN ('ROLE_ADMIN', 'ROLE_CUSTOMER'))
);
GO

CREATE TABLE dbo.Customer (
                              id int PRIMARY KEY,
                              category varchar(50) NOT NULL,
                              shipToAddress nvarchar(255) NOT NULL,
                              CONSTRAINT FK_Customer_Account FOREIGN KEY (id) REFERENCES dbo.Account(id),
                              CONSTRAINT CK_Customer_Category CHECK (category IN ('Gold', 'Silver', 'Copper'))
);
GO

CREATE TABLE dbo.Product (
                             id int IDENTITY(1,1) PRIMARY KEY,
                             description varchar(150) NOT NULL,
                             price float NOT NULL,
                             discount float NOT NULL DEFAULT 0,
                             imageUrl varchar(255) NOT NULL,
                             categoryId int NOT NULL,
                             CONSTRAINT FK_Product_Category FOREIGN KEY (categoryId) REFERENCES dbo.Category(id)
);
GO

CREATE TABLE dbo.OrderHeader (
                                 id int IDENTITY(1,1) PRIMARY KEY,
                                 orderDate datetime NOT NULL DEFAULT GETDATE(),
                                 status varchar(30) NOT NULL DEFAULT 'NEW',
                                 customerId int NOT NULL,
                                 totalAmount float NOT NULL DEFAULT 0,
                                 CONSTRAINT FK_OrderHeader_Customer FOREIGN KEY (customerId) REFERENCES dbo.Customer(id),
                                 CONSTRAINT CK_OrderHeader_Status CHECK (status IN ('NEW', 'SHIPPING', 'PAID'))
);
GO

CREATE TABLE dbo.OrderDetail (
                                 id int IDENTITY(1,1) PRIMARY KEY,
                                 orderHeaderId int NOT NULL,
                                 productId int NOT NULL,
                                 quantity int NOT NULL,
                                 price float NOT NULL,
                                 discount float NOT NULL DEFAULT 0,
                                 CONSTRAINT FK_OrderDetail_OrderHeader FOREIGN KEY (orderHeaderId) REFERENCES dbo.OrderHeader(id),
                                 CONSTRAINT FK_OrderDetail_Product FOREIGN KEY (productId) REFERENCES dbo.Product(id)
);
GO

USE Milk;
GO

SET IDENTITY_INSERT dbo.Category ON;
INSERT INTO dbo.Category (id, name) VALUES
                                        (1, 'Fresh Milk & Nut Milk'),
                                        (2, 'Yogurt & Milk Cream'),
                                        (3, 'Milk Powder & Condensed Milk');
SET IDENTITY_INSERT dbo.Category OFF;
GO

-- Password demo là BCrypt của chuỗi "1"
SET IDENTITY_INSERT dbo.Account ON;
INSERT INTO dbo.Account (id, name, address, phone, email, password, enabled, role) VALUES
	                                                                                       (1, N'Admin', N'Ho Chi Minh City', '0900000001', 'admin@milkstore.com',
	                                                                                        '$2a$10$GoarY2g4D5Q/eAlhBsfgju5g3GikR7byTRPcmyitkqeeNo1sCKsWq', 1, 'ROLE_ADMIN'),

	                                                                                       (2, N'Customer One', N'District 1, HCMC', '0900000002', 'customer1@gmail.com',
	                                                                                        '$2a$10$aoy0ca9G8HyKEUBJszFSXuqFurmquuknAgb78v5kNg7bOM/sVWX9O', 1, 'ROLE_CUSTOMER'),

	                                                                                       (3, N'Customer Two', N'District 7, HCMC', '0900000003', 'customer2@gmail.com',
	                                                                                        '$2a$10$gg42C86d6bFfWzbyLg5m5OQq/S8m4c0CoBEm.EmAUGfdz8yzvCBjW', 1, 'ROLE_CUSTOMER');
SET IDENTITY_INSERT dbo.Account OFF;
GO

INSERT INTO dbo.Customer (id, category, shipToAddress) VALUES
                                                           (2, 'Gold', N'District 1, HCMC'),
                                                           (3, 'Silver', N'District 7, HCMC');
GO

SET IDENTITY_INSERT dbo.Product ON;
INSERT INTO dbo.Product (id, description, price, discount, categoryId, imageUrl) VALUES
                                                                                     (1, 'TH True Milk Low Sugar 180ml', 1.28, 0.05, 1, '/images/products/th-true-milk-low-sugar-180ml.jpg'),
                                                                                     (2, 'Vinamilk Pure Fresh Milk 1L', 1.40, 0, 1, '/images/products/vinamilk-pure-fresh-milk-1l.jpg'),
                                                                                     (3, 'TH True Nut Almond Milk 180ml', 1.80, 0.10, 1, '/images/products/th-true-nut-almond-milk-180ml.jpg'),
                                                                                     (4, 'Fami Pure Soy Milk Pack 6', 1.00, 0, 1, '/images/products/fami-pure-soy-milk-pack-6.jpg'),
                                                                                     (5, 'Dutch Lady UHT Milk 170ml', 1.12, 0.05, 1, '/images/products/dutch-lady-uht-milk-170ml.jpg'),
                                                                                     (6, 'Dalat Milk Unsweetened 450ml', 0.72, 0, 1, '/images/products/dalat-milk-unsweetened-450ml.jpg'),
                                                                                     (7, 'LiF Kun Corn Milk 180ml', 1.04, 0.10, 1, '/images/products/lif-kun-corn-milk-180ml.jpg'),
                                                                                     (8, 'Vinamilk Walnut Milk 180ml', 1.32, 0.05, 1, '/images/products/vinamilk-walnut-milk-180ml.jpg'),
                                                                                     (9, 'Milo Barley Chocolate Milk Pack 4', 1.15, 0, 1, '/images/products/milo-barley-chocolate-milk-pack-4.jpg'),
                                                                                     (10, 'Vinasoy Soy Milk Less Sugar 200ml', 0.25, 0, 1, '/images/products/vinasoy-soy-milk-less-sugar-200ml.jpg'),

                                                                                     (11, 'Vinamilk Sweetened Yogurt Pack 4', 1.04, 0, 2, '/images/products/vinamilk-sweetened-yogurt-pack-4.jpg'),
                                                                                     (12, 'TH True Yogurt Blueberry', 1.28, 0.05, 2, '/images/products/th-true-yogurt-blueberry.jpg'),
                                                                                     (13, 'Monte Classic Milk Cream Pack 4', 2.32, 0.10, 2, '/images/products/monte-classic-milk-cream-pack-4.jpg'),
                                                                                     (14, 'Probi Yogurt Drink Pack 5', 0.96, 0, 2, '/images/products/probi-yogurt-drink-pack-5.jpg'),
                                                                                     (15, 'Vinamilk Aloe Vera Yogurt', 1.12, 0.05, 2, '/images/products/vinamilk-aloe-vera-yogurt.jpg'),
                                                                                     (16, 'Betagen Yogurt Drink 400ml', 0.88, 0, 2, '/images/products/betagen-yogurt-drink-400ml.jpg'),
                                                                                     (17, 'Greek Yogurt Strawberry 100g', 1.50, 0.10, 2, '/images/products/greek-yogurt-strawberry-100g.jpg'),
                                                                                     (18, 'Zott Yogurt Fruit Mixed Berries', 0.85, 0, 2, '/images/products/zott-yogurt-fruit-mixed-berries.jpg'),
                                                                                     (19, 'Hoff Cheese Pods Pack 4', 2.05, 0.05, 2, '/images/products/hoff-cheese-pods-pack-4.jpg'),
                                                                                     (20, 'TH True Yogurt Natural Flavor', 1.20, 0, 2, '/images/products/th-true-yogurt-natural.jpg'),

                                                                                     (21, 'Ong Tho Condensed Milk 380g', 0.96, 0, 3, '/images/products/ong-tho-condensed-milk-380g.jpg'),
                                                                                     (22, 'Southern Star Condensed Milk Blue', 0.72, 0, 3, '/images/products/southern-star-condensed-milk-blue.jpg'),
                                                                                     (23, 'Abbott Ensure Gold Powder 850g', 31.20, 0.05, 3, '/images/products/abbott-ensure-gold-850g.jpg'),
                                                                                     (24, 'PediaSure BA Powder 1.6kg', 42.00, 0.10, 3, '/images/products/pediasure-ba-powder-1-6kg.jpg'),
                                                                                     (25, 'Similac Newborn Stage 1 Powder', 22.00, 0, 3, '/images/products/similac-newborn-stage-1.jpg'),
                                                                                     (26, 'Grow Plus Red Powder 900g', 15.20, 0.05, 3, '/images/products/grow-plus-red-powder-900g.jpg'),
                                                                                     (27, 'Meiji Growing Up Formula 800g', 19.50, 0, 3, '/images/products/meiji-growing-up-formula-800g.jpg'),
                                                                                     (28, 'Vinamilk Adora High Calcium 400g', 6.80, 0.05, 3, '/images/products/vinamilk-adora-high-calcium-400g.jpg'),
                                                                                     (29, 'Milo Powder Active Go 400g', 4.20, 0, 3, '/images/products/milo-powder-active-go-400g.jpg'),
                                                                                     (30, 'Dielac Alpha Gold IQ 900g', 14.90, 0.05, 3, '/images/products/dielac-alpha-gold-iq-900g.jpg');
SET IDENTITY_INSERT dbo.Product OFF;
GO
