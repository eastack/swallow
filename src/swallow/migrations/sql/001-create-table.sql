create table users (
  id bigint unsigned auto_increment primary key comment '用户ID',
  username varchar(64) not null comment '用户名',
  password varchar(256) not null comment '密码'
) comment '用户表';
