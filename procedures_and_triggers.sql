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
    SELECT COALESCE(SUM(br_amount), 0) INTO total_income FROM billing_record WHERE DATE(br_pay_time) = report_date;
    SELECT COALESCE(SUM(br_amount), 0) INTO reg_fee FROM billing_record WHERE DATE(br_pay_time) = report_date AND br_type = 'gua hao fei';
    SELECT COALESCE(SUM(br_amount), 0) INTO drug_fee FROM billing_record WHERE DATE(br_pay_time) = report_date AND br_type != 'gua hao fei';
    SELECT COUNT(*) INTO completed_count FROM orders WHERE DATE(o_start) = report_date AND o_state = 7;
    SELECT COUNT(*) INTO cancelled_count FROM orders WHERE DATE(o_start) = report_date AND o_state = -1;
    SELECT COUNT(*) INTO missed_count FROM orders WHERE DATE(o_start) = report_date AND o_missed = 1;

    SELECT
        report_date AS reportDate,
        total_reg AS totalRegistrations,
        completed_count AS completedVisits,
        cancelled_count AS cancelled,
        missed_count AS missed,
        total_income AS totalIncome,
        reg_fee AS registrationFeeIncome,
        drug_fee AS drugCheckFeeIncome,
        (total_reg - completed_count - cancelled_count) AS inProgressCount;
END$$

DELIMITER ;


DROP PROCEDURE IF EXISTS sp_doctor_workload;

DELIMITER $$

CREATE PROCEDURE sp_doctor_workload(IN start_date DATE, IN end_date DATE, IN doctor_id CHAR(6))
BEGIN
    SELECT
        d.d_id          AS doctorId,
        d.d_name        AS doctorName,
        d.d_section     AS department,
        d.d_post        AS title,
        COUNT(DISTINCT o.o_id) AS totalVisits,
        COUNT(DISTINCT CASE WHEN o.o_state >= 7 THEN o.o_id END) AS completedVisits,
        COUNT(DISTINCT emr.emr_id) AS emrCount,
        COUNT(DISTINCT pm.pm_id) AS prescriptionCount,
        COALESCE(SUM(br.br_amount), 0) AS totalBilling
    FROM doctor d
    LEFT JOIN orders o ON d.d_id = o.d_id
        AND DATE(o.o_start) BETWEEN start_date AND end_date
    LEFT JOIN outpatient_emr emr ON o.o_id = emr.o_id
    LEFT JOIN prescription_master pm ON o.o_id = pm.o_id
    LEFT JOIN billing_record br ON o.o_id = br.o_id
    WHERE (doctor_id IS NULL OR doctor_id = '' OR d.d_id = doctor_id)
    GROUP BY d.d_id, d.d_name, d.d_section, d.d_post
    ORDER BY totalVisits DESC;
END$$

DELIMITER ;


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
            (NEW.dr_id, NEW.db_id, 'fa yao', -NEW.pd_quantity, before_stock, after_stock,
             CONCAT('DISPENSE-', NEW.pd_id), NEW.pd_dispense_by,
             'auto stock deduction by trigger', NOW());
    END IF;
END$$

DELIMITER ;
