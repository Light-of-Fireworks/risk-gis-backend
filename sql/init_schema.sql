-- 用户表
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    enabled BOOLEAN DEFAULT TRUE,
    deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

-- 角色表
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(200)
);

-- 用户角色关联表
CREATE TABLE user_roles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);

-- 地理数据表
CREATE TABLE geo_data (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    geometry GEOMETRY(GEOMETRY, 4326) NOT NULL,
    properties JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_geo_data_geometry ON geo_data USING GIST(geometry);
CREATE INDEX idx_geo_data_type ON geo_data(type);

-- 风险评估表
CREATE TABLE risk_assessment (
    id BIGSERIAL PRIMARY KEY,
    region_id BIGINT REFERENCES geo_data(id),
    risk_type VARCHAR(50) NOT NULL,
    risk_score DECIMAL(5,2),
    risk_level VARCHAR(20),
    assessment_date DATE,
    factors JSONB,
    geometry GEOMETRY(POLYGON, 4326),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_risk_assessment_geometry ON risk_assessment USING GIST(geometry);
CREATE INDEX idx_risk_assessment_type ON risk_assessment(risk_type);
CREATE INDEX idx_risk_assessment_level ON risk_assessment(risk_level);

-- 预警信息表
CREATE TABLE warning (
    id BIGSERIAL PRIMARY KEY,
    warning_type VARCHAR(50) NOT NULL,
    level VARCHAR(20),
    title VARCHAR(200),
    content TEXT,
    location GEOMETRY(POINT, 4326),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_warning_location ON warning USING GIST(location);
CREATE INDEX idx_warning_type ON warning(warning_type);
CREATE INDEX idx_warning_status ON warning(status);

-- 地震记录表
CREATE TABLE earthquake_record (
    id BIGSERIAL PRIMARY KEY,
    occur_time TIMESTAMP NOT NULL,
    magnitude DECIMAL(4,1) NOT NULL,
    latitude DECIMAL(10,6) NOT NULL,
    longitude DECIMAL(10,6) NOT NULL,
    depth DECIMAL(8,2),
    location VARCHAR(200),
    report_time TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(occur_time, latitude, longitude, magnitude)
);

CREATE INDEX idx_earthquake_time ON earthquake_record(occur_time);

-- 洪水预警表
CREATE TABLE flood_warning (
    id BIGSERIAL PRIMARY KEY,
    wr_info_id BIGINT UNIQUE NOT NULL,
    wr_icon VARCHAR(500),
    wr_title VARCHAR(500),
    wr_detail TEXT,
    publish_time TIMESTAMP,
    expire_time TIMESTAMP,
    longitude DECIMAL(10,6),
    latitude DECIMAL(10,6),
    wr_type VARCHAR(50),
    wr_level VARCHAR(20),
    influence_area VARCHAR(500),
    influence_area_cd VARCHAR(100),
    unit_name VARCHAR(200),
    detail_url VARCHAR(500),
    location GEOMETRY(POINT, 4326),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_flood_warning_location ON flood_warning USING GIST(location);
CREATE INDEX idx_flood_warning_type ON flood_warning(wr_type);
CREATE INDEX idx_flood_warning_level ON flood_warning(wr_level);
CREATE INDEX idx_flood_warning_time ON flood_warning(publish_time);

-- 险类表
CREATE TABLE insurance_category (
    category_code VARCHAR(20) PRIMARY KEY,
    category_name VARCHAR(50) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 险种表
CREATE TABLE insurance_type (
    type_code VARCHAR(20) PRIMARY KEY,
    type_name VARCHAR(50) NOT NULL,
    category_code VARCHAR(20) REFERENCES insurance_category(category_code),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_insurance_type_category ON insurance_type(category_code);

-- 机构表
CREATE TABLE IF NOT EXISTS organization (
    org_code VARCHAR(20) PRIMARY KEY,
    org_name VARCHAR(100) NOT NULL,
    parent_code VARCHAR(20),
    level INTEGER NOT NULL,  -- 1=总公司, 2=省级分公司, 3=市级支公司
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_organization_parent ON organization(parent_code);
CREATE INDEX IF NOT EXISTS idx_organization_level ON organization(level);

-- 承保表
CREATE TABLE insurance_policy (
    id BIGSERIAL PRIMARY KEY,
    policy_no VARCHAR(50) NOT NULL,
    policy_holder VARCHAR(100),
    insured_name VARCHAR(100),
    category_code VARCHAR(20) REFERENCES insurance_category(category_code),
    type_code VARCHAR(20) REFERENCES insurance_type(type_code),
    target_no INTEGER DEFAULT 1,
    coverage_amount DECIMAL(15,2),
    premium DECIMAL(15,2),
    start_date DATE,
    end_date DATE,
    status VARCHAR(20) DEFAULT 'active',
    address VARCHAR(200),
    location GEOMETRY(POINT, 4326) NOT NULL,
    org_code VARCHAR(20) REFERENCES organization(org_code),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_insurance_policy_location ON insurance_policy USING GIST(location);
CREATE INDEX IF NOT EXISTS idx_insurance_policy_org ON insurance_policy(org_code);
CREATE INDEX idx_insurance_policy_category ON insurance_policy(category_code);
CREATE INDEX idx_insurance_policy_type ON insurance_policy(type_code);
CREATE INDEX idx_insurance_policy_status ON insurance_policy(status);

-- 风险网格表
CREATE TABLE IF NOT EXISTS risk_grid (
    id BIGSERIAL PRIMARY KEY,
    disaster_type VARCHAR(50) NOT NULL,
    risk_score DECIMAL(5,2) NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    factors JSONB,
    geometry GEOMETRY(POLYGON, 4326) NOT NULL,
    grid_size INTEGER DEFAULT 10000,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_risk_grid_geometry ON risk_grid USING GIST(geometry);
CREATE INDEX IF NOT EXISTS idx_risk_grid_type ON risk_grid(disaster_type);
CREATE INDEX IF NOT EXISTS idx_risk_grid_level ON risk_grid(risk_level);
CREATE INDEX IF NOT EXISTS idx_risk_grid_type_level ON risk_grid(disaster_type, risk_level);

-- 风险因子配置表
CREATE TABLE IF NOT EXISTS risk_factor_config (
    id BIGSERIAL PRIMARY KEY,
    disaster_type VARCHAR(50) NOT NULL,
    factor_name VARCHAR(100) NOT NULL,
    factor_weight DECIMAL(5,4) NOT NULL,
    factor_type VARCHAR(50),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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
