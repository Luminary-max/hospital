-- =====================================================================
-- 医院门诊管理系统 - 课程要求综合SQL脚本
-- 包含：外键约束 + 视图 + 存储过程 + 触发器 + 实验数据
-- =====================================================================
-- 使用方法：mysql -u root -p"密码" hospital < 本文件.sql
-- =====================================================================


-- =====================================================================
-- 第一部分：补全所有外键约束（FK Constraints）
-- 原有4个 + 本次修复自动创建的13个
-- 补充剩余的约20个外键约束
-- =====================================================================

-- 1.2 处方与药品域（补充尚未创建的）
-- prescription_master.d_id 约束需要 (已创建fk_pm_orders, 还需fk_pm_doctor)
ALTER TABLE prescription_master ADD CONSTRAINT fk_pm_doctor FOREIGN KEY (d_id) REFERENCES doctor(d_id);
ALTER TABLE prescription_detail ADD CONSTRAINT fk_pd_orders FOREIGN KEY (o_id) REFERENCES orders(o_id);
ALTER TABLE prescription_detail ADD CONSTRAINT fk_pd_master FOREIGN KEY (pm_id) REFERENCES prescription_master(pm_id);
ALTER TABLE prescription_detail ADD CONSTRAINT fk_pd_drug FOREIGN KEY (dr_id) REFERENCES drug(dr_id);
ALTER TABLE pharmacy_dispensing ADD CONSTRAINT fk_disp_orders FOREIGN KEY (o_id) REFERENCES orders(o_id);
ALTER TABLE pharmacy_dispensing ADD CONSTRAINT fk_disp_presc_detail FOREIGN KEY (presc_detail_id) REFERENCES prescription_detail(pd_id);
ALTER TABLE pharmacy_dispensing ADD CONSTRAINT fk_disp_drug FOREIGN KEY (dr_id) REFERENCES drug(dr_id);
ALTER TABLE pharmacy_dispensing ADD CONSTRAINT fk_disp_batch FOREIGN KEY (db_id) REFERENCES drug_batch(db_id);
ALTER TABLE drug_batch ADD CONSTRAINT fk_batch_drug FOREIGN KEY (dr_id) REFERENCES drug(dr_id);
ALTER TABLE inventory_transaction ADD CONSTRAINT fk_invtx_drug FOREIGN KEY (dr_id) REFERENCES drug(dr_id);
ALTER TABLE inventory_transaction ADD CONSTRAINT fk_invtx_batch FOREIGN KEY (db_id) REFERENCES drug_batch(db_id);
ALTER TABLE dispensing_batch_detail ADD CONSTRAINT fk_dbd_drug FOREIGN KEY (dr_id) REFERENCES drug(dr_id);
ALTER TABLE dispensing_batch_detail ADD CONSTRAINT fk_dbd_batch FOREIGN KEY (db_id) REFERENCES drug_batch(db_id);
ALTER TABLE prescription_review ADD CONSTRAINT fk_pr_orders FOREIGN KEY (o_id) REFERENCES orders(o_id);

-- 1.3 检查检验域
ALTER TABLE order_check ADD CONSTRAINT fk_oc_orders FOREIGN KEY (o_id) REFERENCES orders(o_id);
ALTER TABLE order_check ADD CONSTRAINT fk_oc_checks FOREIGN KEY (ch_id) REFERENCES checks(ch_id);

-- 1.4 收费与发票域
ALTER TABLE billing_record ADD CONSTRAINT fk_billing_orders FOREIGN KEY (o_id) REFERENCES orders(o_id);
ALTER TABLE invoice_record ADD CONSTRAINT fk_invoice_orders FOREIGN KEY (o_id) REFERENCES orders(o_id);
ALTER TABLE invoice_record ADD CONSTRAINT fk_invoice_billing FOREIGN KEY (br_id) REFERENCES billing_record(br_id);
ALTER TABLE refund_request ADD CONSTRAINT fk_refund_orders FOREIGN KEY (o_id) REFERENCES orders(o_id);
ALTER TABLE refund_request ADD CONSTRAINT fk_refund_billing FOREIGN KEY (br_id) REFERENCES billing_record(br_id);
ALTER TABLE refund_request ADD CONSTRAINT fk_refund_ordercheck FOREIGN KEY (oc_id) REFERENCES order_check(oc_id);

-- 1.5 床位域
ALTER TABLE bed ADD CONSTRAINT fk_bed_patient FOREIGN KEY (p_id) REFERENCES patient(p_id);
ALTER TABLE bed ADD CONSTRAINT fk_bed_doctor FOREIGN KEY (d_id) REFERENCES doctor(d_id);

