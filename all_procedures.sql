-- =====================================================================
-- 医院门诊管理系统 - 全部存储过程（共8个）
-- 说明：先 DROP 再 CREATE，可安全重复执行
-- =====================================================================

-- ==============================================================
-- 存储过程1：insert_check（插入检查项目）
-- 功能说明：向 checks（检查项目表）插入一条新的检查项目记录
-- 参数说明：
--   ch_name_param  VARCHAR(255)  - 检查项目名称，如"血常规""CT检查"
--   ch_price_param DECIMAL(10,2) - 检查项目价格，如 50.00
-- 使用场景：管理员在后台维护检查项目列表时调用
-- 表结构：checks(ch_id, ch_name, ch_price)
--   ch_id 为自增主键，插入时无需传入
-- ==============================================================
DROP PROCEDURE IF EXISTS `insert_check`;

DELIMITER $$

CREATE PROCEDURE `insert_check`(
    IN ch_name_param VARCHAR(255),      -- 检查项目名称
    IN ch_price_param DECIMAL(10, 2)    -- 检查项目价格
)
BEGIN
    -- 将传入的名称和价格插入到 checks 表中
    -- ch_id 由数据库自增自动生成
    INSERT INTO checks (ch_name, ch_price)
    VALUES (ch_name_param, ch_price_param);
END$$

DELIMITER ;


-- ==============================================================
-- 存储过程2：insert_drug（插入药品）
-- 功能说明：向 drug（药品表）插入一条新的药品记录
-- 参数说明：
--   dr_name_param     VARCHAR(255)  - 药品名称，如"阿莫西林"
--   dr_price_param    DECIMAL(10,2) - 药品单价
--   dr_number_param   INT           - 初始库存数量
--   dr_publisher_param VARCHAR(255)  - 生产厂家名称
--   dr_unit_param     VARCHAR(255)  - 药品单位，如"盒""瓶""支"
-- 使用场景：管理员在药房管理中添加新药品时调用
-- 注意：此存储过程只插入基本字段，药品的扩展字段
--       （分类、规格、剂型等）需要后续更新补充
-- ==============================================================
DROP PROCEDURE IF EXISTS `insert_drug`;

DELIMITER $$

CREATE PROCEDURE `insert_drug`(
    IN dr_name_param VARCHAR(255),       -- 药品名称
    IN dr_price_param DECIMAL(10, 2),    -- 药品单价
    IN dr_number_param INT,              -- 初始库存数量
    IN dr_publisher_param VARCHAR(255),  -- 生产厂家
    IN dr_unit_param VARCHAR(255)        -- 药品单位（盒/瓶/支）
)
BEGIN
    -- 向 drug 表插入一条基本药品信息
    -- dr_id 由数据库自增自动生成
    -- 其他字段如规格、剂型、分类等默认为 NULL，后续可在编辑页面补充
    INSERT INTO drug (dr_name, dr_price, dr_number, dr_publisher, dr_unit)
    VALUES(dr_name_param, dr_price_param, dr_number_param, dr_publisher_param, dr_unit_param);
END$$

DELIMITER ;


-- ==============================================================
-- 存储过程3：insert_orders（插入挂号订单）
-- 功能说明：向 orders（挂号订单表）插入一条新的挂号订单记录
-- 参数说明：
--   p_id_param        INT           - 患者 ID
--   d_id_param        CHAR(6)       - 医生 ID
--   o_record_param    VARCHAR(255)  - 挂号记录/备注
--   o_start_param     VARCHAR(255)  - 挂号时间段开始时间
--   o_end_param       VARCHAR(255)  - 挂号时间段结束时间
--   o_state_param     INT           - 订单状态（0=已挂号, 3=已开处方, 7=已完成等）
--   o_drug_param      VARCHAR(255)  - 开药信息
--   o_check_param     VARCHAR(255)  - 检查信息
--   o_total_price_param DECIMAL     - 订单总金额
--   o_price_state_param INT         - 缴费状态（0=未缴费, 1=已缴费）
--   o_advice_param    VARCHAR(255)  - 医生建议
-- 使用场景：管理员或护士在后台手动录入挂号信息时调用
-- 注意：患者的挂号操作（患者自助挂号）不通过此存储过程，
--       通过 Java 代码 OrderServiceImpl.addOrder() 处理
-- ==============================================================
DROP PROCEDURE IF EXISTS `insert_orders`;

