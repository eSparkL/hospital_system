/*
SQLyog Community v13.3.1 (64 bit)
MySQL - 8.0.17 : Database - db_hos
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`db_hos` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `db_hos`;

/*Table structure for table `admin_user` */

DROP TABLE IF EXISTS `admin_user`;

CREATE TABLE `admin_user` (
  `a_id` int(11) NOT NULL COMMENT '主键，管理员id（账号）',
  `a_password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '密码',
  `a_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '姓名',
  `a_gender` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '性别',
  `a_card` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '证件号码',
  `a_phone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '手机号',
  `a_email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '邮箱',
  PRIMARY KEY (`a_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

/*Data for the table `admin_user` */

insert  into `admin_user`(`a_id`,`a_password`,`a_name`,`a_gender`,`a_card`,`a_phone`,`a_email`) values 
(202301,'123456','admin','男','34000000000000','13541111111','123@qq.com');

/*Table structure for table `arrange` */

DROP TABLE IF EXISTS `arrange`;

CREATE TABLE `arrange` (
  `ar_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `ar_time` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `d_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`ar_id`) USING BTREE,
  KEY `arTOd` (`d_id`) USING BTREE,
  CONSTRAINT `arTOd` FOREIGN KEY (`d_id`) REFERENCES `doctor_user` (`d_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

/*Data for the table `arrange` */

insert  into `arrange`(`ar_id`,`ar_time`,`d_id`) values 
('10002023-11-19','2023-11-19',1000),
('10002023-11-20','2023-11-20',1000),
('10002025-12-11','2025-12-11',1000),
('10012023-11-19','2023-11-19',1001),
('10012023-11-20','2023-11-20',1001),
('10022023-11-19','2023-11-19',1002),
('10022023-11-20','2023-11-20',1002),
('10032023-11-19','2023-11-19',1003),
('10032025-12-11','2025-12-11',1003),
('10042023-11-19','2023-11-19',1004),
('10042023-11-20','2023-11-20',1004),
('10042023-11-21','2023-11-21',1004),
('10042023-11-23','2023-11-23',1004),
('10072023-11-19','2023-11-19',1007),
('10072023-11-20','2023-11-20',1007),
('10072023-11-23','2023-11-23',1007),
('10082023-11-19','2023-11-19',1008),
('10082023-11-20','2023-11-20',1008),
('10082025-12-11','2025-12-11',1008),
('10092023-11-19','2023-11-19',1009),
('10092023-11-20','2023-11-20',1009),
('10092025-12-11','2025-12-11',1009);

/*Table structure for table `bed` */

DROP TABLE IF EXISTS `bed`;

CREATE TABLE `bed` (
  `b_id` int(11) NOT NULL,
  `p_id` int(11) DEFAULT NULL,
  `b_state` int(11) DEFAULT NULL,
  `b_start` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `d_id` int(11) DEFAULT NULL,
  `b_reason` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`b_id`) USING BTREE,
  KEY `bTOp` (`p_id`) USING BTREE,
  KEY `bTOd` (`d_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

/*Data for the table `bed` */

insert  into `bed`(`b_id`,`p_id`,`b_state`,`b_start`,`d_id`,`b_reason`,`version`) values 
(1,2000,1,'2023-11-19',1000,'病人状况比较糟糕，需要住院3天。照顾下。',NULL),
(2,-1,0,NULL,-1,NULL,NULL),
(3,-1,0,NULL,-1,NULL,NULL),
(4,-1,0,NULL,-1,NULL,NULL),
(5,-1,0,NULL,-1,NULL,NULL),
(6,-1,0,NULL,-1,NULL,NULL),
(7,-1,0,NULL,-1,NULL,NULL),
(8,-1,0,NULL,-1,NULL,NULL),
(9,-1,0,NULL,-1,NULL,NULL),
(10,-1,0,NULL,-1,NULL,NULL);

/*Table structure for table `checks` */

DROP TABLE IF EXISTS `checks`;

CREATE TABLE `checks` (
  `ch_id` int(11) NOT NULL AUTO_INCREMENT,
  `ch_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `ch_price` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`ch_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

/*Data for the table `checks` */

insert  into `checks`(`ch_id`,`ch_name`,`ch_price`) values 
(1,'体液检查',201.00),
(2,'血液检查',135.00),
(3,'CT',453.00),
(4,'B超',334.00),
(5,'肠镜',434.00),
(6,'胃镜',213.00),
(7,'心电图',434.00);

/*Table structure for table `doctor_user` */

DROP TABLE IF EXISTS `doctor_user`;

CREATE TABLE `doctor_user` (
  `d_id` int(11) NOT NULL,
  `d_password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `d_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `d_gender` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `d_phone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `d_card` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `d_email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `d_post` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `d_introduction` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `d_section` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `d_state` int(11) NOT NULL,
  `d_price` decimal(10,2) DEFAULT NULL,
  `d_people` int(11) DEFAULT NULL,
  `d_star` decimal(10,2) DEFAULT NULL,
  `d_avg_star` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`d_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

/*Data for the table `doctor_user` */

insert  into `doctor_user`(`d_id`,`d_password`,`d_name`,`d_gender`,`d_phone`,`d_card`,`d_email`,`d_post`,`d_introduction`,`d_section`,`d_state`,`d_price`,`d_people`,`d_star`,`d_avg_star`) values 
(1000,'e10adc3949ba59abbe56e057f20f883e','张医生','女','18762543671','352225177380837645','zhang@qq.com','主任医师','内科-主任医师','简易门诊（慢病）',1,10.00,2,9.00,4.50),
(1001,'e10adc3949ba59abbe56e057f20f883e','王医生','男','19872635542','348882988376153789','wang@qq.com','副主任医师','内科-副主任医师','精神科门诊',0,5.00,0,0.00,NULL),
(1002,'e10adc3949ba59abbe56e057f20f883e','李医生','男','18627362563','352224827736281','li@gmail.com','主治医生','内科-主治医生','呼吸（心血管、神经）内科门诊',1,20.00,0,0.00,NULL),
(1003,'e10adc3949ba59abbe56e057f20f883e','赵医生','男','18792374621','348882733628236','zhao@163.com','主任医师','内科-主任医师','消化内科门诊',1,10.00,0,0.00,NULL),
(1004,'e10adc3949ba59abbe56e057f20f883e','马医生','男','18562382321','342223947192347','ma@qq.com','主治医生','外科-主治医生','肝胆、肠胃外科',1,50.00,0,0.00,NULL),
(1005,'e10adc3949ba59abbe56e057f20f883e','卞医生','男','18676665544','352229833391837463','bian@qq.com','主任医师','外科-主任医师','肛肠科',1,100.00,0,0.00,NULL),
(1006,'e10adc3949ba59abbe56e057f20f883e','廖医生','女','18766623523','352229378782331','liao@qq.com','副主任医师','外科-副主任医师','甲状腺、乳腺外科',1,10.00,0,0.00,NULL),
(1007,'e10adc3949ba59abbe56e057f20f883e','乌医生','男','18723626312','382227392312132','wu@qq.com','主任医师','外科-主任医师','胸部外科',1,10.00,0,0.00,NULL),
(1008,'e10adc3949ba59abbe56e057f20f883e','田医生','女','19876763231','348887233210237','tian@qq.com','副主任医师','外科-副主任医师','骨科门诊',1,2.00,0,0.00,NULL),
(1009,'e10adc3949ba59abbe56e057f20f883e','苗医生','女','18723776462','387772372313123','miao@163.com','主治医生','外科-主治医生','手足创伤外科门诊',1,30.00,0,0.00,NULL);

/*Table structure for table `drug` */

DROP TABLE IF EXISTS `drug`;

CREATE TABLE `drug` (
  `dr_id` int(11) NOT NULL AUTO_INCREMENT,
  `dr_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `dr_price` decimal(10,2) DEFAULT NULL,
  `dr_number` int(11) DEFAULT NULL,
  `dr_publisher` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `dr_unit` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`dr_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

/*Data for the table `drug` */

insert  into `drug`(`dr_id`,`dr_name`,`dr_price`,`dr_number`,`dr_publisher`,`dr_unit`) values 
(1,'青霉素',23.00,11,'国家医药局','袋'),
(2,'苯唑西林',11.00,28,'国家医药局','盒'),
(3,'氨苄西林',13.00,51,'国家医药局','盒'),
(4,'哌拉西林',2.00,7,'国家医药局','盒'),
(5,'阿莫西林',13.00,20,'国家医药局','盒'),
(6,'头孢唑林',3.00,32,'国家医药局','盒'),
(7,'头孢氨苄',4.00,43,'国家医药局','盒'),
(8,'头孢呋辛',8.00,54,'国家医药局','盒'),
(9,'阿米卡星',5.00,54,'国家医药局','袋'),
(10,'庆大霉素',7.00,63,'国家医药局','袋'),
(11,'红霉素',6.00,75,'国家医药局','袋'),
(12,'阿奇霉素',54.00,52,'国家医药局','袋'),
(13,'克林霉素',65.00,21,'国家医药局','袋'),
(14,'复方磺胺甲噁唑',76.00,54,'国家医药局','袋'),
(15,'诺氟沙星',65.00,33,'国家医药局','袋'),
(16,'左氧氟沙星',76.00,43,'国家医药局','袋');

/*Table structure for table `news` */

DROP TABLE IF EXISTS `news`;

CREATE TABLE `news` (
  `news_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '资讯ID',
  `title` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '内容',
  `cover_image` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '封面图片',
  `news_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT 'general' COMMENT '类型：医院动态、健康科普、通知公告等',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-下架，1-已发布，2-草稿',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `author_id` int(11) DEFAULT NULL COMMENT '发布人ID（管理员ID）',
  `view_count` int(11) DEFAULT '0' COMMENT '浏览量',
  `is_top` tinyint(4) DEFAULT '0' COMMENT '是否置顶：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`news_id`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_type` (`news_type`) USING BTREE,
  KEY `idx_top` (`is_top`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='医院资讯表';

/*Data for the table `news` */

insert  into `news`(`news_id`,`title`,`content`,`cover_image`,`news_type`,`status`,`publish_time`,`author_id`,`view_count`,`is_top`,`create_time`,`update_time`) values 
(1,'我院荣获\"全国优秀口腔医院\"称号','<p>在近日举行的全国口腔医疗质量评比中，我院凭借卓越的医疗服务和专业的医疗团队，荣获\"全国优秀口腔医院\"称号。</p><p>这是对我院多年来坚持\"以患者为中心\"服务理念的充分肯定。</p>','/images/news1.jpg','医院动态',1,'2025-12-11 10:48:16',202301,2,1,'2025-12-11 10:48:16','2025-12-11 11:38:18'),
(2,'口腔健康科普：如何正确刷牙','<p>正确的刷牙方法可以有效预防口腔疾病：</p><ol><li>每天至少刷牙两次</li><li>每次刷牙不少于2分钟</li><li>使用软毛牙刷</li><li>定期更换牙刷</li></ol>','/images/news2.jpg','健康科普',1,'2025-12-11 10:48:16',202301,0,0,'2025-12-11 10:48:16','2025-12-11 10:48:16'),
(3,'冬季口腔护理注意事项','<p>冬季天气干燥，容易引发口腔问题：</p><p>1. 多喝水保持口腔湿润</p><p>2. 避免食用过热过冷的食物</p><p>3. 注意室内加湿</p>','/images/news3.jpg','健康科普',1,'2025-12-11 10:48:16',202301,0,1,'2025-12-11 10:48:16','2025-12-11 10:48:16'),
(4,'元旦期间门诊安排通知','<p>元旦期间（1月1日-1月3日）门诊安排如下：</p><p>急诊：24小时开放</p><p>普通门诊：上午8:00-12:00</p><p>祝大家元旦快乐！</p>',NULL,'通知公告',1,'2025-12-11 10:48:16',202301,0,0,'2025-12-11 10:48:16','2025-12-11 10:48:16');

/*Table structure for table `orders` */

DROP TABLE IF EXISTS `orders`;

CREATE TABLE `orders` (
  `o_id` int(11) NOT NULL AUTO_INCREMENT,
  `p_id` int(11) DEFAULT NULL,
  `d_id` int(11) DEFAULT NULL,
  `o_record` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `o_start` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `o_end` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `o_state` int(11) DEFAULT NULL,
  `o_drug` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `o_check` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `o_total_price` decimal(10,2) DEFAULT NULL,
  `o_price_state` int(11) DEFAULT NULL,
  `o_advice` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`o_id`) USING BTREE,
  KEY `oTOp` (`p_id`) USING BTREE,
  KEY `0TOd` (`d_id`) USING BTREE,
  CONSTRAINT `0TOd` FOREIGN KEY (`d_id`) REFERENCES `doctor_user` (`d_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `oTOp` FOREIGN KEY (`p_id`) REFERENCES `patient_user` (`p_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=211209 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

/*Data for the table `orders` */

insert  into `orders`(`o_id`,`p_id`,`d_id`,`o_record`,`o_start`,`o_end`,`o_state`,`o_drug`,`o_check`,`o_total_price`,`o_price_state`,`o_advice`) values 
(2373,2001,1000,NULL,'2025-12-11 10:30-11:30','2025-12-11 11:24:06',1,NULL,NULL,0.00,1,'感冒'),
(2512,2000,1000,NULL,'2023-11-20 09:30-10:30',NULL,0,NULL,NULL,0.00,1,NULL),
(2975,2001,1003,'肠胃炎','2025-12-11 14:30-15:30','2025-12-11 11:39:58',1,'苯唑西林,1盒,11.00元','',11.00,0,NULL),
(3394,2000,1003,'肠胃炎','2025-12-11 10:30-11:30','2025-12-11 11:45:20',1,'苯唑西林,1盒,11.00元','',11.00,0,NULL),
(5843,2000,1008,NULL,'2023-11-20 10:30-11:30',NULL,0,NULL,NULL,0.00,1,NULL),
(6051,2000,1000,NULL,'2023-11-19 09:30-10:30',NULL,0,NULL,NULL,0.00,1,NULL),
(8215,2004,1008,'骨折','2025-12-11 14:30-15:30','2025-12-11 11:48:21',1,'','CT,1次,453元',453.00,0,NULL),
(9157,2001,1000,'病毒性流感','2025-12-11 14:30-15:30','2025-12-11 11:33:57',1,'青霉素,1盒,23.00元','血液检查,1次,135元',0.00,1,NULL);

/*Table structure for table `patient_user` */

DROP TABLE IF EXISTS `patient_user`;

CREATE TABLE `patient_user` (
  `p_id` int(11) NOT NULL,
  `p_password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `p_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `p_gender` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `p_phone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `p_card` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `p_email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `p_state` int(11) DEFAULT NULL,
  `p_birthday` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `p_age` int(11) DEFAULT NULL,
  PRIMARY KEY (`p_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

/*Data for the table `patient_user` */

insert  into `patient_user`(`p_id`,`p_password`,`p_name`,`p_gender`,`p_phone`,`p_card`,`p_email`,`p_state`,`p_birthday`,`p_age`) values 
(2000,'e10adc3949ba59abbe56e057f20f883e','徐先生','男','18733223345','352227655454361762','xu@qq.com',1,'1994-11-27',29),
(2001,'e10adc3949ba59abbe56e057f20f883e','曾先生','男','18766552374','356662733625364','zeng@qq.com',1,'2000-11-03',23),
(2002,'e10adc3949ba59abbe56e057f20f883e','张女士','女','18677662374','387772633819283','zhang@163.com',1,'1997-11-05',26),
(2003,'e10adc3949ba59abbe56e057f20f883e','杨先生','男','18766256323','353329877638231','yang@qq.com',1,'2023-11-26',0),
(2004,'e10adc3949ba59abbe56e057f20f883e','马女士','女','18766235473','376662537482735','ma@qq.com',1,'2017-11-06',6);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