-- 1.6 通知
ALTER TABLE notification ADD CONSTRAINT fk_notif_patient FOREIGN KEY (p_id) REFERENCES patient(p_id);
ALTER TABLE notification ADD CONSTRAINT fk_notif_doctor FOREIGN KEY (d_id) REFERENCES doctor(d_id);


-- =====================================================================
-- 第二部分：创建视图（至少2个）
-- =====================================================================

-- 视图1：患者就诊完整记录视图
DROP VIEW IF EXISTS v_patient_visit_detail;
CREATE VIEW v_patient_visit_detail AS
SELECT
    o.o_id              AS 挂号编号,
    p.p_name            AS 患者姓名,
    p.p_gender          AS 性别,
    p.p_age             AS 年龄,
    d.d_name            AS 就诊医生,
    d.d_post            AS 医生职称,
    d.d_section         AS 就诊科室,
    o.o_start           AS 挂号时间,
    o.o_triage          AS 挂号类型,
    o.o_state           AS 就诊状态,
    CASE o.o_state
        WHEN 0 THEN '已挂号'
        WHEN 1 THEN '已分诊'
        WHEN 2 THEN '就诊中'
        WHEN 3 THEN '已开单'
        WHEN 4 THEN '待缴费'
        WHEN 5 THEN '已缴费'
        WHEN 6 THEN '已发药/检查完成'
        WHEN 7 THEN '就诊完成'
        WHEN -1 THEN '已取消'
        ELSE '未知'
    END                 AS 就诊状态说明,
    o.o_registration_fee AS 挂号费,
    o.o_total_price     AS 药费检查费,
    o.o_price_state     AS 缴费状态,
    CASE WHEN o.o_price_state = 1 THEN '已缴费' ELSE '未缴费' END AS 缴费状态说明,
    o.o_payment_method  AS 支付方式,
    emr.diagnosis       AS 诊断结果,
    emr.chief_complaint AS 主诉
FROM orders o
JOIN patient p ON o.p_id = p.p_id
JOIN doctor d ON o.d_id = d.d_id
LEFT JOIN outpatient_emr emr ON o.o_id = emr.o_id
ORDER BY o.o_start DESC;


-- 视图2：药品库存预警视图
DROP VIEW IF EXISTS v_drug_stock_alert;
CREATE VIEW v_drug_stock_alert AS
SELECT
    dr.dr_id            AS 药品编号,
    dr.dr_name          AS 药品名称,
    dr.dr_generic_name  AS 通用名,
    dr.dr_form          AS 剂型,
    dr.dr_manufacturer  AS 生产厂家,
    dr.dr_rx_type       AS 处方类型,
    dr.dr_insurance_type AS 医保类型,
    dr.dr_number        AS 当前库存,
    dr.dr_min_stock     AS 最低库存预警值,
    (dr.dr_min_stock - dr.dr_number) AS 缺货数量,
    dr.dr_price         AS 零售价,
    dr.dr_unit          AS 单位,
    (SELECT MIN(db.db_expire_date) FROM drug_batch db
     WHERE db.dr_id = dr.dr_id AND db.db_quantity > 0) AS 最近效期
FROM drug dr
WHERE dr.dr_number <= dr.dr_min_stock
ORDER BY (dr.dr_min_stock - dr.dr_number) DESC;


-- =====================================================================
-- 第三部分：存储过程（至少2个）
-- =====================================================================

-- 存储过程1：门诊日报统计
DROP PROCEDURE IF EXISTS sp_daily_clinic_report;
DELIMITER $$
CREATE PROCEDURE sp_daily_clinic_report(IN report_date DATE)
BEGIN
    DECLARE total_reg INT DEFAULT 0;
    DECLARE total_income DECIMAL(12,2) DEFAULT 0;
    DECLARE reg_fee DECIMAL(12,2) DEFAULT 0;
    DECLARE drug_fee DECIMAL(12,2) DEFAULT 0;
    DECLARE completed_count INT DEFAULT 0;
    DECLARE cancelled_count INT DEFAULT 0;
    DECLARE missed_count INT DEFAULT 0;

    SELECT COUNT(*) INTO total_reg FROM orders WHERE DATE(o_start) = report_date;

    SELECT COALESCE(SUM(br_amount), 0) INTO total_income
    FROM billing_record WHERE DATE(br_pay_time) = report_date;

    SELECT COALESCE(SUM(br_amount), 0) INTO reg_fee
    FROM billing_record WHERE DATE(br_pay_time) = report_date AND br_type = '挂号费';

    SELECT COALESCE(SUM(br_amount), 0) INTO drug_fee
    FROM billing_record WHERE DATE(br_pay_time) = report_date AND br_type = '药费+检查费';

    SELECT COUNT(*) INTO completed_count
    FROM orders WHERE DATE(o_start) = report_date AND o_state = 7;

    SELECT COUNT(*) INTO cancelled_count
    FROM orders WHERE DATE(o_start) = report_date AND o_state = -1;

    SELECT COUNT(*) INTO missed_count
    FROM orders WHERE DATE(o_start) = report_date AND o_missed = 1;

    SELECT
        report_date AS 统计日期,
        total_reg AS 挂号总数,
        completed_count AS 完成就诊数,
        cancelled_count AS 取消数,
        missed_count AS 爽约数,
        total_income AS 总收入,
        reg_fee AS 挂号费收入,
        drug_fee AS 药费检查费收入,
        (total_reg - completed_count - cancelled_count) AS 在诊人数;