DELIMITER $$

CREATE PROCEDURE `insert_orders`(
    IN p_id_param INT,                 -- 患者 ID
    IN d_id_param CHAR(6),             -- 医生 ID
    IN o_record_param VARCHAR(255),    -- 挂号记录
    IN o_start_param VARCHAR(255),     -- 开始时间
    IN o_end_param VARCHAR(255),       -- 结束时间
    IN o_state_param INT,              -- 订单状态
    IN o_drug_param VARCHAR(255),      -- 开药信息
    IN o_check_param VARCHAR(255),     -- 检查信息
    IN o_total_price_param DECIMAL(10, 2),  -- 总金额
    IN o_price_state_param INT,        -- 缴费状态
    IN o_advice_param VARCHAR(255)     -- 医生建议
)
BEGIN
    -- 将传入的所有字段插入到 orders 表中
    -- o_id 由数据库自增自动生成
    INSERT INTO `orders` (
        p_id, d_id, o_record, o_start, o_end, o_state,
        o_drug, o_check, o_total_price, o_price_state, o_advice
    ) VALUES (
        p_id_param, d_id_param, o_record_param, o_start_param, o_end_param,
        o_state_param, o_drug_param, o_check_param, o_total_price_param,
        o_price_state_param, o_advice_param
    );
END$$

DELIMITER ;


-- ==============================================================
-- 存储过程4：sp_daily_clinic_report（日报统计）
-- 功能说明：统计指定日期的门诊运营数据，生成日报表
-- 参数说明：
--   report_date DATE  - 要统计的日期，如 '2026-07-05'
-- 返回字段：
--   reportDate              DATE    - 统计日期
--   totalRegistrations      INT     - 当天挂号总数
--   completedVisits         INT     - 当天已完成就诊数（o_state=7）
--   cancelled               INT     - 当天已取消订单数（o_state=-1）
--   missed                  INT     - 当天爽约数（o_missed=1）
--   totalIncome             DECIMAL - 当天总收入（含挂号费+药费+检查费）
--   registrationFeeIncome   DECIMAL - 挂号费收入
--   drugCheckFeeIncome      DECIMAL - 药品和检查费收入（总收入-挂号费）
--   inProgressCount         INT     - 正在进行的就诊数（挂号数-完成数-取消数）
-- 使用场景：管理后台首页展示今日数据概览，或财务导出日报
-- 注意：挂号费通过 br_type='gua hao fei' 区分
-- ==============================================================
DROP PROCEDURE IF EXISTS `sp_daily_clinic_report`;

DELIMITER $$

CREATE PROCEDURE `sp_daily_clinic_report`(IN report_date DATE)
BEGIN
    -- 声明变量
    DECLARE total_reg INT DEFAULT 0;            -- 挂号总数
    DECLARE total_income DECIMAL(12,2) DEFAULT 0;  -- 总收入
    DECLARE reg_fee DECIMAL(12,2) DEFAULT 0;    -- 挂号费收入
    DECLARE drug_fee DECIMAL(12,2) DEFAULT 0;   -- 药品+检查费收入
    DECLARE completed_count INT DEFAULT 0;      -- 已完成就诊数
    DECLARE cancelled_count INT DEFAULT 0;      -- 已取消数
    DECLARE missed_count INT DEFAULT 0;         -- 爽约数

    -- 1. 统计当天挂号总数（按 o_start 日期匹配）
    SELECT COUNT(*) INTO total_reg
    FROM orders
    WHERE DATE(o_start) = report_date;

    -- 2. 统计当天总收入（按缴费时间 br_pay_time 匹配）
    SELECT COALESCE(SUM(br_amount), 0) INTO total_income
    FROM billing_record
    WHERE DATE(br_pay_time) = report_date;

    -- 3. 统计挂号费收入（br_type = 'gua hao fei'）
    SELECT COALESCE(SUM(br_amount), 0) INTO reg_fee
    FROM billing_record
    WHERE DATE(br_pay_time) = report_date AND br_type = 'gua hao fei';

    -- 4. 统计药品和检查费收入（挂号费以外的费用）
    SELECT COALESCE(SUM(br_amount), 0) INTO drug_fee
    FROM billing_record
    WHERE DATE(br_pay_time) = report_date AND br_type != 'gua hao fei';

    -- 5. 统计已完成就诊数（o_state = 7）
    SELECT COUNT(*) INTO completed_count
    FROM orders
    WHERE DATE(o_start) = report_date AND o_state = 7;

    -- 6. 统计已取消数（o_state = -1）
    SELECT COUNT(*) INTO cancelled_count
    FROM orders
    WHERE DATE(o_start) = report_date AND o_state = -1;

    -- 7. 统计爽约数（o_missed = 1）
    SELECT COUNT(*) INTO missed_count
    FROM orders
    WHERE DATE(o_start) = report_date AND o_missed = 1;

    -- 8. 返回统计结果（一行数据）
    SELECT
        report_date AS reportDate,                          -- 统计日期
        total_reg AS totalRegistrations,                    -- 挂号总数
        completed_count AS completedVisits,                 -- 已完成就诊
        cancelled_count AS cancelled,                       -- 已取消
        missed_count AS missed,                             -- 爽约
        total_income AS totalIncome,                        -- 总收入
        reg_fee AS registrationFeeIncome,                   -- 挂号费收入
        drug_fee AS drugCheckFeeIncome,                     -- 药品+检查费收入
        (total_reg - completed_count - cancelled_count) AS inProgressCount;  -- 进行中
