/*
 Navicat Premium Data Transfer

 Source Server Type    : MySQL
 Source Server Version : 80022
 Source Schema         : test

 Target Server Type    : MySQL
 Target Server Version : 80022
 File Encoding         : 65001

 Date: 16/07/2021 00:38:05
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_test
-- ----------------------------
DROP TABLE IF EXISTS `t_test`;
CREATE TABLE `t_test` (
  `test_id` varchar(255) NOT NULL,
  `test_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`test_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_test
-- ----------------------------
BEGIN;
INSERT INTO `t_test` VALUES ('1', 'testname');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