END$$
DELIMITER ;


-- 存储过程2：医生工作量统计
DROP PROCEDURE IF EXISTS sp_doctor_workload;
DELIMITER $$
CREATE PROCEDURE sp_doctor_workload(IN start_date DATE, IN end_date DATE, IN doctor_id CHAR(6))
BEGIN
    SELECT
        d.d_id          AS 医生编号,
        d.d_name        AS 医生姓名,
        d.d_section     AS 科室,
        d.d_post        AS 职称,
        COUNT(DISTINCT o.o_id)                          AS 接诊人次,
        COUNT(DISTINCT CASE WHEN o.o_state >= 7 THEN o.o_id END) AS 完成就诊数,
        COUNT(DISTINCT emr.emr_id)                      AS 书写病历数,
        COUNT(DISTINCT pm.pm_id)                        AS 开具处方数,
        COALESCE(SUM(br.br_amount), 0)                  AS 总费用,
        COUNT(DISTINCT CASE WHEN br.br_payment_method = '现金' THEN br.br_id END) AS 现金笔数,
        COUNT(DISTINCT CASE WHEN br.br_payment_method = '微信' THEN br.br_id END) AS 微信笔数,
        COUNT(DISTINCT CASE WHEN br.br_payment_method = '支付宝' THEN br.br_id END) AS 支付宝笔数
    FROM doctor d
    LEFT JOIN orders o ON d.d_id = o.d_id
        AND DATE(o.o_start) BETWEEN start_date AND end_date
    LEFT JOIN outpatient_emr emr ON o.o_id = emr.o_id
    LEFT JOIN prescription_master pm ON o.o_id = pm.o_id
    LEFT JOIN billing_record br ON o.o_id = br.o_id
    WHERE (doctor_id IS NULL OR doctor_id = '' OR d.d_id = doctor_id)
    GROUP BY d.d_id, d.d_name, d.d_section, d.d_post
    ORDER BY 接诊人次 DESC;
END$$
DELIMITER ;


-- =====================================================================
-- 第四部分：触发器（至少2个）
-- =====================================================================

-- 触发器1：订单取消后自动释放排队号码
DROP TRIGGER IF EXISTS trg_order_cancel_release_queue;
DELIMITER $$
CREATE TRIGGER trg_order_cancel_release_queue
    AFTER UPDATE ON orders
    FOR EACH ROW
BEGIN
    IF NEW.o_state = -1 AND (OLD.o_state IS NULL OR OLD.o_state >= 0) THEN
        UPDATE queue_number
        SET q_state = 3, q_finish_time = NOW()
        WHERE o_id = NEW.o_id AND q_state IN (0, 1);
    END IF;
END$$
DELIMITER ;


-- 触发器2：发药后自动扣减药品库存并记录流水
DROP TRIGGER IF EXISTS trg_dispensing_update_stock;
DELIMITER $$
CREATE TRIGGER trg_dispensing_update_stock
    AFTER UPDATE ON pharmacy_dispensing
    FOR EACH ROW
BEGIN
    DECLARE before_stock INT;
    DECLARE after_stock INT;

    IF NEW.pd_status = 2 AND (OLD.pd_status IS NULL OR OLD.pd_status < 2) THEN
        SELECT dr_number INTO before_stock FROM drug WHERE dr_id = NEW.dr_id;

        UPDATE drug
        SET dr_number = dr_number - NEW.pd_quantity
        WHERE dr_id = NEW.dr_id;

        SELECT dr_number INTO after_stock FROM drug WHERE dr_id = NEW.dr_id;

        INSERT INTO inventory_transaction
            (dr_id, db_id, it_type, it_quantity, it_before_quantity, it_after_quantity,
             it_reference, it_operator, it_note, it_create_time)
        VALUES
            (NEW.dr_id, NEW.db_id, '发药', -NEW.pd_quantity, before_stock, after_stock,
             CONCAT('DISPENSE-', NEW.pd_id), NEW.pd_dispense_by, '发药自动扣减库存', NOW());
    END IF;
