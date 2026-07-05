-- =====================================================================
-- 修复触发器 & 存储过程脚本
-- 问题修复:
--   1. before_insert_doctor -> 调用不存在的存储过程，已废弃，删除
--   2. trg_dispensing_update_stock -> 退药/发药逻辑反了，重写
--   3. trg_order_auto_queue -> 引用 queue_number 表中不存在的列，已废弃，删除
--   4. 统一用 drop-if-exists + recreate 安全执行
-- =====================================================================

-- ==============================================================
-- 修复1: 删除废弃的 before_insert_doctor（get_or_create_department_id 不存在）
--        保留 department_before_insert_doctor（内联逻辑完整）
-- ==============================================================
DROP TRIGGER IF EXISTS `before_insert_doctor`;

-- ==============================================================
-- 修复2: 删除 trg_order_auto_queue（引用 queue_number.d_id/p_id/q_number 等不存在的列）
-- ==============================================================
DROP TRIGGER IF EXISTS `trg_order_auto_queue`;

-- ==============================================================
-- 修复3: 重写 trg_dispensing_update_stock
--   原问题:
--     - pd_status=2(退药) 时却在扣库存 (dr_number - pd_quantity)，逻辑反了
--     - 发药(pd_status=1) 没有触发器扣库存
--   修复后:
--     - pd_status=1(已发药): 扣减库存，记录出库流水
--     - pd_status=2(已退药): 加回库存，记录退药流水
--     - 通过 presc_detail_id JOIN 获取 dr_id（pharmacy_dispensing 表无直接 dr_id 列）
-- ==============================================================
DROP TRIGGER IF EXISTS `trg_dispensing_update_stock`;

DELIMITER $$

CREATE TRIGGER `trg_dispensing_update_stock`
    AFTER UPDATE ON `pharmacy_dispensing`
    FOR EACH ROW
BEGIN
    DECLARE before_stock INT;
    DECLARE after_stock INT;
    DECLARE v_dr_id CHAR(6);

    -- pd_status=1: 已发药 → 扣库存
    IF NEW.pd_status = 1 AND (OLD.pd_status IS NULL OR OLD.pd_status < 1) THEN
        SELECT dr_id INTO v_dr_id FROM prescription_detail WHERE pd_id = NEW.presc_detail_id LIMIT 1;
        IF v_dr_id IS NOT NULL THEN
            SELECT dr_number INTO before_stock FROM drug WHERE dr_id = v_dr_id;
            UPDATE drug SET dr_number = dr_number - NEW.pd_quantity WHERE dr_id = v_dr_id;
            SELECT dr_number INTO after_stock FROM drug WHERE dr_id = v_dr_id;

            INSERT INTO inventory_transaction
                (dr_id, db_id, it_type, it_quantity, it_before_quantity, it_after_quantity,
                 it_reference, it_operator, it_note, it_create_time)
            VALUES
                (v_dr_id, NEW.db_id, 'fa yao', -NEW.pd_quantity, before_stock, after_stock,
                 CONCAT('DISPENSE-', NEW.pd_id), NEW.pd_dispense_by,
                 'trigger: dispense deduction', NOW());
        END IF;

    -- pd_status=2: 已退药 → 加回库存
    ELSEIF NEW.pd_status = 2 AND (OLD.pd_status IS NULL OR OLD.pd_status < 2) THEN
        SELECT dr_id INTO v_dr_id FROM prescription_detail WHERE pd_id = NEW.presc_detail_id LIMIT 1;
        IF v_dr_id IS NOT NULL THEN
            SELECT dr_number INTO before_stock FROM drug WHERE dr_id = v_dr_id;
            UPDATE drug SET dr_number = dr_number + NEW.pd_quantity WHERE dr_id = v_dr_id;
            SELECT dr_number INTO after_stock FROM drug WHERE dr_id = v_dr_id;

            INSERT INTO inventory_transaction
                (dr_id, db_id, it_type, it_quantity, it_before_quantity, it_after_quantity,
                 it_reference, it_operator, it_note, it_create_time)
            VALUES
                (v_dr_id, NEW.db_id, 'tui yao', NEW.pd_quantity, before_stock, after_stock,
                 CONCAT('RETURN-', NEW.pd_id), NEW.pd_return_by,
                 'trigger: return restock', NOW());
        END IF;
    END IF;
END$$

DELIMITER ;
