-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: hospital
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admini`
--

DROP TABLE IF EXISTS `affair`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admini` (
  `a_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `a_password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `a_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `a_gender` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `a_card` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `a_phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `a_email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`a_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `arrange`
--

DROP TABLE IF EXISTS `arrange`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `arrange` (
  `ar_id` char(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `ar_time` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `d_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`ar_id`) USING BTREE,
  KEY `arTOd` (`d_id`) USING BTREE,
  CONSTRAINT `arTOd` FOREIGN KEY (`d_id`) REFERENCES `doctor` (`d_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `before_insert_arrange` BEFORE INSERT ON `arrange` FOR EACH ROW BEGIN
    DECLARE count INT;
    
    -- 检查 `arrangement` 表中是否已经存在该 `ar_id`
    SELECT COUNT(*) INTO count 
    FROM arrangement 
    WHERE ar_id = NEW.ar_id;
    
    -- 如果不存在，则插入新记录
    IF count = 0 THEN
        INSERT INTO arrangement (ar_id, ar_time)
        VALUES (NEW.ar_id, NEW.ar_time);
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `arrangement`
--

DROP TABLE IF EXISTS `arrangement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `arrangement` (
  `ar_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `ar_time` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`ar_id`) USING BTREE,
  CONSTRAINT `fk_arrangement_aid` FOREIGN KEY (`ar_id`) REFERENCES `arrange` (`ar_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `audit_log`
--

DROP TABLE IF EXISTS `audit_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_log` (
  `al_id` int NOT NULL AUTO_INCREMENT,
  `al_user_id` varchar(50) DEFAULT NULL COMMENT '操作用户ID',
  `al_user_role` varchar(20) DEFAULT NULL COMMENT '用户角色',
  `al_action` varchar(100) DEFAULT NULL COMMENT '操作类型',
  `al_target` varchar(100) DEFAULT NULL COMMENT '操作对象',
  `al_detail` text COMMENT '操作详情',
  `al_ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `al_create_time` datetime DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`al_id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb3 COMMENT='操作审计日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `billing_record`
--

DROP TABLE IF EXISTS `billing_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `billing_record` (
  `br_id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `pm_id` int DEFAULT NULL COMMENT '关联处方主表',
  `emr_id` int DEFAULT NULL COMMENT '关联病历ID',
  `oc_id` int DEFAULT NULL COMMENT '关联检查单ID',
  `br_type` varchar(30) DEFAULT NULL COMMENT '收费类型',
  `br_amount` decimal(10,2) DEFAULT NULL COMMENT '金额',
  `br_payment_method` varchar(30) DEFAULT NULL COMMENT '支付方式',
  `br_invoice_no` varchar(50) DEFAULT NULL COMMENT '发票号',
  `br_pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `br_operator` varchar(50) DEFAULT NULL COMMENT '操作员',
  PRIMARY KEY (`br_id`),
  KEY `idx_br_oc` (`oc_id`),
  KEY `fk_billing_emr` (`emr_id`),
  CONSTRAINT `fk_billing_emr` FOREIGN KEY (`emr_id`) REFERENCES `outpatient_emr` (`emr_id`)
) ENGINE=InnoDB AUTO_INCREMENT=115 DEFAULT CHARSET=utf8mb3 COMMENT='收费记录明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `checks`
--

DROP TABLE IF EXISTS `checks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `checks` (
  `ch_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `ch_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `ch_price` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`ch_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `delivery_request`
--

DROP TABLE IF EXISTS `delivery_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `delivery_request` (
  `dl_id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `p_id` int DEFAULT NULL COMMENT '患者ID',
  `dl_agent_name` varchar(50) DEFAULT NULL COMMENT '代办人姓名',
  `dl_agent_id_card` varchar(20) DEFAULT NULL COMMENT '代办人身份证',
  `dl_agent_phone` varchar(20) DEFAULT NULL COMMENT '代办人电话',
  `dl_pickup_code` varchar(50) DEFAULT NULL COMMENT '取药码',
  `dl_status` tinyint DEFAULT '0' COMMENT '状态:0=待取药 1=已取药 2=已取消',
  `dl_create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `dl_pickup_time` datetime DEFAULT NULL COMMENT '取药时间',
  PRIMARY KEY (`dl_id`),
  KEY `idx_dl_patient` (`p_id`),
  KEY `idx_dl_status` (`dl_status`),
  CONSTRAINT `fk_delivery_pid` FOREIGN KEY (`p_id`) REFERENCES `patient` (`p_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='送药申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `department`
--

DROP TABLE IF EXISTS `department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `department` (
  `de_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `de_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `de_number` int DEFAULT NULL,
  PRIMARY KEY (`de_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `diagnosis_dict`
--

DROP TABLE IF EXISTS `diagnosis_dict`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `diagnosis_dict` (
  `dd_id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dd_code` varchar(20) DEFAULT NULL COMMENT 'ICD编码',
  `dd_name` varchar(255) NOT NULL COMMENT '诊断名称',
  `dd_dept` varchar(50) DEFAULT NULL COMMENT '所属科室',
  `dd_sort` int DEFAULT '0' COMMENT '排序号',
  `dd_pinyin` varchar(100) DEFAULT NULL COMMENT '拼音码',
  PRIMARY KEY (`dd_id`),
  KEY `idx_dd_name` (`dd_name`),
  KEY `idx_dd_pinyin` (`dd_pinyin`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='诊断字典表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `doctor`
--

DROP TABLE IF EXISTS `doctor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctor` (
  `d_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `de_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `d_password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `d_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `d_gender` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `d_phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `d_card` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `d_email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `d_post` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `d_introduction` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `d_section` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `d_state` int NOT NULL,
  `d_price` decimal(10,2) DEFAULT NULL,
  `d_people` int DEFAULT NULL,
  `d_star` decimal(10,2) DEFAULT NULL,
  `d_avg_star` decimal(10,2) DEFAULT NULL,
  `d_max_daily` int DEFAULT '50',
  PRIMARY KEY (`d_id`) USING BTREE,
  KEY `dTOd` (`de_id`) USING BTREE,
  CONSTRAINT `dTOd` FOREIGN KEY (`de_id`) REFERENCES `department` (`de_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `before_insert_doctor` BEFORE INSERT ON `doctor` FOR EACH ROW BEGIN
    DECLARE section_id CHAR(6);

    -- 检查科室是否存在，不调用不存在的存储过程
    SELECT de_id INTO section_id
    FROM department
    WHERE de_name = NEW.d_section
    LIMIT 1;

    IF section_id IS NOT NULL THEN
        SET NEW.de_id = section_id;
        UPDATE department SET de_number = de_number + 1 WHERE de_id = section_id;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `department_before_insert_doctor` BEFORE INSERT ON `doctor` FOR EACH ROW BEGIN
    DECLARE section_id CHAR(6);
    DECLARE new_section_id CHAR(6);
    
    -- 检查科室是否存在
    SELECT de_id INTO section_id
    FROM department
    WHERE de_name = NEW.d_section
    LIMIT 1;

    -- 如果科室不存在，插入新的科室记录并生成新的科室编号
    IF section_id IS NULL THEN
        SET new_section_id = CONCAT('S', LPAD((SELECT COUNT(*) + 1 FROM department), 5, '0'));
        INSERT INTO department (de_id, de_name, de_number) VALUES (new_section_id, NEW.d_section, 1);
        SET NEW.de_id = new_section_id;
    ELSE
        -- 如果科室存在，获取科室编号并更新科室人数
        SET NEW.de_id = section_id;
        UPDATE department
        SET de_number = de_number + 1
        WHERE de_id = section_id;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `update_department_count_after_doctor_deactivation` AFTER UPDATE ON `doctor` FOR EACH ROW BEGIN
    IF NEW.d_state = 0 AND OLD.d_state != 0 THEN
        -- 更新 department 表，减少相应科室的科室人数
        UPDATE department
        SET de_number = de_number - 1
        WHERE de_id = NEW.de_id;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `drug`
--

DROP TABLE IF EXISTS `drug`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `drug` (
  `dr_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `dr_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `dr_price` decimal(10,2) DEFAULT NULL,
  `dr_number` int DEFAULT NULL,
  `dr_publisher` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `dr_unit` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `dr_type` int DEFAULT '1' COMMENT '药品分类:1=西药,2=中药',
  `dr_spec` varchar(100) DEFAULT NULL COMMENT '规格',
  `dr_approval_no` varchar(100) DEFAULT NULL COMMENT '批准文号',
  `dr_form` varchar(50) DEFAULT NULL COMMENT '剂型',
  `dr_manufacturer` varchar(200) DEFAULT NULL COMMENT '生产厂家',
  `dr_generic_name` varchar(200) DEFAULT NULL COMMENT '通用名',
  `dr_pinyin` varchar(100) DEFAULT NULL COMMENT '拼音码',
  `dr_subtype` varchar(50) DEFAULT NULL COMMENT '细分类',
  `dr_rx_type` varchar(20) DEFAULT NULL COMMENT '处方属性',
  `dr_insurance_type` varchar(20) DEFAULT NULL COMMENT '医保类别',
  `dr_antibiotic_level` varchar(20) DEFAULT NULL COMMENT '抗菌级别',
  `dr_controlled` tinyint DEFAULT '0' COMMENT '管制药品',
  `dr_essential` tinyint DEFAULT '0' COMMENT '基本药物',
  `dr_min_stock` int DEFAULT '20' COMMENT '库存下限',
  `dr_storage` varchar(200) DEFAULT NULL COMMENT '储存条件',
  `dr_indication` text COMMENT '适应症',
  `dr_contraindication` text COMMENT '禁忌症',
  `dr_adverse_reaction` text COMMENT '不良反应',
  `dr_tcm_nature` varchar(50) DEFAULT NULL COMMENT '药性',
  `dr_tcm_flavor` varchar(50) DEFAULT NULL COMMENT '药味',
  `dr_tcm_meridian` varchar(100) DEFAULT NULL COMMENT '归经',
  `dr_decoction_method` varchar(200) DEFAULT NULL COMMENT '煎服方法',
  `dr_image` varchar(500) DEFAULT NULL COMMENT '药品图片',
  `dr_disabled` tinyint DEFAULT '0',
  `dr_max_stock` int DEFAULT '9999',
  `dr_warn_days` int DEFAULT '30',
  PRIMARY KEY (`dr_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `drug_batch`
--

DROP TABLE IF EXISTS `drug_batch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `drug_batch` (
  `db_id` int NOT NULL AUTO_INCREMENT,
  `dr_id` char(6) DEFAULT NULL COMMENT '药品ID',
  `db_batch_no` varchar(50) DEFAULT NULL COMMENT '生产批号',
  `db_expire_date` date DEFAULT NULL COMMENT '有效期',
  `db_quantity` int DEFAULT '0' COMMENT '批次库存',
  `db_purchase_price` decimal(10,2) DEFAULT NULL COMMENT '进货价',
  `db_supplier` varchar(100) DEFAULT NULL COMMENT '供应商',
  `db_create_time` datetime DEFAULT NULL COMMENT '入库时间',
  PRIMARY KEY (`db_id`),
  KEY `fk_db_drug` (`dr_id`),
  CONSTRAINT `fk_db_drug` FOREIGN KEY (`dr_id`) REFERENCES `drug` (`dr_id`)
) ENGINE=InnoDB AUTO_INCREMENT=211 DEFAULT CHARSET=utf8mb3 COMMENT='药品批次表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `drug_category`
--

DROP TABLE IF EXISTS `drug_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `drug_category` (
  `dc_id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dc_name` varchar(100) NOT NULL COMMENT '分类名称',
  `dc_parent_id` int DEFAULT '0' COMMENT '父分类ID',
  `dc_code` varchar(50) DEFAULT NULL COMMENT '分类编码',
  `dc_note` varchar(200) DEFAULT NULL COMMENT '备注说明',
  `dc_sort` int DEFAULT '0' COMMENT '排序号',
  PRIMARY KEY (`dc_id`),
  KEY `idx_dc_parent` (`dc_parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='药品分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `drug_price_log`
--

DROP TABLE IF EXISTS `drug_price_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `drug_price_log` (
  `dpl_id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dr_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '药品ID',
  `old_price` decimal(10,2) NOT NULL COMMENT '原价',
  `new_price` decimal(10,2) NOT NULL COMMENT '新价',
  `change_reason` varchar(200) DEFAULT NULL COMMENT '调价原因',
  `operator` varchar(50) DEFAULT NULL COMMENT '操作人',
  `create_time` datetime DEFAULT NULL COMMENT '调价时间',
  PRIMARY KEY (`dpl_id`),
  KEY `idx_dpl_drug` (`dr_id`),
  CONSTRAINT `fk_dpl_drug` FOREIGN KEY (`dr_id`) REFERENCES `drug` (`dr_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='药品调价记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `emr_template`
--

DROP TABLE IF EXISTS `emr_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `emr_template` (
  `et_id` int NOT NULL AUTO_INCREMENT,
  `et_name` varchar(100) DEFAULT NULL COMMENT '模板名称',
  `et_section` varchar(50) DEFAULT NULL COMMENT '适用科室',
  `et_chief_complaint` text COMMENT '主诉模板',
  `et_present_illness` text COMMENT '现病史模板',
  `et_past_history` text COMMENT '既往史模板',
  `et_physical_exam` text COMMENT '查体模板',
  `et_diagnosis` text COMMENT '诊断模板',
  `et_treatment_plan` text COMMENT '治疗方案模板',
  `et_create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `et_dept` varchar(50) DEFAULT NULL,
  `d_id` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`et_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb3 COMMENT='电子病历模板表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `inventory_transaction`
--

DROP TABLE IF EXISTS `inventory_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory_transaction` (
  `it_id` int NOT NULL AUTO_INCREMENT,
  `dr_id` varchar(50) DEFAULT NULL,
  `db_id` int DEFAULT NULL,
  `it_type` varchar(30) DEFAULT NULL,
  `it_quantity` int DEFAULT NULL,
  `it_before_quantity` int DEFAULT NULL,
  `it_after_quantity` int DEFAULT NULL,
  `it_reference` varchar(200) DEFAULT NULL,
  `it_operator` varchar(50) DEFAULT NULL,
  `it_note` varchar(500) DEFAULT NULL,
  `it_create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`it_id`),
  KEY `idx_it_dr` (`dr_id`),
  KEY `idx_it_type` (`it_type`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invoice_record`
--

DROP TABLE IF EXISTS `invoice_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoice_record` (
  `inv_id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `inv_no` varchar(50) NOT NULL COMMENT '发票号',
  `br_id` int DEFAULT NULL COMMENT '关联缴费记录ID',
  `inv_type` varchar(20) DEFAULT '电子' COMMENT '发票类型',
  `inv_amount` decimal(10,2) NOT NULL COMMENT '发票金额',
  `inv_status` tinyint DEFAULT '0' COMMENT '状态:0=正常 1=已作废 2=已红冲',
  `inv_operator` varchar(50) DEFAULT NULL COMMENT '开票人',
  `inv_create_time` datetime DEFAULT NULL COMMENT '开票时间',
  `inv_cancel_time` datetime DEFAULT NULL COMMENT '作废时间',
  `inv_cancel_reason` varchar(200) DEFAULT NULL COMMENT '作废原因',
  PRIMARY KEY (`inv_id`),
  UNIQUE KEY `inv_no` (`inv_no`),
  KEY `fk_invoice_billing` (`br_id`),
  CONSTRAINT `fk_invoice_billing` FOREIGN KEY (`br_id`) REFERENCES `billing_record` (`br_id`)
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `issue_check`
--

DROP TABLE IF EXISTS `issue_check`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `issue_check` (
  `ch_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `o_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`ch_id`,`o_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `n_id` int NOT NULL AUTO_INCREMENT,
  `p_id` int DEFAULT NULL COMMENT '患者ID',
  `d_id` char(6) DEFAULT NULL COMMENT '医生ID',
  `n_type` varchar(30) DEFAULT NULL COMMENT '类型:queue/payment/refund/reminder',
  `n_title` varchar(100) DEFAULT NULL COMMENT '标题',
  `n_content` text COMMENT '内容',
  `n_is_read` int DEFAULT '0' COMMENT '是否已读:0=未读,1=已读',
  `n_create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`n_id`),
  KEY `fk_notification_pid` (`p_id`),
  CONSTRAINT `fk_notification_pid` FOREIGN KEY (`p_id`) REFERENCES `patient` (`p_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3 COMMENT='消息通知表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `order_check`
--

DROP TABLE IF EXISTS `order_check`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_check` (
  `oc_id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `emr_id` int DEFAULT NULL COMMENT '关联病历',
  `ch_id` char(6) NOT NULL,
  `ch_name` varchar(200) DEFAULT NULL COMMENT '检查项目名称',
  `ch_price` decimal(10,2) DEFAULT '0.00' COMMENT '价格',
  `oc_status` tinyint DEFAULT '0' COMMENT '状态:0=未缴费 1=待检查 2=已完成 3=异常',
  `oc_result` text COMMENT '检查结果',
  `oc_attachment` varchar(500) DEFAULT NULL COMMENT '附件路径',
  `oc_result_time` datetime DEFAULT NULL COMMENT '结果录入时间',
  `oc_operator` varchar(50) DEFAULT NULL COMMENT '操作人',
  `oc_create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `oc_note` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`oc_id`),
  KEY `idx_oc_status` (`oc_status`),
  KEY `fk_oc_emr` (`emr_id`),
  CONSTRAINT `fk_oc_emr` FOREIGN KEY (`emr_id`) REFERENCES `outpatient_emr` (`emr_id`)
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='检查开单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `o_id` int NOT NULL AUTO_INCREMENT,
  `p_id` int DEFAULT NULL,
  `d_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `o_record` text,
  `o_start` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `o_end` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `o_state` int DEFAULT NULL,
  `o_drug` text,
  `o_check` text,
  `o_total_price` decimal(10,2) DEFAULT NULL,
  `o_price_state` int DEFAULT NULL,
  `o_advice` text,
  `o_queue_number` varchar(20) DEFAULT NULL COMMENT '排队号码',
  `o_triage` varchar(50) DEFAULT NULL COMMENT '分诊类别(普通门诊/专家门诊/急诊)',
  `o_registration_fee` decimal(10,2) DEFAULT '0.00' COMMENT '挂号费',
  `o_payment_method` varchar(30) DEFAULT NULL COMMENT '支付方式',
  `o_invoice_no` varchar(50) DEFAULT NULL COMMENT '发票号',
  `o_insurance_covered` decimal(10,2) DEFAULT '0.00' COMMENT '医保报销金额',
  `o_self_pay` decimal(10,2) DEFAULT '0.00' COMMENT '自付金额',
  `o_reg_type` varchar(20) DEFAULT '普通号',
  `o_cancel_reason` varchar(500) DEFAULT NULL,
  `o_missed` tinyint DEFAULT '0',
  PRIMARY KEY (`o_id`) USING BTREE,
  KEY `oTOp` (`p_id`) USING BTREE,
  KEY `0TOd` (`d_id`) USING BTREE,
  CONSTRAINT `oTOd` FOREIGN KEY (`d_id`) REFERENCES `doctor` (`d_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `oTOp` FOREIGN KEY (`p_id`) REFERENCES `patient` (`p_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=21007 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_order_cancel_release_queue` AFTER UPDATE ON `orders` FOR EACH ROW BEGIN
    IF NEW.o_state = -1 AND (OLD.o_state IS NULL OR OLD.o_state >= 0) THEN
        UPDATE queue_number
        SET q_state = 3, q_finish_time = NOW()
        WHERE o_id = NEW.o_id AND q_state IN (0, 1);
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_missed_release_queue` AFTER UPDATE ON `orders` FOR EACH ROW BEGIN
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
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `outpatient_emr`
--

DROP TABLE IF EXISTS `outpatient_emr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `outpatient_emr` (
  `emr_id` int NOT NULL AUTO_INCREMENT,
  `o_id` int DEFAULT NULL COMMENT '关联挂号订单',
  `p_id` int DEFAULT NULL COMMENT '患者ID',
  `d_id` char(6) DEFAULT NULL COMMENT '医生ID',
  `chief_complaint` text COMMENT '主诉',
  `present_illness` text COMMENT '现病史',
  `past_history` text COMMENT '既往史',
  `physical_exam` text COMMENT '体格检查',
  `diagnosis` text COMMENT '诊断',
  `treatment_plan` text COMMENT '处理意见',
  `allergy_history` varchar(500) DEFAULT NULL COMMENT '过敏史',
  `medical_advice` text COMMENT '医嘱',
  `follow_up_suggest` varchar(500) DEFAULT NULL COMMENT '随访建议',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`emr_id`) USING BTREE,
  KEY `emrTOo` (`o_id`),
  KEY `emrTOp` (`p_id`),
  KEY `emrTOd` (`d_id`),
  CONSTRAINT `fk_emr_doctor` FOREIGN KEY (`d_id`) REFERENCES `doctor` (`d_id`),
  CONSTRAINT `fk_emr_orders` FOREIGN KEY (`o_id`) REFERENCES `orders` (`o_id`),
  CONSTRAINT `fk_emr_patient` FOREIGN KEY (`p_id`) REFERENCES `patient` (`p_id`)
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb3 COMMENT='门诊电子病历表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `patient`
--

DROP TABLE IF EXISTS `patient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `patient` (
  `p_id` int NOT NULL,
  `p_password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `p_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `p_gender` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `p_phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `p_card` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `p_email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `p_state` int DEFAULT NULL,
  `p_birthday` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `p_age` int DEFAULT NULL,
  `p_insurance_id` varchar(50) DEFAULT NULL COMMENT '医保号',
  `p_insurance_type` varchar(30) DEFAULT NULL COMMENT '医保类型',
  `p_contact_person` varchar(100) DEFAULT NULL COMMENT '联系人',
  `p_contact_phone` varchar(20) DEFAULT NULL COMMENT '联系人电话',
  `p_address` varchar(255) DEFAULT NULL COMMENT '家庭住址',
  `p_nation` varchar(30) DEFAULT NULL COMMENT '民族',
  `p_marital_status` varchar(10) DEFAULT NULL COMMENT '婚姻状况',
  `p_blood_type` varchar(5) DEFAULT NULL COMMENT '血型',
  `p_blacklisted` tinyint DEFAULT '0',
  `p_tags` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`p_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pharmacy_dispensing`
--

DROP TABLE IF EXISTS `pharmacy_dispensing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pharmacy_dispensing` (
  `pd_id` int NOT NULL AUTO_INCREMENT,
  `presc_detail_id` int DEFAULT NULL COMMENT '关联prescription_detail.pd_id',
  `pd_quantity` int DEFAULT NULL COMMENT '发药数量',
  `pd_status` int DEFAULT '0' COMMENT '状态:0=待发药,1=已发药,2=已退药',
  `pd_create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `pd_dispense_time` datetime DEFAULT NULL COMMENT '发药时间',
  `pd_dispense_by` varchar(50) DEFAULT NULL COMMENT '发药人',
  `pd_note` varchar(255) DEFAULT NULL COMMENT '备注',
  `db_id` int DEFAULT NULL COMMENT '批次ID',
  `pd_review_by` varchar(50) DEFAULT NULL COMMENT '复核人',
  `pd_review_time` datetime DEFAULT NULL COMMENT '复核时间',
  `pd_return_time` datetime DEFAULT NULL COMMENT '退药时间',
  `pd_return_by` varchar(50) DEFAULT NULL COMMENT '退药人',
  PRIMARY KEY (`pd_id`),
  KEY `idx_presc_detail` (`presc_detail_id`),
  CONSTRAINT `fk_dispensing_presc_detail` FOREIGN KEY (`presc_detail_id`) REFERENCES `prescription_detail` (`pd_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb3 COMMENT='药房发药明细表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_dispensing_update_stock` AFTER UPDATE ON `pharmacy_dispensing` FOR EACH ROW BEGIN
    DECLARE before_stock INT;
    DECLARE after_stock INT;
    DECLARE v_dr_id CHAR(6);

    -- pd_status=1: 发药 → 扣库存
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
    -- pd_status=2: 退药 → 加回库存
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
END */;;
DELIMITER ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `prescribe`
--

DROP TABLE IF EXISTS `prescribe`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prescribe` (
  `dr_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `o_id` int DEFAULT NULL,
  `dosage` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`dr_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `prescription_detail`
--

DROP TABLE IF EXISTS `prescription_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prescription_detail` (
  `pd_id` int NOT NULL AUTO_INCREMENT,
  `pm_id` int DEFAULT NULL COMMENT '关联prescription_master.pm_id',
  `dr_id` char(6) DEFAULT NULL COMMENT '药品ID',
  `pd_usage` varchar(50) DEFAULT NULL COMMENT '用法(口服/注射/外用等)',
  `pd_dosage` varchar(50) DEFAULT NULL COMMENT '每次用量',
  `pd_frequency` varchar(50) DEFAULT NULL COMMENT '频次(qd/bid/tid/qid等)',
  `pd_days` int DEFAULT NULL COMMENT '用药天数',
  `pd_quantity` int DEFAULT NULL COMMENT '总数量',
  `pd_note` varchar(255) DEFAULT NULL COMMENT '备注',
  `pd_price` decimal(10,2) DEFAULT NULL COMMENT '单价',
  `pd_route` varchar(50) DEFAULT NULL COMMENT '给药途径',
  `pd_timing` varchar(50) DEFAULT NULL COMMENT '服药时机',
  `pd_skin_test` tinyint DEFAULT '0' COMMENT '是否需要皮试',
  `pd_tcm_group_no` varchar(30) DEFAULT NULL COMMENT '中药方剂组号',
  `pd_decoction_method` varchar(100) DEFAULT NULL COMMENT '煎服方法',
  PRIMARY KEY (`pd_id`) USING BTREE,
  KEY `pdTOdr` (`dr_id`),
  KEY `idx_pd_pm` (`pm_id`),
  CONSTRAINT `fk_pd_drug` FOREIGN KEY (`dr_id`) REFERENCES `drug` (`dr_id`),
  CONSTRAINT `fk_pd_pm` FOREIGN KEY (`pm_id`) REFERENCES `prescription_master` (`pm_id`)
) ENGINE=InnoDB AUTO_INCREMENT=108 DEFAULT CHARSET=utf8mb3 COMMENT='处方明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `prescription_master`
--

DROP TABLE IF EXISTS `prescription_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prescription_master` (
  `pm_id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `emr_id` int DEFAULT NULL COMMENT '关联病历',
  `d_id` char(6) DEFAULT NULL COMMENT '开方医生ID',
  `pm_diagnosis` varchar(500) DEFAULT NULL COMMENT '处方诊断',
  `pm_type` varchar(20) DEFAULT '西药' COMMENT '处方类型:西药/中药/混合',
  `pm_status` tinyint DEFAULT '0' COMMENT '0=待审核 1=已审核',
  `pm_create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`pm_id`),
  KEY `fk_pm_emr` (`emr_id`),
  CONSTRAINT `fk_pm_emr` FOREIGN KEY (`emr_id`) REFERENCES `outpatient_emr` (`emr_id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='处方主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `prescription_template`
--

DROP TABLE IF EXISTS `prescription_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prescription_template` (
  `pt_id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `pt_name` varchar(200) NOT NULL COMMENT '模板名称',
  `d_id` varchar(50) DEFAULT NULL COMMENT '创建医生',
  `pt_diagnosis` varchar(500) DEFAULT NULL COMMENT '关联诊断',
  `pt_dept` varchar(50) DEFAULT NULL COMMENT '科室',
  `pt_content` text COMMENT '药品内容(JSON)',
  `pt_create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`pt_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='处方模板表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `queue_number`
--

DROP TABLE IF EXISTS `queue_number`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `queue_number` (
  `q_id` int NOT NULL AUTO_INCREMENT,
  `o_id` int DEFAULT NULL COMMENT '挂号订单ID',
  `q_state` int DEFAULT '0' COMMENT '状态:0=待叫号,1=已叫号,2=已过号,3=已完成',
  `q_create_time` datetime DEFAULT NULL COMMENT '取号时间',
  `q_call_time` datetime DEFAULT NULL COMMENT '叫号时间',
  `q_finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`q_id`) USING BTREE,
  KEY `qTOo` (`o_id`),
  CONSTRAINT `fk_queue_orders` FOREIGN KEY (`o_id`) REFERENCES `orders` (`o_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3 COMMENT='排队叫号表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `refund_request`
--

DROP TABLE IF EXISTS `refund_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refund_request` (
  `rf_id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `br_id` int DEFAULT NULL COMMENT '收费记录ID',
  `rf_amount` decimal(10,2) NOT NULL COMMENT '退费金额',
  `rf_reason` varchar(500) DEFAULT NULL COMMENT '退费原因',
  `rf_status` tinyint DEFAULT '0' COMMENT '状态:0=待审核 1=已通过 2=已拒绝',
  `rf_requester` varchar(50) DEFAULT NULL COMMENT '申请人',
  `rf_approver` varchar(50) DEFAULT NULL COMMENT '审核人',
  `rf_approve_time` datetime DEFAULT NULL COMMENT '审核时间',
  `rf_create_time` datetime DEFAULT NULL COMMENT '申请时间',
  `rf_note` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`rf_id`),
  KEY `fk_refund_billing` (`br_id`),
  CONSTRAINT `fk_refund_billing` FOREIGN KEY (`br_id`) REFERENCES `billing_record` (`br_id`)
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_refund_void_invoice` AFTER UPDATE ON `refund_request` FOR EACH ROW BEGIN
    IF NEW.rf_status = 1 AND (OLD.rf_status IS NULL OR OLD.rf_status != 1) THEN
        UPDATE invoice_record inv
        JOIN billing_record br ON inv.br_id = br.br_id
        SET inv.inv_status = -1,
            inv.inv_cancel_time = NOW(),
            inv.inv_cancel_reason = CONCAT('refund_approved rf_id=', NEW.rf_id)
        WHERE br.br_id = NEW.br_id AND inv.inv_status = 0;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `registrate`
--

DROP TABLE IF EXISTS `registrate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `registrate` (
  `p_id` int NOT NULL,
  `d_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `appoint_time` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`appoint_time`,`p_id`) USING BTREE,
  KEY `rTOd` (`d_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `triage_record`
--

DROP TABLE IF EXISTS `triage_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `triage_record` (
  `t_id` int NOT NULL AUTO_INCREMENT,
  `p_id` int DEFAULT NULL COMMENT '患者ID',
  `d_id` char(6) DEFAULT NULL COMMENT '医生ID',
  `t_level` int DEFAULT '0' COMMENT '分诊级别:0=普通,1=优先,2=急诊',
  `t_status` int DEFAULT '0' COMMENT '状态:0=待分诊,1=已分诊,2=已就诊',
  `t_note` varchar(255) DEFAULT NULL COMMENT '分诊备注',
  `t_create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `t_chief_complaint` varchar(500) DEFAULT NULL COMMENT '主诉',
  `t_temperature` decimal(5,1) DEFAULT NULL COMMENT '体温(℃)',
  `t_blood_pressure` varchar(20) DEFAULT NULL COMMENT '血压(mmHg)',
  `t_heart_rate` int DEFAULT NULL COMMENT '心率(次/分)',
  `t_weight` decimal(5,1) DEFAULT NULL COMMENT '体重(kg)',
  `t_source` varchar(20) DEFAULT '现场' COMMENT '来源:现场/预约/转诊',
  PRIMARY KEY (`t_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='分诊记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `v_doctor_today_queue`
--

DROP TABLE IF EXISTS `v_doctor_today_queue`;
/*!50001 DROP VIEW IF EXISTS `v_doctor_today_queue`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_doctor_today_queue` AS SELECT 
 1 AS `doctorId`,
 1 AS `doctorName`,
 1 AS `department`,
 1 AS `title`,
 1 AS `queueId`,
 1 AS `patientId`,
 1 AS `patientName`,
 1 AS `queueState`,
 1 AS `stateLabel`,
 1 AS `queueTime`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_patient_billing_detail`
--

DROP TABLE IF EXISTS `v_patient_billing_detail`;
/*!50001 DROP VIEW IF EXISTS `v_patient_billing_detail`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_patient_billing_detail` AS SELECT 
 1 AS `patientId`,
 1 AS `patientName`,
 1 AS `orderId`,
 1 AS `orderTime`,
 1 AS `regType`,
 1 AS `regFee`,
 1 AS `drugFee`,
 1 AS `checkFee`,
 1 AS `totalFee`,
 1 AS `payState`,
 1 AS `payStateLabel`,
 1 AS `payMethod`*/;
SET character_set_client = @saved_cs_client;

--
-- Dumping routines for database 'hospital'
--
/*!50003 DROP PROCEDURE IF EXISTS `insert_check` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `insert_check`(IN ch_name_param VARCHAR(255),
          IN ch_price_param DECIMAL(10, 2))
BEGIN
         INSERT INTO checks (ch_name, ch_price)
         VALUES (ch_name_param, ch_price_param);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `insert_drug` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `insert_drug`(IN dr_name_param VARCHAR(255),
          IN dr_price_param DECIMAL(10, 2),
          IN dr_number_param INT,
         IN dr_publisher_param VARCHAR(255),
         IN dr_unit_param VARCHAR(255))
BEGIN
         INSERT INTO drug (dr_name, dr_price, dr_number, dr_publisher, dr_unit)
         VALUES(dr_name_param,dr_price_param,dr_number_param,dr_publisher_param,
dr_unit_param);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `insert_orders` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `insert_orders`(IN p_id_param INT,
          IN d_id_param CHAR(6),
          IN o_record_param VARCHAR(255),
          IN o_start_param VARCHAR(255),
          IN o_end_param VARCHAR(255),
          IN o_state_param INT,
          IN o_drug_param VARCHAR(255),
          IN o_check_param VARCHAR(255),
          IN o_total_price_param DECIMAL(10, 2),
          IN o_price_state_param INT,
          IN o_advice_param VARCHAR(255))
BEGIN
       INSERT INTO `orders` (p_id, d_id, o_record, o_start, o_end, o_state, o_drug, o_check, o_total_price, o_price_state, o_advice)
      VALUES(p_id_param,d_id_param,o_record_param,o_start_param,o_end_param,o_state_param, o_drug_param,o_check_param,o_total_price_param,o_price_state_param,o_advice_param);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_daily_clinic_report` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_daily_clinic_report`(IN report_date DATE)
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
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_department_income` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_department_income`(IN start_date DATE, IN end_date DATE)
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
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_doctor_workload` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_doctor_workload`(IN start_date DATE, IN end_date DATE, IN doctor_id CHAR(6))
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
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_drug_consumption_ranking` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_drug_consumption_ranking`(
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
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `sp_patient_visit_history` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_patient_visit_history`(IN patient_id INT)
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
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Final view structure for view `v_doctor_today_queue`
--

/*!50001 DROP VIEW IF EXISTS `v_doctor_today_queue`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_doctor_today_queue` AS select `d`.`d_id` AS `doctorId`,`d`.`d_name` AS `doctorName`,`d`.`d_section` AS `department`,`d`.`d_post` AS `title`,`qn`.`q_id` AS `queueId`,`o`.`p_id` AS `patientId`,`p`.`p_name` AS `patientName`,`qn`.`q_state` AS `queueState`,(case `qn`.`q_state` when 0 then 'waiting' when 1 then 'in_consult' when 2 then 'skipped' when 3 then 'finished' else 'unknown' end) AS `stateLabel`,`qn`.`q_create_time` AS `queueTime` from (((`doctor` `d` join `orders` `o` on((`d`.`d_id` = `o`.`d_id`))) join `queue_number` `qn` on((`o`.`o_id` = `qn`.`o_id`))) join `patient` `p` on((`o`.`p_id` = `p`.`p_id`))) where (cast(`qn`.`q_create_time` as date) = curdate()) order by `d`.`d_id`,`qn`.`q_state`,`qn`.`q_id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_patient_billing_detail`
--

/*!50001 DROP VIEW IF EXISTS `v_patient_billing_detail`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_patient_billing_detail` AS select `p`.`p_id` AS `patientId`,`p`.`p_name` AS `patientName`,`o`.`o_id` AS `orderId`,`o`.`o_start` AS `orderTime`,`o`.`o_triage` AS `regType`,`o`.`o_registration_fee` AS `regFee`,coalesce((select sum((`pd`.`pd_price` * `pd`.`pd_quantity`)) from ((`prescription_detail` `pd` join `prescription_master` `pm` on((`pd`.`pm_id` = `pm`.`pm_id`))) join `outpatient_emr` `e` on((`pm`.`emr_id` = `e`.`emr_id`))) where (`e`.`o_id` = `o`.`o_id`)),0) AS `drugFee`,coalesce((select sum(`oc`.`ch_price`) from (`order_check` `oc` join `outpatient_emr` `e` on((`oc`.`emr_id` = `e`.`emr_id`))) where (`e`.`o_id` = `o`.`o_id`)),0) AS `checkFee`,((`o`.`o_registration_fee` + coalesce((select sum((`pd`.`pd_price` * `pd`.`pd_quantity`)) from ((`prescription_detail` `pd` join `prescription_master` `pm` on((`pd`.`pm_id` = `pm`.`pm_id`))) join `outpatient_emr` `e` on((`pm`.`emr_id` = `e`.`emr_id`))) where (`e`.`o_id` = `o`.`o_id`)),0)) + coalesce((select sum(`oc`.`ch_price`) from (`order_check` `oc` join `outpatient_emr` `e` on((`oc`.`emr_id` = `e`.`emr_id`))) where (`e`.`o_id` = `o`.`o_id`)),0)) AS `totalFee`,`o`.`o_price_state` AS `payState`,(case when (`o`.`o_price_state` = 1) then 'paid' else 'unpaid' end) AS `payStateLabel`,`o`.`o_payment_method` AS `payMethod` from (`patient` `p` join `orders` `o` on((`p`.`p_id` = `o`.`p_id`))) order by `o`.`o_start` desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-04 22:48:36