END$$
DELIMITER ;


-- =====================================================================
-- 第五部分：实验数据录入
-- =====================================================================

-- 科室
INSERT IGNORE INTO department (de_id, de_name, de_number) VALUES
('D00001','内科',10),('D00002','外科',8),('D00003','妇产科',6),
('D00004','儿科',5),('D00005','五官科',4),('D00006','中医科',4),
('D00007','皮肤科',3),('D00008','急诊科',6);

-- 医生（16位）
INSERT IGNORE INTO doctor (d_id, de_id, d_password, d_name, d_gender, d_phone, d_card, d_email, d_post, d_section, d_state, d_price, d_star, d_avg_star, d_maxDaily) VALUES
('202401','D00001','123456','张伟','男','13800001001','110101199001011001','zhangwei@hospital.com','主任医师','内科',1,50.00,4.80,4.80,40),
('202402','D00001','123456','李芳','女','13800001002','110101199202022002','lifang@hospital.com','主治医师','内科',1,20.00,4.50,4.50,50),
('202403','D00002','123456','王刚','男','13800001003','110101198503033003','wanggang@hospital.com','主任医师','外科',1,50.00,4.90,4.90,30),
('202404','D00002','123456','赵敏','女','13800001004','110101199304044004','zhaomin@hospital.com','副主任医师','外科',1,30.00,4.60,4.60,40),
('202405','D00003','123456','陈静','女','13800001005','110101198805055005','chenjing@hospital.com','主任医师','妇产科',1,50.00,4.70,4.70,35),
('202406','D00003','123456','刘洋','女','13800001006','110101199406066006','liuyang@hospital.com','医师','妇产科',1,10.00,4.20,4.20,45),
('202407','D00004','123456','孙明','男','13800001007','110101198906077007','sunming@hospital.com','副主任医师','儿科',1,30.00,4.80,4.80,50),
('202408','D00004','123456','周丽','女','13800001008','110101199508088008','zhouli@hospital.com','医师','儿科',1,10.00,4.30,4.30,60),
('202409','D00005','123456','吴强','男','13800001009','110101198706099009','wuqiang@hospital.com','主任医师','五官科',1,50.00,4.60,4.60,30),
('202410','D00005','123456','郑洁','女','13800001010','110101199606100010','zhengjie@hospital.com','主治医师','五官科',1,20.00,4.40,4.40,45),
('202411','D00006','123456','冯涛','男','13800001011','110101198406111011','fengtao@hospital.com','主任医师','中医科',1,50.00,4.90,4.90,35),
('202412','D00006','123456','韩雪','女','13800001012','110101199706122012','hanxue@hospital.com','医师','中医科',1,10.00,4.10,4.10,55),
('202413','D00007','123456','曹磊','男','13800001013','110101198506133013','caolei@hospital.com','副主任医师','皮肤科',1,30.00,4.50,4.50,40),
('202414','D00007','123456','彭丽','女','13800001014','110101199806144014','pengli@hospital.com','主治医师','皮肤科',1,20.00,4.30,4.30,50),
('202415','D00008','123456','董浩','男','13800001015','110101198206155015','donghao@hospital.com','主任医师','急诊科',1,50.00,4.80,4.80,60),
('202416','D00008','123456','魏芳','女','13800001016','110101199106166016','weifang@hospital.com','副主任医师','急诊科',1,30.00,4.50,4.50,50);

