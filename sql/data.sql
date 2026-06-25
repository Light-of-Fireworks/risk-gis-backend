-- 角色种子数据
INSERT INTO roles (name, description) VALUES
('ROLE_ADMIN', '管理员'),
('ROLE_USER', '普通用户'),
('ROLE_ANALYST', '分析师')
ON CONFLICT (name) DO NOTHING;

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

-- 险类种子数据
INSERT INTO insurance_category (category_code, category_name) VALUES
('property', '财产险'),
('vehicle', '车险'),
('life', '人寿险'),
('health', '健康险'),
('liability', '责任险'),
('agriculture', '农业险')
ON CONFLICT (category_code) DO NOTHING;

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
('forest', '森林险', 'agriculture')
ON CONFLICT (type_code) DO NOTHING;

-- 机构种子数据
-- 总公司
INSERT INTO organization (org_code, org_name, parent_code, level) VALUES
('HEAD', '总公司', NULL, 1)
ON CONFLICT (org_code) DO NOTHING;

-- 省级分公司
INSERT INTO organization (org_code, org_name, parent_code, level) VALUES
('1100', '北京市分公司', 'HEAD', 2),
('1200', '天津市分公司', 'HEAD', 2),
('1300', '河北省分公司', 'HEAD', 2),
('1400', '山西省分公司', 'HEAD', 2),
('1500', '内蒙古自治区分公司', 'HEAD', 2),
('2100', '辽宁省分公司', 'HEAD', 2),
('2200', '吉林省分公司', 'HEAD', 2),
('2300', '黑龙江省分公司', 'HEAD', 2),
('3100', '上海市分公司', 'HEAD', 2),
('3200', '江苏省分公司', 'HEAD', 2),
('3300', '浙江省分公司', 'HEAD', 2),
('3400', '安徽省分公司', 'HEAD', 2),
('3500', '福建省分公司', 'HEAD', 2),
('3600', '江西省分公司', 'HEAD', 2),
('3700', '山东省分公司', 'HEAD', 2),
('4100', '河南省分公司', 'HEAD', 2),
('4200', '湖北省分公司', 'HEAD', 2),
('4300', '湖南省分公司', 'HEAD', 2),
('4400', '广东省分公司', 'HEAD', 2),
('4500', '广西壮族自治区分公司', 'HEAD', 2),
('4600', '海南省分公司', 'HEAD', 2),
('5000', '重庆市分公司', 'HEAD', 2),
('5100', '四川省分公司', 'HEAD', 2),
('5200', '贵州省分公司', 'HEAD', 2),
('5300', '云南省分公司', 'HEAD', 2),
('5400', '西藏自治区分公司', 'HEAD', 2),
('6100', '陕西省分公司', 'HEAD', 2),
('6200', '甘肃省分公司', 'HEAD', 2),
('6300', '青海省分公司', 'HEAD', 2),
('6400', '宁夏回族自治区分公司', 'HEAD', 2),
('6500', '新疆维吾尔自治区分公司', 'HEAD', 2)
ON CONFLICT (org_code) DO NOTHING;

-- 市级支公司（示例：广东省部分城市）
INSERT INTO organization (org_code, org_name, parent_code, level) VALUES
('4401', '广州市支公司', '4400', 3),
('4402', '深圳市支公司', '4400', 3),
('4403', '珠海市支公司', '4400', 3),
('4404', '汕头市支公司', '4400', 3),
('4405', '佛山市支公司', '4400', 3),
('4406', '韶关市支公司', '4400', 3),
('4407', '湛江市支公司', '4400', 3),
('4408', '肇庆市支公司', '4400', 3),
('4409', '江门市支公司', '4400', 3),
('4412', '惠州市支公司', '4400', 3),
('4413', '梅州市支公司', '4400', 3),
('4414', '汕尾市支公司', '4400', 3),
('4415', '东莞市支公司', '4400', 3),
('4416', '中山市支公司', '4400', 3),
('4417', '阳江市支公司', '4400', 3),
('4418', '清远市支公司', '4400', 3),
('4419', '潮州市支公司', '4400', 3),
('4420', '揭阳市支公司', '4400', 3),
('4451', '云浮市支公司', '4400', 3),
('4452', '茂名市支公司', '4400', 3),
('4453', '河源市支公司', '4400', 3)
ON CONFLICT (org_code) DO NOTHING;