END$$

DELIMITER ;


-- ==============================================================
-- 存储过程5：sp_department_income（科室收入统计）
-- 功能说明：统计指定时间段内各科室的收入和就诊情况
-- 参数说明：
--   start_date DATE  - 统计开始日期
--   end_date   DATE  - 统计结束日期
-- 返回字段：
--   department   VARCHAR  - 科室名称
--   visitCount   INT      - 就诊人次
--   completedCount INT    - 已完成就诊人次
--   totalRegFee  DECIMAL  - 挂号费总收入
--   drugCheckFee DECIMAL  - 药品和检查费收入
--   totalIncome  DECIMAL  - 总收入
--   avgPerVisit  DECIMAL  - 人均费用（总收入/就诊人次）
-- 使用场景：财务统计、科室绩效考核
-- 关联表：department → doctor → orders → billing_record
-- ==============================================================
DROP PROCEDURE IF EXISTS `sp_department_income`;

DELIMITER $$

CREATE PROCEDURE `sp_department_income`(
    IN start_date DATE,    -- 统计开始日期
    IN end_date DATE       -- 统计结束日期
)
BEGIN
    SELECT
        d.d_section     AS department,              -- 科室名称
        COUNT(DISTINCT o.o_id) AS visitCount,       -- 就诊人次（去重）
        COUNT(DISTINCT CASE WHEN o.o_state >= 7 THEN o.o_id END) AS completedCount,  -- 已完成数
        COALESCE(SUM(o.o_registration_fee), 0) AS totalRegFee,     -- 挂号费收入合计
        COALESCE(SUM(CASE WHEN br.br_type != 'gua hao fei' THEN br.br_amount ELSE 0 END), 0) AS drugCheckFee,  -- 药费+检查费
        COALESCE(SUM(br.br_amount), 0) AS totalIncome,            -- 总收入
        ROUND(COALESCE(SUM(br.br_amount), 0) / NULLIF(COUNT(DISTINCT o.o_id), 0), 2) AS avgPerVisit  -- 人均费用
    FROM department dept
    JOIN doctor d ON dept.de_id = d.de_id           -- 关联科室→医生
    LEFT JOIN orders o ON d.d_id = o.d_id           -- 关联医生→订单
        AND DATE(o.o_start) BETWEEN start_date AND end_date  -- 按时间范围过滤
    LEFT JOIN billing_record br ON o.o_id = br.o_id -- 关联订单→收费记录
    GROUP BY d.d_section                            -- 按科室分组
    ORDER BY totalIncome DESC;                      -- 按总收入降序排列
END$$

DELIMITER ;


