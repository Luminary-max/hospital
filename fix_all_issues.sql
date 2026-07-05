-- Issue 6a + Issue 8: 补充当天排队记录 + 多表数据

-- 1. Queue records for today
INSERT IGNORE INTO queue_number (o_id, q_state, q_create_time, q_call_time, q_finish_time) VALUES
(21001, 3, CONCAT(CURDATE(), ' 08:30:00'), CONCAT(CURDATE(), ' 09:00:00'), CONCAT(CURDATE(), ' 09:45:00')),
(21002, 3, CONCAT(CURDATE(), ' 08:35:00'), CONCAT(CURDATE(), ' 09:05:00'), CONCAT(CURDATE(), ' 09:50:00')),
(21003, 3, CONCAT(CURDATE(), ' 08:40:00'), CONCAT(CURDATE(), ' 09:50:00'), CONCAT(CURDATE(), ' 10:30:00')),
(21004, 3, CONCAT(CURDATE(), ' 08:45:00'), CONCAT(CURDATE(), ' 10:15:00'), CONCAT(CURDATE(), ' 11:00:00')),
(21005, 2, CONCAT(CURDATE(), ' 08:50:00'), CONCAT(CURDATE(), ' 10:45:00'), NULL),
(21006, 1, CONCAT(CURDATE(), ' 13:30:00'), CONCAT(CURDATE(), ' 14:00:00'), NULL);

-- 额外的等待中排队记录
INSERT IGNORE INTO queue_number (o_id, q_state, q_create_time)
SELECT o_id, 0, CONCAT(CURDATE(), ' ', SUBSTRING(o_start, 12)) FROM orders WHERE o_id >= 21007 AND o_id <= 21020;

-- 2. Triage records
INSERT INTO triage_record (p_id, d_id, t_level, t_status, t_chief_complaint, t_temperature, t_blood_pressure, t_heart_rate, t_note, t_create_time) VALUES
(1001, 'D24001', 0, 2, '头痛头晕1周', 36.5, '145/90', 78, '既往高血压病史', '2026-07-04 08:45:00'),
(1002, 'D24029', 0, 2, '咳嗽咳痰1周', 38.2, '120/80', 88, '咽部充血', '2026-07-04 08:50:00'),
(1003, 'D24002', 1, 2, '发热2天体温39.2℃', 39.0, '125/85', 92, '扁桃体Ⅱ°肿大', '2026-07-04 09:15:00'),
(1004, 'D24003', 0, 2, '腹痛腹泻3天', 37.8, '118/75', 82, '脐周压痛', '2026-07-04 10:00:00'),
(1005, 'D24034', 1, 1, '胸闷气促1月', 36.8, '130/85', 92, '双肺底湿啰音', '2026-07-04 10:30:00');

-- 3. Delivery requests
INSERT INTO delivery_request (p_id, dl_agent_name, dl_agent_phone, dl_agent_id_card, dl_status, dl_create_time) VALUES
(1006, '王芳', '13600001001', '440101199001011234', 0, '2026-07-04 15:00:00'),
(1004, '赵小明', '13600001002', '440101199307071234', 1, '2026-07-04 11:30:00'),
(1001, '张伟', '13600001003', '440101197505051234', 0, '2026-07-04 10:00:00');

-- 4. More billing records for multi-date income
INSERT INTO billing_record (pm_id, emr_id, oc_id, br_type, br_amount, br_payment_method, br_invoice_no, br_pay_time, br_operator) VALUES
(NULL, 101, NULL, '挂号费', 20.00, '微信', 'INV-20260705-0001', '2026-07-05 08:30:00', '收费员001'),
(51, 101, NULL, '药费', 44.50, '微信', 'INV-20260705-0001', '2026-07-05 08:40:00', '收费员001'),
(51, 101, 102, '检查费', 25.00, '微信', 'INV-20260705-0001', '2026-07-05 08:40:00', '收费员001'),
(51, 101, 103, '检查费', 30.00, '微信', 'INV-20260705-0001', '2026-07-05 08:40:00', '收费员001'),
(NULL, 102, NULL, '挂号费', 50.00, '现金', 'INV-20260705-0002', '2026-07-05 09:00:00', '收费员002'),
(NULL, 103, NULL, '挂号费', 20.00, '支付宝', 'INV-20260706-0001', '2026-07-06 08:30:00', '收费员001');

-- 5. Set some orders to completed
UPDATE orders SET o_state = 7, o_price_state = 1 WHERE o_id IN (21001, 21002, 21003, 21004);