-- 省会城市支公司（其他省份示例）
INSERT INTO organization (org_code, org_name, parent_code, level) VALUES
('1101', '北京市支公司', '1100', 3),
('3101', '上海市支公司', '3100', 3),
('3201', '南京市支公司', '3200', 3),
('3301', '杭州市支公司', '3300', 3),
('3501', '福州市支公司', '3500', 3),
('3502', '厦门市支公司', '3500', 3),
('3701', '济南市支公司', '3700', 3),
('3702', '青岛市支公司', '3700', 3),
('4201', '武汉市支公司', '4200', 3),
('4301', '长沙市支公司', '4300', 3),
('5101', '成都市支公司', '5100', 3),
('5001', '重庆市支公司', '5000', 3)
ON CONFLICT (org_code) DO NOTHING;

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

-- 保单测试数据
-- 深茂商业中心附近保单测试数据（坐标：114.043103, 22.541129）
-- 位置1：深茂商业中心（同一位置3条保单，测试分组）
INSERT INTO insurance_policy (policy_no, policy_holder, insured_name, category_code, type_code, target_no, coverage_amount, premium, start_date, end_date, status, address, location, org_code)
VALUES
('P20240001', '深圳市深茂物业有限公司', '深茂商业中心A栋', 'property', 'fire', 1, 5000000.00, 25000.00, '2024-01-01', '2025-01-01', 'active', '深圳市深茂商业中心A栋', ST_SetSRID(ST_MakePoint(114.043103, 22.541129), 4326), '4403'),
('P20240002', '深圳市深茂物业有限公司', '深茂商业中心B栋', 'property', 'fire', 2, 3000000.00, 15000.00, '2024-01-01', '2025-01-01', 'active', '深圳市深茂商业中心B栋', ST_SetSRID(ST_MakePoint(114.043103, 22.541129), 4326), '4403'),
('P20240003', '深圳市深茂物业有限公司', '深茂商业中心停车场', 'property', 'flood', 1, 2000000.00, 10000.00, '2024-03-01', '2025-03-01', 'active', '深圳市深茂商业中心停车场', ST_SetSRID(ST_MakePoint(114.043103, 22.541129), 4326), '4403');

-- 位置2：距离约500米（东侧）
INSERT INTO insurance_policy (policy_no, policy_holder, insured_name, category_code, type_code, target_no, coverage_amount, premium, start_date, end_date, status, address, location, org_code)
VALUES
('P20240004', '张三', '张三店铺', 'property', 'fire', 1, 800000.00, 4000.00, '2024-02-15', '2025-02-15', 'active', '深圳市福田区福华三路88号', ST_SetSRID(ST_MakePoint(114.043603, 22.541129), 4326), '4403'),
('P20240005', '李四', '李四商铺', 'property', 'explosion', 1, 600000.00, 3000.00, '2024-04-01', '2025-04-01', 'active', '深圳市福田区福华三路90号', ST_SetSRID(ST_MakePoint(114.043603, 22.541129), 4326), '4403');

-- 位置3：距离约1公里（北侧）
INSERT INTO insurance_policy (policy_no, policy_holder, insured_name, category_code, type_code, target_no, coverage_amount, premium, start_date, end_date, status, address, location, org_code)
VALUES
('P20240006', '王五', '王五工厂', 'property', 'fire', 1, 10000000.00, 50000.00, '2024-01-01', '2025-01-01', 'active', '深圳市福田区深南大道100号', ST_SetSRID(ST_MakePoint(114.043103, 22.542129), 4326), '4403'),
('P20240007', '王五', '王五仓库', 'property', 'flood', 1, 5000000.00, 25000.00, '2024-02-01', '2025-02-01', 'active', '深圳市福田区深南大道102号', ST_SetSRID(ST_MakePoint(114.043103, 22.542129), 4326), '4403');

-- 位置4：距离约2公里（西侧）
INSERT INTO insurance_policy (policy_no, policy_holder, insured_name, category_code, type_code, target_no, coverage_amount, premium, start_date, end_date, status, address, location, org_code)
VALUES
('P20240008', '赵六', '赵六公司', 'liability', 'product', 1, 2000000.00, 12000.00, '2024-03-01', '2025-03-01', 'active', '深圳市福田区滨河大道200号', ST_SetSRID(ST_MakePoint(114.041103, 22.541129), 4326), '4403'),
('P20240009', '赵六', '赵六仓库', 'property', 'fire', 1, 3000000.00, 15000.00, '2024-04-01', '2025-04-01', 'active', '深圳市福田区滨河大道202号', ST_SetSRID(ST_MakePoint(114.041103, 22.541129), 4326), '4403');

