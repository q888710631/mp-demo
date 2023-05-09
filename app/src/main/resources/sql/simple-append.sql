CREATE TABLE `article` (
`id` bigint NOT NULL AUTO_INCREMENT,
`title` varchar(255) DEFAULT NULL COMMENT '标题',
`cover` varchar(255) DEFAULT NULL COMMENT '封面，oss的文件相对路径',
`content` text COMMENT '内容',
`create_by` varchar(32) DEFAULT NULL,
`create_date` timestamp NULL DEFAULT NULL,
`update_by` varchar(32) DEFAULT NULL,
`update_date` timestamp NULL DEFAULT NULL,
PRIMARY KEY (`id`)
);