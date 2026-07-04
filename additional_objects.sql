-- =====================================================================
-- 医院门诊管理系统 - 新增视图(2)、存储过程(3)、触发器(3)
-- 所有列别名使用英文避免GBK编码问题
-- =====================================================================

-- ===== 视图3：医生今日接诊队列 =====
DROP VIEW IF EXISTS v_doctor_today_queue;
CREATE VIEW v_doctor_today_queue AS
SELECT
    d.d_id          AS doctorId,
    d.d_name        AS doctorName,
    d.d_section     AS department,
    d.d_post        AS title,
    qn.q_number     AS queueNumber,
    qn.p_id         AS patientId,
    p.p_name        AS patientName,
    qn.q_state      AS queueState,
    CASE qn.q_state
        WHEN 0 THEN 'waiting'
        WHEN 1 THEN 'in_consult'
        WHEN 2 THEN 'skipped'
        WHEN 3 THEN 'finished'
        ELSE 'unknown'
    END             AS stateLabel,
    qn.q_create_time AS queueTime
FROM doctor d
JOIN queue_number qn ON d.d_id = qn.d_id
JOIN patient p ON qn.p_id = p.p_id
WHERE DATE(qn.q_create_time) = CURDATE()
ORDER BY d.d_id, qn.q_state ASC, qn.q_create_time ASC;

-- ===== 视图4：患者费用明细 =====
DROP VIEW IF EXISTS v_patient_billing_detail;
CREATE VIEW v_patient_billing_detail AS
SELECT
    p.p_id              AS patientId,
    p.p_name            AS patientName,
    o.o_id              AS orderId,
    o.o_start           AS orderTime,
    o.o_triage          AS regType,
    o.o_registration_fee AS regFee,
    IFNULL(
        (SELECT SUM(pd.pd_price * pd.pd_quantity)
         FROM prescription_detail pd WHERE pd.o_id = o.o_id), 0
    )                   AS drugFee,
    IFNULL(
        (SELECT SUM(oc.ch_price)
         FROM order_check oc WHERE oc.o_id = o.o_id), 0
    )                   AS checkFee,
    (o.o_registration_fee +
        IFNULL((SELECT SUM(pd.pd_price * pd.pd_quantity)
         FROM prescription_detail pd WHERE pd.o_id = o.o_id), 0) +
        IFNULL((SELECT SUM(oc.ch_price)
         FROM order_check oc WHERE oc.o_id = o.o_id), 0)
    )                   AS totalFee,
    o.o_price_state     AS payState,
    CASE WHEN o.o_price_state = 1 THEN 'paid' ELSE 'unpaid' END AS payStateLabel,
    o.o_payment_method  AS payMethod
FROM patient p
JOIN orders o ON p.p_id = o.p_id
ORDER BY o.o_start DESC;


-- ===== 存储过程3：科室收入统计 =====
DROP PROCEDURE IF EXISTS sp_department_income;

DELIMITER $$

CREATE PROCEDURE sp_department_income(IN start_date DATE, IN end_date DATE)
BEGIN
    SELECT
        d.d_section     AS department,
        COUNT(DISTINCT o.o_id) AS visitCount,
        COUNT(DISTINCT CASE WHEN o.o_state >= 7 THEN o.o_id END) AS completedCount,
        COALESCE(SUM(o.o_registration_fee), 0) AS totalRegFee,
        COALESCE(SUM(CASE WHEN br.br_type != 'gua hao fei' THEN br.br_amount ELSE 0 END), 0) AS drugCheckFee,
        COALESCE(SUM(br.br_amount), 0) AS totalIncome,
        ROUND(COALESCE(SUM(br.br_amount), 0) / NULLIF(COUNT(DISTINCT o.o_id), 0), 2) AS avgPerVisit
    FROM department dept
    JOIN doctor d ON dept.de_id = d.de_id
    LEFT JOIN orders o ON d.d_id = o.d_id
        AND DATE(o.o_start) BETWEEN start_date AND end_date
    LEFT JOIN billing_record br ON o.o_id = br.o_id
    GROUP BY d.d_section
    ORDER BY totalIncome DESC;
END$$

DELIMITER ;


-- ===== 存储过程4：患者就诊历史查询 =====
DROP PROCEDURE IF EXISTS sp_patient_visit_history;

DELIMITER $$

CREATE PROCEDURE sp_patient_visit_history(IN patient_id INT)
BEGIN
    SELECT
        o.o_id          AS orderId,
        o.o_start       AS orderTime,
        o.o_triage      AS regType,
        d.d_name        AS doctorName,
        d.d_section     AS department,
        emr.diagnosis   AS diagnosis,
        o.o_state       AS state,
        CASE o.o_state
            WHEN 0 THEN 'registered' WHEN 1 THEN 'triaged' WHEN 2 THEN 'consulting'
            WHEN 3 THEN 'ordered' WHEN 4 THEN 'pendingPay' WHEN 5 THEN 'paid'
            WHEN 6 THEN 'dispensed' WHEN 7 THEN 'completed' WHEN -1 THEN 'cancelled'
        END             AS stateLabel,
        o.o_total_price + o.o_registration_fee AS totalCost,
        CASE WHEN o.o_price_state = 1 THEN 'paid' ELSE 'unpaid' END AS payStatus,
        o.o_payment_method AS payMethod
    FROM orders o
    JOIN doctor d ON o.d_id = d.d_id
    LEFT JOIN outpatient_emr emr ON o.o_id = emr.o_id
    WHERE o.p_id = patient_id
    ORDER BY o.o_start DESC;
END$$

DELIMITER ;