-- 位置5：距离约3公里（南侧）
INSERT INTO insurance_policy (policy_no, policy_holder, insured_name, category_code, type_code, target_no, coverage_amount, premium, start_date, end_date, status, address, location, org_code)
VALUES
('P20240010', '孙七', '孙七商场', 'property', 'fire', 1, 15000000.00, 75000.00, '2024-01-15', '2025-01-15', 'active', '深圳市福田区福强路300号', ST_SetSRID(ST_MakePoint(114.043103, 22.538129), 4326), '4403'),
('P20240011', '孙七', '孙七仓库', 'property', 'flood', 1, 8000000.00, 40000.00, '2024-02-15', '2025-02-15', 'active', '深圳市福田区福强路302号', ST_SetSRID(ST_MakePoint(114.043103, 22.538129), 4326), '4403'),
('P20240012', '孙七', '孙七办公楼', 'property', 'earthquake', 1, 20000000.00, 100000.00, '2024-03-15', '2025-03-15', 'active', '深圳市福田区福强路304号', ST_SetSRID(ST_MakePoint(114.043103, 22.538129), 4326), '4403');

-- 位置6：距离约5公里（超出默认查询范围，用于测试边界）
INSERT INTO insurance_policy (policy_no, policy_holder, insured_name, category_code, type_code, target_no, coverage_amount, premium, start_date, end_date, status, address, location, org_code)
VALUES
('P20240013', '周八', '周八工厂', 'property', 'fire', 1, 6000000.00, 30000.00, '2024-01-01', '2025-01-01', 'active', '深圳市南山区科技园路500号', ST_SetSRID(ST_MakePoint(114.043103, 22.546129), 4326), '4403');

-- 车险保单（测试不同险类筛选）
INSERT INTO insurance_policy (policy_no, policy_holder, insured_name, category_code, type_code, target_no, coverage_amount, premium, start_date, end_date, status, address, location, org_code)
VALUES
('P20240014', '吴九', '粤B12345', 'vehicle', 'compulsory', 1, 122000.00, 950.00, '2024-06-01', '2025-06-01', 'active', '深圳市深茂商业中心停车场', ST_SetSRID(ST_MakePoint(114.043200, 22.541200), 4326), '4403'),
('P20240015', '吴九', '粤B12345', 'vehicle', 'commercial', 1, 500000.00, 3500.00, '2024-06-01', '2025-06-01', 'active', '深圳市深茂商业中心停车场', ST_SetSRID(ST_MakePoint(114.043200, 22.541200), 4326), '4403');

-- 健康险保单（测试不同险类筛选）
INSERT INTO insurance_policy (policy_no, policy_holder, insured_name, category_code, type_code, target_no, coverage_amount, premium, start_date, end_date, status, address, location, org_code)
VALUES
('P20240016', '郑十', '郑十', 'health', 'medical', 1, 1000000.00, 800.00, '2024-01-01', '2025-01-01', 'active', '深圳市深茂商业中心A栋', ST_SetSRID(ST_MakePoint(114.043103, 22.541129), 4326), '4403');

-- 台风"米克拉"(202607)路径沿线保单测试数据
-- 用于验证台风保单查询功能
-- 路径经过：马里亚纳群岛 -> 菲律宾以东 -> 琉球群岛 -> 冲绳附近

-- 关岛附近（台风起点区域）(138.80, 14.00)附近
INSERT INTO insurance_policy (policy_no, policy_holder, insured_name, category_code, type_code, target_no, coverage_amount, premium, start_date, end_date, status, address, location, org_code)
VALUES
('TYP20260001', '关岛度假酒店集团', '关岛海滨度假村', 'property', 'flood', 1, 8000000.00, 45000.00, '2026-01-01', '2027-01-01', 'active', '关岛塔穆宁区海滨大道100号', ST_SetSRID(ST_MakePoint(138.75, 14.05), 4326), '3501'),
('TYP20260002', '关岛度假酒店集团', '关岛海滨度假村B区', 'property', 'fire', 1, 5000000.00, 28000.00, '2026-01-01', '2027-01-01', 'active', '关岛塔穆宁区海滨大道102号', ST_SetSRID(ST_MakePoint(138.76, 14.04), 4326), '3501'),
('TYP20260003', '太平洋航运公司', '货轮太平洋之星', 'vehicle', 'commercial', 1, 15000000.00, 85000.00, '2026-01-01', '2027-01-01', 'active', '关岛阿加尼亚港口', ST_SetSRID(ST_MakePoint(138.72, 14.08), 4326), '3501');

