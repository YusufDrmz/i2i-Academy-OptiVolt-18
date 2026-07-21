-- Temizlik (Sıfırdan kurulum garantisi için)
DROP TABLE IF EXISTS event_logs CASCADE;
DROP TABLE IF EXISTS historical_consumptions CASCADE;
DROP TABLE IF EXISTS appliances CASCADE;
DROP TABLE IF EXISTS residential_structures CASCADE;

-- 1. Evler Tablosu (Residential Structures)
CREATE TABLE residential_structures (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    contact_email VARCHAR(150) NOT NULL,
    power_quota_watt DOUBLE PRECISION NOT NULL,   -- Maksimum güç limiti (Watt)
    budget_quota_try DOUBLE PRECISION NOT NULL,  -- Bütçe limiti (TL)
    standard_rate DOUBLE PRECISION NOT NULL,     -- Standart kWh birim fiyatı
    penalty_rate DOUBLE PRECISION NOT NULL,      -- Ceza tavan birim fiyatı
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Cihazlar Tablosu (Appliances)
CREATE TABLE appliances (
    id BIGSERIAL PRIMARY KEY,
    home_id BIGINT NOT NULL,
    device_name VARCHAR(100) NOT NULL,
    device_type VARCHAR(50) NOT NULL,             -- Refrigerator, WashingMachine vb.
    max_safe_watt DOUBLE PRECISION NOT NULL,      -- Güvenli çalışma tavan Watt'ı
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_appliance_home FOREIGN KEY (home_id) REFERENCES residential_structures(id) ON DELETE CASCADE
);

-- 3. Tarihsel Tüketim Snapshot Tablosu (Frontend Grafikleri İçin)
CREATE TABLE historical_consumptions (
    id BIGSERIAL PRIMARY KEY,
    home_id BIGINT NOT NULL,
    total_watt DOUBLE PRECISION NOT NULL,
    total_cost DOUBLE PRECISION NOT NULL,
    recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_history_home FOREIGN KEY (home_id) REFERENCES residential_structures(id) ON DELETE CASCADE
);

-- 4. İhlal ve AI Bildirim Günlüğü (Audit & Event Log)
CREATE TABLE event_logs (
    id BIGSERIAL PRIMARY KEY,
    home_id BIGINT NOT NULL,
    event_type VARCHAR(50) NOT NULL,              -- QUOTA_80_PERCENT, QUOTA_100_PERCENT, DEVICE_ANOMALY
    severity VARCHAR(20) NOT NULL,                -- INFO, WARNING, CRITICAL
    message TEXT NOT NULL,                        -- LLM / Gemini tarafından üretilen metin
    email_sent BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_log_home FOREIGN KEY (home_id) REFERENCES residential_structures(id) ON DELETE CASCADE
);

-- Başlangıç İçin Örnek Test Verisi
INSERT INTO residential_structures (name, address, contact_email, power_quota_watt, budget_quota_try, standard_rate, penalty_rate)
VALUES ('Daire 12 - Yılmaz Ailesi', 'Göztepe, İzmir', 'test@example.com', 5000.0, 1500.0, 2.5, 5.0);

INSERT INTO appliances (home_id, device_name, device_type, max_safe_watt)
VALUES 
(1, 'Çamaşır Makinesi', 'WashingMachine', 2200.0),
(1, 'Buzdolabı', 'Refrigerator', 300.0),
(1, 'Klima', 'AirConditioner', 1800.0);