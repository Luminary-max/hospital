-- =====================================================================
-- 医院门诊管理系统 - 全部触发器（共7个）
-- 说明：先 DROP 再 CREATE，可安全重复执行
-- =====================================================================

-- ==============================================================
-- 触发器1：before_insert_arrange（排班同步触发器）
-- 所属表：arrange（排班表）
-- 触发时机：向 arrange 表插入数据之前（BEFORE INSERT）
-- 业务功能：当新增一条排班记录时，自动将排班信息同步写入 arrangement 表
--           （arrangement 表用于存储某天的排班时间，支持arrange表的主外键关联）
-- 处理逻辑：
--   1. 声明一个局部变量 count 用来统计 arrangement 表中是否已有相同 ar_id
--   2. 根据新插入的 ar_id 去 arrangement 表查询
--   3. 如果 count = 0（不存在），则插入一条新记录
--   4. 如果已存在则不操作（避免主键冲突）
-- ==============================================================
DROP TRIGGER IF EXISTS `before_insert_arrange`;

DELIMITER $$

CREATE TRIGGER `before_insert_arrange`
    BEFORE INSERT ON `arrange`
    FOR EACH ROW
BEGIN
    -- 声明局部变量，用于存放查询到的记录数
    DECLARE count INT;

    -- 检查 arrangement 表中是否已经存在相同的 ar_id
    -- ar_id 是 arrangement 表的主键，不能重复
    SELECT COUNT(*) INTO count
    FROM arrangement
    WHERE ar_id = NEW.ar_id;

    -- 如果不存在相同记录，则插入新数据
    -- 这样保证了 arrange 和 arrangement 两张表的数据一致性
    IF count = 0 THEN
        INSERT INTO arrangement (ar_id, ar_time)
        VALUES (NEW.ar_id, NEW.ar_time);
    END IF;
END$$

DELIMITER ;


-- ==============================================================
-- 触发器2：before_insert_doctor（医生-科室关联触发器）
-- 所属表：doctor（医生表）
-- 触发时机：向 doctor 表插入数据之前（BEFORE INSERT）
-- 业务功能：新增医生时，根据医生填写的科室名称（d_section）
--           自动去 department 表找到对应的科室编号（de_id），
--           设置到新医生记录的 de_id 字段中，并让科室人数 +1
-- 处理逻辑：
--   1. 声明一个变量 section_id 用来存放查到的科室编号
--   2. 用 d_section（如"内科"）去 department 表匹配科室
--   3. 如果匹配到了，就将 de_id 赋值为该科室编号
--   4. 同时将该科室的 de_number（人数）加 1
--   5. 如果没匹配到，不做处理（由 department_before_insert_doctor 兜底创建科室）
-- 配合关系：这个触发器先执行，department_before_insert_doctor 后执行
-- ==============================================================
DROP TRIGGER IF EXISTS `before_insert_doctor`;

DELIMITER $$

CREATE TRIGGER `before_insert_doctor`
    BEFORE INSERT ON `doctor`
    FOR EACH ROW
BEGIN
    -- 声明变量用于存放查到的科室编号
    DECLARE section_id CHAR(6);

    -- 根据医生填写的科室名称（如"内科""外科"），查找对应的科室编号
    -- department 表的 de_name 存的是科室名称，de_id 存的是科室编号
    SELECT de_id INTO section_id
    FROM department
    WHERE de_name = NEW.d_section
    LIMIT 1;

    -- 如果找到了对应的科室，则设置医生所属科室编号，并让该科室人数加1
    IF section_id IS NOT NULL THEN
        -- 将科室编号赋值给医生记录的 de_id 字段
        SET NEW.de_id = section_id;
        -- 将该科室的统计人数加 1
        UPDATE department SET de_number = de_number + 1 WHERE de_id = section_id;
    END IF;
END$$

DELIMITER ;


