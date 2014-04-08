/*
 Navicat Premium Data Transfer

 Source Server         : 10.0.0.30(alpha)
 Source Server Type    : MySQL
 Source Server Version : 50161
 Source Host           : 10.0.0.30
 Source Database       : fileserver

 Target Server Type    : MySQL
 Target Server Version : 50161
 File Encoding         : utf-8

 Date: 04/08/2014 14:59:01 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `metadata`
-- ----------------------------
DROP TABLE IF EXISTS `metadata`;
CREATE TABLE `metadata` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT '0',
  `request_id` varchar(36) NOT NULL DEFAULT '0' COMMENT '请求id',
  `contenttype` varchar(2000) DEFAULT NULL COMMENT 'http header content-type',
  `contentmd5` varchar(32) DEFAULT NULL COMMENT 'http header contentMD5',
  `contentlength` int(11) DEFAULT '0' COMMENT 'http header content-length',
  `useragent` varchar(2000) DEFAULT NULL COMMENT 'http header user-agent',
  `accesskey` varchar(64) DEFAULT NULL,
  `filename` varchar(64) DEFAULT NULL COMMENT '文件名',
  `pic_width` int(8) DEFAULT NULL,
  `pic_height` int(8) DEFAULT NULL,
  `file_type` varchar(16) DEFAULT NULL,
  `ip` varchar(32) DEFAULT NULL COMMENT '用户上传文件的ip',
  `method` varchar(8) DEFAULT 'post' COMMENT 'post/put',
  `remark` varchar(2000) DEFAULT NULL,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间戳',
  `product` enum('user','share') DEFAULT NULL COMMENT '哪个产品使用',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1539 DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