-- 患者（12位）
INSERT IGNORE INTO patient (p_id, p_password, p_name, p_gender, p_phone, p_card, p_email, p_state, p_birthday, p_age, p_insurance_id, p_insurance_type, p_contact_person, p_contact_phone, p_address, p_nation, p_marital_status, p_blood_type) VALUES
(10001,'123456','张三','男','13900010001','320101198503010011','zhangsan@qq.com',1,'1985-03-01',41,'YB10001','城镇职工','张父','13900010002','北京市海淀区中关村大街1号','汉族','已婚','A'),
(10002,'123456','李四','女','13900020001','320101199007150022','lisi@qq.com',1,'1990-07-15',36,'YB10002','城镇居民','李母','13900020002','北京市朝阳区建国路2号','汉族','已婚','B'),
(10003,'123456','王五','男','13900030001','320101198812200033','wangwu@qq.com',1,'1988-12-20',37,'YB10003','城镇职工','王妻','13900030002','北京市西城区复兴门3号','汉族','已婚','O'),
(10004,'123456','赵六','女','13900040001','320101199509080044','zhaoliu@qq.com',1,'1995-09-08',30,'YB10004','城镇居民','赵姐','13900040002','北京市丰台区方庄路4号','汉族','未婚','AB'),
(10005,'123456','孙七','男','13900050001','320101198005250055','sunqi@qq.com',1,'1980-05-25',46,'YB10005','城镇职工','孙妻','13900050002','北京市石景山区古城路5号','汉族','已婚','A'),
(10006,'123456','周八','女','13900060001','320101199811300066','zhouba@qq.com',1,'1998-11-30',27,'YB10006','城镇居民','周母','13900060002','北京市通州区新华大街6号','汉族','未婚','B'),
(10007,'123456','吴九','男','13900070001','320101197506180077','wujiu@qq.com',1,'1975-06-18',51,'YB10007','城镇职工','吴子','13900070002','北京市大兴区黄村7号','汉族','已婚','O'),
(10008,'123456','郑十','女','13900080001','320101200003120088','zhengshi@qq.com',1,'2000-03-12',26,'YB10008','城镇居民','郑父','13900080002','北京市昌平区回龙观8号','汉族','未婚','A'),
(10009,'123456','刘备','男','13900090001','320101196504220099','liubei@qq.com',1,'1965-04-22',61,'YB10009','城镇职工','刘妻','13900090002','北京市顺义区光明街9号','汉族','已婚','B'),
(10010,'123456','关羽','男','13900100001','320101197008150101','guanyu@qq.com',1,'1970-08-15',55,'YB10010','城镇职工','关妻','13900100002','北京市房山区良乡10号','汉族','已婚','AB'),
(10012,'123456','赵云','男','13900120001','320101197812250121','zhaoyun@qq.com',1,'1978-12-25',47,'YB10012','城镇职工','赵妻','13900120002','北京市东城区王府井13号','汉族','已婚','A');

-- 药品（20种）
INSERT IGNORE INTO drug (dr_id, dr_name, dr_generic_name, dr_price, dr_number, dr_publisher, dr_unit, dr_type, dr_spec, dr_approval_no, dr_form, dr_manufacturer, dr_subtype, dr_rx_type, dr_insurance_type, dr_min_stock) VALUES
('DR0001','阿莫西林胶囊','阿莫西林',15.50,500,'国家药监局','盒',1,'0.25g*24粒','国药准字H44021351','胶囊剂','广州白云山制药','西药','处方药','医保甲类',50),
('DR0002','头孢克洛缓释片','头孢克洛',28.00,300,'国家药监局','盒',1,'0.375g*12片','国药准字H10960107','片剂','深圳致君制药','西药','处方药','医保甲类',30),
('DR0003','布洛芬缓释胶囊','布洛芬',22.00,400,'国家药监局','盒',1,'0.3g*20粒','国药准字H20093691','胶囊剂','中美天津史克','西药','非处方药','医保乙类',40),
('DR0004','阿托伐他汀钙片','阿托伐他汀钙',45.00,250,'国家药监局','盒',1,'20mg*7片','国药准字H20051407','片剂','辉瑞制药','西药','处方药','医保乙类',25),
('DR0005','盐酸二甲双胍片','二甲双胍',12.00,600,'国家药监局','盒',1,'0.5g*20片','国药准字H20023370','片剂','上海施贵宝','西药','处方药','医保甲类',60),
('DR0006','苯磺酸氨氯地平片','氨氯地平',35.00,350,'国家药监局','盒',1,'5mg*7片','国药准字H10950224','片剂','辉瑞制药','西药','处方药','医保甲类',35),
('DR0007','蒙脱石散','蒙脱石',18.00,200,'国家药监局','盒',1,'3g*10袋','国药准字H20000690','散剂','博福-益普生','西药','非处方药','医保乙类',20),
('DR0008','氯雷他定片','氯雷他定',25.00,300,'国家药监局','盒',1,'10mg*6片','国药准字H20051114','片剂','先灵葆雅','西药','非处方药','医保乙类',30),
('DR0009','奥美拉唑肠溶胶囊','奥美拉唑',32.00,280,'国家药监局','盒',1,'20mg*14粒','国药准字H20059008','胶囊剂','阿斯利康','西药','处方药','医保乙类',28),
('DR0010','左氧氟沙星片','左氧氟沙星',38.00,220,'国家药监局','盒',1,'0.5g*4片','国药准字H20059217','片剂','第一三共','西药','处方药','医保乙类',22),
('DR0011','复方丹参滴丸','复方丹参',28.50,400,'国家药监局','瓶',2,'27mg*180丸','国药准字Z10950111','滴丸','天士力制药','中成药','处方药','医保甲类',40),
('DR0012','板蓝根颗粒','板蓝根',15.00,500,'国家药监局','袋',2,'10g*20袋','国药准字Z44021532','颗粒剂','广州白云山和记黄埔','中成药','非处方药','医保乙类',50),
('DR0013','六味地黄丸','六味地黄',35.00,350,'国家药监局','瓶',2,'360丸','国药准字Z41022128','丸剂','北京同仁堂','中成药','非处方药','医保甲类',35),
('DR0014','连花清瘟胶囊','连花清瘟',26.00,450,'国家药监局','盒',2,'0.35g*36粒','国药准字Z20040063','胶囊剂','石家庄以岭药业','中成药','非处方药','医保甲类',45),
('DR0015','云南白药气雾剂','云南白药',48.00,180,'国家药监局','瓶',2,'85g/30g','国药准字Z53021107','气雾剂','云南白药集团','中成药','非处方药','医保乙类',18),
('DR0016','消炎利胆片','消炎利胆',12.50,300,'国家药监局','瓶',2,'100片','国药准字Z44022008','片剂','广州白云山','中成药','处方药','医保甲类',30),
('DR0017','小柴胡颗粒','小柴胡',16.00,380,'国家药监局','盒',2,'10g*10袋','国药准字Z44020777','颗粒剂','广州白云山光华','中成药','非处方药','医保乙类',38),
('DR0018','葡萄糖注射液','葡萄糖',5.00,1000,'国家药监局','瓶',1,'5% 500ml','国药准字H20043201','注射液','四川科伦药业','西药','处方药','医保甲类',100),
('DR0019','氯化钠注射液','氯化钠',4.50,800,'国家药监局','瓶',1,'0.9% 500ml','国药准字H20055008','注射液','四川科伦药业','西药','处方药','医保甲类',80),
('DR0020','藿香正气水','藿香正气',9.80,420,'国家药监局','盒',2,'10ml*10支','国药准字Z11020377','口服液','北京同仁堂','中成药','非处方药','医保乙类',42);