-- 塞班岛附近（台风路径经过）(136.00, 14.80)附近
INSERT INTO insurance_policy (policy_no, policy_holder, insured_name, category_code, type_code, target_no, coverage_amount, premium, start_date, end_date, status, address, location, org_code)
VALUES
('TYP20260004', '塞班岛旅游公司', '塞班岛度假中心', 'property', 'flood', 1, 6000000.00, 35000.00, '2026-02-01', '2027-02-01', 'active', '塞班岛加拉班市区', ST_SetSRID(ST_MakePoint(135.95, 14.85), 4326), '3502'),
('TYP20260005', '塞班岛旅游公司', '塞班岛水上乐园', 'property', 'fire', 1, 4000000.00, 22000.00, '2026-02-01', '2027-02-01', 'active', '塞班岛微海滩', ST_SetSRID(ST_MakePoint(135.98, 14.82), 4326), '3502');

-- 帛琉附近（台风路径经过）(134.00, 14.90)附近
INSERT INTO insurance_policy (policy_no, policy_holder, insured_name, category_code, type_code, target_no, coverage_amount, premium, start_date, end_date, status, address, location, org_code)
VALUES
('TYP20260006', '帛琉海洋保护协会', '帛琉海洋研究中心', 'property', 'flood', 1, 3000000.00, 18000.00, '2026-03-01', '2027-03-01', 'active', '帛琉科罗尔州', ST_SetSRID(ST_MakePoint(134.05, 14.95), 4326), '3501'),
('TYP20260007', '帛琉渔业公司', '帛琉远洋渔船队', 'vehicle', 'commercial', 1, 12000000.00, 68000.00, '2026-03-01', '2027-03-01', 'active', '帛琉马拉卡尔港', ST_SetSRID(ST_MakePoint(134.02, 14.88), 4326), '3501');

-- 冲绳附近（台风增强区域）(131.00, 16.30)附近
INSERT INTO insurance_policy (policy_no, policy_holder, insured_name, category_code, type_code, target_no, coverage_amount, premium, start_date, end_date, status, address, location, org_code)
VALUES
('TYP20260008', '冲绳县渔业协同组合', '冲绳近海养殖渔场', 'agriculture', 'livestock', 1, 2000000.00, 12000.00, '2026-01-15', '2027-01-15', 'active', '冲绳县那霸市泊港', ST_SetSRID(ST_MakePoint(131.05, 16.35), 4326), '3501'),
('TYP20260009', '冲绳观光酒店', '冲绳海滨酒店', 'property', 'flood', 1, 9000000.00, 52000.00, '2026-01-15', '2027-01-15', 'active', '冲绳县那霸市国际通', ST_SetSRID(ST_MakePoint(131.02, 16.28), 4326), '3501');

-- 琉球群岛北部（超强台风区域）(127.60, 17.60)附近
INSERT INTO insurance_policy (policy_no, policy_holder, insured_name, category_code, type_code, target_no, coverage_amount, premium, start_date, end_date, status, address, location, org_code)
VALUES
('TYP20260010', '琉球群岛海洋牧场', '近海养殖基地', 'agriculture', 'livestock', 1, 1500000.00, 8500.00, '2026-02-01', '2027-02-01', 'active', '冲绳县久米岛', ST_SetSRID(ST_MakePoint(127.55, 17.65), 4326), '3502'),
('TYP20260011', '琉球海运集团', '集装箱货轮', 'vehicle', 'commercial', 1, 25000000.00, 145000.00, '2026-02-01', '2027-02-01', 'active', '冲绳县中城湾港', ST_SetSRID(ST_MakePoint(127.65, 17.55), 4326), '3502');

-- 冲绳本岛附近（台风经过区域）(125.40, 18.50)附近
INSERT INTO insurance_policy (policy_no, policy_holder, insured_name, category_code, type_code, target_no, coverage_amount, premium, start_date, end_date, status, address, location, org_code)
VALUES
('TYP20260012', '冲绳电力公司', '冲绳发电厂', 'property', 'fire', 1, 50000000.00, 280000.00, '2026-01-01', '2027-01-01', 'active', '冲绳县北谷町', ST_SetSRID(ST_MakePoint(125.45, 18.55), 4326), '3501'),
('TYP20260013', '冲绳电力公司', '输电线路设施', 'property', 'explosion', 1, 30000000.00, 170000.00, '2026-01-01', '2027-01-01', 'active', '冲绳县宜野湾市', ST_SetSRID(ST_MakePoint(125.38, 18.48), 4326), '3501'),
('TYP20260014', '冲绳县农业协同组合', '菠萝种植园', 'agriculture', 'crop', 1, 8000000.00, 45000.00, '2026-01-01', '2027-01-01', 'active', '冲绳县读谷村', ST_SetSRID(ST_MakePoint(125.42, 18.52), 4326), '3501');

