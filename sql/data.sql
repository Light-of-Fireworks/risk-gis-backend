-- 创建默认管理员用户（密码: admin123）
-- 使用 ON CONFLICT 避免重复插入
-- 密码哈希由 BCryptPasswordEncoder 生成
INSERT INTO users (username, password, email, enabled, deleted)
VALUES ('admin', '$2a$10$OxXsXHzP.USZOhg/B0wU7e9Nb4iMYVE.RxuyUI9k1PYG4BdPfj0CW', 'admin@riskgis.com', true, false)
ON CONFLICT (username) DO UPDATE SET password = '$2a$10$OxXsXHzP.USZOhg/B0wU7e9Nb4iMYVE.RxuyUI9k1PYG4BdPfj0CW';

-- 为管理员分配角色（如果尚未分配）
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN'
AND NOT EXISTS (
    SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id
);

-- 深茂商业中心附近保单测试数据（坐标：114.043103, 22.541129）
-- 位置1：深茂商业中心（同一位置3条保单，测试分组）
INSERT INTO insurance_policy (policy_no, policy_holder, insured_name, category_code, type_code, target_no, coverage_amount, premium, start_date, end_date, status, address, location)
VALUES
('P20240001', '深圳市深茂物业有限公司', '深茂商业中心A栋', 'property', 'fire', 1, 5000000.00, 25000.00, '2024-01-01', '2025-01-01', 'active', '深圳市深茂商业中心A栋', ST_SetSRID(ST_MakePoint(114.043103, 22.541129), 4326)),
('P20240002', '深圳市深茂物业有限公司', '深茂商业中心B栋', 'property', 'fire', 2, 3000000.00, 15000.00, '2024-01-01', '2025-01-01', 'active', '深圳市深茂商业中心B栋', ST_SetSRID(ST_MakePoint(114.043103, 22.541129), 4326)),
('P20240003', '深圳市深茂物业有限公司', '深茂商业中心停车场', 'property', 'flood', 1, 2000000.00, 10000.00, '2024-03-01', '2025-03-01', 'active', '深圳市深茂商业中心停车场', ST_SetSRID(ST_MakePoint(114.043103, 22.541129), 4326));

-- 位置2：距离约500米（东侧）
INSERT INTO insurance_policy (policy_no, policy_holder, insured_name, category_code, type_code, target_no, coverage_amount, premium, start_date, end_date, status, address, location)
VALUES
('P20240004', '张三', '张三店铺', 'property', 'fire', 1, 800000.00, 4000.00, '2024-02-15', '2025-02-15', 'active', '深圳市福田区福华三路88号', ST_SetSRID(ST_MakePoint(114.043603, 22.541129), 4326)),
('P20240005', '李四', '李四商铺', 'property', 'explosion', 1, 600000.00, 3000.00, '2024-04-01', '2025-04-01', 'active', '深圳市福田区福华三路90号', ST_SetSRID(ST_MakePoint(114.043603, 22.541129), 4326));

-- 位置3：距离约1公里（北侧）
INSERT INTO insurance_policy (policy_no, policy_holder, insured_name, category_code, type_code, target_no, coverage_amount, premium, start_date, end_date, status, address, location)
VALUES
('P20240006', '王五', '王五工厂', 'property', 'fire', 1, 10000000.00, 50000.00, '2024-01-01', '2025-01-01', 'active', '深圳市福田区深南大道100号', ST_SetSRID(ST_MakePoint(114.043103, 22.542129), 4326)),
('P20240007', '王五', '王五仓库', 'property', 'flood', 1, 5000000.00, 25000.00, '2024-02-01', '2025-02-01', 'active', '深圳市福田区深南大道102号', ST_SetSRID(ST_MakePoint(114.043103, 22.542129), 4326));

-- 位置4：距离约2公里（西侧）
INSERT INTO insurance_policy (policy_no, policy_holder, insured_name, category_code, type_code, target_no, coverage_amount, premium, start_date, end_date, status, address, location)
VALUES
('P20240008', '赵六', '赵六公司', 'liability', 'product', 1, 2000000.00, 12000.00, '2024-03-01', '2025-03-01', 'active', '深圳市福田区滨河大道200号', ST_SetSRID(ST_MakePoint(114.041103, 22.541129), 4326)),
('P20240009', '赵六', '赵六仓库', 'property', 'fire', 1, 3000000.00, 15000.00, '2024-04-01', '2025-04-01', 'active', '深圳市福田区滨河大道202号', ST_SetSRID(ST_MakePoint(114.041103, 22.541129), 4326));

-- 位置5：距离约3公里（南侧）
INSERT INTO insurance_policy (policy_no, policy_holder, insured_name, category_code, type_code, target_no, coverage_amount, premium, start_date, end_date, status, address, location)
VALUES
('P20240010', '孙七', '孙七商场', 'property', 'fire', 1, 15000000.00, 75000.00, '2024-01-15', '2025-01-15', 'active', '深圳市福田区福强路300号', ST_SetSRID(ST_MakePoint(114.043103, 22.538129), 4326)),
('P20240011', '孙七', '孙七仓库', 'property', 'flood', 1, 8000000.00, 40000.00, '2024-02-15', '2025-02-15', 'active', '深圳市福田区福强路302号', ST_SetSRID(ST_MakePoint(114.043103, 22.538129), 4326)),
('P20240012', '孙七', '孙七办公楼', 'property', 'earthquake', 1, 20000000.00, 100000.00, '2024-03-15', '2025-03-15', 'active', '深圳市福田区福强路304号', ST_SetSRID(ST_MakePoint(114.043103, 22.538129), 4326));

-- 位置6：距离约5公里（超出默认查询范围，用于测试边界）
INSERT INTO insurance_policy (policy_no, policy_holder, insured_name, category_code, type_code, target_no, coverage_amount, premium, start_date, end_date, status, address, location)
VALUES
('P20240013', '周八', '周八工厂', 'property', 'fire', 1, 6000000.00, 30000.00, '2024-01-01', '2025-01-01', 'active', '深圳市南山区科技园路500号', ST_SetSRID(ST_MakePoint(114.043103, 22.546129), 4326));

-- 车险保单（测试不同险类筛选）
INSERT INTO insurance_policy (policy_no, policy_holder, insured_name, category_code, type_code, target_no, coverage_amount, premium, start_date, end_date, status, address, location)
VALUES
('P20240014', '吴九', '粤B12345', 'vehicle', 'compulsory', 1, 122000.00, 950.00, '2024-06-01', '2025-06-01', 'active', '深圳市深茂商业中心停车场', ST_SetSRID(ST_MakePoint(114.043200, 22.541200), 4326)),
('P20240015', '吴九', '粤B12345', 'vehicle', 'commercial', 1, 500000.00, 3500.00, '2024-06-01', '2025-06-01', 'active', '深圳市深茂商业中心停车场', ST_SetSRID(ST_MakePoint(114.043200, 22.541200), 4326));

-- 健康险保单（测试不同险类筛选）
INSERT INTO insurance_policy (policy_no, policy_holder, insured_name, category_code, type_code, target_no, coverage_amount, premium, start_date, end_date, status, address, location)
VALUES
('P20240016', '郑十', '郑十', 'health', 'medical', 1, 1000000.00, 800.00, '2024-01-01', '2025-01-01', 'active', '深圳市深茂商业中心A栋', ST_SetSRID(ST_MakePoint(114.043103, 22.541129), 4326));