-- ==============================================================
-- 触发器3：department_before_insert_doctor（科室自动创建触发器）
-- 所属表：doctor（医生表）
-- 触发时机：向 doctor 表插入数据之前（BEFORE INSERT）
--           在 before_insert_doctor 之后执行
-- 业务功能：如果医生填写的科室名称在 department 表中不存在，
--           则自动创建一个新的科室记录，生成科室编号并设人数为 1
--           如果科室已存在则直接使用已有科室编号并更新人数
-- 处理逻辑：
--   1. 声明两个变量：section_id（已有科室编号），new_section_id（新生成科室编号）
--   2. 根据 d_section 查询 department 表
--   3. 查不到（科室不存在）：
--      a. 用 CONCAT('S', LPAD(现有科室数+1, 5, '0')) 生成编号，如 S00001、S00012
--      b. 向 department 表插入一条新记录（编号、名称、人数=1）
--      c. 将新编号设置到医生记录的 de_id
--   4. 查到了（科室已存在）：
--      a. 直接使用查到编号
--      b. 该科室人数加 1
-- ==============================================================
DROP TRIGGER IF EXISTS `department_before_insert_doctor`;

DELIMITER $$

CREATE TRIGGER `department_before_insert_doctor`
    BEFORE INSERT ON `doctor`
    FOR EACH ROW
BEGIN
    -- 已有科室的编号
    DECLARE section_id CHAR(6);
    -- 新建科室的编号
    DECLARE new_section_id CHAR(6);

    -- 先查一下科室表中有没有对应的科室
    SELECT de_id INTO section_id
    FROM department
    WHERE de_name = NEW.d_section
    LIMIT 1;

    -- 如果科室表中找不到这个科室名称，说明是新科室，需要自动创建
    IF section_id IS NULL THEN
        -- 生成新的科室编号：S + 5位数字（如 S00001, S00002, ...）
        -- LPAD 函数在数字左边补 0 到 5 位
        SET new_section_id = CONCAT('S', LPAD((SELECT COUNT(*) + 1 FROM department), 5, '0'));
        -- 插入新科室：编号、名称、初始人数=1
        INSERT INTO department (de_id, de_name, de_number)
        VALUES (new_section_id, NEW.d_section, 1);
        -- 将新科室编号设置到医生记录中
        SET NEW.de_id = new_section_id;
    ELSE
        -- 科室已存在，直接使用已有编号
        SET NEW.de_id = section_id;
        -- 该科室人数增加 1
        UPDATE department
        SET de_number = de_number + 1
        WHERE de_id = section_id;
    END IF;
END$$

DELIMITER ;


-- ==============================================================
-- 触发器4：update_department_count_after_doctor_deactivation（离职减人触发器）
-- 所属表：doctor（医生表）
-- 触发时机：更新 doctor 表之后（AFTER UPDATE）
-- 业务功能：当医生从"在职"状态改为"离职"状态时，
--           自动将其所在科室的人数减 1，保持科室人数统计准确
-- 处理逻辑：
--   1. 判断两个条件同时满足：
--      a. NEW.d_state = 0（新状态是"离职"）
--      b. OLD.d_state != 0（旧状态不是"离职"，即确实发生了状态变化）
--   2. 条件满足时，找到医生的科室（de_id），将该科室人数减 1
--   3. 如果只是修改其他字段（如电话、职称），d_state 没变，则不会触发
-- ==============================================================
DROP TRIGGER IF EXISTS `update_department_count_after_doctor_deactivation`;

DELIMITER $$

CREATE TRIGGER `update_department_count_after_doctor_deactivation`
    AFTER UPDATE ON `doctor`
    FOR EACH ROW
BEGIN
    -- 只处理从"在职"变为"离职"的情况
    -- d_state: 1 = 在职, 0 = 离职
    IF NEW.d_state = 0 AND OLD.d_state != 0 THEN
        -- 将医生所在科室的总人数减 1
        UPDATE department
        SET de_number = de_number - 1
        WHERE de_id = NEW.de_id;
    END IF;
END$$

DELIMITER ;


-- ==============================================================
-- 触发器5：trg_order_cancel_release_queue（取消订单释放排队号触发器）
-- 所属表：orders（挂号订单表）
-- 触发时机：更新 orders 表之后（AFTER UPDATE）
-- 业务功能：当挂号订单被取消（o_state = -1）时，
--           自动将对应的排队叫号记录标记为"已完成"（q_state = 3），
--           释放号位，让后续患者的排队号可以往前递补
-- 处理逻辑：
--   1. 判断更新后的状态 o_state = -1（已取消）
--   2. 且旧状态不是 -1（排除重复触发）
--   3. 执行 UPDATE 将 queue_number 表中对应的记录状态改为 3（已完成）
--   4. 只影响还在等待（q_state=0）或正在就诊（q_state=1）的记录
--      - 如果已经完成就诊（q_state=3），则不重复操作
--   5. 同时记录完成时间 q_finish_time = NOW()
-- q_state 状态说明：0=待叫号, 1=已叫号, 2=已过号, 3=已完成
-- o_state 状态说明：-1=已取消, 0=已挂号, 3=已开处方, 7=已完成
-- ==============================================================
DROP TRIGGER IF EXISTS `trg_order_cancel_release_queue`;

