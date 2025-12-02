/*
 Navicat Premium Data Transfer

 Source Server         : hos_db
 Source Server Type    : MySQL
 Source Server Version : 80041 (8.0.41)
 Source Host           : localhost:3306
 Source Schema         : db_hos

 Target Server Type    : MySQL
 Target Server Version : 80041 (8.0.41)
 File Encoding         : 65001

 Date: 28/05/2025 17:02:28
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin_user
-- ----------------------------
DROP TABLE IF EXISTS `admin_user`;
CREATE TABLE `admin_user`  (
  `a_id` INT NOT NULL COMMENT '主键，管理员id（账号）',
  `a_password` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '密码',
  `a_name` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `a_gender` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '性别',
  `a_card` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '证件号码',
  `a_phone` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `a_email` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  PRIMARY KEY (`a_id`) USING BTREE
) ENGINE = INNODB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of admin_user
-- ----------------------------
INSERT INTO `admin_user` VALUES (202301, '123456', 'admin', '男', '34000000000000', '13541111111', '123@qq.com');

-- ----------------------------
-- Table structure for arrange
-- ----------------------------
DROP TABLE IF EXISTS `arrange`;
CREATE TABLE `arrange`  (
  `ar_id` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `ar_time` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `d_id` INT NULL DEFAULT NULL,
  PRIMARY KEY (`ar_id`) USING BTREE,
  INDEX `arTOd`(`d_id` ASC) USING BTREE,
  CONSTRAINT `arTOd` FOREIGN KEY (`d_id`) REFERENCES `doctor_user` (`d_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = INNODB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of arrange
-- ----------------------------
INSERT INTO `arrange` VALUES ('10002023-11-19', '2023-11-19', 1000);
INSERT INTO `arrange` VALUES ('10002023-11-20', '2023-11-20', 1000);
INSERT INTO `arrange` VALUES ('10012023-11-19', '2023-11-19', 1001);
INSERT INTO `arrange` VALUES ('10012023-11-20', '2023-11-20', 1001);
INSERT INTO `arrange` VALUES ('10022023-11-19', '2023-11-19', 1002);
INSERT INTO `arrange` VALUES ('10022023-11-20', '2023-11-20', 1002);
INSERT INTO `arrange` VALUES ('10032023-11-19', '2023-11-19', 1003);
INSERT INTO `arrange` VALUES ('10042023-11-19', '2023-11-19', 1004);
INSERT INTO `arrange` VALUES ('10042023-11-20', '2023-11-20', 1004);
INSERT INTO `arrange` VALUES ('10042023-11-21', '2023-11-21', 1004);
INSERT INTO `arrange` VALUES ('10042023-11-23', '2023-11-23', 1004);
INSERT INTO `arrange` VALUES ('10072023-11-19', '2023-11-19', 1007);
INSERT INTO `arrange` VALUES ('10072023-11-20', '2023-11-20', 1007);
INSERT INTO `arrange` VALUES ('10072023-11-23', '2023-11-23', 1007);
INSERT INTO `arrange` VALUES ('10082023-11-19', '2023-11-19', 1008);
INSERT INTO `arrange` VALUES ('10082023-11-20', '2023-11-20', 1008);
INSERT INTO `arrange` VALUES ('10092023-11-19', '2023-11-19', 1009);
INSERT INTO `arrange` VALUES ('10092023-11-20', '2023-11-20', 1009);

-- ----------------------------
-- Table structure for bed
-- ----------------------------
DROP TABLE IF EXISTS `bed`;
CREATE TABLE `bed`  (
  `b_id` INT NOT NULL,
  `p_id` INT NULL DEFAULT NULL,
  `b_state` INT NULL DEFAULT NULL,
  `b_start` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `d_id` INT NULL DEFAULT NULL,
  `b_reason` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `version` INT NULL DEFAULT NULL,
  PRIMARY KEY (`b_id`) USING BTREE,
  INDEX `bTOp`(`p_id` ASC) USING BTREE,
  INDEX `bTOd`(`d_id` ASC) USING BTREE
) ENGINE = INNODB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bed
-- ----------------------------
INSERT INTO `bed` VALUES (1, 2000, 1, '2023-11-19', 1000, '病人状况比较糟糕，需要住院3天。照顾下。', NULL);
INSERT INTO `bed` VALUES (2, -1, 0, NULL, -1, NULL, NULL);
INSERT INTO `bed` VALUES (3, -1, 0, NULL, -1, NULL, NULL);
INSERT INTO `bed` VALUES (4, -1, 0, NULL, -1, NULL, NULL);
INSERT INTO `bed` VALUES (5, -1, 0, NULL, -1, NULL, NULL);
INSERT INTO `bed` VALUES (6, -1, 0, NULL, -1, NULL, NULL);
INSERT INTO `bed` VALUES (7, -1, 0, NULL, -1, NULL, NULL);
INSERT INTO `bed` VALUES (8, -1, 0, NULL, -1, NULL, NULL);
INSERT INTO `bed` VALUES (9, -1, 0, NULL, -1, NULL, NULL);
INSERT INTO `bed` VALUES (10, -1, 0, NULL, -1, NULL, NULL);

-- ----------------------------
-- Table structure for checks
-- ----------------------------
DROP TABLE IF EXISTS `checks`;
CREATE TABLE `checks`  (
  `ch_id` INT NOT NULL AUTO_INCREMENT,
  `ch_name` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `ch_price` DECIMAL(10, 2) NULL DEFAULT NULL,
  PRIMARY KEY (`ch_id`) USING BTREE
) ENGINE = INNODB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of checks
-- ----------------------------
INSERT INTO `checks` VALUES (1, '牙齿X光', 201.00);
INSERT INTO `checks` VALUES (2, '口腔清洁', 435.00);
INSERT INTO `checks` VALUES (3, '牙齿评估', 653.00);
INSERT INTO `checks` VALUES (4, '咬合关系检查', 534.00);
INSERT INTO `checks` VALUES (5, '口腔全面检查', 634.00);
INSERT INTO `checks` VALUES (6, '口腔卫生指导', 213.00);
INSERT INTO `checks` VALUES (7, '口腔病理检查', 434.00);

-- ----------------------------
-- Table structure for doctor_user
-- ----------------------------
DROP TABLE IF EXISTS `doctor_user`;
CREATE TABLE `doctor_user`  (
  `d_id` INT NOT NULL,
  `d_password` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `d_name` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `d_gender` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `d_phone` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `d_card` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `d_email` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `d_post` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `d_introduction` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `d_section` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `d_state` INT NOT NULL,
  `d_price` DECIMAL(10, 2) NULL DEFAULT NULL,
  `d_people` INT NULL DEFAULT NULL,
  `d_star` DECIMAL(10, 2) NULL DEFAULT NULL,
  `d_avg_star` DECIMAL(10, 2) NULL DEFAULT NULL,
  PRIMARY KEY (`d_id`) USING BTREE
) ENGINE = INNODB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of doctor_user
-- ----------------------------
INSERT INTO `doctor_user` VALUES (1000, 'e10adc3949ba59abbe56e057f20f883e', '张医生', '女', '18762543671', '352225177380837645', 'zhang@qq.com', '主任医师', '内科-主任医师', '简易门诊（慢病）', 1, 10.00, 1, 4.00, 4.00);
INSERT INTO `doctor_user` VALUES (1001, 'e10adc3949ba59abbe56e057f20f883e', '王医生', '男', '19872635542', '348882988376153789', 'wang@qq.com', '副主任医师', '内科-副主任医师', '精神科门诊', 0, 5.00, 0, 0.00, NULL);
INSERT INTO `doctor_user` VALUES (1002, 'e10adc3949ba59abbe56e057f20f883e', '李医生', '男', '18627362563', '352224827736281', 'li@gmail.com', '主治医生', '内科-主治医生', '呼吸（心血管、神经）内科门诊', 1, 20.00, 0, 0.00, NULL);
INSERT INTO `doctor_user` VALUES (1003, 'e10adc3949ba59abbe56e057f20f883e', '赵医生', '男', '18792374621', '348882733628236', 'zhao@163.com', '主任医师', '内科-主任医师', '消化内科门诊', 1, 10.00, 0, 0.00, NULL);
INSERT INTO `doctor_user` VALUES (1004, 'e10adc3949ba59abbe56e057f20f883e', '马医生', '男', '18562382321', '342223947192347', 'ma@qq.com', '主治医生', '外科-主治医生', '肝胆、肠胃外科', 1, 50.00, 0, 0.00, NULL);
INSERT INTO `doctor_user` VALUES (1005, 'e10adc3949ba59abbe56e057f20f883e', '卞医生', '男', '18676665544', '352229833391837463', 'bian@qq.com', '主任医师', '外科-主任医师', '肛肠科', 1, 100.00, 0, 0.00, NULL);
INSERT INTO `doctor_user` VALUES (1006, 'e10adc3949ba59abbe56e057f20f883e', '廖医生', '女', '18766623523', '352229378782331', 'liao@qq.com', '副主任医师', '外科-副主任医师', '甲状腺、乳腺外科', 1, 10.00, 0, 0.00, NULL);
INSERT INTO `doctor_user` VALUES (1007, 'e10adc3949ba59abbe56e057f20f883e', '乌医生', '男', '18723626312', '382227392312132', 'wu@qq.com', '主任医师', '外科-主任医师', '胸部外科', 1, 10.00, 0, 0.00, NULL);
INSERT INTO `doctor_user` VALUES (1008, 'e10adc3949ba59abbe56e057f20f883e', '田医生', '女', '19876763231', '348887233210237', 'tian@qq.com', '副主任医师', '外科-副主任医师', '骨科门诊', 1, 2.00, 0, 0.00, NULL);
INSERT INTO `doctor_user` VALUES (1009, 'e10adc3949ba59abbe56e057f20f883e', '苗医生', '女', '18723776462', '387772372313123', 'miao@163.com', '主治医生', '外科-主治医生', '手足创伤外科门诊', 1, 30.00, 0, 0.00, NULL);

-- ----------------------------
-- Table structure for drug
-- ----------------------------
DROP TABLE IF EXISTS `drug`;
CREATE TABLE `drug`  (
  `dr_id` INT NOT NULL AUTO_INCREMENT,
  `dr_name` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `dr_price` DECIMAL(10, 2) NULL DEFAULT NULL,
  `dr_number` INT NULL DEFAULT NULL,
  `dr_publisher` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `dr_unit` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`dr_id`) USING BTREE
) ENGINE = INNODB AUTO_INCREMENT = 101 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of drug
-- ----------------------------
INSERT INTO `drug` VALUES (1, '青霉素', 23.00, 11, '国家医药局', '袋');
INSERT INTO `drug` VALUES (2, '苯唑西林', 11.00, 28, '国家医药局', '盒');
INSERT INTO `drug` VALUES (3, '氨苄西林', 13.00, 51, '国家医药局', '盒');
INSERT INTO `drug` VALUES (4, '哌拉西林', 2.00, 7, '国家医药局', '盒');
INSERT INTO `drug` VALUES (5, '阿莫西林', 13.00, 20, '国家医药局', '盒');
INSERT INTO `drug` VALUES (6, '头孢唑林', 3.00, 32, '国家医药局', '盒');
INSERT INTO `drug` VALUES (7, '头孢氨苄', 4.00, 43, '国家医药局', '盒');
INSERT INTO `drug` VALUES (8, '头孢呋辛', 8.00, 54, '国家医药局', '盒');
INSERT INTO `drug` VALUES (9, '阿米卡星', 5.00, 54, '国家医药局', '袋');
INSERT INTO `drug` VALUES (10, '庆大霉素', 7.00, 63, '国家医药局', '袋');
INSERT INTO `drug` VALUES (11, '红霉素', 6.00, 75, '国家医药局', '袋');
INSERT INTO `drug` VALUES (12, '阿奇霉素', 54.00, 52, '国家医药局', '袋');
INSERT INTO `drug` VALUES (13, '克林霉素', 65.00, 21, '国家医药局', '袋');
INSERT INTO `drug` VALUES (14, '复方磺胺甲噁唑', 76.00, 54, '国家医药局', '袋');
INSERT INTO `drug` VALUES (15, '诺氟沙星', 65.00, 33, '国家医药局', '袋');
INSERT INTO `drug` VALUES (16, '左氧氟沙星', 76.00, 43, '国家医药局', '袋');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `o_id` INT NOT NULL AUTO_INCREMENT,
  `p_id` INT NULL DEFAULT NULL,
  `d_id` INT NULL DEFAULT NULL,
  `o_record` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `o_start` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `o_end` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `o_state` INT NULL DEFAULT NULL,
  `o_drug` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `o_check` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `o_total_price` DECIMAL(10, 2) NULL DEFAULT NULL,
  `o_price_state` INT NULL DEFAULT NULL,
  `o_advice` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`o_id`) USING BTREE,
  INDEX `oTOp`(`p_id` ASC) USING BTREE,
  INDEX `0TOd`(`d_id` ASC) USING BTREE,
  CONSTRAINT `0TOd` FOREIGN KEY (`d_id`) REFERENCES `doctor_user` (`d_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `oTOp` FOREIGN KEY (`p_id`) REFERENCES `patient_user` (`p_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = INNODB AUTO_INCREMENT = 211209 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (2512, 2000, 1000, NULL, '2023-11-20 09:30-10:30', NULL, 0, NULL, NULL, NULL, 0, NULL);
INSERT INTO `orders` VALUES (5843, 2000, 1008, NULL, '2023-11-20 10:30-11:30', NULL, 0, NULL, NULL, NULL, 0, NULL);
INSERT INTO `orders` VALUES (6051, 2000, 1000, NULL, '2023-11-19 09:30-10:30', NULL, 0, NULL, NULL, 0.00, 1, NULL);

-- ----------------------------
-- Table structure for patient_user
-- ----------------------------
DROP TABLE IF EXISTS `patient_user`;
CREATE TABLE `patient_user`  (
  `p_id` INT NOT NULL,
  `p_password` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `p_name` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `p_gender` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `p_phone` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `p_card` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `p_email` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `p_state` INT NULL DEFAULT NULL,
  `p_birthday` VARCHAR(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `p_age` INT NULL DEFAULT NULL,
  PRIMARY KEY (`p_id`) USING BTREE
) ENGINE = INNODB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of patient_user
-- ----------------------------
INSERT INTO `patient_user` VALUES (2000, 'e10adc3949ba59abbe56e057f20f883e', '徐先生', '男', '18733223345', '352227655454361762', 'xu@qq.com', 1, '1994-11-27', 29);
INSERT INTO `patient_user` VALUES (2001, 'e10adc3949ba59abbe56e057f20f883e', '曾先生', '男', '18766552374', '356662733625364', 'zeng@qq.com', 1, '2000-11-03', 23);
INSERT INTO `patient_user` VALUES (2002, 'e10adc3949ba59abbe56e057f20f883e', '张女士', '女', '18677662374', '387772633819283', 'zhang@163.com', 1, '1997-11-05', 26);
INSERT INTO `patient_user` VALUES (2003, 'e10adc3949ba59abbe56e057f20f883e', '杨先生', '男', '18766256323', '353329877638231', 'yang@qq.com', 1, '2023-11-26', 0);
INSERT INTO `patient_user` VALUES (2004, 'e10adc3949ba59abbe56e057f20f883e', '马女士', '女', '18766235473', '376662537482735', 'ma@qq.com', 1, '2017-11-06', 6);

SET FOREIGN_KEY_CHECKS = 1;