-- ==============================================================
-- 存储过程6：sp_doctor_workload（医生工作量统计）
-- 功能说明：统计指定时间段内医生的接诊工作量
-- 参数说明：
--   start_date DATE   - 统计开始日期
--   end_date   DATE   - 统计结束日期
--   doctor_id  CHAR(6) - 医生ID（可选，传空字符串或 NULL 查全部医生）
-- 返回字段：
--   doctorId         CHAR(6)   - 医生ID
--   doctorName       VARCHAR   - 医生姓名
--   department       VARCHAR   - 所属科室
--   title            VARCHAR   - 医生职称
--   totalVisits      INT       - 总接诊人次
--   completedVisits  INT       - 已完成接诊人次（o_state>=7）
--   emrCount         INT       - 书写的病历数
--   prescriptionCount INT      - 开出的处方数
--   totalBilling     DECIMAL   - 产生的总费用
-- 使用场景：医院管理统计、医生绩效考核
-- ==============================================================
DROP PROCEDURE IF EXISTS `sp_doctor_workload`;

DELIMITER $$

CREATE PROCEDURE `sp_doctor_workload`(
    IN start_date DATE,       -- 统计开始日期
    IN end_date DATE,         -- 统计结束日期
    IN doctor_id CHAR(6)      -- 医生ID（传空字符串或不传则查全部）
)
BEGIN
    SELECT
        d.d_id          AS doctorId,             -- 医生ID
        d.d_name        AS doctorName,           -- 医生姓名
        d.d_section     AS department,           -- 所属科室
        d.d_post        AS title,                -- 医生职称（主任医师/主治医师等）
        COUNT(DISTINCT o.o_id) AS totalVisits,   -- 总接诊人次
        COUNT(DISTINCT CASE WHEN o.o_state >= 7 THEN o.o_id END) AS completedVisits,  -- 已完成数
        COUNT(DISTINCT emr.emr_id) AS emrCount,  -- 病历数量
        COUNT(DISTINCT pm.pm_id) AS prescriptionCount,  -- 处方数量
        COALESCE(SUM(br.br_amount), 0) AS totalBilling  -- 产生的费用总额
    FROM doctor d
    LEFT JOIN orders o ON d.d_id = o.d_id           -- 医生→订单
        AND DATE(o.o_start) BETWEEN start_date AND end_date  -- 时间范围
    LEFT JOIN outpatient_emr emr ON o.o_id = emr.o_id       -- 订单→病历
    LEFT JOIN prescription_master pm ON o.o_id = pm.o_id    -- 订单→处方
    LEFT JOIN billing_record br ON o.o_id = br.o_id         -- 订单→收费
    WHERE (doctor_id IS NULL OR doctor_id = '' OR d.d_id = doctor_id)  -- 按医生过滤
    GROUP BY d.d_id, d.d_name, d.d_section, d.d_post
    ORDER BY totalVisits DESC;                      -- 按接诊量降序排列
END$$

DELIMITER ;


-- ==============================================================
-- 存储过程7：sp_drug_consumption_ranking（药品消耗排行）
-- 功能说明：统计指定时间段内药品的消耗量排名
-- 参数说明：
--   start_date DATE  - 统计开始日期
--   end_date   DATE  - 统计结束日期
--   top_n      INT   - 返回前 N 条记录，默认 10
-- 返回字段：
--   drugId           CHAR(6)   - 药品ID
--   drugName         VARCHAR   - 药品名称
--   form             VARCHAR   - 剂型（片剂/胶囊/注射液等）
--   manufacturer     VARCHAR   - 生产厂家
--   totalQty         INT       - 消耗总数量
--   unit             VARCHAR   - 计量单位
--   totalAmount      DECIMAL   - 消耗总金额
--   prescriptionCount INT      - 开出处方次数
--   currentStock     INT       - 当前库存
-- 使用场景：药房管理、药品采购计划
-- ==============================================================
DROP PROCEDURE IF EXISTS `sp_drug_consumption_ranking`;

DELIMITER $$