DELIMITER $$

CREATE TRIGGER `trg_order_cancel_release_queue`
    AFTER UPDATE ON `orders`
    FOR EACH ROW
BEGIN
    -- 检测订单状态是否变为"已取消"
    -- o_state = -1 表示订单被取消
    -- OLD.o_state >= 0 确保不是重复触发（排除从 -1 再更新到 -1）
    IF NEW.o_state = -1 AND (OLD.o_state IS NULL OR OLD.o_state >= 0) THEN
        -- 释放排队号：将该订单的排队记录置为"已完成"
        UPDATE queue_number
        SET q_state = 3,              -- 3 = 已完成，表示号位已释放
            q_finish_time = NOW()     -- 记录完成时间
        WHERE o_id = NEW.o_id         -- 关联到被取消的订单
          AND q_state IN (0, 1);      -- 只释放还在等待或正在就诊的号
    END IF;
END$$

DELIMITER ;


-- ==============================================================
-- 触发器6：trg_missed_release_queue（爽约释放排队号+通知触发器）
-- 所属表：orders（挂号订单表）
-- 触发时机：更新 orders 表之后（AFTER UPDATE）
-- 业务功能：当患者被标记为"爽约"（o_missed = 1）时：
--   1. 自动释放该患者的排队号，让候诊队列可以继续叫号
--   2. 向 notification 消息通知表中插入一条爽约提醒记录，
--      以便患者下次登录时可以看到提醒
-- 处理逻辑：
--   1. 判断 o_missed 从 0 变为 1（从未爽约变为已爽约）
--   2. 更新 queue_number 表，将对应排队记录状态置为 3（已完成）
--   3. 向 notification 表插入一条通知：
--      - p_id: 患者ID（用于患者登录后查看）
--      - n_type: 'missed'（通知类型为爽约）
--      - n_title: 通知标题
--      - n_content: 通知内容（含订单号）
--      - n_is_read: 0 = 未读（患者登录后会显示红点提示）
--      - n_create_time: 当前时间
-- ==============================================================
DROP TRIGGER IF EXISTS `trg_missed_release_queue`;

DELIMITER $$

CREATE TRIGGER `trg_missed_release_queue`
    AFTER UPDATE ON `orders`
    FOR EACH ROW
BEGIN
    -- 检测是否从"未爽约"变为"已爽约"
    -- o_missed: 0 = 正常, 1 = 爽约
    IF NEW.o_missed = 1 AND (OLD.o_missed IS NULL OR OLD.o_missed = 0) THEN
        -- 第一步：释放排队号
        -- 将该订单的排队记录状态置为 3（已完成），号位释放给后面的人
        UPDATE queue_number
        SET q_state = 3,
            q_finish_time = NOW()
        WHERE o_id = NEW.o_id AND q_state IN (0, 1);

        -- 第二步：向通知表插入一条爽约提醒
        -- 患者登录后可以在消息中心看到这条通知
        INSERT INTO notification (
            p_id,           -- 患者ID，用于通知归属
            n_type,         -- 通知类型：missed = 爽约
            n_title,        -- 通知标题
            n_content,      -- 通知正文内容（包含订单ID方便患者查询）
            n_is_read,      -- 是否已读：0 = 未读（默认）
            n_create_time   -- 创建时间：当前系统时间
        ) VALUES (
            NEW.p_id,
            'missed',
            'Missed Appointment Alert',   -- 爽约提醒
            CONCAT('Your appointment (orderId=', NEW.o_id, ') has been marked as missed. Please re-register.'),
            0,
            NOW()
        );
    END IF;
END$$

DELIMITER ;


