CREATE SCHEMA `house` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin ;

CREATE TABLE `users` (
    `id` bigint(8) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` varchar(16) NOT NULL,
    `username` varchar(16) NOT NULL,
    `password` varchar(128) NOT NULL,
    `phone` varchar(11) DEFAULT NULL,
    `role` varchar(16) DEFAULT 'Customer' COMMENT '角色（Customer 客户/ Agent 经纪人 / Reviewer 审核员 / Admin 管理员）',
    `is_default` tinyint(1) DEFAULT 0 COMMENT '是否系统预置（0 否 / 1 是）',
    `creator` varchar(16) NOT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `updater` varchar(16) DEFAULT NULL,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `user_id_un` (`user_id`),
    UNIQUE KEY `username_un` (`username`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户表';
-- 预置系统管理员admin
INSERT INTO users (`user_id`, `username`, `password`, `role`, `is_default`, `creator`)
values ('e8dE0f3G8C', 'admin', '$2a$10$tGqiyhCnsPDZWQAPbPJQxuuGvBDAsgzVJ7Cb0OZcRQrkyucDy9D0q', 'Admin', 1, 'SYS_PRE');

CREATE TABLE `houses` (
    `id` bigint(8) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` varchar(16) NOT NULL,
    `house_id` varchar(16) NOT NULL,
    `title` varchar(32) DEFAULT NULL COMMENT '标题',
    `layout` ENUM('ONE', 'TWO', 'THREE', 'FOUR') NOT NULL COMMENT '户型（ONE one-bedroom / 2 two-bedroom / 3 three-bedroom / four-bedroom）',
    `area` DECIMAL(10, 2) NOT NULL COMMENT '房屋面积',
    `floor` tinyint(1) DEFAULT NULL COMMENT '层高',
    `city` varchar(32) NOT NULL COMMENT '城市',
    `community` varchar(64) NOT NULL COMMENT '小区',
    `address` varchar(128) NOT NULL COMMENT '详细地址',
    `expect_price` DECIMAL(10, 2) NOT NULL COMMENT '期望售价（万元）',
    `name` varchar(11) NOT NULL COMMENT '联系人',
    `phone` varchar(11) NOT NULL COMMENT '联系电话',
    `intention` ENUM('RENT', 'SALE') NOT NULL COMMENT '意愿类型（出租、出售）',
    `status` ENUM('PENDING', 'AVAILABLE', 'RENTED', 'SOLD', 'UNAVAILABLE') DEFAULT 'PENDING',
    `agent` varchar(16) DEFAULT NULL COMMENT '经纪人',
    `creator` varchar(16) NOT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `updater` varchar(16) DEFAULT NULL,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `house_id_un` (`house_id`),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='房源表';

CREATE TABLE house_images (
    `id` bigint(8) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `house_id` varchar(16) NOT NULL,
    `image_url` varchar(255) NOT NULL,
    `is_cover` tinyint(1) DEFAULT 0 COMMENT '是否封面（0 非封面 / 1 封面）',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (house_id) REFERENCES houses(house_id)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='房源图片表';

CREATE TABLE house_filter (
    `id` bigint(8) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` varchar(16) NOT NULL COMMENT '过滤项名称',
    `code` varchar(16) NOT NULL COMMENT '过滤项代码',
    `config` varchar(256) NOT NULL COMMENT '过滤项配置',
    `suffix` varchar(16) NOT NULL COMMENT '过滤项单位',
    `status` tinyint(1) DEFAULT 1 COMMENT '状态（0 禁用 / 1 启用）',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `name_un` (`name`),
    UNIQUE KEY `code_un` (`code`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='房源过滤配置表';
# 预置过滤项
insert into house_filter (name, code, config, suffix) values
('单价', 'unitPrice', '[{"max":5000},{"min":5000,"max":8000},{"min":8000,"max":100000},{"min":10000}]', '元/㎡'),
('总价', 'totalPrice', '[{"max":40},{"min":40,"max":60},{"min":60,"max":80},{"min":80}]', '元'),
('面积', 'area', '[{"max":50},{"min":50,"max":70},{"min":70,"max":90},{"min":90}]', '㎡'),
('户型', 'layout',
 '[{"eq":1},{"eq":2},{"eq":3},{"eq":4},{"min":5}]', '居');

CREATE TABLE appointments (
    id bigint(8) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id varchar(16) NOT NULL,
    house_id varchar(16) NOT NULL,
    appointment_time datetime NOT NULL COMMENT '约定时间',
    status ENUM('pending', 'confirmed', 'cancelled') DEFAULT 'pending' COMMENT '预约状态',
    create_time datetime DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (house_id) REFERENCES houses(house_id)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='预约表';

CREATE TABLE comments (
    id bigint(8) AUTO_INCREMENT PRIMARY KEY,
    parent_id bigint(8) DEFAULT NULL,
    house_id varchar(16) NOT NULL,
    user_id varchar(16) NOT NULL,
    content TEXT NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (house_id) REFERENCES houses(house_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='评论表';

# CREATE TABLE replies (
#     reply_id int AUTO_INCREMENT PRIMARY KEY,
#     comment_id int NOT NULL,
#     user_id varchar(16) NOT NULL,
#     content TEXT NOT NULL,
#     created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
#     updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
#     FOREIGN KEY (comment_id) REFERENCES comments(id),
#     FOREIGN KEY (user_id) REFERENCES users(user_id)
# ) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='回复评论表';

CREATE TABLE likes (
   id bigint(8) AUTO_INCREMENT PRIMARY KEY,
   house_id varchar(16) NOT NULL,
   user_id varchar(16) NOT NULL,
   create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   FOREIGN KEY (house_id) REFERENCES houses(house_id),
   FOREIGN KEY (user_id) REFERENCES users(user_id),
   UNIQUE (house_id, user_id) -- 确保一个用户只能对一个房屋关注一次
)ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='关注表';