-- 检查项目（10项）
INSERT IGNORE INTO checks (ch_id, ch_name, ch_price) VALUES
('CH0001','血常规（三分类）',25.00),('CH0002','尿常规',15.00),
('CH0003','肝功能全套',120.00),('CH0004','肾功能三项',80.00),
('CH0005','空腹血糖',10.00),('CH0006','胸部X线正位片',95.00),
('CH0007','心电图（十二导联）',35.00),('CH0008','腹部B超（肝胆脾胰）',150.00),
('CH0009','CT平扫（头颅）',350.00),('CH0010','核磁共振（腰椎）',650.00);

-- 挂号订单（8条）
INSERT IGNORE INTO orders (o_id, p_id, d_id, o_record, o_start, o_state, o_total_price, o_price_state, o_triage, o_registration_fee, o_reg_type) VALUES
(2026070401,10001,'202401','头痛、发热、咳嗽3天','2026-07-04 08:35:00',5,225.50,1,'普通',50.00,'普通门诊'),
(2026070402,10002,'202404','右上腹疼痛1周','2026-07-04 09:10:00',5,380.00,1,'专家',30.00,'专家门诊'),
(2026070403,10003,'202403','左侧膝关节扭伤2天','2026-07-04 09:45:00',3,0.00,0,'普通',50.00,'普通门诊'),
(2026070404,10004,'202405','停经6周，要求产检','2026-07-04 10:20:00',7,180.00,1,'普通',50.00,'普通门诊'),
(2026070405,10005,'202407','发热39.5℃一天','2026-07-04 10:55:00',5,80.00,1,'普通',30.00,'普通门诊'),
(2026070406,10006,'202409','右眼红肿疼痛3天','2026-07-04 11:30:00',2,0.00,0,'专家',50.00,'专家门诊'),
(2026070407,10007,'202411','腰酸背痛半年','2026-07-04 14:00:00',7,165.00,1,'普通',50.00,'普通门诊'),
(2026070408,10008,'202416','突发胸痛2小时','2026-07-04 14:35:00',6,650.00,1,'急诊',30.00,'急诊');

-- 排队号
INSERT IGNORE INTO queue_number (o_id, p_id, d_id, q_number, q_state, q_create_time) VALUES
(2026070401,10001,'202401','N001',3,'2026-07-04 08:35:00'),
(2026070402,10002,'202404','W001',3,'2026-07-04 09:10:00'),
(2026070403,10003,'202403','W002',1,'2026-07-04 09:45:00'),
(2026070404,10004,'202405','F001',3,'2026-07-04 10:20:00'),
(2026070405,10005,'202407','P001',3,'2026-07-04 10:55:00'),
(2026070406,10006,'202409','E001',0,'2026-07-04 11:30:00'),
(2026070407,10007,'202411','T001',3,'2026-07-04 14:00:00'),
(2026070408,10008,'202416','A001',3,'2026-07-04 14:35:00');

