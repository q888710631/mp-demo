drop table if exists honyee.`article`;

CREATE TABLE honyee.`article` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `title` varchar(255) DEFAULT NULL COMMENT '标题',
    `cover` varchar(255) DEFAULT NULL COMMENT '封面，oss的文件相对路径',
    `content` text COMMENT '内容',
    `create_by` varchar(32) DEFAULT NULL,
    `create_date` datetime NULL DEFAULT NULL,
    `update_by` varchar(32) DEFAULT NULL,
    `update_date` datetime NULL DEFAULT NULL,
    PRIMARY KEY (`id`)
);


drop table if exists honyee.`inpatient_record`;

CREATE TABLE honyee.`inpatient_record` (
	`id` BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	`enter_date` date DEFAULT NULL COMMENT '入院日期',
	`out_date` datetime DEFAULT NULL COMMENT '出院日期',
	`enter_reason` VARCHAR ( 255 ) DEFAULT NULL COMMENT '入院原因',
	`bed_number` VARCHAR ( 32 ) DEFAULT NULL COMMENT '床号',
	`patient_name` VARCHAR ( 32 ) DEFAULT NULL COMMENT '患者姓名',
	`contact_name` VARCHAR ( 32 ) DEFAULT NULL COMMENT '联系人姓名',
	`contact_relation` VARCHAR ( 32 ) DEFAULT NULL COMMENT '联系人关系',
	`contact_area` VARCHAR ( 32 ) DEFAULT NULL COMMENT '联系人所在地区',
	`contact_phone_1` VARCHAR ( 32 ) DEFAULT NULL COMMENT '联系人手机号1',
	`contact_phone_2` VARCHAR ( 32 ) DEFAULT NULL COMMENT '联系人手机号2',
	`state` TINYINT DEFAULT NULL COMMENT '状态',
	`create_by` VARCHAR ( 32 ) DEFAULT NULL,
	`create_date` datetime DEFAULT NULL,
	`update_by` VARCHAR ( 32 ) DEFAULT NULL,
	`update_date` datetime DEFAULT NULL
);