-- =====================================================
-- 医院门诊管理系统 - 高级药事与处方管理扩展
-- 使用方法：在 hospital 数据库中执行一次本脚本
-- 本脚本只增加字段和表，不删除现有数据
-- =====================================================

ALTER TABLE `drug`
  ADD COLUMN `dr_generic_name` varchar(100) DEFAULT NULL COMMENT '通用名' AFTER `dr_name`,
  ADD COLUMN `dr_pinyin` varchar(100) DEFAULT NULL COMMENT '拼音码' AFTER `dr_generic_name`,
  ADD COLUMN `dr_subtype` varchar(50) DEFAULT NULL COMMENT '细分类：抗感染/心血管/中药饮片等' AFTER `dr_type`,
  ADD COLUMN `dr_rx_type` varchar(20) DEFAULT '处方药' COMMENT '处方药/非处方药' AFTER `dr_subtype`,
  ADD COLUMN `dr_insurance_type` varchar(20) DEFAULT '自费' COMMENT '医保甲类/医保乙类/自费' AFTER `dr_rx_type`,
  ADD COLUMN `dr_antibiotic_level` varchar(20) DEFAULT NULL COMMENT '抗菌药级别：非限制/限制/特殊' AFTER `dr_insurance_type`,
  ADD COLUMN `dr_controlled` tinyint DEFAULT 0 COMMENT '是否特殊管制药品' AFTER `dr_antibiotic_level`,
  ADD COLUMN `dr_essential` tinyint DEFAULT 0 COMMENT '是否基本药物' AFTER `dr_controlled`,
  ADD COLUMN `dr_min_stock` int DEFAULT 20 COMMENT '最低库存预警值' AFTER `dr_number`,
  ADD COLUMN `dr_storage` varchar(100) DEFAULT NULL COMMENT '储存条件' AFTER `dr_manufacturer`,
  ADD COLUMN `dr_indication` text COMMENT '适应症/功能主治' AFTER `dr_storage`,
  ADD COLUMN `dr_contraindication` text COMMENT '禁忌症' AFTER `dr_indication`,
  ADD COLUMN `dr_adverse_reaction` text COMMENT '不良反应' AFTER `dr_contraindication`,
  ADD COLUMN `dr_tcm_nature` varchar(30) DEFAULT NULL COMMENT '中药药性' AFTER `dr_adverse_reaction`,
  ADD COLUMN `dr_tcm_flavor` varchar(50) DEFAULT NULL COMMENT '中药药味' AFTER `dr_tcm_nature`,
  ADD COLUMN `dr_tcm_meridian` varchar(100) DEFAULT NULL COMMENT '中药归经' AFTER `dr_tcm_flavor`,
  ADD COLUMN `dr_decoction_method` varchar(100) DEFAULT NULL COMMENT '中药煎服方法' AFTER `dr_tcm_meridian`;

ALTER TABLE `prescription_detail`
  ADD COLUMN `pd_route` varchar(50) DEFAULT NULL COMMENT '给药途径' AFTER `pd_usage`,
  ADD COLUMN `pd_timing` varchar(50) DEFAULT NULL COMMENT '服药时机' AFTER `pd_frequency`,
  ADD COLUMN `pd_skin_test` tinyint DEFAULT 0 COMMENT '是否需要皮试' AFTER `pd_timing`,
  ADD COLUMN `pd_tcm_group_no` varchar(30) DEFAULT NULL COMMENT '中药方剂组号' AFTER `pd_skin_test`,
  ADD COLUMN `pd_decoction_method` varchar(100) DEFAULT NULL COMMENT '煎服方法' AFTER `pd_tcm_group_no`;

