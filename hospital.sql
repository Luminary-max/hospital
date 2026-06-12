/*
 Navicat Premium Data Transfer

 Source Server         : xsgl
 Source Server Type    : MySQL
 Source Server Version : 80100 (8.1.0)
 Source Host           : localhost:3306
 Source Schema         : hospital

 Target Server Type    : MySQL
 Target Server Version : 80100 (8.1.0)
 File Encoding         : 65001

 Date: 15/07/2024 20:49:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admini
-- ----------------------------
DROP TABLE IF EXISTS `admini`;
CREATE TABLE `admini`  (
  `a_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `a_password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `a_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `a_gender` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `a_card` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `a_phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `a_email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`a_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of admini
-- ----------------------------
INSERT INTO `admini` VALUES ('202301', '123456', 'admin', '男', '440111111111111111', '13544444444', '123@qq.com');

-- ----------------------------
-- Table structure for arrange
-- ----------------------------
DROP TABLE IF EXISTS `arrange`;
CREATE TABLE `arrange`  (
  `ar_id` char(16) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `ar_time` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `d_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ar_id`) USING BTREE,
  INDEX `arTOd`(`d_id` ASC) USING BTREE,
  CONSTRAINT `arTOd` FOREIGN KEY (`d_id`) REFERENCES `doctor` (`d_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of arrange
-- ----------------------------
INSERT INTO `arrange` VALUES ('D240012024-07-14', '2024-07-14', 'D24001');
INSERT INTO `arrange` VALUES ('D240012024-07-15', '2024-07-15', 'D24001');
INSERT INTO `arrange` VALUES ('D240012024-07-16', '2024-07-16', 'D24001');
INSERT INTO `arrange` VALUES ('D240022024-07-14', '2024-07-14', 'D24002');
INSERT INTO `arrange` VALUES ('D240022024-07-16', '2024-07-16', 'D24002');
INSERT INTO `arrange` VALUES ('D240032024-07-16', '2024-07-16', 'D24003');
INSERT INTO `arrange` VALUES ('D240292024-07-14', '2024-07-14', 'D24029');
INSERT INTO `arrange` VALUES ('D240292024-07-15', '2024-07-15', 'D24029');

-- ----------------------------
-- Table structure for arrangement
-- ----------------------------
DROP TABLE IF EXISTS `arrangement`;
CREATE TABLE `arrangement`  (
  `ar_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `ar_time` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ar_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of arrangement
-- ----------------------------
INSERT INTO `arrangement` VALUES ('D240012024-07-15', '2024-07-15');
INSERT INTO `arrangement` VALUES ('D240022024-07-16', '2024-07-16');
INSERT INTO `arrangement` VALUES ('D240292024-07-15', '2024-07-15');

-- ----------------------------
-- Table structure for bed
-- ----------------------------
DROP TABLE IF EXISTS `bed`;
CREATE TABLE `bed`  (
  `b_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `p_id` int NULL DEFAULT NULL,
  `b_state` int NULL DEFAULT NULL,
  `b_start` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `b_reason` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `d_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `version` int NULL DEFAULT NULL,
  PRIMARY KEY (`b_id`) USING BTREE,
  INDEX `bTOp`(`p_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bed
-- ----------------------------
INSERT INTO `bed` VALUES ('BD0001', 100001, 1, '2024-07-15', '治疗骨折', 'D24029', 2);
INSERT INTO `bed` VALUES ('BD0002', 100002, 1, '2024-07-02 09:00', '骨折手术后恢复', NULL, 1);
INSERT INTO `bed` VALUES ('BD0003', -1, 0, NULL, NULL, '-1', 1);
INSERT INTO `bed` VALUES ('BD0004', 100004, 1, '2024-07-03 10:00', '急性阑尾炎手术', NULL, 1);
INSERT INTO `bed` VALUES ('BD0005', 100005, 1, '2024-07-04 11:00', '长期慢性病住院治疗', NULL, 1);
INSERT INTO `bed` VALUES ('BD0006', 100002, 1, '2024-07-14', '骨折', 'D24001', 2);
INSERT INTO `bed` VALUES ('BD0007', 100007, 1, '2024-07-05 12:00', '糖尿病住院调理', NULL, 1);
INSERT INTO `bed` VALUES ('BD0008', -1, 0, NULL, NULL, '-1', 1);
INSERT INTO `bed` VALUES ('BD0009', -1, 0, NULL, NULL, '-1', 1);
INSERT INTO `bed` VALUES ('BD0010', 100010, 1, '2024-07-07 14:00', '肿瘤切除术后化疗', NULL, 1);
INSERT INTO `bed` VALUES ('BD0020', -1, 0, NULL, NULL, '-1', NULL);

-- ----------------------------
-- Table structure for checks
-- ----------------------------
DROP TABLE IF EXISTS `checks`;
CREATE TABLE `checks`  (
  `ch_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `ch_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `ch_price` decimal(10, 2) NULL DEFAULT NULL,
  PRIMARY KEY (`ch_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of checks
-- ----------------------------
INSERT INTO `checks` VALUES ('CH0001', 'B超', 201.00);
INSERT INTO `checks` VALUES ('CH0002', '甲状腺检查', 434.00);
INSERT INTO `checks` VALUES ('CH0003', '视力检查', 50.00);
INSERT INTO `checks` VALUES ('CH0004', 'X光摄影', 120.00);
INSERT INTO `checks` VALUES ('CH0005', 'DR', 240.00);
INSERT INTO `checks` VALUES ('CH0006', '钼靶检查', 360.00);
INSERT INTO `checks` VALUES ('CH0007', '骨密度检测', 280.00);
INSERT INTO `checks` VALUES ('CH0008', '胃镜', 500.00);
INSERT INTO `checks` VALUES ('CH0009', '结肠镜', 600.00);
INSERT INTO `checks` VALUES ('CH0010', '脑电图', 450.00);
INSERT INTO `checks` VALUES ('CH0011', '肌电图', 400.00);
INSERT INTO `checks` VALUES ('CH0012', 'CT', 435.00);
INSERT INTO `checks` VALUES ('CH0013', '核磁共振', 653.00);
INSERT INTO `checks` VALUES ('CH0014', '心电图', 634.00);
INSERT INTO `checks` VALUES ('CH0015', '彩超', 213.00);
INSERT INTO `checks` VALUES ('CH0016', '血常规', 434.00);
INSERT INTO `checks` VALUES ('CH0017', '肝功能', 543.00);
INSERT INTO `checks` VALUES ('CH0018', '血糖', 434.00);
INSERT INTO `checks` VALUES ('CH0019', '肺功能测试', 300.00);
INSERT INTO `checks` VALUES ('CH0020', '过敏原检测', 350.00);
INSERT INTO `checks` VALUES ('CH0021', '乳腺钼靶', 390.00);
INSERT INTO `checks` VALUES ('CH0022', '胶囊胃镜', 800.00);
INSERT INTO `checks` VALUES ('CH0023', '动态心电图', 700.00);
INSERT INTO `checks` VALUES ('CH0024', '动态血压监测', 600.00);
INSERT INTO `checks` VALUES ('CH0025', '睡眠监测', 900.00);
INSERT INTO `checks` VALUES ('CH0026', '骨龄检测', 480.00);
INSERT INTO `checks` VALUES ('CH0027', '基因检测', 1500.00);
INSERT INTO `checks` VALUES ('CH0028', '胎儿心电图', 700.00);
INSERT INTO `checks` VALUES ('CH0029', '听力测试', 300.00);
INSERT INTO `checks` VALUES ('CH0030', '视力矫正筛查', 450.00);
INSERT INTO `checks` VALUES ('CH0031', '皮肤病理检测', 500.00);
INSERT INTO `checks` VALUES ('CH0032', '尿液分析', 280.00);
INSERT INTO `checks` VALUES ('CH0033', '血液流变学检测', 360.00);
INSERT INTO `checks` VALUES ('CH0034', '精液分析', 400.00);
INSERT INTO `checks` VALUES ('CH0035', '激素水平测试', 500.00);
INSERT INTO `checks` VALUES ('CH0036', '肿瘤标志物检测', 800.00);
INSERT INTO `checks` VALUES ('CH0037', '自身免疫抗体检测', 600.00);
INSERT INTO `checks` VALUES ('CH0038', '微量元素检测', 350.00);

-- ----------------------------
-- Table structure for department
-- ----------------------------
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department`  (
  `de_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `de_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `de_number` int NULL DEFAULT NULL,
  PRIMARY KEY (`de_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of department
-- ----------------------------
INSERT INTO `department` VALUES ('DE0001', '神经内科', 1);
INSERT INTO `department` VALUES ('DE0002', '呼吸与危重症医学科', 1);
INSERT INTO `department` VALUES ('DE0003', '内分泌科', 2);
INSERT INTO `department` VALUES ('DE0004', '消化内科', 1);
INSERT INTO `department` VALUES ('DE0005', '心血管内科', 1);
INSERT INTO `department` VALUES ('DE0006', '肾内科', 1);
INSERT INTO `department` VALUES ('DE0007', '发热门诊', 1);
INSERT INTO `department` VALUES ('DE0008', '外科', 1);
INSERT INTO `department` VALUES ('DE0009', '手足外科', 1);
INSERT INTO `department` VALUES ('DE0010', '普通外科', 1);
INSERT INTO `department` VALUES ('DE0011', '肛肠外科', 1);
INSERT INTO `department` VALUES ('DE0012', '神经外科', 1);
INSERT INTO `department` VALUES ('DE0013', '泌尿外科', 1);
INSERT INTO `department` VALUES ('DE0014', '骨科', 1);
INSERT INTO `department` VALUES ('DE0015', '烧伤整形外科', 1);
INSERT INTO `department` VALUES ('DE0016', '妇产科', 1);
INSERT INTO `department` VALUES ('DE0017', '妇科', 1);
INSERT INTO `department` VALUES ('DE0018', '产科', 1);
INSERT INTO `department` VALUES ('DE0019', '儿科', 1);
INSERT INTO `department` VALUES ('DE0020', '儿童保健科', 1);
INSERT INTO `department` VALUES ('DE0021', '耳鼻咽喉科', 1);
INSERT INTO `department` VALUES ('DE0022', '眼科', 1);
INSERT INTO `department` VALUES ('DE0023', '口腔科', 1);
INSERT INTO `department` VALUES ('DE0024', '中医科', 1);
INSERT INTO `department` VALUES ('DE0025', '康复医学科', 1);
INSERT INTO `department` VALUES ('DE0026', '急诊科', 1);
INSERT INTO `department` VALUES ('DE0027', '皮肤性病科', 1);
INSERT INTO `department` VALUES ('DE0028', '功能科', 1);
INSERT INTO `department` VALUES ('DE0029', NULL, NULL);

-- ----------------------------
-- Table structure for doctor
-- ----------------------------
DROP TABLE IF EXISTS `doctor`;
CREATE TABLE `doctor`  (
  `d_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `de_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `d_password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `d_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `d_gender` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `d_phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `d_card` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `d_email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `d_post` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `d_introduction` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `d_section` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `d_state` int NOT NULL,
  `d_price` decimal(10, 2) NULL DEFAULT NULL,
  `d_people` int NULL DEFAULT NULL,
  `d_star` decimal(10, 2) NULL DEFAULT NULL,
  `d_avg_star` decimal(10, 2) NULL DEFAULT NULL,
  PRIMARY KEY (`d_id`) USING BTREE,
  INDEX `dTOd`(`de_id` ASC) USING BTREE,
  CONSTRAINT `dTOd` FOREIGN KEY (`de_id`) REFERENCES `department` (`de_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of doctor
-- ----------------------------
INSERT INTO `doctor` VALUES ('D24001', 'DE0001', '3d7dd7b26500bd0595573b651d0080fd', '李明', '男', '13500000001', '110101199001010000', 'liming@example.com', '主任医师', '神经内科专家，10年临床经验', '神经内科', 1, 245.00, 9, 39.80, 4.42);
INSERT INTO `doctor` VALUES ('D24002', 'DE0002', 'Pass5678', '王芳', '女', '13500000002', '110102199002020000', 'wangfang@example.com', '副主任医师', '呼吸科专业医生，5年工作经验', '呼吸与危重症医学科', 1, 150.00, 1, 4.70, 4.70);
INSERT INTO `doctor` VALUES ('D24003', 'DE0003', 'Pass910111', '张伟', '男', '13500000003', '110103199003030000', 'zhangwei@example.com', '主治医师', '内分泌科医师，专长糖尿病治疗', '内分泌科', 1, 180.00, 1, 4.90, 4.90);
INSERT INTO `doctor` VALUES ('D24004', 'DE0004', 'Pass1314', '赵敏', '女', '13500000004', '110104199004040000', 'zhaomin@example.com', '住院医师', '消化内科医师，擅长胃肠疾病', '消化内科', 1, 120.00, 1, 4.60, 4.60);
INSERT INTO `doctor` VALUES ('D24005', 'DE0005', 'Pass2023', '孙杨', '男', '13500000005', '110105199005050000', 'sunyang@example.com', '主任医师', '心血管内科专家，20年手术经验', '心血管内科', 1, 300.00, 1, 5.00, 5.00);
INSERT INTO `doctor` VALUES ('D24006', 'DE0006', 'Pass55667788', '周梅', '女', '13500000006', '110106199006060000', 'zhoumei@example.com', '副主任医师', '肾内科专业医生，8年临床经验', '肾内科', 1, 160.00, 1, 4.50, 4.50);
INSERT INTO `doctor` VALUES ('D24007', 'DE0007', 'PassA1b2C3', '吴刚', '男', '13500000007', '110107199007070000', 'wugang@example.com', '主治医师', '发热门诊医师，熟悉各类传染病', '发热门诊', 1, 100.00, 1, 4.40, 4.40);
INSERT INTO `doctor` VALUES ('D24008', 'DE0008', 'Pass4567', '陈晨', '女', '13500000008', '110108199008080000', 'chenchen@example.com', '主任医师', '外科手术专家，擅长微创手术', '外科', 1, 250.00, 1, 4.70, 4.70);
INSERT INTO `doctor` VALUES ('D24009', 'DE0009', 'PassAbc123', '刘强', '男', '13500000009', '110109199009090000', 'liuqiang@example.com', '副主任医师', '手足外科医师，精通手足显微手术', '手足外科', 1, 220.00, 1, 4.60, 4.60);
INSERT INTO `doctor` VALUES ('D24010', 'DE0010', 'Pass987654', '郭静', '女', '13500000010', '110110199010110011', 'guojing@example.com', '主治医师', '普通外科医师，擅长腹部手术', '普通外科', 1, 180.00, 1, 4.50, 4.50);
INSERT INTO `doctor` VALUES ('D24011', 'DE0011', 'Pass111213', '陈丽', '女', '13500000011', '110111199011011111', 'chenli@example.com', '副主任医师', '肛肠外科专家，擅长复杂病例治疗', '肛肠外科', 1, 200.00, 1, 4.70, 4.70);
INSERT INTO `doctor` VALUES ('D24012', 'DE0012', 'Pass131415', '马云', '男', '13500000012', '110112199012021212', 'mayun1@example.com', '主任医师', '神经外科资深医生，手术技术娴熟', '神经外科', 1, 300.00, 1, 5.00, 5.00);
INSERT INTO `doctor` VALUES ('D24013', 'DE0013', 'Pass151617', '徐磊', '男', '13500000013', '110113199013031313', 'xulei@example.com', '主治医师', '泌尿外科专业医生，对肾结石有深入研究', '泌尿外科', 1, 250.00, 7, 4.80, 4.80);
INSERT INTO `doctor` VALUES ('D24014', 'DE0014', 'Pass171819', '朱静', '女', '13500000014', '110114199014041414', 'zhujing@example.com', '副主任医师', '骨科专业医生，擅长骨折治疗', '骨科', 1, 180.00, 1, 4.60, 4.60);
INSERT INTO `doctor` VALUES ('D24015', 'DE0015', 'Pass191120', '周杰', '男', '13500000015', '110115199015051515', 'zhoujie@example.com', '主任医师', '烧伤整形外科资深医生，擅长皮肤移植', '烧伤整形外科', 1, 280.00, 1, 4.90, 4.90);
INSERT INTO `doctor` VALUES ('D24016', 'DE0016', 'Pass202122', '吴艳', '女', '13500000016', '110116199016061616', 'wuyan@example.com', '副主任医师', '妇产科医生，专长于孕产妇护理', '妇产科', 1, 260.00, 1, 4.80, 4.80);
INSERT INTO `doctor` VALUES ('D24017', 'DE0017', 'Pass222324', '李强', '男', '13500000017', '110117199017071717', 'liqiang@example.com', '主治医师', '妇科专业医生，擅长妇科疾病治疗', '妇科', 1, 240.00, 1, 4.70, 4.70);
INSERT INTO `doctor` VALUES ('D24018', 'DE0018', 'Pass242526', '张敏', '女', '13500000018', '110118199018081818', 'zhangmin@example.com', '主任医师', '产科专业医生，专长于分娩过程管理', '产科', 1, 220.00, 7, 4.90, 4.90);
INSERT INTO `doctor` VALUES ('D24019', 'DE0019', 'Pass262728', '赵勇', '男', '13500000019', '110119199019091919', 'zhaoyong@example.com', '副主任医师', '儿科医生，专注于儿童常见疾病', '儿科', 1, 150.00, 1, 4.50, 4.50);
INSERT INTO `doctor` VALUES ('D24020', 'DE0020', 'Pass282930', '钱芳', '女', '13500000020', '110120199021022020', 'qianfang@example.com', '主治医师', '儿童保健科医生，擅长儿童成长监测', '儿童保健科', 1, 120.00, 1, 4.60, 4.60);
INSERT INTO `doctor` VALUES ('D24021', 'DE0021', 'Pass303132', '孙洁', '女', '13500000021', '110121199022032121', 'sunjie@example.com', '副主任医师', '耳鼻咽喉科医生，专长于听力问题', '耳鼻咽喉科', 1, 160.00, 1, 4.70, 4.70);
INSERT INTO `doctor` VALUES ('D24022', 'DE0022', 'Pass323334', '刘波', '男', '13500000022', '110122199023042222', 'liubo@example.com', '主任医师', '眼科医生，擅长白内障手术', '眼科', 1, 210.00, 1, 4.80, 4.80);
INSERT INTO `doctor` VALUES ('D24023', 'DE0023', 'Pass343536', '郭阳', '男', '13500000023', '110123199024052323', 'guoyang@example.com', '副主任医师', '口腔科医生，专长于牙齿矫正', '口腔科', 1, 130.00, 1, 4.60, 4.60);
INSERT INTO `doctor` VALUES ('D24024', 'DE0024', 'Pass363738', '马云', '女', '13500000024', '110124199025062424', 'mayun@example.com', '主任医师', '中医科专家，擅长中草药治疗', '中医科', 1, 140.00, 1, 4.90, 4.90);
INSERT INTO `doctor` VALUES ('D24025', 'DE0025', 'Pass383940', '徐静', '女', '13500000025', '110125199026072525', 'xujing@example.com', '副主任医师', '康复医学科医生，专长于物理治疗', '康复医学科', 1, 100.00, 1, 4.40, 4.40);
INSERT INTO `doctor` VALUES ('D24026', 'DE0026', 'Pass404142', '朱丽', '女', '13500000026', '110126199027082626', 'zhuli@example.com', '主治医师', '急诊科医生，擅长急救处理', '急诊科', 1, 110.00, 1, 4.50, 4.50);
INSERT INTO `doctor` VALUES ('D24027', 'DE0027', 'Pass424344', '周波', '男', '13500000027', '110127199028092727', 'zhoubo@example.com', '主任医师', '皮肤性病科医生，专长于皮肤病治疗', '皮肤性病科', 1, 120.00, 1, 4.60, 4.60);
INSERT INTO `doctor` VALUES ('D24028', 'DE0028', 'Pass444546', '吴芳', '女', '13500000028', '110128199029102828', 'wufang@example.com', '副主任医师', '功能科医生，专长于医学影像诊断', '功能科', 1, 90.00, 1, 4.30, 4.30);
INSERT INTO `doctor` VALUES ('D24029', 'DE0001', '3d7dd7b26500bd0595573b651d0080fd', '吴芳', '女', '13500000028', '110128199029102828', 'wufang@example.com', '副主任医师', '功能科医生，专长于医学影像诊断', '神经内科', 1, 90.00, 3, 14.30, 4.77);
INSERT INTO `doctor` VALUES ('D24034', 'DE0003', '3d7dd7b26500bd0595573b651d0080fd', '肖辉', '男', '18970469354', '243546543234565', 'ruolanljl666@outlook.com', '副主任医师', '11', '内分泌科', 1, 23.00, 0, 0.00, NULL);

-- ----------------------------
-- Table structure for drug
-- ----------------------------
DROP TABLE IF EXISTS `drug`;
CREATE TABLE `drug`  (
  `dr_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `dr_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `dr_price` decimal(10, 2) NULL DEFAULT NULL,
  `dr_number` int NULL DEFAULT NULL,
  `dr_publisher` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `dr_unit` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`dr_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of drug
-- ----------------------------
INSERT INTO `drug` VALUES ('D00001', '对乙酰氨基酚片', 0.50, 989, '华东制药', '片');
INSERT INTO `drug` VALUES ('D00002', '阿莫西林胶囊', 5.75, 497, '华北制药', '粒');
INSERT INTO `drug` VALUES ('D00003', '布洛芬缓释胶囊', 3.20, 799, '华南药业', '粒');
INSERT INTO `drug` VALUES ('D00004', '罗红霉素片', 15.80, 297, '东北制药', '片');
INSERT INTO `drug` VALUES ('D00005', '复方甘草口服液', 12.30, 600, '西北制药', '支');
INSERT INTO `drug` VALUES ('D00006', '硝酸甘油注射液', 29.90, 200, '同济药业', 'ml');
INSERT INTO `drug` VALUES ('D00007', '胰岛素注射液', 45.60, 150, '华东生物', 'ml');
INSERT INTO `drug` VALUES ('D00008', '头孢克肟颗粒', 18.50, 450, '华西制药', 'g');
INSERT INTO `drug` VALUES ('D00009', '蒙脱石散', 9.90, 700, '中原药业', 'g');
INSERT INTO `drug` VALUES ('D00010', '复方氨基酸注射液', 36.20, 250, '东南制药', 'ml');
INSERT INTO `drug` VALUES ('D00011', '维生素C片', 1.20, 1200, '健康药业', '片');
INSERT INTO `drug` VALUES ('D00012', '感冒灵颗粒', 8.50, 900, '康复制药', '袋');
INSERT INTO `drug` VALUES ('D00013', '吗啡注射液', 49.75, 100, '国药集团', 'ml');
INSERT INTO `drug` VALUES ('D00014', '青霉素V钾片', 3.60, 1500, '联合制药', '片');
INSERT INTO `drug` VALUES ('D00015', '甲硝唑凝胶', 22.00, 400, '华康生物', 'g');
INSERT INTO `drug` VALUES ('D00016', '地塞米松磷酸钠注射液', 4.80, 800, '天力药业', 'ml');
INSERT INTO `drug` VALUES ('D00017', '阿司匹林肠溶片', 6.30, 1000, '康泰制药', '片');
INSERT INTO `drug` VALUES ('D00018', '硫酸氢氯吡格雷片', 28.50, 600, '安邦制药', '片');
INSERT INTO `drug` VALUES ('D00019', '多潘立酮片', 11.90, 700, '利民药业', '片');
INSERT INTO `drug` VALUES ('D00020', '硝酸异山梨酯片', 19.80, 499, '顺心制药', '片');
INSERT INTO `drug` VALUES ('D00021', '利巴韦林颗粒', 7.60, 800, '阳光药业', '袋');
INSERT INTO `drug` VALUES ('D00022', '酮洛芬胶囊', 16.50, 600, '康宁制药', '粒');
INSERT INTO `drug` VALUES ('D00023', '格列本脲片', 9.10, 1000, '瑞康制药', '片');
INSERT INTO `drug` VALUES ('D00024', '复方丹参滴丸', 25.40, 900, '同仁堂', '丸');
INSERT INTO `drug` VALUES ('D00025', '氯雷他定片', 5.90, 1200, '华瑞制药', '片');
INSERT INTO `drug` VALUES ('D00026', '罗格列酮钠片', 34.20, 400, '博爱制药', '片');
INSERT INTO `drug` VALUES ('D00027', '氨茶碱片', 3.00, 1500, '天宇制药', '片');
INSERT INTO `drug` VALUES ('D00028', '曲马多缓释片', 29.00, 300, '新世纪药业', '片');
INSERT INTO `drug` VALUES ('D00029', '奥美拉唑肠溶胶囊', 14.60, 800, '康华制药', '粒');
INSERT INTO `drug` VALUES ('D00030', '碳酸氢钠注射液', 2.50, 1000, '海王制药', 'ml');
INSERT INTO `drug` VALUES ('D00036', '大力丸', 23.00, 1, '江西制药', '袋');

-- ----------------------------
-- Table structure for issue_check
-- ----------------------------
DROP TABLE IF EXISTS `issue_check`;
CREATE TABLE `issue_check`  (
  `ch_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `o_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`ch_id`, `o_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of issue_check
-- ----------------------------

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `o_id` int NOT NULL AUTO_INCREMENT,
  `p_id` int NULL DEFAULT NULL,
  `d_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `o_record` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `o_start` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `o_end` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `o_state` int NULL DEFAULT NULL,
  `o_drug` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `o_check` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `o_total_price` decimal(10, 2) NULL DEFAULT NULL,
  `o_price_state` int NULL DEFAULT NULL,
  `o_advice` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`o_id`) USING BTREE,
  INDEX `oTOp`(`p_id` ASC) USING BTREE,
  INDEX `0TOd`(`d_id` ASC) USING BTREE,
  CONSTRAINT `oTOd` FOREIGN KEY (`d_id`) REFERENCES `doctor` (`d_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `oTOp` FOREIGN KEY (`p_id`) REFERENCES `patient` (`p_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 211209 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (103280, 100002, 'D24001', '骨折\n', '2024-07-15 08:30-09:30', '2024-07-15 8:16:6', 1, '对乙酰氨基酚片*0.5(元)*1  药物总价0.5元  // 阿莫西林胶囊*5.75(元)*1 药物总价5.75元', 'B超*201(元)  项目总价201元  // B超*201(元) 项目总价201元', 0.00, 1, '二次诊断');
INSERT INTO `orders` VALUES (105281, 100001, 'D24029', '骨折', '2024-07-15 09:30-10:30', '2024-07-15 9:13:12', 1, '对乙酰氨基酚片*0.5(元)*1 阿莫西林胶囊*5.75(元)*1  药物总价6.25元  // 阿莫西林胶囊*5.75(元)*1 布洛芬缓释胶囊*3.2(元)*1 药物总价8.95元', 'B超*201(元) 甲状腺检查*434(元)  项目总价635元  // 甲状腺检查*434(元) 项目总价434元', 0.00, 1, '');
INSERT INTO `orders` VALUES (106198, 100001, 'D24001', '', '2024-07-14 09:30-10:30', '2024-07-14 9:37:29', 1, '对乙酰氨基酚片*0.5(元)*1  药物总价0.5元 ', ' 项目总价0元 ', 0.00, 1, NULL);
INSERT INTO `orders` VALUES (106479, 100002, 'D24001', '', '2024-07-14 15:30-16:30', '2024-07-14 18:55:46', 1, '对乙酰氨基酚片*0.5(元)*1  药物总价0.5元  // 硝酸异山梨酯片*19.8(元)*1 药物总价19.8元 // 对乙酰氨基酚片*0.5(元)*1 药物总价0.5元', 'B超*201(元)  项目总价201元  // 动态血压监测*600(元) 项目总价600元 // B超*201(元) 项目总价201元', 0.00, 1, '');
INSERT INTO `orders` VALUES (106993, 100001, 'D24001', 'dgs', '2024-07-14 15:30-16:30', '2024-07-14 15:58:13', 1, '对乙酰氨基酚片*0.5(元)*1 罗红霉素片*15.8(元)*1  药物总价16.3元 ', '甲状腺检查*434(元)  项目总价434元 ', 0.00, 1, NULL);

-- ----------------------------
-- Table structure for patient
-- ----------------------------
DROP TABLE IF EXISTS `patient`;
CREATE TABLE `patient`  (
  `p_id` int NOT NULL,
  `p_password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `p_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `p_gender` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `p_phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `p_card` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `p_email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `p_state` int NULL DEFAULT NULL,
  `p_birthday` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `p_age` int NULL DEFAULT NULL,
  PRIMARY KEY (`p_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of patient
-- ----------------------------
INSERT INTO `patient` VALUES (100001, '3d7dd7b26500bd0595573b651d0080fd', '李雷', '男', '13500001111', '110101199001010111', 'leili@example.com', 1, '1990-01-01', 34);
INSERT INTO `patient` VALUES (100002, '3d7dd7b26500bd0595573b651d0080fd', '韩梅梅', '女', '13500002222', '110102199002020222', 'meimei.han@example.com', 1, '1990-02-02', 34);
INSERT INTO `patient` VALUES (100003, '3d7dd7b26500bd0595573b651d0080fd', '张强', '男', '13500003333', '110103199003030333', 'zqiang@example.com', 1, '1990-03-03', 34);
INSERT INTO `patient` VALUES (100004, '3d7dd7b26500bd0595573b651d0080fd', '王芳', '女', '13500004444', '110104199004040444', 'wangfang@example.com', 1, '1990-04-04', 34);
INSERT INTO `patient` VALUES (100005, '3d7dd7b26500bd0595573b651d0080fd', '刘波', '男', '13500005555', '110105199005050555', 'liubo@example.com', 1, '1990-05-05', 34);
INSERT INTO `patient` VALUES (100006, '3d7dd7b26500bd0595573b651d0080fd', '陈思', '女', '13500006666', '110106199006060666', 'sisi.chen@example.com', 1, '1990-06-06', 34);
INSERT INTO `patient` VALUES (100007, '3d7dd7b26500bd0595573b651d0080fd', '马云', '男', '13500007777', '110107199007070777', 'mayun@example.com', 1, '1990-07-07', 34);
INSERT INTO `patient` VALUES (100008, '3d7dd7b26500bd0595573b651d0080fd', '赵云', '女', '13500008888', '110108199008080888', 'yun.zhao@example.com', 1, '1990-08-08', 34);
INSERT INTO `patient` VALUES (100009, '3d7dd7b26500bd0595573b651d0080fd', '黄忠', '男', '13500009999', '110109199009090999', 'zhong.huang@example.com', 1, '1990-09-09', 34);
INSERT INTO `patient` VALUES (100010, 'Pass0123d7dd7b26500bd0595573b651d0080fd', '周杰', '男', '13500010101', '110110199010101010', 'jiezhou@example.com', 0, '1990-10-10', 34);
INSERT INTO `patient` VALUES (100021, '3d7dd7b26500bd0595573b651d0080fd', '人员', '男', '16654565446', '345324543532453654', 'ruolanljl66@outlook.com', 0, '2024-07-01', 24);

-- ----------------------------
-- Table structure for prescribe
-- ----------------------------
DROP TABLE IF EXISTS `prescribe`;
CREATE TABLE `prescribe`  (
  `dr_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `o_id` int NULL DEFAULT NULL,
  `dosage` decimal(10, 2) NULL DEFAULT NULL,
  PRIMARY KEY (`dr_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of prescribe
-- ----------------------------
INSERT INTO `prescribe` VALUES ('', NULL, NULL);

-- ----------------------------
-- Table structure for registrate
-- ----------------------------
DROP TABLE IF EXISTS `registrate`;
CREATE TABLE `registrate`  (
  `p_id` int NOT NULL,
  `d_id` char(6) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `appoint_time` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`appoint_time`, `p_id`) USING BTREE,
  INDEX `rTOd`(`d_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of registrate
-- ----------------------------
INSERT INTO `registrate` VALUES (100002, 'D24001', '2024-07-14 15:30-16:30');
INSERT INTO `registrate` VALUES (100002, 'D24001', '2024-07-14 16:30-17:30');
INSERT INTO `registrate` VALUES (100002, 'D24001', '2024-07-15 08:30-09:30');
INSERT INTO `registrate` VALUES (100001, 'D24029', '2024-07-15 09:30-10:30');
INSERT INTO `registrate` VALUES (100002, 'D24029', '2024-07-15 09:30-10:30');

-- ----------------------------
-- Procedure structure for insert_check
-- ----------------------------
DROP PROCEDURE IF EXISTS `insert_check`;
delimiter ;;
CREATE PROCEDURE `insert_check`(IN ch_name_param VARCHAR(255),
          IN ch_price_param DECIMAL(10, 2))
BEGIN
         INSERT INTO checks (ch_name, ch_price)
         VALUES (ch_name_param, ch_price_param);
END
;;
delimiter ;

-- ----------------------------
-- Procedure structure for insert_drug
-- ----------------------------
DROP PROCEDURE IF EXISTS `insert_drug`;
delimiter ;;
CREATE PROCEDURE `insert_drug`(IN dr_name_param VARCHAR(255),
          IN dr_price_param DECIMAL(10, 2),
          IN dr_number_param INT,
         IN dr_publisher_param VARCHAR(255),
         IN dr_unit_param VARCHAR(255))
BEGIN
         INSERT INTO drug (dr_name, dr_price, dr_number, dr_publisher, dr_unit)
         VALUES(dr_name_param,dr_price_param,dr_number_param,dr_publisher_param,
dr_unit_param);
END
;;
delimiter ;

-- ----------------------------
-- Procedure structure for insert_orders
-- ----------------------------
DROP PROCEDURE IF EXISTS `insert_orders`;
delimiter ;;
CREATE PROCEDURE `insert_orders`(IN p_id_param INT,
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
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table arrange
-- ----------------------------
DROP TRIGGER IF EXISTS `before_insert_arrange`;
delimiter ;;
CREATE TRIGGER `before_insert_arrange` BEFORE INSERT ON `arrange` FOR EACH ROW BEGIN
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
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table doctor
-- ----------------------------
DROP TRIGGER IF EXISTS `before_insert_doctor`;
delimiter ;;
CREATE TRIGGER `before_insert_doctor` BEFORE INSERT ON `doctor` FOR EACH ROW BEGIN
    DECLARE section_id CHAR(6);

    -- 获取或创建科室编号
    CALL get_or_create_department_id(NEW.d_section, section_id);

    -- 设置新的科室编号到插入记录中
    SET NEW.de_id = section_id;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table doctor
-- ----------------------------
DROP TRIGGER IF EXISTS `department_before_insert_doctor`;
delimiter ;;
CREATE TRIGGER `department_before_insert_doctor` BEFORE INSERT ON `doctor` FOR EACH ROW BEGIN
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
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table doctor
-- ----------------------------
DROP TRIGGER IF EXISTS `update_department_count_after_doctor_deactivation`;
delimiter ;;
CREATE TRIGGER `update_department_count_after_doctor_deactivation` AFTER UPDATE ON `doctor` FOR EACH ROW BEGIN
    IF NEW.d_state = 0 AND OLD.d_state != 0 THEN
        -- 更新 department 表，减少相应科室的科室人数
        UPDATE department
        SET de_number = de_number - 1
        WHERE de_id = NEW.de_id;
    END IF;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table orders
-- ----------------------------
DROP TRIGGER IF EXISTS `insert_into_registrate_after_order_insert`;
delimiter ;;
CREATE TRIGGER `insert_into_registrate_after_order_insert` AFTER INSERT ON `orders` FOR EACH ROW BEGIN
    -- 将订单表中的 p_id, d_id, o_start 插入到挂号表 registrate 中
    INSERT INTO registrate (p_id, d_id, appoint_time)
    VALUES (NEW.p_id, NEW.d_id, NEW.o_start);
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table orders
-- ----------------------------
DROP TRIGGER IF EXISTS `update_prescribe_after_insert`;
delimiter ;;
CREATE TRIGGER `update_prescribe_after_insert` AFTER INSERT ON `orders` FOR EACH ROW BEGIN
    DECLARE drug_name VARCHAR(255);
    DECLARE dosage DECIMAL(10, 2);
    DECLARE drug_id CHAR(6);
    DECLARE pos INT;
    DECLARE remaining_drug TEXT;
    
    SET remaining_drug = NEW.o_drug;
    
    WHILE LENGTH(remaining_drug) > 0 DO
        -- 获取药品名称和剂量
        SET pos = INSTR(remaining_drug, '*');
        SET drug_name = SUBSTRING(remaining_drug, 1, pos - 1);
        SET dosage = CAST(SUBSTRING(remaining_drug, pos + 1, INSTR(SUBSTRING(remaining_drug, pos + 1), ' ') - 1) AS DECIMAL(10, 2));
        
        -- 获取药品ID
        SELECT dr_id INTO drug_id FROM drug WHERE dr_name = drug_name LIMIT 1;
        
        -- 插入prescribe表
        INSERT INTO prescribe (dr_id, o_id, dosage)
        VALUES (drug_id, NEW.o_id, dosage);
        
        -- 更新剩余药品字符串
        SET remaining_drug = TRIM(SUBSTRING(remaining_drug, pos + LENGTH(dosage) + 2));
        
        -- 移除逗号和空格
        SET remaining_drug = TRIM(LEADING ', ' FROM remaining_drug);
    END WHILE;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
