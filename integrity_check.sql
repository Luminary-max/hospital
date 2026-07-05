-- ============================================================
-- 医院门诊管理系统 - 数据完整性检查脚本
-- ============================================================

SET NAMES utf8mb4;

SELECT '========================================' AS '';
SELECT '1. 外键约束检查 (FOREIGN KEY VIOLATIONS)' AS '';
SELECT '========================================' AS '';

-- 1a. patient 表引用 section
SELECT '--- patient.p_sec -> section.s_id ---' AS '';
SELECT p.p_id AS patient_id, p.p_name, p.p_sec AS section_id
FROM patient p
LEFT JOIN section s ON p.p_sec = s.s_id
WHERE s.s_id IS NULL AND p.p_sec IS NOT NULL;

-- 1b. doctor 表引用 section
SELECT '--- doctor.d_sec -> section.s_id ---' AS '';
SELECT d.d_id, d.d_name, d.d_sec AS section_id
FROM doctor d
LEFT JOIN section s ON d.d_sec = s.s_id
WHERE s.s_id IS NULL AND d.d_sec IS NOT NULL;

-- 1c. registration 表引用 patient
SELECT '--- registration.p_id -> patient.p_id ---' AS '';
SELECT r.r_id, r.p_id
FROM registration r
LEFT JOIN patient p ON r.p_id = p.p_id
WHERE p.p_id IS NULL;

-- 1d. registration 表引用 doctor
SELECT '--- registration.d_id -> doctor.d_id ---' AS '';
SELECT r.r_id, r.d_id
FROM registration r
LEFT JOIN doctor d ON r.d_id = d.d_id
WHERE d.d_id IS NULL;

-- 1e. registration 表引用 arrange
SELECT '--- registration.ar_id -> arrange.ar_id ---' AS '';
SELECT r.r_id, r.ar_id
FROM registration r
LEFT JOIN arrange a ON r.ar_id = a.ar_id
WHERE a.ar_id IS NULL;

-- 1f. triage_record 表引用 registration
SELECT '--- triage_record.r_id -> registration.r_id ---' AS '';
SELECT t.tr_id, t.r_id
FROM triage_record t
LEFT JOIN registration r ON t.r_id = r.r_id
WHERE r.r_id IS NULL;

-- 1g. orders 表引用 patient
SELECT '--- orders.p_id -> patient.p_id ---' AS '';
SELECT o.o_id, o.p_id
FROM orders o
LEFT JOIN patient p ON o.p_id = p.p_id
WHERE p.p_id IS NULL;

-- 1h. orders 表引用 doctor
SELECT '--- orders.d_id -> doctor.d_id ---' AS '';
SELECT o.o_id, o.d_id
FROM orders o
LEFT JOIN doctor d ON o.d_id = d.d_id
WHERE d.d_id IS NULL;

-- 1i. order_details 表引用 orders
SELECT '--- order_details.o_id -> orders.o_id ---' AS '';
SELECT od.od_id, od.o_id
FROM order_details od
LEFT JOIN orders o ON od.o_id = o.o_id
WHERE o.o_id IS NULL;

-- 1j. billing_record 表引用 orders
SELECT '--- billing_record.o_id -> orders.o_id ---' AS '';
SELECT br.br_id, br.o_id
FROM billing_record br
LEFT JOIN orders o ON br.o_id = o.o_id
WHERE o.o_id IS NULL;

-- 1k. prescribe 表引用 orders
SELECT '--- prescribe.o_id -> orders.o_id ---' AS '';
SELECT p.dr_id, p.o_id
FROM prescribe p
LEFT JOIN orders o ON p.o_id = o.o_id
WHERE o.o_id IS NULL;

-- 1l. prescribe 表引用 drug
SELECT '--- prescribe.dr_id -> drug.dr_id ---' AS '';
SELECT p.dr_id, p.o_id
FROM prescribe p
LEFT JOIN drug d ON p.dr_id = d.dr_id
WHERE d.dr_id IS NULL;

-- 1m. arrange 表引用 doctor
SELECT '--- arrange.d_id -> doctor.d_id ---' AS '';
SELECT a.ar_id, a.d_id
FROM arrange a
LEFT JOIN doctor d ON a.d_id = d.d_id
WHERE d.d_id IS NULL;

