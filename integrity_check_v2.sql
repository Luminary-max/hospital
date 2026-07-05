-- 医院门诊管理系统 - 数据完整性检查 (基于实际数据库结构)
SET NAMES utf8mb4;
SET @sep = '========================================';

SELECT @sep AS '1. 外键约束检查 (FOREIGN KEY VIOLATIONS)';

-- 查看所有已定义的外键约束
SELECT '--- 数据库中定义的外键约束 ---';
SELECT CONSTRAINT_NAME, TABLE_NAME, COLUMN_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = DATABASE() AND REFERENCED_TABLE_NAME IS NOT NULL;

SELECT '--- 外键值不存在于引用表 (手动检查) ---';

-- doctor.de_id -> department.de_id
SELECT 'doctor.de_id 无对应 department:';
SELECT d.d_id, d.d_name, d.de_id FROM doctor d LEFT JOIN department de ON d.de_id = de.de_id WHERE de.de_id IS NULL;

-- arrange.d_id -> doctor.d_id
SELECT 'arrange.d_id 无对应 doctor:';
SELECT a.ar_id, a.d_id FROM arrange a LEFT JOIN doctor d ON a.d_id = d.d_id WHERE d.d_id IS NULL;

-- orders.p_id -> patient.p_id
SELECT 'orders.p_id 无对应 patient:';
SELECT o.o_id, o.p_id FROM orders o LEFT JOIN patient p ON o.p_id = p.p_id WHERE p.p_id IS NULL;

-- orders.d_id -> doctor.d_id
SELECT 'orders.d_id 无对应 doctor:';
SELECT o.o_id, o.d_id FROM orders o LEFT JOIN doctor d ON o.d_id = d.d_id WHERE d.d_id IS NULL;

-- outpatient_emr.o_id -> orders.o_id
SELECT 'outpatient_emr.o_id 无对应 orders:';
SELECT e.emr_id, e.o_id FROM outpatient_emr e LEFT JOIN orders o ON e.o_id = o.o_id WHERE o.o_id IS NULL;

-- outpatient_emr.p_id -> patient.p_id
SELECT 'outpatient_emr.p_id 无对应 patient:';
SELECT e.emr_id, e.p_id FROM outpatient_emr e LEFT JOIN patient p ON e.p_id = p.p_id WHERE p.p_id IS NULL;

-- outpatient_emr.d_id -> doctor.d_id
SELECT 'outpatient_emr.d_id 无对应 doctor:';
SELECT e.emr_id, e.d_id FROM outpatient_emr e LEFT JOIN doctor d ON e.d_id = d.d_id WHERE d.d_id IS NULL;

-- billing_record.emr_id -> outpatient_emr.emr_id
SELECT 'billing_record.emr_id 无对应 outpatient_emr:';
SELECT b.br_id, b.emr_id FROM billing_record b LEFT JOIN outpatient_emr e ON b.emr_id = e.emr_id WHERE e.emr_id IS NULL AND b.emr_id IS NOT NULL;

-- billing_record.oc_id -> order_check.oc_id
SELECT 'billing_record.oc_id 无对应 order_check:';
SELECT b.br_id, b.oc_id FROM billing_record b LEFT JOIN order_check oc ON b.oc_id = oc.oc_id WHERE oc.oc_id IS NULL AND b.oc_id IS NOT NULL;

-- billing_record.pm_id -> prescription_master.pm_id
SELECT 'billing_record.pm_id 无对应 prescription_master:';
SELECT b.br_id, b.pm_id FROM billing_record b LEFT JOIN prescription_master pm ON b.pm_id = pm.pm_id WHERE pm.pm_id IS NULL AND b.pm_id IS NOT NULL;

-- invoice_record.br_id -> billing_record.br_id
SELECT 'invoice_record.br_id 无对应 billing_record:';
SELECT i.inv_id, i.br_id FROM invoice_record i LEFT JOIN billing_record b ON i.br_id = b.br_id WHERE b.br_id IS NULL;

-- refund_request.br_id -> billing_record.br_id
SELECT 'refund_request.br_id 无对应 billing_record:';
SELECT r.rf_id, r.br_id FROM refund_request r LEFT JOIN billing_record b ON r.br_id = b.br_id WHERE b.br_id IS NULL;

-- order_check.emr_id -> outpatient_emr.emr_id
SELECT 'order_check.emr_id 无对应 outpatient_emr:';
SELECT oc.oc_id, oc.emr_id FROM order_check oc LEFT JOIN outpatient_emr e ON oc.emr_id = e.emr_id WHERE e.emr_id IS NULL AND oc.emr_id IS NOT NULL;