-- ==============================================================
-- 触发器7：trg_dispensing_update_stock（发药退药库存更新触发器）
-- 所属表：pharmacy_dispensing（药房发药明细表）
-- 触发时机：更新 pharmacy_dispensing 表之后（AFTER UPDATE）
-- 业务功能：药房人员进行发药或退药操作后，自动更新药品库存数量：
--   1. pd_status 从其他状态变为 1（已发药）→ 库存扣减
--   2. pd_status 从其他状态变为 2（已退药）→ 库存加回
--   同时自动记录一条库存变动流水到 inventory_transaction 表，
--   方便后续审计和库存追溯
-- 处理逻辑：
--   1. 声明三个变量：
--      - before_stock: 变动前库存数量
--      - after_stock: 变动后库存数量
--      - v_dr_id: 药品ID（通过处方明细表查询）
--   2. 判断 pd_status 的变化方向
--   3. 通过 presc_detail_id 关联 prescription_detail（处方明细表）
--      查出对应的药品 dr_id（drug 表主键）
--   4. 先记录变动前库存 before_stock
--   5. 执行 UPDATE 更新 drug 表的 dr_number（库存数量）
--   6. 记录变动后库存 after_stock
--   7. 将变动记录插入 inventory_transaction（库存流水表）
-- pd_status 状态说明：0=待发药, 1=已发药, 2=已退药
-- 注意：pharmacy_dispensing 表中没有直接存 dr_id，
--       需要通过 presc_detail_id → prescription_detail → dr_id 关联获取
-- ==============================================================
DROP TRIGGER IF EXISTS `trg_dispensing_update_stock`;

DELIMITER $$

CREATE TRIGGER `trg_dispensing_update_stock`
    AFTER UPDATE ON `pharmacy_dispensing`
    FOR EACH ROW
BEGIN
    -- 声明变量
    DECLARE before_stock INT;     -- 库存变动前的数量
    DECLARE after_stock INT;      -- 库存变动后的数量
    DECLARE v_dr_id CHAR(6);      -- 药品ID（从处方明细表查得）

    ============================================================
    -- 情况1：pd_status = 1（已发药）
    -- 发药意味着药品出库，需要从库存中扣减
    -- 触发条件：新状态pd_status=1，且旧状态pd_status<1（即从待发药变为已发药）
    ============================================================
    IF NEW.pd_status = 1 AND (OLD.pd_status IS NULL OR OLD.pd_status < 1) THEN
        -- 通过处方明细表查出发药对应的药品ID
        -- presc_detail_id 关联 prescription_detail 表的 pd_id
        SELECT dr_id INTO v_dr_id
        FROM prescription_detail
        WHERE pd_id = NEW.presc_detail_id
        LIMIT 1;

        -- 如果查到了药品ID，执行库存扣减
        IF v_dr_id IS NOT NULL THEN
            -- 记录扣减前的库存
            SELECT dr_number INTO before_stock
            FROM drug WHERE dr_id = v_dr_id;

            -- 扣减库存：发药数量从库存中减去
            UPDATE drug
            SET dr_number = dr_number - NEW.pd_quantity
            WHERE dr_id = v_dr_id;

            -- 记录扣减后的库存
            SELECT dr_number INTO after_stock
            FROM drug WHERE dr_id = v_dr_id;

            -- 写入库存变动流水记录（出库类型）
            INSERT INTO inventory_transaction (
                dr_id,              -- 药品ID
                db_id,              -- 批次ID
                it_type,            -- 变动类型：'fa yao' = 发药
                it_quantity,        -- 变动数量（负值表示出库）
                it_before_quantity, -- 变动前库存
                it_after_quantity,  -- 变动后库存
                it_reference,       -- 关联单据号
                it_operator,        -- 操作人
                it_note,            -- 备注说明
                it_create_time      -- 创建时间
            ) VALUES (
                v_dr_id,
                NEW.db_id,
                'fa yao',                               -- 类型：发药
                -NEW.pd_quantity,                        -- 出库为负值
                before_stock,
                after_stock,
                CONCAT('DISPENSE-', NEW.pd_id),         -- 关联发药单号
                NEW.pd_dispense_by,                     -- 发药人
                'trigger: dispense deduction',           -- 注明由触发器自动处理
                NOW()
            );
        END IF;

    ============================================================
    -- 情况2：pd_status = 2（已退药）
    -- 退药意味着药品重新入库，需要加回库存
    -- 触发条件：新状态pd_status=2，且旧状态pd_status<2（即从未退药变为已退药）
    ============================================================
    ELSEIF NEW.pd_status = 2 AND (OLD.pd_status IS NULL OR OLD.pd_status < 2) THEN
        -- 通过处方明细表查出发药对应的药品ID
        SELECT dr_id INTO v_dr_id
        FROM prescription_detail
        WHERE pd_id = NEW.presc_detail_id
        LIMIT 1;

        -- 如果查到了药品ID，执行库存加回
        IF v_dr_id IS NOT NULL THEN
            -- 记录加回前的库存
            SELECT dr_number INTO before_stock
            FROM drug WHERE dr_id = v_dr_id;

            -- 加回库存：退药数量加回到库存中
            UPDATE drug
            SET dr_number = dr_number + NEW.pd_quantity
            WHERE dr_id = v_dr_id;

            -- 记录加回后的库存
            SELECT dr_number INTO after_stock
            FROM drug WHERE dr_id = v_dr_id;

            -- 写入库存变动流水记录（入库类型）
            INSERT INTO inventory_transaction (
                dr_id,              -- 药品ID
                db_id,              -- 批次ID
                it_type,            -- 变动类型：'tui yao' = 退药
                it_quantity,        -- 变动数量（正值表示入库）
                it_before_quantity, -- 变动前库存
                it_after_quantity,  -- 变动后库存
                it_reference,       -- 关联单据号
                it_operator,        -- 操作人
                it_note,            -- 备注说明
                it_create_time      -- 创建时间
            ) VALUES (
                v_dr_id,
                NEW.db_id,
                'tui yao',                               -- 类型：退药
                NEW.pd_quantity,                          -- 入库为正值
                before_stock,
                after_stock,
                CONCAT('RETURN-', NEW.pd_id),            -- 关联退药单号
                NEW.pd_return_by,                        -- 退药人
                'trigger: return restock',                -- 注明由触发器自动处理
                NOW()
            );
        END IF;
    END IF;