-- 1n. queue_number 表引用 registration
SELECT '--- queue_number.r_id -> registration.r_id ---' AS '';
SELECT q.qu_id, q.r_id
FROM queue_number q
LEFT JOIN registration r ON q.r_id = r.r_id
WHERE r.r_id IS NULL;

-- 1o. wait 表引用 registration
SELECT '--- wait.r_id -> registration.r_id ---' AS '';
SELECT w.w_id, w.r_id
FROM `wait` w
LEFT JOIN registration r ON w.r_id = r.r_id
WHERE r.r_id IS NULL;

-- 1p. bed 表引用 patient
SELECT '--- bed.p_id -> patient.p_id ---' AS '';
SELECT b.b_id, b.p_id
FROM bed b
LEFT JOIN patient p ON b.p_id = p.p_id
WHERE p.p_id IS NULL AND b.p_id IS NOT NULL;

-- 1q. dispensing 表引用 orders
SELECT '--- dispensing.o_id -> orders.o_id ---' AS '';
SELECT d.disp_id, d.o_id
FROM dispensing d
LEFT JOIN orders o ON d.o_id = o.o_id
WHERE o.o_id IS NULL;

-- 1r. dispensing 表引用 drug
SELECT '--- dispensing.dr_id -> drug.dr_id ---' AS '';
SELECT d.disp_id, d.dr_id
FROM dispensing d
LEFT JOIN drug dr ON d.dr_id = dr.dr_id
WHERE dr.dr_id IS NULL;

SELECT '' AS '';
SELECT '========================================' AS '';
SELECT '2. 空值/NULL 检查 (REQUIRED FIELDS)' AS '';
SELECT '========================================' AS '';

SELECT '--- patient 表必填字段 NULL ---' AS '';
SELECT p_id, p_name, p_phone FROM patient WHERE p_name IS NULL OR p_phone IS NULL;

SELECT '--- doctor 表必填字段 NULL ---' AS '';
SELECT d_id, d_name FROM doctor WHERE d_name IS NULL;

SELECT '--- registration 表必填字段 NULL ---' AS '';
SELECT r_id, p_id, d_id FROM registration WHERE p_id IS NULL OR d_id IS NULL;

SELECT '--- orders 表必填字段 NULL ---' AS '';
SELECT o_id, p_id, d_id, o_time FROM orders WHERE p_id IS NULL OR d_id IS NULL OR o_time IS NULL;

SELECT '--- billing_record 表必填字段 NULL ---' AS '';
SELECT br_id, o_id, br_money FROM billing_record WHERE o_id IS NULL OR br_money IS NULL;

SELECT '' AS '';
SELECT '========================================' AS '';
SELECT '3. 数据逻辑一致性检查' AS '';
SELECT '========================================' AS '';

-- 3a. 挂号记录的日期与 arrange 日期不匹配
SELECT '--- registration日期 vs arrange日期不一致 ---' AS '';
SELECT r.r_id, r.r_date AS reg_date, a.ar_time AS arrange_date
FROM registration r
JOIN arrange a ON r.ar_id = a.ar_id
WHERE r.r_date IS NOT NULL AND r.r_date != a.ar_time;

-- 3b. 缴费金额为负或零
SELECT '--- billing_record 金额异常 (<=0) ---' AS '';
SELECT br_id, o_id, br_money
FROM billing_record
WHERE br_money <= 0;

-- 3c. 发药数量 <= 0
SELECT '--- dispensing 数量异常 (<=0) ---' AS '';
SELECT disp_id, dr_id, disp_number
FROM dispensing
WHERE disp_number <= 0;

-- 3d. 处方数量 <= 0
SELECT '--- prescribe 数量异常 (<=0) ---' AS '';
SELECT dr_id, o_id, p_number
FROM prescribe
WHERE p_number <= 0;

-- 3e. 重复的挂号记录（同一患者同一医生同一天）
SELECT '--- 疑似重复挂号 ---' AS '';
SELECT r1.r_id, r1.p_id, r1.d_id, r1.r_date
FROM registration r1
JOIN registration r2 ON r1.p_id = r2.p_id
    AND r1.d_id = r2.d_id
    AND r1.r_date = r2.r_date
    AND r1.r_id < r2.r_id;