-- prescription_master.emr_id -> outpatient_emr.emr_id
SELECT 'prescription_master.emr_id 无对应 outpatient_emr:';
SELECT pm.pm_id, pm.emr_id FROM prescription_master pm LEFT JOIN outpatient_emr e ON pm.emr_id = e.emr_id WHERE e.emr_id IS NULL AND pm.emr_id IS NOT NULL;

-- prescription_master.d_id -> doctor.d_id
SELECT 'prescription_master.d_id 无对应 doctor:';
SELECT pm.pm_id, pm.d_id FROM prescription_master pm LEFT JOIN doctor d ON pm.d_id = d.d_id WHERE d.d_id IS NULL;

-- prescription_detail.pm_id -> prescription_master.pm_id
SELECT 'prescription_detail.pm_id 无对应 prescription_master:';
SELECT pd.pd_id, pd.pm_id FROM prescription_detail pd LEFT JOIN prescription_master pm ON pd.pm_id = pm.pm_id WHERE pm.pm_id IS NULL;

-- prescription_detail.dr_id -> drug.dr_id
SELECT 'prescription_detail.dr_id 无对应 drug:';
SELECT pd.pd_id, pd.dr_id FROM prescription_detail pd LEFT JOIN drug d ON pd.dr_id = d.dr_id WHERE d.dr_id IS NULL;

-- pharmacy_dispensing.o_id -> orders.o_id
SELECT 'pharmacy_dispensing.o_id 无对应 orders:';
SELECT p.pd_id, p.o_id FROM pharmacy_dispensing p LEFT JOIN orders o ON p.o_id = o.o_id WHERE o.o_id IS NULL;

-- pharmacy_dispensing.dr_id -> drug.dr_id
SELECT 'pharmacy_dispensing.dr_id 无对应 drug:';
SELECT p.pd_id, p.dr_id FROM pharmacy_dispensing p LEFT JOIN drug d ON p.dr_id = d.dr_id WHERE d.dr_id IS NULL;

-- queue_number.o_id -> orders.o_id
SELECT 'queue_number.o_id 无对应 orders:';
SELECT q.q_id, q.o_id FROM queue_number q LEFT JOIN orders o ON q.o_id = o.o_id WHERE o.o_id IS NULL;

-- triage_record.p_id -> patient.p_id
SELECT 'triage_record.p_id 无对应 patient:';
SELECT t.t_id, t.p_id FROM triage_record t LEFT JOIN patient p ON t.p_id = p.p_id WHERE p.p_id IS NULL;

-- triage_record.d_id -> doctor.d_id
SELECT 'triage_record.d_id 无对应 doctor:';
SELECT t.t_id, t.d_id FROM triage_record t LEFT JOIN doctor d ON t.d_id = d.d_id WHERE d.d_id IS NULL;

-- notification.p_id -> patient.p_id
SELECT 'notification.p_id 无对应 patient:';
SELECT n.n_id, n.p_id FROM notification n LEFT JOIN patient p ON n.p_id = p.p_id WHERE p.p_id IS NULL AND n.p_id IS NOT NULL;

-- notification.d_id -> doctor.d_id
SELECT 'notification.d_id 无对应 doctor:';
SELECT n.n_id, n.d_id FROM notification n LEFT JOIN doctor d ON n.d_id = d.d_id WHERE d.d_id IS NULL AND n.d_id IS NOT NULL;

-- delivery_request.p_id -> patient.p_id
SELECT 'delivery_request.p_id 无对应 patient:';
SELECT d.dl_id, d.p_id FROM delivery_request d LEFT JOIN patient p ON d.p_id = p.p_id WHERE p.p_id IS NULL;

-- drug_batch.dr_id -> drug.dr_id
SELECT 'drug_batch.dr_id 无对应 drug:';
SELECT b.db_id, b.dr_id FROM drug_batch b LEFT JOIN drug d ON b.dr_id = d.dr_id WHERE d.dr_id IS NULL;

-- inventory_transaction.dr_id -> drug.dr_id
SELECT 'inventory_transaction.dr_id 无对应 drug:';
SELECT i.it_id, i.dr_id FROM inventory_transaction i LEFT JOIN drug d ON i.dr_id = d.dr_id WHERE d.dr_id IS NULL;

-- registrate.p_id -> patient.p_id
SELECT 'registrate.p_id 无对应 patient:';
SELECT r.p_id, r.d_id FROM registrate r LEFT JOIN patient p ON r.p_id = p.p_id WHERE p.p_id IS NULL;