-- 门诊病历
INSERT IGNORE INTO outpatient_emr (o_id, p_id, d_id, chief_complaint, present_illness, past_history, physical_exam, diagnosis, treatment_plan, create_time) VALUES
(2026070401,10001,'202401','头痛、发热、咳嗽3天','受凉后出现头痛咽痛T38.2℃伴干咳','既往体健无药物过敏史','T38.2℃ P88次/分 R20次/分 BP120/80mmHg 咽部充血','急性上呼吸道感染','1.阿莫西林0.5g tid 2.布洛芬0.3g prn 3.多饮水注意休息','2026-07-04 09:00:00'),
(2026070402,10002,'202404','右上腹疼痛1周','右上腹阵发性隐痛进食油腻后加重','胆囊结石病史2年','腹部软右上腹轻压痛Murphy征(±)','慢性胆囊炎急性发作','1.头孢克洛0.375g bid 2.消炎利胆片3# tid 3.低脂饮食','2026-07-04 09:45:00'),
(2026070404,10004,'202405','停经6周要求产检','LMP 2026-05-20 停经6周自测尿HCG(+)','G1P0无流产史','BP116/72mmHg妇科检查未见异常','早孕','1.产前检查(血尿常规肝功能) 2.叶酸0.4mg qd 3.预约NT','2026-07-04 10:55:00'),
(2026070405,10005,'202407','发热一天','无诱因发热T39.5℃伴咳嗽鼻塞流涕食欲减退','既往体健按时预防接种','T39.5℃ P120次/分 R28次/分 咽部充血双肺呼吸音粗可闻痰鸣音','急性支气管炎(小儿)','1.阿莫西林按体重 2.蒙脱石散备用 3.物理降温多饮水','2026-07-04 11:30:00'),
(2026070407,10007,'202411','腰酸背痛半年','近半年反复腰酸久坐加重伴头晕耳鸣夜尿增多','既往体健','BP128/82mmHg腰部无压痛肾区无叩击痛','肾阴虚证','1.六味地黄丸8丸 tid 2.避免劳累适当锻炼 3.忌辛辣生冷','2026-07-04 14:45:00'),
(2026070408,10008,'202416','突发胸痛2小时','突发胸骨后压榨性疼痛向左肩放射伴出汗胸闷','高血压病史5年','T36.5℃ P96次/分 R22次/分 BP160/95mmHg','冠心病不稳定型心绞痛','1.阿托伐他汀20mg qn 2.氨氯地平5mg qd 3.胸部X线+心电图 4.低盐低脂饮食','2026-07-04 15:10:00');

-- 处方主表
INSERT IGNORE INTO prescription_master (pm_id, o_id, d_id, pm_diagnosis, pm_type, pm_status, pm_create_time) VALUES
(1,2026070401,'202401','急性上呼吸道感染','西药',1,'2026-07-04 09:05:00'),
(2,2026070402,'202404','慢性胆囊炎急性发作','混合',1,'2026-07-04 09:50:00'),
(3,2026070404,'202405','早孕','西药',1,'2026-07-04 10:55:00'),
(4,2026070405,'202407','急性支气管炎(小儿)','西药',1,'2026-07-04 11:35:00'),
(5,2026070407,'202411','肾阴虚证','中药',1,'2026-07-04 14:55:00'),
(6,2026070408,'202416','冠心病不稳定型心绞痛','西药',1,'2026-07-04 15:15:00');

-- 处方明细
INSERT IGNORE INTO prescription_detail (o_id, pm_id, dr_id, pd_usage, pd_dosage, pd_frequency, pd_days, pd_quantity, pd_price) VALUES
(2026070401,1,'DR0001','口服','0.5g','tid',7,21,15.50),
(2026070401,1,'DR0003','口服','0.3g','bid',3,6,22.00),
(2026070402,2,'DR0002','口服','0.375g','bid',7,14,28.00),
(2026070402,2,'DR0016','口服','3片','tid',14,42,12.50),
(2026070404,3,'DR0001','口服','0.5g','tid',5,15,15.50),
(2026070405,4,'DR0001','口服','按体重','tid',5,15,15.50),
(2026070405,4,'DR0007','口服','3g','tid',3,9,18.00),
(2026070407,5,'DR0013','口服','8丸','tid',30,240,35.00),
(2026070408,6,'DR0004','口服','20mg','qn',30,30,45.00),
(2026070408,6,'DR0006','口服','5mg','qd',30,30,35.00);

-- 药品批次
INSERT IGNORE INTO drug_batch (db_id, dr_id, db_batch_no, db_expire_date, db_quantity, db_purchase_price, db_supplier, db_create_time) VALUES
(1,'DR0001','AMX20260701','2027-06-30',500,12.00,'广州白云山制药','2026-07-01 08:00:00'),
(2,'DR0002','CFX20260701','2027-12-31',300,22.00,'深圳致君制药','2026-07-01 08:00:00'),
(3,'DR0004','ATV20260701','2027-06-30',250,38.00,'辉瑞制药','2026-07-01 08:00:00');

