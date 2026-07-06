-- Database optimization and smart hospital extension.
-- Run after hospital_complete.sql:
--   mysql -u root -p hospital < db_optimization_ai_extension.sql

DELIMITER $$
DROP PROCEDURE IF EXISTS add_column_if_absent$$
CREATE PROCEDURE add_column_if_absent(IN p_table varchar(64), IN p_column varchar(64), IN p_def varchar(500))
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = p_table AND column_name = p_column
  ) THEN
    SET @ddl = CONCAT('ALTER TABLE `', p_table, '` ADD COLUMN `', p_column, '` ', p_def);
    PREPARE stmt FROM @ddl;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;
END$$
DELIMITER ;

CALL add_column_if_absent('patient', 'p_blacklisted', 'int DEFAULT 0 COMMENT ''黑名单:0否1是''');
CALL add_column_if_absent('patient', 'p_tags', 'varchar(255) DEFAULT NULL COMMENT ''患者标签''');

DROP PROCEDURE add_column_if_absent;

CREATE TABLE IF NOT EXISTS `admin_role_permission` (
  `rp_id` int NOT NULL AUTO_INCREMENT,
  `a_id` char(6) NOT NULL COMMENT '管理员ID',
  `role_code` varchar(30) NOT NULL COMMENT '角色编码',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `permissions` text COMMENT '权限编码JSON/逗号串',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`rp_id`),
  UNIQUE KEY `uk_admin_role` (`a_id`,`role_code`),
  KEY `idx_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='管理员分角色权限';

CREATE TABLE IF NOT EXISTS `staff_user` (
  `staff_id` varchar(20) NOT NULL COMMENT '职工账号',
  `staff_password` varchar(255) NOT NULL COMMENT '登录密码，支持明文或系统MD5',
  `staff_name` varchar(50) NOT NULL,
  `role_code` varchar(30) NOT NULL COMMENT 'nurse/pharmacist/cashier',
  `role_name` varchar(50) NOT NULL,
  `staff_phone` varchar(20) DEFAULT NULL,
  `staff_state` int DEFAULT 1,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`staff_id`),
  KEY `idx_staff_role_state` (`role_code`,`staff_state`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='护士、药师、收费员等平级职工账号';

CREATE TABLE IF NOT EXISTS `health_profile` (
  `hp_id` int NOT NULL AUTO_INCREMENT,
  `p_id` int NOT NULL,
  `chronic_history` text,
  `family_history` text,
  `allergy_history` text,
  `summary` text,
  `risk_level` varchar(20) DEFAULT '低',
  `last_visit_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`hp_id`),
  UNIQUE KEY `uk_hp_patient` (`p_id`),
  KEY `idx_hp_risk` (`risk_level`),
  CONSTRAINT `fk_hp_patient` FOREIGN KEY (`p_id`) REFERENCES `patient` (`p_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='患者长期健康档案';

CREATE TABLE IF NOT EXISTS `hospital_announcement` (
  `ha_id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(120) NOT NULL,
  `content` text NOT NULL,
  `target_role` varchar(20) DEFAULT 'all',
  `status` int DEFAULT 1,
  `publisher` varchar(50) DEFAULT NULL,
  `publish_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ha_id`),
  KEY `idx_ha_role_status_time` (`target_role`,`status`,`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='医院公告';

CREATE TABLE IF NOT EXISTS `ai_diagnosis_record` (
  `adr_id` int NOT NULL AUTO_INCREMENT,
  `p_id` int DEFAULT NULL,
  `symptoms` text NOT NULL,
  `history_context` text,
  `suggestion` text,
  `risk_level` varchar(20) DEFAULT NULL,
  `model_name` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`adr_id`),
  KEY `idx_adr_patient_time` (`p_id`,`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='AI辅助诊断记录';

CREATE TABLE IF NOT EXISTS `ai_report_analysis` (
  `ara_id` int NOT NULL AUTO_INCREMENT,
  `oc_id` int DEFAULT NULL,
  `p_id` int DEFAULT NULL,
  `raw_result` text,
  `abnormal_flags` text,
  `summary` text,
  `risk_level` varchar(20) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ara_id`),
  KEY `idx_ara_oc` (`oc_id`),
  KEY `idx_ara_patient_time` (`p_id`,`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='检查报告AI解析';

CREATE TABLE IF NOT EXISTS `referral_record` (
  `rr_id` int NOT NULL AUTO_INCREMENT,
  `o_id` int DEFAULT NULL,
  `p_id` int NOT NULL,
  `from_dept` varchar(80) DEFAULT NULL,
  `to_hospital` varchar(120) DEFAULT NULL,
  `to_dept` varchar(80) DEFAULT NULL,
  `reason` text,
  `status` int DEFAULT 0 COMMENT '0待转诊1已转诊2已关闭',
  `operator` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`rr_id`),
  KEY `idx_rr_patient_status` (`p_id`,`status`),
  KEY `idx_rr_order` (`o_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='转诊记录';

CREATE TABLE IF NOT EXISTS `insurance_settlement` (
  `is_id` int NOT NULL AUTO_INCREMENT,
  `o_id` int DEFAULT NULL,
  `p_id` int NOT NULL,
  `insurance_no` varchar(50) DEFAULT NULL,
  `total_amount` decimal(10,2) NOT NULL DEFAULT 0,
  `reimburse_ratio` decimal(5,2) NOT NULL DEFAULT 0,
  `reimburse_amount` decimal(10,2) NOT NULL DEFAULT 0,
  `self_pay_amount` decimal(10,2) NOT NULL DEFAULT 0,
  `status` int DEFAULT 0,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`is_id`),
  KEY `idx_ins_order` (`o_id`),
  KEY `idx_ins_patient_time` (`p_id`,`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='医保模拟结算';

INSERT INTO `admin_role_permission` (`a_id`,`role_code`,`role_name`,`permissions`)
VALUES
  ('202601','pharmacist','药师','pharmacy,health_profile,report,prescription_review'),
  ('202601','nurse','护士','triage,queue'),
  ('202601','cashier','收费员','cashier,insurance')
ON DUPLICATE KEY UPDATE role_name=VALUES(role_name), permissions=VALUES(permissions), update_time=NOW();

INSERT INTO `staff_user` (`staff_id`,`staff_password`,`staff_name`,`role_code`,`role_name`,`staff_phone`,`staff_state`)
VALUES
  ('N26001','123456','分诊护士','nurse','护士','13600010001',1),
  ('P26001','123456','药房药师','pharmacist','药师','13600010002',1),
  ('C26001','123456','门诊收费员','cashier','收费员','13600010003',1)
ON DUPLICATE KEY UPDATE staff_name=VALUES(staff_name), role_code=VALUES(role_code), role_name=VALUES(role_name), staff_state=VALUES(staff_state);

INSERT INTO `hospital_announcement` (`title`,`content`,`target_role`,`status`,`publisher`)
SELECT '智慧门诊功能上线','AI辅诊、报告解析、转诊记录和医保试算已开放，请以医生面诊结论为准。','all',1,'系统管理员'
WHERE NOT EXISTS (SELECT 1 FROM `hospital_announcement` WHERE `title`='智慧门诊功能上线');

-- Seed data for tables that are empty in the original dump, so every table has usable demo rows.
INSERT INTO `prescribe` (`dr_id`,`o_id`,`dosage`)
VALUES
  ('D00003',2237,14.00),
  ('D00016',2237,3.00),
  ('D00029',2490,56.00)
ON DUPLICATE KEY UPDATE o_id=VALUES(o_id), dosage=VALUES(dosage);

INSERT INTO `health_profile` (`p_id`,`chronic_history`,`family_history`,`allergy_history`,`summary`,`risk_level`,`last_visit_time`)
VALUES
  (1001,'高血压病史5年，规律随访。','父亲有高血压史。','无明确药物过敏史。','近期就诊以皮疹、颈肩痛为主，建议持续管理血压并记录复诊情况。','中','2026-07-05 12:00:00'),
  (1002,'反复泌尿系统感染史，近期血压偏高。','母亲有糖尿病史。','青霉素过敏。','需关注感染复发和血压控制，避免自行停药。','中','2026-07-05 11:46:35'),
  (1003,'腰椎间盘突出，偶有下肢放射痛。','无特殊家族病史。','无明确药物过敏史。','建议康复锻炼、避免重体力劳动，必要时复查影像。','低','2026-06-22 10:20:00')
ON DUPLICATE KEY UPDATE
  chronic_history=VALUES(chronic_history),
  family_history=VALUES(family_history),
  allergy_history=VALUES(allergy_history),
  summary=VALUES(summary),
  risk_level=VALUES(risk_level),
  last_visit_time=VALUES(last_visit_time);

INSERT INTO `ai_diagnosis_record` (`p_id`,`symptoms`,`history_context`,`suggestion`,`risk_level`,`model_name`,`create_time`)
SELECT 1002,'尿频、尿急、尿痛2天，伴轻度腰酸。','既往有泌尿系统感染史，青霉素过敏。','考虑急性下尿路感染可能，建议完善尿常规和尿培养；如出现高热、腰痛加重需及时复诊。','中','deepseek-chat','2026-07-05 11:50:00'
WHERE NOT EXISTS (
  SELECT 1 FROM `ai_diagnosis_record`
  WHERE `p_id`=1002 AND `symptoms`='尿频、尿急、尿痛2天，伴轻度腰酸。'
);

INSERT INTO `ai_report_analysis` (`oc_id`,`p_id`,`raw_result`,`abnormal_flags`,`summary`,`risk_level`,`create_time`)
SELECT 102,1002,'血常规：白细胞 12.1×10^9/L，中性粒细胞比例 82%。','白细胞升高；中性粒细胞比例升高','提示感染或炎症反应可能，建议结合症状和医生诊断判断是否需要抗感染治疗。','中','2026-07-04 10:05:00'
WHERE NOT EXISTS (
  SELECT 1 FROM `ai_report_analysis`
  WHERE `oc_id`=102 AND `p_id`=1002
);

INSERT INTO `referral_record` (`o_id`,`p_id`,`from_dept`,`to_hospital`,`to_dept`,`reason`,`status`,`operator`,`create_time`)
SELECT 2490,1002,'呼吸与危重症医学科','市人民医院','神经内科','突发口齿不清、右侧肢体无力，建议上级医院进一步评估。',0,'王芳','2026-07-05 09:00:00'
WHERE NOT EXISTS (
  SELECT 1 FROM `referral_record`
  WHERE `o_id`=2490 AND `p_id`=1002
);

INSERT INTO `insurance_settlement` (`o_id`,`p_id`,`insurance_no`,`total_amount`,`reimburse_ratio`,`reimburse_amount`,`self_pay_amount`,`status`,`create_time`)
SELECT 21002,1002,'YB420101198808082345',2460.00,0.70,1722.00,738.00,1,'2026-06-22 10:00:00'
WHERE NOT EXISTS (
  SELECT 1 FROM `insurance_settlement`
  WHERE `o_id`=21002 AND `p_id`=1002
);

-- Query path optimization based on current business flows.
DELIMITER $$
DROP PROCEDURE IF EXISTS add_index_if_absent$$
CREATE PROCEDURE add_index_if_absent(IN p_table varchar(64), IN p_index varchar(64), IN p_cols varchar(500))
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = DATABASE() AND table_name = p_table
  ) AND NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = p_table AND index_name = p_index
  ) THEN
    SET @ddl = CONCAT('ALTER TABLE `', p_table, '` ADD INDEX `', p_index, '` ', p_cols);
    PREPARE stmt FROM @ddl;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;
END$$
DELIMITER ;

CALL add_index_if_absent('orders', 'idx_orders_patient_time', '(`p_id`,`o_start`)');
CALL add_index_if_absent('orders', 'idx_orders_doctor_state_time', '(`d_id`,`o_state`,`o_start`)');
CALL add_index_if_absent('orders', 'idx_orders_price_state', '(`o_price_state`)');
CALL add_index_if_absent('queue_number', 'idx_queue_order_state_time', '(`o_id`,`q_state`,`q_create_time`)');
CALL add_index_if_absent('queue_number', 'idx_queue_state_time', '(`q_state`,`q_create_time`)');
CALL add_index_if_absent('outpatient_emr', 'idx_emr_patient_time', '(`p_id`,`create_time`)');
CALL add_index_if_absent('outpatient_emr', 'idx_emr_order', '(`o_id`)');
CALL add_index_if_absent('prescription_detail', 'idx_prescription_detail_pm_drug', '(`pm_id`,`dr_id`)');
CALL add_index_if_absent('order_check', 'idx_order_check_emr_status', '(`emr_id`,`oc_status`)');
CALL add_index_if_absent('drug', 'idx_drug_name_type', '(`dr_name`,`dr_type`)');
CALL add_index_if_absent('billing_record', 'idx_billing_emr_time', '(`emr_id`,`br_pay_time`)');

DROP PROCEDURE add_index_if_absent;