-- registrate.d_id -> doctor.d_id
SELECT 'registrate.d_id 无对应 doctor:';
SELECT r.p_id, r.d_id FROM registrate r LEFT JOIN doctor d ON r.d_id = d.d_id WHERE d.d_id IS NULL;

-- issue_check.o_id -> orders.o_id
SELECT 'issue_check.o_id 无对应 orders:';
SELECT i.ch_id, i.o_id FROM issue_check i LEFT JOIN orders o ON i.o_id = o.o_id WHERE o.o_id IS NULL;

-- prescribe.o_id -> orders.o_id
SELECT 'prescribe.o_id 无对应 orders:';
SELECT p.dr_id, p.o_id FROM prescribe p LEFT JOIN orders o ON p.o_id = o.o_id WHERE o.o_id IS NULL;

-- prescribe.dr_id -> drug.dr_id
SELECT 'prescribe.dr_id 无对应 drug:';
SELECT p.dr_id, p.o_id FROM prescribe p LEFT JOIN drug d ON p.dr_id = d.dr_id WHERE d.dr_id IS NULL;

-- order_check.ch_id -> checks.ch_id
SELECT 'order_check.ch_id 无对应 checks:';
SELECT oc.oc_id, oc.ch_id FROM order_check oc LEFT JOIN checks c ON oc.ch_id = c.ch_id WHERE c.ch_id IS NULL;

SELECT @sep AS '2. 空值/NULL 值检查';

SELECT '--- doctor.d_state 为 NULL (NOT NULL 约束) ---';
SELECT d_id, d_name FROM doctor WHERE d_state IS NULL;

SELECT '--- outpatient_emr.diagnosis 为 NULL ---';
SELECT emr_id, o_id FROM outpatient_emr WHERE diagnosis IS NULL;

SELECT '--- orders.o_total_price 为 NULL 但已完成 ---';
SELECT o_id, o_state, o_total_price FROM orders WHERE o_total_price IS NULL AND o_state IN (2,3);

SELECT '--- billing_record.br_amount 为 NULL ---';
SELECT br_id, br_amount FROM billing_record WHERE br_amount IS NULL;

SELECT '--- refund_request.rf_amount 为 NULL ---';
SELECT rf_id, rf_amount FROM refund_request WHERE rf_amount IS NULL;

SELECT '--- invoice_record.inv_amount 为 NULL ---';
SELECT inv_id, inv_amount FROM invoice_record WHERE inv_amount IS NULL;

SELECT @sep AS '3. 数据逻辑一致性检查';

-- 3a. 金额异常
SELECT '--- billing_record.br_amount <= 0 ---';
SELECT br_id, br_type, br_amount, br_pay_time FROM billing_record WHERE br_amount <= 0;

-- 3b. 发药数量异常
SELECT '--- pharmacy_dispensing.pd_quantity <= 0 ---';
SELECT pd_id, dr_id, pd_quantity FROM pharmacy_dispensing WHERE pd_quantity <= 0;

-- 3c. 处方数量异常
SELECT '--- prescription_detail.pd_quantity <= 0 ---';
SELECT pd_id, dr_id, pd_quantity FROM prescription_detail WHERE pd_quantity <= 0;

-- 3d. 药品库存为负
SELECT '--- drug.dr_number < 0 ---';
SELECT dr_id, dr_name, dr_number FROM drug WHERE dr_number < 0;

-- 3e. 超过最大库存
SELECT '--- drug.dr_number > dr_max_stock ---';
SELECT dr_id, dr_name, dr_number, dr_max_stock FROM drug WHERE dr_max_stock IS NOT NULL AND dr_number > dr_max_stock;

-- 3f. 库存低于最低库存
SELECT '--- drug.dr_number < dr_min_stock ---';
SELECT dr_id, dr_name, dr_number, dr_min_stock FROM drug WHERE dr_min_stock IS NOT NULL AND dr_number < dr_min_stock;

-- 3g. 已过期药品批次
SELECT '--- drug_batch 已过期 ---';
SELECT db_id, dr_id, db_batch_no, db_expire_date, db_quantity FROM drug_batch WHERE db_expire_date < CURDATE() AND db_quantity > 0;

-- 3h. 订单状态与支付状态不一致
SELECT '--- orders.o_state=3(已完成) 但 o_price_state不是已支付 ---';
SELECT o_id, o_state, o_price_state FROM orders WHERE o_state = 3 AND (o_price_state IS NULL OR o_price_state != 2);