-- 那霸附近（台风接近区域）(124.50, 21.40)附近
INSERT INTO insurance_policy (policy_no, policy_holder, insured_name, category_code, type_code, target_no, coverage_amount, premium, start_date, end_date, status, address, location, org_code)
VALUES
('TYP20260015', '那霸港口公司', '那霸国际港口', 'property', 'flood', 1, 80000000.00, 450000.00, '2026-01-01', '2027-01-01', 'active', '冲绳县那霸市', ST_SetSRID(ST_MakePoint(124.55, 21.45), 4326), '3501'),
('TYP20260016', '那霸港口公司', '港口起重机设备', 'property', 'fire', 1, 20000000.00, 115000.00, '2026-01-01', '2027-01-01', 'active', '冲绳县那霸市泊港', ST_SetSRID(ST_MakePoint(124.52, 21.42), 4326), '3501'),
('TYP20260017', '冲绳海上保安厅', '巡视船', 'vehicle', 'commercial', 1, 35000000.00, 200000.00, '2026-01-01', '2027-01-01', 'active', '冲绳县那霸市', ST_SetSRID(ST_MakePoint(124.48, 21.38), 4326), '3501');

-- 宫古岛附近（台风路径附近）(125.90, 24.40)附近
INSERT INTO insurance_policy (policy_no, policy_holder, insured_name, category_code, type_code, target_no, coverage_amount, premium, start_date, end_date, status, address, location, org_code)
VALUES
('TYP20260018', '宫古岛观光协会', '宫古岛度假酒店', 'property', 'flood', 1, 7000000.00, 40000.00, '2026-03-01', '2027-03-01', 'active', '冲绳县宫古岛市', ST_SetSRID(ST_MakePoint(125.85, 24.45), 4326), '3502'),
('TYP20260019', '宫古岛甘蔗农场', '甘蔗种植基地', 'agriculture', 'crop', 1, 4000000.00, 22000.00, '2026-03-01', '2027-03-01', 'active', '冲绳县宫古岛市', ST_SetSRID(ST_MakePoint(125.92, 24.42), 4326), '3502'),
('TYP20260020', '宫古岛渔业公司', '近海渔船', 'vehicle', 'commercial', 1, 6000000.00, 34000.00, '2026-03-01', '2027-03-01', 'active', '冲绳县宫古岛市平良港', ST_SetSRID(ST_MakePoint(125.88, 24.38), 4326), '3502');

-- 额外保单：路径中段（用于测试缓冲区覆盖）(130.00, 17.00)附近
INSERT INTO insurance_policy (policy_no, policy_holder, insured_name, category_code, type_code, target_no, coverage_amount, premium, start_date, end_date, status, address, location, org_code)
VALUES
('TYP20260021', '东海航运公司', '远洋货轮东海号', 'vehicle', 'commercial', 1, 18000000.00, 102000.00, '2026-02-15', '2027-02-15', 'active', '冲绳县系满市', ST_SetSRID(ST_MakePoint(130.05, 17.05), 4326), '3501'),
('TYP20260022', '东海航运公司', '近海运输船', 'vehicle', 'compulsory', 1, 5000000.00, 28000.00, '2026-02-15', '2027-02-15', 'active', '冲绳县糸満港', ST_SetSRID(ST_MakePoint(130.02, 16.98), 4326), '3501');

-- 额外保单：路径后段（用于测试多点覆盖）(126.50, 19.50)附近
INSERT INTO insurance_policy (policy_no, policy_holder, insured_name, category_code, type_code, target_no, coverage_amount, premium, start_date, end_date, status, address, location, org_code)
VALUES
('TYP20260023', '先岛群岛海洋牧场', '金枪鱼养殖基地', 'agriculture', 'livestock', 1, 12000000.00, 68000.00, '2026-01-01', '2027-01-01', 'active', '冲绳县石垣市', ST_SetSRID(ST_MakePoint(126.55, 19.55), 4326), '3502'),
('TYP20260024', '先岛群岛海洋牧场', '海产品加工厂', 'property', 'fire', 1, 9000000.00, 51000.00, '2026-01-01', '2027-01-01', 'active', '冲绳县石垣市', ST_SetSRID(ST_MakePoint(126.52, 19.48), 4326), '3502');
