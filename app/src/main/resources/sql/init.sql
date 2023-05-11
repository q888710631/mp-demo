drop table if exists honyee.`user`;

CREATE TABLE honyee.`user` (
    id bigint primary key auto_increment NOT null,
    nickname varchar(100),
    username varchar(100) unique,
    password varchar(255),
    state int default 1,
    lock_begin_date datetime,
    lock_end_date datetime,
    create_by varchar(32),
    create_date datetime,
    update_by varchar(32),
    update_date datetime
);

insert into honyee.`user` (nickname,username,password,state,create_by,create_date) values('管理员','admin', '$2a$10$9fbei2..mD4NOSZ2XyMOTeiwYK2nG6b6LS2Qt5esmOWozdg5AtiP6',1, 'init', now());
insert into honyee.`user` (nickname,username,password,state,create_by,create_date) values('测试用户','client', '$2a$10$9fbei2..mD4NOSZ2XyMOTeiwYK2nG6b6LS2Qt5esmOWozdg5AtiP6',1, 'init', now());


drop table if exists honyee.`role`;

CREATE TABLE honyee.`role` (
                               id bigint primary key auto_increment NOT null,
                               role_key varchar(100) unique ,
                               role_name varchar(255),
                               create_by varchar(32),
                               create_date datetime,
                               update_by varchar(32),
                               update_time datetime
);

insert into honyee.`role` (role_key,role_name,create_by,create_date,update_by,update_time) values('admin', '管理员', 'init', now(), 'init', now());
insert into honyee.`role` (role_key,role_name,create_by,create_date,update_by,update_time) values('tenant', '租户', 'init', now(), 'init', now());
INSERT INTO honyee.`role` (role_key,role_name,create_by,create_date,update_by,update_time) VALUES ('client', '普通用户', 'init', now(), 'init', now());


drop table if exists honyee.`user_role`;

CREATE TABLE honyee.`user_role` (
    id bigint primary key auto_increment NOT null,
    user_id bigint,
    role_id bigint,
    create_by varchar(32),
    create_date datetime,
    update_by varchar(32),
    update_time datetime
);

alter table honyee.`user_role` add index `idx_user_id`(user_id);

alter table honyee.`user_role` add unique `union_user_role`(user_id, role_id);

insert into honyee.user_role (user_id, role_id,create_by,create_date)
    (
        select
            (select id from honyee.`user` where username = 'admin') as user_id,
            (select id from honyee.`role` where role_key = 'admin') as role_id,
            'init' as create_by,
            now() as create_date
        from dual
    );

# 测试用
CREATE TABLE honyee.`person` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `nickname` varchar(100) DEFAULT NULL,
    `role_list` varchar(1000) DEFAULT NULL,
    `role` varchar(1000) DEFAULT NULL,
    `create_by` varchar(100) DEFAULT NULL,
    `create_date` datetime DEFAULT NULL,
    `update_by` varchar(100) DEFAULT NULL,
    `update_date` datetime DEFAULT NULL,
    PRIMARY KEY (`id`)
);


