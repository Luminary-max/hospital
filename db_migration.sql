-- ========================================
-- 医院门诊管理系统 - 数据库升级脚本
-- 新增功能对应的数据库变更
-- ========================================

-- 1. 就诊流程状态字段说明
-- orders.o_state 扩展为:
-- 0=已挂号 1=已分诊 2=就诊中 3=已开处方/检查 4=待缴费 5=已缴费 6=已发药/检查完成 7=就诊完成

-- 2. 增强分诊表 - 增加生命体征字段
ALTER TABLE triage_record
  ADD COLUMN t_chief_complaint VARCHAR(500) DEFAULT NULL COMMENT '主诉',
  ADD COLUMN t_temperature DECIMAL(5,1) DEFAULT NULL COMMENT '体温(℃)',
  ADD COLUMN t_blood_pressure VARCHAR(20) DEFAULT NULL COMMENT '血压(mmHg)',
  ADD COLUMN t_heart_rate INT DEFAULT NULL COMMENT '心率(次/分)',
  ADD COLUMN t_weight DECIMAL(5,1) DEFAULT NULL COMMENT '体重(kg)',
  ADD COLUMN t_source VARCHAR(20) DEFAULT '现场' COMMENT '来源:现场/预约/转诊';

-- 3. 检查开单表 - 医生开的检查单
CREATE TABLE IF NOT EXISTS order_check (
  oc_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  o_id INT NOT NULL COMMENT '订单ID',
  ch_id VARCHAR(50) NOT NULL COMMENT '检查项目ID',
  ch_name VARCHAR(200) DEFAULT NULL COMMENT '检查项目名称',
  ch_price DECIMAL(10,2) DEFAULT 0.00 COMMENT '价格',
  oc_status TINYINT DEFAULT 0 COMMENT '状态:0=未缴费 1=待检查 2=已完成 3=异常',
  oc_result TEXT DEFAULT NULL COMMENT '检查结果',
  oc_attachment VARCHAR(500) DEFAULT NULL COMMENT '附件路径',
  oc_result_time DATETIME DEFAULT NULL COMMENT '结果录入时间',
  oc_operator VARCHAR(50) DEFAULT NULL COMMENT '操作人',
  oc_create_time DATETIME DEFAULT NULL COMMENT '创建时间',
  oc_note VARCHAR(500) DEFAULT NULL COMMENT '备注',
  INDEX idx_oc_order (o_id),
  INDEX idx_oc_status (oc_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检查开单表';

-- 4. 药品分类管理表
CREATE TABLE IF NOT EXISTS drug_category (
  dc_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  dc_name VARCHAR(100) NOT NULL COMMENT '分类名称',
  dc_parent_id INT DEFAULT 0 COMMENT '父分类ID',
  dc_code VARCHAR(50) DEFAULT NULL COMMENT '分类编码',
  dc_note VARCHAR(200) DEFAULT NULL COMMENT '备注说明',
  dc_sort INT DEFAULT 0 COMMENT '排序号',
  INDEX idx_dc_parent (dc_parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药品分类表';

-- 插入默认药品分类
INSERT INTO drug_category (dc_name, dc_parent_id, dc_code, dc_note, dc_sort) VALUES
('西药', 0, 'WM', '化学药品/西药', 1),
('中成药', 0, 'CP', '中成药', 2),
('中药饮片', 0, 'TCM', '中药饮片', 3),
('中药颗粒', 0, 'TCMG', '中药配方颗粒', 4),
('处方药', 0, 'RX', '处方药', 5),
('非处方药', 0, 'OTC', '非处方药(OTC)', 6),
('抗生素', 0, 'ANT', '抗生素类', 7),
('精神/麻醉药品', 0, 'PSY', '精神类/麻醉类特殊药品', 8),
('外用药', 0, 'EXT', '外用药品', 9),
('注射剂', 0, 'INJ', '注射剂', 10),
('检验耗材', 0, 'SUP', '检验耗材/医疗耗材', 11);

-- 5. 药品调价记录表
CREATE TABLE IF NOT EXISTS drug_price_log (
  dpl_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  dr_id VARCHAR(50) NOT NULL COMMENT '药品ID',
  old_price DECIMAL(10,2) NOT NULL COMMENT '原价',
  new_price DECIMAL(10,2) NOT NULL COMMENT '新价',
  change_reason VARCHAR(200) DEFAULT NULL COMMENT '调价原因',
  operator VARCHAR(50) DEFAULT NULL COMMENT '操作人',
  create_time DATETIME DEFAULT NULL COMMENT '调价时间',
  INDEX idx_dpl_drug (dr_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药品调价记录表';

-- 6. 发药复核 - 扩展状态 (字段已存在, 仅修改状态定义)
-- pd_status: 0=待发药 1=待复核 2=已发药 3=已退药

-- 7. 药品图片字段 (可选)
ALTER TABLE drug
  ADD COLUMN IF NOT EXISTS dr_image VARCHAR(500) DEFAULT NULL COMMENT '药品图片';

-- 8. 药品扩展字段 - 细分类/处方属性/医保类别
ALTER TABLE drug
  ADD COLUMN IF NOT EXISTS dr_subtype VARCHAR(50) DEFAULT NULL COMMENT '药品细分类',
  ADD COLUMN IF NOT EXISTS dr_rx_type VARCHAR(20) DEFAULT NULL COMMENT '处方属性:处方药/非处方药',
  ADD COLUMN IF NOT EXISTS dr_insurance_type VARCHAR(20) DEFAULT NULL COMMENT '医保类别:医保甲类/医保乙类/自费';

-- 更新已有药品的默认值（西药默认处方药+自费，中药默认非处方药+医保乙类）
UPDATE drug SET dr_rx_type='处方药', dr_insurance_type='自费' WHERE dr_rx_type IS NULL AND dr_type=1;
UPDATE drug SET dr_rx_type='非处方药', dr_insurance_type='医保乙类' WHERE dr_rx_type IS NULL AND dr_type=2;
UPDATE drug SET dr_subtype='解热镇痛药' WHERE dr_subtype IS NULL AND dr_name LIKE '%对乙酰氨基酚%';
UPDATE drug SET dr_subtype='抗感染药' WHERE dr_subtype IS NULL AND dr_name LIKE '%阿莫西林%';
UPDATE drug SET dr_subtype='解热镇痛药' WHERE dr_subtype IS NULL AND dr_name LIKE '%布洛芬%';
UPDATE drug SET dr_subtype='抗感染药' WHERE dr_subtype IS NULL AND dr_name LIKE '%罗红霉素%' OR dr_name LIKE '%青霉素%' OR dr_name LIKE '%头孢%' OR dr_name LIKE '%利巴韦林%';
UPDATE drug SET dr_subtype='中成药' WHERE dr_subtype IS NULL AND dr_type=2;
UPDATE drug SET dr_subtype='心血管药' WHERE dr_subtype IS NULL AND dr_name LIKE '%硝酸%' OR dr_name LIKE '%地塞米松%';
UPDATE drug SET dr_subtype='内分泌药' WHERE dr_subtype IS NULL AND dr_name LIKE '%胰岛素%' OR dr_name LIKE '%格列本脲%' OR dr_name LIKE '%罗格列酮%';
UPDATE drug SET dr_subtype='解热镇痛药' WHERE dr_subtype IS NULL AND dr_name LIKE '%维生素C%' OR dr_name LIKE '%阿司匹林%';
UPDATE drug SET dr_subtype='消化系统药' WHERE dr_subtype IS NULL AND dr_name LIKE '%蒙脱石散%' OR dr_name LIKE '%多潘立酮%' OR dr_name LIKE '%奥美拉唑%';
UPDATE drug SET dr_subtype='抗感染药' WHERE dr_subtype IS NULL AND dr_name LIKE '%甲硝唑%';
UPDATE drug SET dr_subtype='内分泌药' WHERE dr_subtype IS NULL AND dr_name LIKE '%氯雷他定%';
UPDATE drug SET dr_subtype='中成药' WHERE dr_subtype IS NULL AND dr_name LIKE '%复方丹参%';
UPDATE drug SET dr_subtype='外用药' WHERE dr_subtype IS NULL AND dr_name LIKE '%酮洛芬%';
UPDATE drug SET dr_subtype='外用药' WHERE dr_subtype IS NULL AND dr_name LIKE '%曲马多%';
UPDATE drug SET dr_subtype='呼吸系统药' WHERE dr_subtype IS NULL AND dr_name LIKE '%氨茶碱%';

-- 8. 发票管理表
CREATE TABLE IF NOT EXISTS invoice_record (
  inv_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  inv_no VARCHAR(50) NOT NULL UNIQUE COMMENT '发票号',
  o_id INT NOT NULL COMMENT '订单ID',
  inv_type VARCHAR(20) DEFAULT '电子' COMMENT '发票类型:电子/纸质',
  inv_amount DECIMAL(10,2) NOT NULL COMMENT '发票金额',
  inv_status TINYINT DEFAULT 0 COMMENT '状态:0=正常 1=已作废 2=已红冲',
  inv_operator VARCHAR(50) DEFAULT NULL COMMENT '开票人',
  inv_create_time DATETIME DEFAULT NULL COMMENT '开票时间',
  inv_cancel_time DATETIME DEFAULT NULL COMMENT '作废时间',
  inv_cancel_reason VARCHAR(200) DEFAULT NULL COMMENT '作废原因',
  INDEX idx_inv_order (o_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发票管理表';

-- 9. 退费申请表
CREATE TABLE IF NOT EXISTS refund_request (
  rf_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  o_id INT NOT NULL COMMENT '订单ID',
  br_id INT DEFAULT NULL COMMENT '收费记录ID',
  rf_amount DECIMAL(10,2) NOT NULL COMMENT '退费金额',
  rf_reason VARCHAR(500) DEFAULT NULL COMMENT '退费原因',
  rf_status TINYINT DEFAULT 0 COMMENT '状态:0=待审核 1=已通过 2=已拒绝',
  rf_requester VARCHAR(50) DEFAULT NULL COMMENT '申请人',
  rf_approver VARCHAR(50) DEFAULT NULL COMMENT '审核人',
  rf_approve_time DATETIME DEFAULT NULL COMMENT '审核时间',
  rf_create_time DATETIME DEFAULT NULL COMMENT '申请时间',
  rf_note VARCHAR(500) DEFAULT NULL COMMENT '备注',
  INDEX idx_rf_order (o_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退费申请表';
