CREATE DATABASE IF NOT EXISTS agrilink CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE agrilink;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  mobile VARCHAR(16) NOT NULL UNIQUE,
  email VARCHAR(255) UNIQUE,
  password_hash VARCHAR(255),
  role ENUM('FARMER','BUYER','ADMIN') NOT NULL DEFAULT 'FARMER',
  language VARCHAR(10) DEFAULT 'en', district VARCHAR(255), latitude DOUBLE, longitude DOUBLE,
  detected_location VARCHAR(500), verified BOOLEAN DEFAULT FALSE,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP, last_login_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  login_count INT DEFAULT 0
);
CREATE TABLE IF NOT EXISTS buyers (
  id BIGINT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255) NOT NULL, company_name VARCHAR(255), mobile VARCHAR(16) NOT NULL,
  district VARCHAR(255) NOT NULL, preferred_crop VARCHAR(100), preferred_grade VARCHAR(20), max_price_per_kg DOUBLE,
  capacity_kg DOUBLE, reliability_score INT DEFAULT 80, completed_deals INT DEFAULT 0, cancelled_deals INT DEFAULT 0,
  on_time_payment_percent DOUBLE DEFAULT 90, verified BOOLEAN DEFAULT TRUE, created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS crop_listings (
  id BIGINT PRIMARY KEY AUTO_INCREMENT, farmer_id BIGINT, crop_name VARCHAR(100) NOT NULL, quantity_kg DOUBLE NOT NULL,
  grade VARCHAR(20), price_per_kg DOUBLE, location VARCHAR(255) NOT NULL, harvest_date DATE, image_url VARCHAR(500),
  status ENUM('AVAILABLE','MATCHED','SOLD','CANCELLED') DEFAULT 'AVAILABLE', created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX(farmer_id), FOREIGN KEY(farmer_id) REFERENCES users(id)
);
CREATE TABLE IF NOT EXISTS transport_requests (
  id BIGINT PRIMARY KEY AUTO_INCREMENT, farmer_id BIGINT, crop_name VARCHAR(100), quantity_kg DOUBLE,
  pickup_village VARCHAR(255), delivery_market VARCHAR(255), harvest_date DATE,
  status ENUM('OPEN','MATCHED','COMPLETED','CANCELLED') DEFAULT 'OPEN', created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX(farmer_id), FOREIGN KEY(farmer_id) REFERENCES users(id)
);
CREATE TABLE IF NOT EXISTS rescue_requests (
  id BIGINT PRIMARY KEY AUTO_INCREMENT, farmer_id BIGINT, crop_name VARCHAR(100), available_quantity_kg DOUBLE,
  minimum_price_per_100kg_bag DOUBLE, freshness_remaining VARCHAR(100), pickup_village VARCHAR(255),
  status ENUM('OPEN','BUYERS_ALERTED','OFFER_ACCEPTED','CLOSED') DEFAULT 'OPEN', created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX(farmer_id), FOREIGN KEY(farmer_id) REFERENCES users(id)
);
CREATE TABLE IF NOT EXISTS analysis_history (
  id BIGINT PRIMARY KEY AUTO_INCREMENT, user_id BIGINT NOT NULL, service_type VARCHAR(50) NOT NULL,
  request_json LONGTEXT, result_json LONGTEXT, created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX(user_id), FOREIGN KEY(user_id) REFERENCES users(id)
);

INSERT INTO buyers(name,company_name,mobile,district,preferred_crop,preferred_grade,max_price_per_kg,capacity_kg,reliability_score,completed_deals,on_time_payment_percent,verified)
SELECT 'ABC Foods','ABC Foods','9000000096','Ongole','Tomato','A',30,5000,96,148,98,TRUE
WHERE NOT EXISTS (SELECT 1 FROM buyers WHERE company_name='ABC Foods');
INSERT INTO buyers(name,company_name,mobile,district,preferred_crop,preferred_grade,max_price_per_kg,capacity_kg,reliability_score,completed_deals,on_time_payment_percent,verified)
SELECT 'FreshMart','FreshMart','9000000091','Prakasam','Tomato','A',28,3000,91,102,94,TRUE
WHERE NOT EXISTS (SELECT 1 FROM buyers WHERE company_name='FreshMart');
INSERT INTO buyers(name,company_name,mobile,district,preferred_crop,preferred_grade,max_price_per_kg,capacity_kg,reliability_score,completed_deals,on_time_payment_percent,verified)
SELECT 'XYZ Processing','XYZ Processing','9000000087','Guntur','Tomato','B',27,8000,87,76,90,TRUE
WHERE NOT EXISTS (SELECT 1 FROM buyers WHERE company_name='XYZ Processing');