END$$

DELIMITER ;


-- ==============================================================
-- 触发器8：trg_refund_void_invoice（退费作废发票触发器）
-- 所属表：refund_request（退费申请表）
-- 触发时机：更新 refund_request 表之后（AFTER UPDATE）
-- 业务功能：当退费申请被审核通过（rf_status = 1）时，
--           自动将关联的发票记录标记为"已作废"（inv_status = -1），
--           确保发票不能重复使用，符合财务合规要求
-- 处理逻辑：
--   1. 判断退费状态是否从"非通过"变为"已通过"
--   2. 通过 JOIN 关联 refund_request → billing_record（收费记录）
--      → invoice_record（发票记录）
--   3. 更新 invoice_record 表：
--      - inv_status = -1（已作废）
--      - inv_cancel_time = NOW()（作废时间）
--      - inv_cancel_reason = 'refund_approved rf_id=xxx'（作废原因）
--   4. 只作废状态为 0（正常）的发票，避免重复作废
-- rf_status 状态说明：0=待审核, 1=已通过, 2=已拒绝
-- inv_status 状态说明：0=正常, -1=已作废
-- ==============================================================
DROP TRIGGER IF EXISTS `trg_refund_void_invoice`;

DELIMITER $$

CREATE TRIGGER `trg_refund_void_invoice`
    AFTER UPDATE ON `refund_request`
    FOR EACH ROW
BEGIN
    -- 检测退费申请是否刚被审核通过
    -- rf_status = 1 表示审核通过
    -- 排除 OLD.rf_status = 1 的情况（防止重复触发）
    IF NEW.rf_status = 1 AND (OLD.rf_status IS NULL OR OLD.rf_status != 1) THEN
        -- 关联收费记录表找到对应的发票，将其状态置为"已作废"
        -- billing_record 存收费明细，invoice_record 存发票信息
        -- 两者通过 br_id 字段关联
        UPDATE invoice_record inv
        JOIN billing_record br ON inv.br_id = br.br_id
        SET inv.inv_status = -1,                              -- -1 = 已作废
            inv.inv_cancel_time = NOW(),                      -- 记录作废时间
            inv.inv_cancel_reason = CONCAT('refund_approved rf_id=', NEW.rf_id)  -- 注明对应的退费单号
        WHERE br.br_id = NEW.br_id                            -- 关联到本次退费对应的收费记录
          AND inv.inv_status = 0;                              -- 只作废状态正常的发票
    END IF;
END$$

DELIMITER ;
