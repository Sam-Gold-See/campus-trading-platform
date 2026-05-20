DROP SCHEMA IF EXISTS `campus_trading_platform`;
CREATE SCHEMA `campus_trading_platform`;
USE
`campus_trading_platform`;

-- ----------------------------
-- 1. Table structure for user (用户表)
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识',
    `edu_email`     VARCHAR(100) NOT NULL COMMENT '教育邮箱（实名认证凭证）',
    `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希值',
    `nickname`      VARCHAR(50)  NOT NULL COMMENT '昵称',
    `avatar_url`    VARCHAR(255) DEFAULT NULL COMMENT '头像（采用系统默认或淡化处理的URL）',
    `credit_score`  INT          DEFAULT 100 COMMENT '信用分（初始100）',
    `user_status`   TINYINT      DEFAULT 1 COMMENT '账号状态 (1正常, 0禁言拦截发布)',
    `created_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_email` (`edu_email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户核心身份信息与信用分';
-- ----------------------------
-- 2. Table structure for category (分类字典表)
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`
(
    `id`         INT         NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name`       VARCHAR(50) NOT NULL COMMENT '分类名称',
    `sort_order` INT DEFAULT 0 COMMENT '排序权重',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物品或需求的结构化分类';
-- ----------------------------
-- 3. Table structure for item (需求/物品表 - 核心交易池)
-- ----------------------------
DROP TABLE IF EXISTS `item`;
CREATE TABLE `item`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '帖文ID',
    `user_id`         BIGINT       NOT NULL COMMENT '发布者ID',
    `type`            TINYINT      NOT NULL COMMENT '交易类型 (1求购, 2转让)',
    `category_id`     INT          NOT NULL COMMENT '分类ID',
    `campus`          VARCHAR(50)  NOT NULL COMMENT '校区/楼栋位置',
    `price`           DECIMAL(10, 2) DEFAULT NULL COMMENT '期望价格（NULL代表面议）',
    `content`         VARCHAR(500) NOT NULL COMMENT '纯文本描述',
    `image_url`       VARCHAR(255)   DEFAULT NULL COMMENT '补充实物图（仅限1张缩略图）',
    `item_status`     TINYINT        DEFAULT 0 COMMENT '状态 (0展示中, 1已成交, 2已失效, 3已删除)',
    `matched_user_id` BIGINT         DEFAULT NULL COMMENT '成交方用户ID',
    `created_at`      DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '发布/擦亮时间',
    `expire_at`       DATETIME     NOT NULL COMMENT '过期时间',
    PRIMARY KEY (`id`),
    -- 组合筛选索引
    INDEX             `idx_search` (`item_status`, `type`, `category_id`, `campus`),
    -- 用于关联查询发布者的索引
    INDEX             `idx_user_id` (`user_id`),
    -- 文本搜索索引
    FULLTEXT          INDEX `ft_content` (`content`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='需求/物品表 - 核心交易池';
-- ----------------------------
-- 4. Table structure for message (站内信/私信表)
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '消息唯一ID',
    `sender_id`   BIGINT       NOT NULL COMMENT '发送方ID',
    `receiver_id` BIGINT       NOT NULL COMMENT '接收方ID',
    `item_id`     BIGINT       NOT NULL COMMENT '意向物品ID（上下文环境）',
    `content`     VARCHAR(500) NOT NULL COMMENT '聊天文本内容',
    `is_read`     TINYINT  DEFAULT 0 COMMENT '是否已读 (0未读, 1已读)',
    `created_at`  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '消息发送时间',
    PRIMARY KEY (`id`),
    -- 聊天双方索引
    INDEX         `idx_conversation` (`sender_id`, `receiver_id`),
    -- 接收方筛选（用于红点通知）
    INDEX         `idx_receiver_read` (`receiver_id`, `is_read`),
    -- 物品关联索引
    INDEX         `idx_item_id` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站内信/私信表';
-- ----------------------------
-- 5. Table structure for review (交易评价与信控表)
-- ----------------------------
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review`
(
    `id`          BIGINT  NOT NULL AUTO_INCREMENT COMMENT '评价流水ID',
    `item_id`     BIGINT  NOT NULL COMMENT '关联的交易物品ID',
    `reviewer_id` BIGINT  NOT NULL COMMENT '评价人ID',
    `reviewee_id` BIGINT  NOT NULL COMMENT '被评价人ID',
    `rating_type` TINYINT NOT NULL COMMENT '评价性质 (1好评加分, 0中评, -1差评扣分)',
    `tags`        VARCHAR(100) DEFAULT NULL COMMENT '结构化标签',
    `content`     VARCHAR(255) DEFAULT NULL COMMENT '文字短评',
    `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
    PRIMARY KEY (`id`),
    -- 计算用户信用分所需的索引
    INDEX         `idx_reviewee` (`reviewee_id`),
    -- 关联统计索引
    INDEX         `idx_reviewer` (`reviewer_id`),
    INDEX         `idx_item` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易评价与信控表';