-- 发药记录
INSERT IGNORE INTO pharmacy_dispensing (pd_id, o_id, dr_id, db_id, pd_quantity, pd_status, pd_create_time, pd_dispense_time, pd_dispense_by) VALUES
(1,2026070401,'DR0001',1,21,2,'2026-07-04 09:10:00','2026-07-04 09:30:00','药师A'),
(2,2026070402,'DR0002',2,14,2,'2026-07-04 09:55:00','2026-07-04 10:15:00','药师B'),
(3,2026070408,'DR0004',3,30,2,'2026-07-04 15:20:00','2026-07-04 15:35:00','药师A');

-- 收费记录
INSERT IGNORE INTO billing_record (br_id, o_id, br_type, br_amount, br_payment_method, br_invoice_no, br_pay_time, br_operator) VALUES
(1,2026070401,'挂号费',50.00,'微信','INV-20260704-0001','2026-07-04 08:36:00','收费员A'),
(2,2026070401,'药费+检查费',225.50,'微信','INV-20260704-0001','2026-07-04 09:40:00','收费员A'),
(3,2026070402,'挂号费',30.00,'支付宝','INV-20260704-0002','2026-07-04 09:11:00','收费员B'),
(4,2026070402,'药费+检查费',380.00,'支付宝','INV-20260704-0002','2026-07-04 10:20:00','收费员B'),
(5,2026070408,'挂号费',30.00,'支付宝','INV-20260704-0003','2026-07-04 14:36:00','收费员A'),
(6,2026070408,'药费+检查费',650.00,'支付宝','INV-20260704-0003','2026-07-04 15:40:00','收费员A');

-- 发票记录
INSERT IGNORE INTO invoice_record (inv_no, o_id, br_id, inv_type, inv_amount, inv_status, inv_operator, inv_create_time) VALUES
('INV-20260704-0001',2026070401,1,'电子',275.50,0,'收费员A','2026-07-04 09:40:00'),
('INV-20260704-0002',2026070402,3,'电子',410.00,0,'收费员B','2026-07-04 10:20:00'),
('INV-20260704-0003',2026070408,5,'电子',680.00,0,'收费员A','2026-07-04 15:40:00');

-- 床位
INSERT IGNORE INTO bed (b_id, p_id, d_id, b_state, b_start, b_reason, b_type) VALUES
('B00001',10008,'202416',1,'2026-07-04 15:45:00','冠心病观察',0);

-- 通知
INSERT IGNORE INTO notification (n_id, p_id, d_id, n_type, n_title, n_content, n_is_read, n_create_time) VALUES
(1,10001,NULL,'queue','排队提醒','挂号(2026070401)已取号成功排队号N001请到内科候诊区等候',1,'2026-07-04 08:35:30'),
(2,10002,NULL,'queue','排队提醒','挂号(2026070402)已取号成功排队号W001请到外科候诊区等候',1,'2026-07-04 09:10:30'),
(3,10008,NULL,'payment','缴费提醒','有新的费用待缴纳：药费+检查费合计650.00元',0,'2026-07-04 15:20:00');

-- 审计日志
INSERT IGNORE INTO audit_log (al_id, al_user_id, al_user_role, al_action, al_target, al_detail, al_create_time) VALUES
(1,'10001','patient','ORDER_CREATE','o_id=2026070401','患者挂号：内科张伟主任医师','2026-07-04 08:35:00'),
(2,'202401','doctor','CONSULT_START','o_id=2026070401','医生开始接诊','2026-07-04 09:00:00'),
(3,'202401','doctor','PRESCRIPTION_SAVE','o_id=2026070401','开具处方：阿莫西林+布洛芬','2026-07-04 09:05:00'),
(4,'10001','patient','PAYMENT','o_id=2026070401','缴费275.50元(微信)','2026-07-04 09:40:00'),
(5,'药师A','pharmacist','DISPENSE','pd_id=1','发药：阿莫西林胶囊21粒','2026-07-04 09:30:00');

-- =====================================================================
-- 脚本执行完毕
-- 验证命令：
-- SHOW FULL TABLES WHERE Table_type='VIEW';
-- SHOW PROCEDURE STATUS WHERE Db='hospital';
-- SHOW TRIGGERS;
-- CALL sp_daily_clinic_report('2026-07-04');
-- CALL sp_doctor_workload('2026-07-01','2026-07-04',NULL);
-- SELECT * FROM v_drug_stock_alert;
-- =====================================================================
