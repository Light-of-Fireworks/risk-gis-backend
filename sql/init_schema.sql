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

INSERT INTO roles (name, description) VALUES
('ROLE_ADMIN', '管理员'),
('ROLE_USER', '普通用户'),
('ROLE_ANALYST', '分析师');

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

-- 险类种子数据
INSERT INTO insurance_category (category_code, category_name) VALUES
('property', '财产险'),
('vehicle', '车险'),
('life', '人寿险'),
('health', '健康险'),
('liability', '责任险'),
('agriculture', '农业险');

-- 险种种子数据
INSERT INTO insurance_type (type_code, type_name, category_code) VALUES
('fire', '火灾险', 'property'),
('flood', '洪水险', 'property'),
('earthquake', '地震险', 'property'),
('explosion', '爆炸险', 'property'),
('compulsory', '交强险', 'vehicle'),
('commercial', '商业车险', 'vehicle'),
('third_party', '第三者责任险', 'vehicle'),
('term', '定期寿险', 'life'),
('whole', '终身寿险', 'life'),
('annuity', '年金险', 'life'),
('medical', '医疗险', 'health'),
('critical', '重疾险', 'health'),
('accident', '意外险', 'health'),
('product', '产品责任险', 'liability'),
('employer', '雇主责任险', 'liability'),
('professional', '职业责任险', 'liability'),
('crop', '种植险', 'agriculture'),
('livestock', '养殖险', 'agriculture'),
('forest', '森林险', 'agriculture');

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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_insurance_policy_location ON insurance_policy USING GIST(location);
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

-- 风险因子配置种子数据
INSERT INTO risk_factor_config (disaster_type, factor_name, factor_weight, factor_type, description) VALUES
-- 水灾/洪水
('FLOOD', '年降雨量', 0.2500, 'continuous', '年均降雨量(mm)'),
('FLOOD', '地形高程', 0.2500, 'continuous', '平均海拔高度(m)'),
('FLOOD', '河流距离', 0.2000, 'continuous', '距最近主要河流距离(km)'),
('FLOOD', '排水能力', 0.1500, 'discrete', '排水系统完善程度'),
('FLOOD', '历史洪灾频次', 0.1500, 'continuous', '近30年洪灾发生次数'),
('FLOOD_RIVER', '年降雨量', 0.2500, 'continuous', '年均降雨量(mm)'),
('FLOOD_RIVER', '地形高程', 0.2500, 'continuous', '平均海拔高度(m)'),
('FLOOD_RIVER', '河流距离', 0.2000, 'continuous', '距最近主要河流距离(km)'),
('FLOOD_RIVER', '排水能力', 0.1500, 'discrete', '排水系统完善程度'),
('FLOOD_RIVER', '历史洪灾频次', 0.1500, 'continuous', '近30年洪灾发生次数'),
-- 暴雨
('HEAVY_RAIN', '年暴雨日数', 0.3500, 'continuous', '年暴雨日数(日降雨量≥50mm)'),
('HEAVY_RAIN', '地形坡度', 0.2500, 'continuous', '平均地形坡度(度)'),
('HEAVY_RAIN', '排水能力', 0.2000, 'discrete', '排水系统完善程度'),
('HEAVY_RAIN', '城市化率', 0.2000, 'continuous', '城市化率(%)'),
-- 雪灾
('SNOWSTORM', '年降雪量', 0.3000, 'continuous', '年均降雪量(mm)'),
('SNOWSTORM', '极端低温日数', 0.2500, 'continuous', '年极端低温日数(日最低温≤-10℃)'),
('SNOWSTORM', '海拔', 0.2500, 'continuous', '平均海拔高度(m)'),
('SNOWSTORM', '纬度', 0.2000, 'continuous', '中心纬度(度)'),
-- 冰雹
('HAIL', '历史冰雹频次', 0.4000, 'continuous', '近30年冰雹发生次数'),
('HAIL', '地形高度', 0.3000, 'continuous', '平均海拔高度(m)'),
('HAIL', '季节因素', 0.3000, 'discrete', '冰雹季节风险系数'),
-- 雷电
('LIGHTNING', '年雷暴日数', 0.4000, 'continuous', '年均雷暴日数'),
('LIGHTNING', '地形高度', 0.3000, 'continuous', '平均海拔高度(m)'),
('LIGHTNING', '纬度', 0.3000, 'continuous', '中心纬度(度)'),
-- 地震
('EARTHQUAKE', '地震烈度区划', 0.4000, 'discrete', '中国地震烈度区划值'),
('EARTHQUAKE', '距活动断层距离', 0.3500, 'continuous', '距最近活动断层距离(km)'),
('EARTHQUAKE', '历史地震频次', 0.2500, 'continuous', '近50年4级以上地震次数'),
-- 台风
('TYPHOON', '距海岸距离', 0.3500, 'continuous', '距海岸线距离(km)'),
('TYPHOON', '历史台风频次', 0.3500, 'continuous', '近30年台风登陆次数'),
('TYPHOON', '地形遮蔽', 0.3000, 'discrete', '地形对台风的遮蔽程度'),
-- 风暴潮
('STORM_SURGE', '距海岸距离', 0.3500, 'continuous', '距海岸线距离(km)'),
('STORM_SURGE', '海拔', 0.3000, 'continuous', '平均海拔高度(m)'),
('STORM_SURGE', '历史风暴潮频次', 0.3500, 'continuous', '近30年风暴潮发生次数'),
-- 滑坡-泥石流
('LANDSLIDE', '地形坡度', 0.3000, 'continuous', '平均地形坡度(度)'),
('LANDSLIDE', '地质岩性', 0.2500, 'discrete', '地质岩性易滑程度'),
('LANDSLIDE', '降雨量', 0.2500, 'continuous', '年均降雨量(mm)'),
('LANDSLIDE', '植被覆盖', 0.2000, 'continuous', '植被覆盖率(%)');