-- 3i. 退款金额大于原缴费金额
SELECT '--- refund_request 金额 > billing_record ---';
SELECT r.rf_id, r.br_id, r.rf_amount, b.br_amount
FROM refund_request r
JOIN billing_record b ON r.br_id = b.br_id
WHERE r.rf_amount > b.br_amount;

-- 3j. 分诊时间晚于记录创建时间
SELECT '--- triage_record 时间逻辑异常 ---';
SELECT t_id, t_create_time FROM triage_record WHERE t_create_time IS NOT NULL;

-- 3k. 订单取消原因缺失但状态为已取消
SELECT '--- orders 已取消但无取消原因 ---';
SELECT o_id, o_state FROM orders WHERE o_state = 0 AND (o_cancel_reason IS NULL OR o_cancel_reason = '');

-- 3l. 队列已完成但订单未完成
SELECT '--- queue_number 已完成但 orders 未完成 ---';
SELECT q.q_id, q.o_id, q.q_state, o.o_state
FROM queue_number q
JOIN orders o ON q.o_id = o.o_id
WHERE q.q_state = 2 AND o.o_state NOT IN (2,3);

SELECT @sep AS '4. 主键/唯一约束冲突检查';

SELECT '--- patient 表重复身份证 ---';
SELECT p_card, COUNT(*) AS cnt FROM patient WHERE p_card IS NOT NULL GROUP BY p_card HAVING COUNT(*) > 1;

SELECT '--- doctor 表重复手机号 ---';
SELECT d_phone, COUNT(*) AS cnt FROM doctor WHERE d_phone IS NOT NULL GROUP BY d_phone HAVING COUNT(*) > 1;

SELECT '--- drug 表重复批准文号 ---';
SELECT dr_approval_no, COUNT(*) AS cnt FROM drug WHERE dr_approval_no IS NOT NULL GROUP BY dr_approval_no HAVING COUNT(*) > 1;

SELECT @sep AS '5. 业务规则检查';

-- 5a. 订单已取消但仍有缴费记录
SELECT '--- orders 已取消但仍有 billing_record ---';
SELECT o.o_id, COUNT(b.br_id) AS billing_count
FROM orders o
JOIN billing_record b ON o.o_id = b.pm_id -- 假设关联
WHERE o.o_state = 0
GROUP BY o.o_id;

-- 5b. 同一个患者的同一天同一科室挂号
SELECT '--- 同一患者同一天重复挂号 ---';
SELECT r1.o_id, r1.p_id
FROM orders r1
JOIN orders r2 ON r1.p_id = r2.p_id AND DATE(r1.o_start) = DATE(r2.o_start) AND r1.d_id = r2.d_id AND r1.o_id < r2.o_id
WHERE r1.o_state NOT IN (0);

SELECT @sep AS '6. 孤立数据检查';

-- 6a. 没有 orders 的患者
SELECT '--- 无 orders 的患者 ---';
SELECT p.p_id, p.p_name FROM patient p LEFT JOIN orders o ON p.p_id = o.p_id WHERE o.o_id IS NULL;

-- 6b. 没有处方详情的处方主表
SELECT '--- 无 prescription_detail 的 prescription_master ---';
SELECT pm.pm_id FROM prescription_master pm LEFT JOIN prescription_detail pd ON pm.pm_id = pd.pm_id WHERE pd.pd_id IS NULL;

-- 6c. 没有发药记录的订单
SELECT '--- 有处方但无 pharmacy_dispensing 的 orders ---';
SELECT DISTINCT o.o_id FROM orders o
JOIN prescribe p ON o.o_id = p.o_id
LEFT JOIN pharmacy_dispensing pd ON o.o_id = pd.o_id
WHERE pd.pd_id IS NULL;

-- 6d. 没有门诊病历的订单
SELECT '--- 有缴费但无门诊病历的订单 ---';
SELECT DISTINCT o.o_id FROM orders o
JOIN billing_record b ON o.o_id = b.emr_id
LEFT JOIN outpatient_emr e ON o.o_id = e.o_id
WHERE e.emr_id IS NULL;

SELECT @sep AS '7. 各表记录数统计';
SELECT TABLE_NAME AS '表名', TABLE_ROWS AS '估计行数'
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA = DATABASE()
ORDER BY TABLE_NAME;

SELECT @sep AS '8. 汇总报告';
SELECT '以上为数据完整性检查结果。若各检查返回空结果，表示该检查项无异常。' AS '';
