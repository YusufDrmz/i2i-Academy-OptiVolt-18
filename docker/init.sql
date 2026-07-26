-- Temizlik (Sıfırdan kurulum garantisi için)
DROP TABLE IF EXISTS event_logs CASCADE;
DROP TABLE IF EXISTS daily_consumption_history CASCADE;
DROP TABLE IF EXISTS appliances CASCADE;
DROP TABLE IF EXISTS homes CASCADE;

-- 1. Evler Tablosu (matches Home.java @Table(name = "homes"))
CREATE TABLE homes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    address VARCHAR(255),
    contact_email VARCHAR(255) NOT NULL,
    max_power_budget DOUBLE PRECISION,
    max_financial_budget DOUBLE PRECISION,
    standard_rate DOUBLE PRECISION,
    penalty_rate DOUBLE PRECISION,
    registered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Cihazlar Tablosu (matches Appliance.java @Table(name = "appliances"))
CREATE TABLE appliances (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    safe_power_limit DOUBLE PRECISION,
    home_id BIGINT NOT NULL,
    CONSTRAINT fk_appliance_home FOREIGN KEY (home_id) REFERENCES homes(id) ON DELETE CASCADE
);

-- 3. Tarihsel Tüketim (matches DailyConsumptionHistory.java @Table(name = "daily_consumption_history"))
CREATE TABLE daily_consumption_history (
    id BIGSERIAL PRIMARY KEY,
    home_id BIGINT NOT NULL,
    date DATE,
    total_consumption DOUBLE PRECISION,
    total_billed_amount DOUBLE PRECISION,
    CONSTRAINT fk_history_home FOREIGN KEY (home_id) REFERENCES homes(id) ON DELETE CASCADE
);

-- 4. İhlal ve AI Bildirim Günlüğü (matches EventLog.java @Table(name = "event_logs"))
CREATE TABLE event_logs (
    id BIGSERIAL PRIMARY KEY,
    home_id BIGINT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    email_sent BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Başlangıç İçin Örnek Test Verisi
INSERT INTO homes (name, address, contact_email, max_power_budget, max_financial_budget, standard_rate, penalty_rate, registered_at)
VALUES ('Daire 12 - Yılmaz Ailesi', 'Göztepe, İzmir', 'test@example.com', 5000.0, 1500.0, 2.5, 5.0, CURRENT_TIMESTAMP);

INSERT INTO appliances (name, type, safe_power_limit, home_id)
VALUES
('Çamaşır Makinesi', 'WashingMachine', 2200.0, 1),
('Buzdolabı', 'Refrigerator', 300.0, 1),
('Klima', 'AirConditioner', 1800.0, 1);