SELECT '' AS '';
SELECT '========================================' AS '';
SELECT '4. 主键/唯一约束冲突检查' AS '';
SELECT '========================================' AS '';

SELECT '--- patient 表重复身份证 ---' AS '';
SELECT p_card, COUNT(*) AS cnt
FROM patient
GROUP BY p_card
HAVING COUNT(*) > 1;

SELECT '--- doctor 表重复手机号 ---' AS '';
SELECT d_phone, COUNT(*) AS cnt
FROM doctor
GROUP BY d_phone
HAVING COUNT(*) > 1;

SELECT '' AS '';
SELECT '========================================' AS '';
SELECT '5. 库存/业务规则检查' AS '';
SELECT '========================================' AS '';

-- 5a. 药品库存为负
SELECT '--- drug 库存为负 ---' AS '';
SELECT dr_id, dr_name, dr_number
FROM drug
WHERE dr_number < 0;

-- 5b. 处方数量超过库存
SELECT '--- prescribe 数量超过库存 ---' AS '';
SELECT p.dr_id, d.dr_name, SUM(p.p_number) AS prescribed, d.dr_number AS stock
FROM prescribe p
JOIN drug d ON p.dr_id = d.dr_id
GROUP BY p.dr_id, d.dr_name, d.dr_number
HAVING SUM(p.p_number) > d.dr_number;

-- 5c. 发药数量超过处方数量
SELECT '--- dispensing 数量超过 prescribe ---' AS '';
SELECT dp.disp_id, dp.dr_id, dp.o_id, dp.disp_number, pr.p_number
FROM dispensing dp
JOIN prescribe pr ON dp.dr_id = pr.dr_id AND dp.o_id = pr.o_id
WHERE dp.disp_number > pr.p_number;

SELECT '' AS '';
SELECT '========================================' AS '';
SELECT '6. 孤立数据检查 (orphan records)' AS '';
SELECT '========================================' AS '';

-- 6a. 没有挂号记录的患者（非患者表问题，仅提示）
SELECT '--- 无任何挂号记录的患者 ---' AS '';
SELECT p.p_id, p.p_name
FROM patient p
LEFT JOIN registration r ON p.p_id = r.p_id
WHERE r.r_id IS NULL;

-- 6b. 没有开单记录的挂号和医生
SELECT '--- 有挂号但无 orders 的记录 ---' AS '';
SELECT r.r_id, r.p_id, r.d_id
FROM registration r
LEFT JOIN orders o ON r.r_id = o.r_id
WHERE o.o_id IS NULL;

SELECT '' AS '';
SELECT '========================================' AS '';
SELECT '7. 数据汇总统计' AS '';
SELECT '========================================' AS '';

SELECT '--- 各表记录数 ---' AS '';

SELECT 'admini' AS table_name, COUNT(*) AS cnt FROM admini
UNION ALL SELECT 'section', COUNT(*) FROM section
UNION ALL SELECT 'patient', COUNT(*) FROM patient
UNION ALL SELECT 'doctor', COUNT(*) FROM doctor
UNION ALL SELECT 'arrange', COUNT(*) FROM arrange
UNION ALL SELECT 'registration', COUNT(*) FROM registration
UNION ALL SELECT 'triage_record', COUNT(*) FROM triage_record
UNION ALL SELECT 'orders', COUNT(*) FROM orders
UNION ALL SELECT 'order_details', COUNT(*) FROM order_details
UNION ALL SELECT 'drug', COUNT(*) FROM drug
UNION ALL SELECT 'prescribe', COUNT(*) FROM prescribe
UNION ALL SELECT 'billing_record', COUNT(*) FROM billing_record
UNION ALL SELECT 'queue_number', COUNT(*) FROM queue_number
UNION ALL SELECT 'wait', COUNT(*) FROM `wait`
UNION ALL SELECT 'bed', COUNT(*) FROM bed
UNION ALL SELECT 'dispensing', COUNT(*) FROM dispensing
UNION ALL SELECT 'invoice_record', COUNT(*) FROM invoice_record
UNION ALL SELECT 'audit_log', COUNT(*) FROM audit_log
ORDER BY table_name;