CREATE PROCEDURE `sp_drug_consumption_ranking`(
    IN start_date DATE,     -- 统计开始日期
    IN end_date DATE,       -- 统计结束日期
    IN top_n INT            -- 返回条数限制（默认10条）
)
BEGIN
    -- 如果 top_n 为空或小于等于 0，默认返回前 10 名
    IF top_n IS NULL OR top_n <= 0 THEN
        SET top_n = 10;
    END IF;

    SELECT
        d.dr_id         AS drugId,              -- 药品ID
        d.dr_name       AS drugName,            -- 药品名称
        d.dr_form       AS form,                -- 剂型
        d.dr_manufacturer AS manufacturer,       -- 生产厂家
        COALESCE(SUM(pd.pd_quantity), 0) AS totalQty,    -- 消耗总量
        d.dr_unit       AS unit,                -- 单位
        ROUND(COALESCE(SUM(pd.pd_quantity * pd.pd_price), 0), 2) AS totalAmount,  -- 消耗金额
        COUNT(DISTINCT pd.o_id) AS prescriptionCount,   -- 处方次数
        d.dr_number     AS currentStock         -- 当前库存
    FROM drug d
    LEFT JOIN prescription_detail pd ON d.dr_id = pd.dr_id     -- 药品→处方明细
    LEFT JOIN orders o ON pd.o_id = o.o_id                     -- 处方明细→订单
        AND DATE(o.o_start) BETWEEN start_date AND end_date    -- 时间范围
    GROUP BY d.dr_id, d.dr_name, d.dr_form, d.dr_manufacturer, d.dr_number, d.dr_unit
    HAVING totalQty > 0                                         -- 只显示有消耗的药品
    ORDER BY totalQty DESC                                      -- 按消耗量降序排列
    LIMIT top_n;                                                -- 取前 N 条
END$$

DELIMITER ;


-- ==============================================================
-- 存储过程8：sp_patient_visit_history（患者就诊历史）
-- 功能说明：查询某位患者的全部就诊历史记录
-- 参数说明：
--   patient_id INT  - 患者ID
-- 返回字段：
--   orderId     INT      - 订单ID
--   orderTime   VARCHAR  - 就诊时间
--   regType     VARCHAR  - 挂号类型（普通号/专家号/急诊）
--   doctorName  VARCHAR  - 接诊医生姓名
--   department  VARCHAR  - 就诊科室
--   diagnosis   TEXT     - 诊断结果（来自门诊病历）
--   state       INT      - 订单状态编码
--   stateLabel  VARCHAR  - 订单状态文字说明
--   totalCost   DECIMAL  - 总费用（挂号费+药费+检查费）
--   payStatus   VARCHAR  - 缴费状态（paid/unpaid）
--   payMethod   VARCHAR  - 支付方式
-- 使用场景：患者在个人中心查看就诊历史
-- o_state 状态说明：
--   0=已挂号, 1=已分诊, 2=就诊中, 3=已开处方
--   4=待缴费, 5=已缴费, 6=已发药, 7=已完成, -1=已取消
-- ==============================================================
DROP PROCEDURE IF EXISTS `sp_patient_visit_history`;

DELIMITER $$

CREATE PROCEDURE `sp_patient_visit_history`(IN patient_id INT)
BEGIN
    SELECT
        o.o_id          AS orderId,             -- 订单ID
        o.o_start       AS orderTime,           -- 就诊时间
        o.o_triage      AS regType,             -- 挂号类型（普通门诊/专家门诊/急诊）
        d.d_name        AS doctorName,          -- 医生姓名
        d.d_section     AS department,          -- 就诊科室
        emr.diagnosis   AS diagnosis,           -- 诊断结果
        o.o_state       AS state,               -- 状态编码
        -- 将状态编码转为可读的文字说明
        CASE o.o_state
            WHEN 0 THEN 'registered'    -- 已挂号
            WHEN 1 THEN 'triaged'       -- 已分诊
            WHEN 2 THEN 'consulting'    -- 就诊中
            WHEN 3 THEN 'ordered'       -- 已开处方
            WHEN 4 THEN 'pendingPay'    -- 待缴费
            WHEN 5 THEN 'paid'          -- 已缴费
            WHEN 6 THEN 'dispensed'     -- 已发药
            WHEN 7 THEN 'completed'     -- 已完成
            WHEN -1 THEN 'cancelled'    -- 已取消
        END             AS stateLabel,          -- 状态文字说明
        o.o_total_price + o.o_registration_fee AS totalCost,  -- 总费用
        CASE WHEN o.o_price_state = 1 THEN 'paid' ELSE 'unpaid' END AS payStatus,  -- 缴费状态
        o.o_payment_method AS payMethod         -- 支付方式
    FROM orders o
    JOIN doctor d ON o.d_id = d.d_id            -- 关联医生表
    LEFT JOIN outpatient_emr emr ON o.o_id = emr.o_id  -- 关联病历表（可能没有病历，用 LEFT JOIN）
    WHERE o.p_id = patient_id                   -- 按患者ID过滤
    ORDER BY o.o_start DESC;                    -- 按就诊时间降序排列（最新的在最上面）
END$$

DELIMITER ;