-- ===== 存储过程5：药品消耗排行 =====
DROP PROCEDURE IF EXISTS sp_drug_consumption_ranking;

DELIMITER $$

CREATE PROCEDURE sp_drug_consumption_ranking(
    IN start_date DATE,
    IN end_date DATE,
    IN top_n INT
)
BEGIN
    IF top_n IS NULL OR top_n <= 0 THEN SET top_n = 10; END IF;

    SELECT
        d.dr_id         AS drugId,
        d.dr_name       AS drugName,
        d.dr_form       AS form,
        d.dr_manufacturer AS manufacturer,
        COALESCE(SUM(pd.pd_quantity), 0) AS totalQty,
        d.dr_unit       AS unit,
        ROUND(COALESCE(SUM(pd.pd_quantity * pd.pd_price), 0), 2) AS totalAmount,
        COUNT(DISTINCT pd.o_id) AS prescriptionCount,
        d.dr_number     AS currentStock
    FROM drug d
    LEFT JOIN prescription_detail pd ON d.dr_id = pd.dr_id
    LEFT JOIN orders o ON pd.o_id = o.o_id
        AND DATE(o.o_start) BETWEEN start_date AND end_date
    GROUP BY d.dr_id, d.dr_name, d.dr_form, d.dr_manufacturer, d.dr_number, d.dr_unit
    HAVING totalQty > 0
    ORDER BY totalQty DESC
    LIMIT top_n;
END$$

DELIMITER ;


-- ===== 触发器3：挂号后自动创建排队号码 =====
DROP TRIGGER IF EXISTS trg_order_auto_queue;

DELIMITER $$

CREATE TRIGGER trg_order_auto_queue
    AFTER INSERT ON orders
    FOR EACH ROW
BEGIN
    DECLARE dept_prefix CHAR(1);
    DECLARE today_max INT;
    DECLARE new_number VARCHAR(20);
    DECLARE dept_name VARCHAR(50);

    SELECT d_section INTO dept_name FROM doctor WHERE d_id = NEW.d_id;

    IF dept_name LIKE '%nei%' OR dept_name LIKE '%Nei%' OR dept_name LIKE '%NEI%'
       OR dept_name LIKE '%internal%' THEN SET dept_prefix = 'N';
    ELSEIF dept_name LIKE '%wai%' OR dept_name LIKE '%Wai%' OR dept_name LIKE '%surgery%' THEN SET dept_prefix = 'W';
    ELSEIF dept_name LIKE '%fu%' OR dept_name LIKE '%chan%' OR dept_name LIKE '%ob%' THEN SET dept_prefix = 'F';
    ELSEIF dept_name LIKE '%er%' OR dept_name LIKE '%ped%' THEN SET dept_prefix = 'P';
    ELSEIF dept_name LIKE '%wu%' OR dept_name LIKE '%yan%' OR dept_name LIKE '%kou%' OR dept_name LIKE '%ENT%' THEN SET dept_prefix = 'E';
    ELSEIF dept_name LIKE '%zhong%' OR dept_name LIKE '%TCM%' THEN SET dept_prefix = 'T';
    ELSEIF dept_name LIKE '%ji%' OR dept_name LIKE '%emergency%' THEN SET dept_prefix = 'A';
    ELSEIF dept_name LIKE '%pi%' OR dept_name LIKE '%derma%' THEN SET dept_prefix = 'S';
    ELSE SET dept_prefix = 'G';
    END IF;

    SELECT IFNULL(MAX(CAST(SUBSTRING(q_number, 2) AS UNSIGNED)), 0)
    INTO today_max
    FROM queue_number
    WHERE d_id = NEW.d_id AND DATE(q_create_time) = CURDATE();

    SET new_number = CONCAT(dept_prefix, LPAD(today_max + 1, 3, '0'));

    INSERT INTO queue_number (o_id, p_id, d_id, q_number, q_state, q_create_time)
    VALUES (NEW.o_id, NEW.p_id, NEW.d_id, new_number, 0, NOW());
END$$

DELIMITER ;


-- ===== 触发器4：退费审批通过后自动作废发票 =====
DROP TRIGGER IF EXISTS trg_refund_void_invoice;

DELIMITER $$

CREATE TRIGGER trg_refund_void_invoice
    AFTER UPDATE ON refund_request
    FOR EACH ROW
BEGIN
    IF NEW.rf_status = 1 AND (OLD.rf_status IS NULL OR OLD.rf_status != 1) THEN
        UPDATE invoice_record
        SET inv_status = -1,
            inv_cancel_time = NOW(),
            inv_cancel_reason = CONCAT('refund_approved rf_id=', NEW.rf_id)
        WHERE o_id = NEW.o_id AND inv_status = 0;
    END IF;
END$$

DELIMITER ;


-- ===== 触发器5：爽约标记后自动释放排队号并通知 =====
DROP TRIGGER IF EXISTS trg_missed_release_queue;

DELIMITER $$

CREATE TRIGGER trg_missed_release_queue
    AFTER UPDATE ON orders
    FOR EACH ROW
BEGIN
    IF NEW.o_missed = 1 AND (OLD.o_missed IS NULL OR OLD.o_missed = 0) THEN
        UPDATE queue_number
        SET q_state = 3, q_finish_time = NOW()
        WHERE o_id = NEW.o_id AND q_state IN (0, 1);

        INSERT INTO notification (p_id, n_type, n_title, n_content, n_is_read, n_create_time)
        VALUES (NEW.p_id, 'missed',
            'Missed Appointment Alert',
            CONCAT('Your appointment (orderId=', NEW.o_id, ') has been marked as missed. Please re-register.'),
            0, NOW());
    END IF;
END$$

DELIMITER ;
