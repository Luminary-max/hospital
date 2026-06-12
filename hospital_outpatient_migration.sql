-- =====================================================
-- 医院门诊管理系统 - 数据库迁移脚本
-- 在现有 hospital 数据库基础上新增门诊相关表和字段
-- =====================================================

-- 1. orders 表 - 新增门诊排队号码和分诊类别字段
ALTER TABLE `orders`
  ADD COLUMN `o_queue_number` varchar(20) DEFAULT NULL COMMENT '排队号码' AFTER `o_advice`,
  ADD COLUMN `o_triage` varchar(50) DEFAULT NULL COMMENT '分诊类别(普通门诊/专家门诊/急诊)' AFTER `o_queue_number`;

-- 2. bed 表 - 新增门诊观察相关字段
ALTER TABLE `bed`
  ADD COLUMN `b_type` int DEFAULT 0 COMMENT '类型:0=观察床,1=输液椅' AFTER `version`,
  ADD COLUMN `b_obs_start` datetime DEFAULT NULL COMMENT '观察开始时间' AFTER `b_type`,
  ADD COLUMN `b_obs_end` datetime DEFAULT NULL COMMENT '观察结束时间' AFTER `b_obs_start`,
  ADD COLUMN `b_obs_note` text COMMENT '观察记录' AFTER `b_obs_end`;

-- 3. 门诊电子病历表
CREATE TABLE IF NOT EXISTS `outpatient_emr` (
  `emr_id` int NOT NULL AUTO_INCREMENT,
  `o_id` int DEFAULT NULL COMMENT '关联挂号订单',
  `p_id` int DEFAULT NULL COMMENT '患者ID',
  `d_id` char(6) DEFAULT NULL COMMENT '医生ID',
  `chief_complaint` text COMMENT '主诉',
  `present_illness` text COMMENT '现病史',
  `past_history` text COMMENT '既往史',
  `physical_exam` text COMMENT '体格检查',
  `diagnosis` text COMMENT '诊断',
  `treatment_plan` text COMMENT '处理意见',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`emr_id`) USING BTREE,
  KEY `emrTOo` (`o_id`),
  KEY `emrTOp` (`p_id`),
  KEY `emrTOd` (`d_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='门诊电子病历表';

-- 4. 处方明细表
CREATE TABLE IF NOT EXISTS `prescription_detail` (
  `pd_id` int NOT NULL AUTO_INCREMENT,
  `o_id` int DEFAULT NULL COMMENT '挂号订单ID',
  `dr_id` char(6) DEFAULT NULL COMMENT '药品ID',
  `pd_usage` varchar(50) DEFAULT NULL COMMENT '用法(口服/注射/外用等)',
  `pd_dosage` varchar(50) DEFAULT NULL COMMENT '每次用量',
  `pd_frequency` varchar(50) DEFAULT NULL COMMENT '频次(qd/bid/tid/qid等)',
  `pd_days` int DEFAULT NULL COMMENT '用药天数',
  `pd_quantity` int DEFAULT NULL COMMENT '总数量',
  `pd_note` varchar(255) DEFAULT NULL COMMENT '备注',
  `pd_price` decimal(10,2) DEFAULT NULL COMMENT '单价',
  PRIMARY KEY (`pd_id`) USING BTREE,
  KEY `pdTOo` (`o_id`),
  KEY `pdTOdr` (`dr_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='处方明细表';

-- 5. 排队叫号表
CREATE TABLE IF NOT EXISTS `queue_number` (
  `q_id` int NOT NULL AUTO_INCREMENT,
  `o_id` int DEFAULT NULL COMMENT '挂号订单ID',
  `p_id` int DEFAULT NULL COMMENT '患者ID',
  `d_id` char(6) DEFAULT NULL COMMENT '医生ID',
  `q_number` varchar(20) DEFAULT NULL COMMENT '排队号码(如N001)',
  `q_state` int DEFAULT 0 COMMENT '状态:0=待叫号,1=已叫号,2=已过号,3=已完成',
  `q_create_time` datetime DEFAULT NULL COMMENT '取号时间',
  `q_call_time` datetime DEFAULT NULL COMMENT '叫号时间',
  `q_finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`q_id`) USING BTREE,
  KEY `qTOo` (`o_id`),
  KEY `qTOp` (`p_id`),
  KEY `qTOd` (`d_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='排队叫号表';