ALTER TABLE `pharmacy_dispensing`
  ADD COLUMN `db_id` int DEFAULT NULL COMMENT '实际发药批次' AFTER `dr_id`,
  ADD COLUMN `pd_review_by` varchar(50) DEFAULT NULL COMMENT '审核药师' AFTER `pd_dispense_by`,
  ADD COLUMN `pd_review_time` datetime DEFAULT NULL COMMENT '审核时间' AFTER `pd_review_by`,
  ADD COLUMN `pd_return_time` datetime DEFAULT NULL COMMENT '退药时间' AFTER `pd_review_time`,
  ADD COLUMN `pd_return_by` varchar(50) DEFAULT NULL COMMENT '退药操作人' AFTER `pd_return_time`;

CREATE TABLE IF NOT EXISTS `inventory_transaction` (
  `it_id` int NOT NULL AUTO_INCREMENT,
  `dr_id` char(6) NOT NULL COMMENT '药品ID',
  `db_id` int DEFAULT NULL COMMENT '批次ID',
  `it_type` varchar(20) NOT NULL COMMENT '入库/发药/退药/盘盈/盘亏/报损',
  `it_quantity` int NOT NULL COMMENT '变动数量，增加为正，减少为负',
  `it_before_quantity` int DEFAULT NULL COMMENT '变动前总库存',
  `it_after_quantity` int DEFAULT NULL COMMENT '变动后总库存',
  `it_reference` varchar(50) DEFAULT NULL COMMENT '业务单号',
  `it_operator` varchar(50) DEFAULT NULL COMMENT '操作人',
  `it_note` varchar(255) DEFAULT NULL COMMENT '备注',
  `it_create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`it_id`),
  KEY `idx_it_drug` (`dr_id`),
  KEY `idx_it_batch` (`db_id`),
  KEY `idx_it_time` (`it_create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药品库存流水';

CREATE TABLE IF NOT EXISTS `dispensing_batch_detail` (
  `dbd_id` int NOT NULL AUTO_INCREMENT,
  `pd_reference` varchar(50) NOT NULL COMMENT '发药业务单号',
  `dr_id` char(6) NOT NULL COMMENT '药品ID',
  `db_id` int NOT NULL COMMENT '批次ID',
  `dbd_quantity` int NOT NULL COMMENT '该批次发药数量',
  `dbd_returned` tinyint NOT NULL DEFAULT 0 COMMENT '是否已退回',
  `dbd_create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`dbd_id`),
  KEY `idx_dbd_reference` (`pd_reference`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发药批次分摊明细';

CREATE TABLE IF NOT EXISTS `prescription_review` (
  `pr_id` int NOT NULL AUTO_INCREMENT,
  `o_id` int NOT NULL COMMENT '挂号订单ID',
  `pr_status` int NOT NULL DEFAULT 0 COMMENT '0待审核,1通过,2驳回',
  `pr_risk_level` varchar(20) DEFAULT '普通' COMMENT '普通/关注/高风险',
  `pr_issue` varchar(500) DEFAULT NULL COMMENT '审核问题',
  `pr_suggestion` varchar(500) DEFAULT NULL COMMENT '药师建议',
  `pr_pharmacist` varchar(50) DEFAULT NULL COMMENT '审核药师',
  `pr_review_time` datetime DEFAULT NULL COMMENT '审核时间',
  `pr_create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`pr_id`),
  UNIQUE KEY `uk_pr_order` (`o_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处方审核记录';

UPDATE `drug`
SET `dr_subtype` = CASE
  WHEN `dr_type` = 2 AND (`dr_form` LIKE '%片%' OR `dr_form` LIKE '%丸%' OR `dr_form` LIKE '%颗粒%') THEN '中成药'
  WHEN `dr_type` = 2 THEN '中药饮片'
  ELSE '西药'
END
WHERE `dr_subtype` IS NULL;

UPDATE `drug`
SET `dr_rx_type` = CASE WHEN `dr_approval_no` LIKE '%OTC%' THEN '非处方药' ELSE '处方药' END
WHERE `dr_rx_type` IS NULL OR `dr_rx_type` = '';
