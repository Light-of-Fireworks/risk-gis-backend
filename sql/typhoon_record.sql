-- 台风主表
CREATE TABLE IF NOT EXISTS typhoon_record (
    id BIGSERIAL PRIMARY KEY,
    tfid VARCHAR(32) NOT NULL UNIQUE,
    name VARCHAR(64),
    en_name VARCHAR(64),
    strong VARCHAR(32),
    power VARCHAR(16),
    speed VARCHAR(16),
    pressure VARCHAR(16),
    lat DECIMAL(10, 4),
    lng DECIMAL(10, 4),
    move_direction VARCHAR(16),
    move_speed VARCHAR(16),
    radius7 VARCHAR(16),
    radius10 VARCHAR(16),
    is_active BOOLEAN DEFAULT TRUE,
    data_time TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_typhoon_record_tfid ON typhoon_record(tfid);
CREATE INDEX IF NOT EXISTS idx_typhoon_record_data_time ON typhoon_record(data_time DESC);

-- 台风轨迹点表
CREATE TABLE IF NOT EXISTS typhoon_point (
    id BIGSERIAL PRIMARY KEY,
    tfid VARCHAR(32) NOT NULL,
    point_time TIMESTAMP,
    lat VARCHAR(16),
    lng VARCHAR(16),
    strong VARCHAR(32),
    power VARCHAR(16),
    speed VARCHAR(16),
    pressure VARCHAR(16),
    move_direction VARCHAR(16),
    move_speed VARCHAR(16),
    radius7 VARCHAR(16),
    radius10 VARCHAR(16),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_typhoon_point_tfid ON typhoon_point(tfid);
CREATE INDEX IF NOT EXISTS idx_typhoon_point_time ON typhoon_point(point_time);
