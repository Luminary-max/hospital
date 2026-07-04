-- =====================================================
-- 医院门诊管理系统 - 架构修复迁移脚本（阶段0）
-- 创建缺失的数据库表
-- =====================================================

-- 0.1 检查开单表
CREATE TABLE IF NOT EXISTS order_check (
  oc_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  o_id INT NOT NULL COMMENT '订单ID(关联orders)',
  ch_id VARCHAR(50) NOT NULL COMMENT '检查项目ID(关联checks)',
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

-- 0.2 退费申请表（支持按检查项逐项退费）
CREATE TABLE IF NOT EXISTS refund_request (
  rf_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  o_id INT NOT NULL COMMENT '订单ID(关联orders)',
  br_id INT DEFAULT NULL COMMENT '收费记录ID(关联billing_record,支持按收费项退费)',
  oc_id INT DEFAULT NULL COMMENT '检查单ID(关联order_check,支持按检查项退费)',
  rf_amount DECIMAL(10,2) NOT NULL COMMENT '退费金额',
  rf_reason VARCHAR(500) DEFAULT NULL COMMENT '退费原因',
  rf_status TINYINT DEFAULT 0 COMMENT '状态:0=待审核 1=已通过 2=已拒绝',
  rf_requester VARCHAR(50) DEFAULT NULL COMMENT '申请人',
  rf_approver VARCHAR(50) DEFAULT NULL COMMENT '审核人',
  rf_approve_time DATETIME DEFAULT NULL COMMENT '审核时间',
  rf_create_time DATETIME DEFAULT NULL COMMENT '申请时间',
  rf_note VARCHAR(500) DEFAULT NULL COMMENT '备注',
  INDEX idx_rf_order (o_id),
  INDEX idx_rf_br (br_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退费申请表';

-- 0.3 发票管理表（关联缴费记录）
CREATE TABLE IF NOT EXISTS invoice_record (
  inv_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  inv_no VARCHAR(50) NOT NULL UNIQUE COMMENT '发票号',
  o_id INT NOT NULL COMMENT '订单ID(关联orders)',
  br_id INT DEFAULT NULL COMMENT '关联缴费记录ID(关联billing_record)',
  inv_type VARCHAR(20) DEFAULT '电子' COMMENT '发票类型:电子/纸质',
  inv_amount DECIMAL(10,2) NOT NULL COMMENT '发票金额',
  inv_status TINYINT DEFAULT 0 COMMENT '状态:0=正常 -1=已作废',
  inv_operator VARCHAR(50) DEFAULT NULL COMMENT '开票人',
  inv_create_time DATETIME DEFAULT NULL COMMENT '开票时间',
  inv_cancel_time DATETIME DEFAULT NULL COMMENT '作废时间',
  inv_cancel_reason VARCHAR(200) DEFAULT NULL COMMENT '作废原因',
  INDEX idx_inv_order (o_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发票管理表';

-- 0.4 为 pharmacy_dispensing 添加处方明细关联列
ALTER TABLE pharmacy_dispensing
  ADD COLUMN presc_detail_id INT DEFAULT NULL COMMENT '关联prescription_detail.pd_id' AFTER o_id,
  ADD INDEX idx_presc_detail (presc_detail_id);

-- 0.5 为 prescription_detail 添加处方主表关联列
ALTER TABLE prescription_detail
  ADD COLUMN pm_id INT DEFAULT NULL COMMENT '关联prescription_master.pm_id' AFTER o_id,
  ADD INDEX idx_pd_pm (pm_id);

-- 0.6 处方主表
CREATE TABLE IF NOT EXISTS prescription_master (
  pm_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  o_id INT NOT NULL COMMENT '订单ID(关联orders)',
  d_id CHAR(6) DEFAULT NULL COMMENT '开方医生ID',
  pm_diagnosis VARCHAR(500) DEFAULT NULL COMMENT '处方诊断',
  pm_type VARCHAR(20) DEFAULT '西药' COMMENT '处方类型:西药/中药/混合',
  pm_status TINYINT DEFAULT 0 COMMENT '0=待审核 1=已审核',
  pm_create_time DATETIME DEFAULT NULL COMMENT '创建时间',
  INDEX idx_pm_order (o_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处方主表';
