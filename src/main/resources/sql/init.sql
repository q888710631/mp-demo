drop table if exists honyee.`user`;

CREATE TABLE honyee.`user` (
   id bigint primary key auto_increment NOT null,
   nickname varchar(100),
   username varchar(100) unique,
   password varchar(255),
   create_by varchar(32),
   create_date timestamp,
   update_by varchar(32),
   update_date timestamp
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;

insert into honyee.`user` (nickname,username,password,create_by,create_date) values('管理员','admin', '$2a$10$9fbei2..mD4NOSZ2XyMOTeiwYK2nG6b6LS2Qt5esmOWozdg5AtiP6', 'init', now());

drop table if exists honyee.`role`;

CREATE TABLE honyee.`role` (
   id bigint primary key auto_increment NOT null,
   role_key varchar(100) unique ,
   role_name varchar(255),
   create_by varchar(32),
   create_date timestamp,
   update_by varchar(32),
   update_time timestamp
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;

insert into honyee.`role` (role_key,role_name,create_by,create_date) values('admin', '管理员', 'init', now());

drop table if exists honyee.`user_role`;

CREATE TABLE honyee.`user_role` (
        id bigint primary key auto_increment NOT null,
        user_id bigint,
        role_id bigint,
        create_by varchar(32),
        create_date timestamp,
        update_by varchar(32),
        update_time timestamp
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;

alter table honyee.`user_role` add unique `union_user_role`(user_id, role_id);

insert into honyee.`user_role` (user_id,role_id,create_by,create_date) values(1, 1, 'init', now());




