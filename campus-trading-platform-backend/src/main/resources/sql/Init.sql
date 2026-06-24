DROP SCHEMA IF EXISTS `campus_trading_platform`;
CREATE SCHEMA `campus_trading_platform`;
USE `campus_trading_platform`;

-- ----------------------------
-- 1. Table structure for user (用户表)
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`                    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识',
    `edu_email`             VARCHAR(100) NOT NULL COMMENT '教育邮箱（实名认证凭证）',
    `password_hash`         VARCHAR(255) NOT NULL COMMENT '密码哈希值',
    `nickname`              VARCHAR(50)  NOT NULL COMMENT '昵称',
    `avatar_url`            VARCHAR(255)   DEFAULT '' COMMENT '头像（采用系统默认或淡化处理的URL）',
    `credit_score`          INT            DEFAULT 100 COMMENT '信用分（初始100）',
    `user_status`           TINYINT        DEFAULT 0 COMMENT '账号状态 (0正常, 1禁言拦截发布)',
    `is_admin`              TINYINT        DEFAULT 0 COMMENT '是否管理员 (0普通用户，1管理员)',
    `last_nickname_change`  DATETIME       DEFAULT NULL COMMENT '上次昵称修改时间',
    `created_at`            DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `updated_at`            DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_email` (`edu_email`),
    UNIQUE KEY `uk_nickname` (`nickname`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户核心身份信息与信用分';

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
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='物品或需求的结构化分类';

-- ----------------------------
-- 3. Table structure for item (需求/物品表 - 核心交易池)
-- ----------------------------
DROP TABLE IF EXISTS `item`;
CREATE TABLE `item`
(
    `id`              BIGINT         NOT NULL AUTO_INCREMENT COMMENT '帖文ID',
    `user_id`         BIGINT         NOT NULL COMMENT '发布者ID',
    `type`            TINYINT        NOT NULL COMMENT '交易类型 (1求购, 2转让)',
    `category_id`     INT            NOT NULL COMMENT '分类ID',
    `campus`          VARCHAR(50)    NOT NULL COMMENT '校区/楼栋位置',
    `price`           DECIMAL(10, 2)   DEFAULT NULL COMMENT '期望价格（NULL代表面议）',
    `content`         VARCHAR(500)   NOT NULL COMMENT '纯文本描述',
    `image_url`       VARCHAR(255)     DEFAULT NULL COMMENT '补充实物图（仅限1张缩略图）',
    `item_status`     TINYINT          DEFAULT 0 COMMENT '状态 (0展示中, 1已成交, 2已失效, 3已删除)',
    `matched_user_id` BIGINT           DEFAULT NULL COMMENT '成交方用户ID',
    `created_at`      DATETIME         DEFAULT CURRENT_TIMESTAMP COMMENT '发布/擦亮时间',
    `expire_at`       DATETIME       NOT NULL COMMENT '过期时间',
    PRIMARY KEY (`id`),
    INDEX `idx_search` (`item_status`, `type`, `category_id`, `campus`),
    INDEX `idx_user_id` (`user_id`),
    FULLTEXT INDEX `ft_content` (`content`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='需求/物品表 - 核心交易池';

-- ----------------------------
-- 4. Table structure for review (交易评价与信控表)
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
    INDEX `idx_reviewee` (`reviewee_id`),
    INDEX `idx_reviewer` (`reviewer_id`),
    INDEX `idx_item` (`item_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='交易评价与信控表';

-- =====================================================
-- 模拟数据插入
-- =====================================================

-- 插入分类数据
INSERT INTO `category` (`name`, `sort_order`) VALUES
('书籍教材', 1),
('二手数码', 2),
('生活用品', 3),
('体育器材', 4),
('服装配饰', 5),
('电子产品', 6),
('学习工具', 7),
('其他', 99);

-- 插入模拟用户数据（密码都是 'password123' 的 BCrypt 哈希）
-- BCrypt hash for 'password123': $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi
INSERT INTO `user` (`edu_email`, `password_hash`, `nickname`, `credit_score`, `user_status`, `is_admin`) VALUES
('admin@scnu.edu.cn', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '管理员', 100, 0, 1),
('zhangsan@scnu.edu.cn', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张三', 95, 0, 0),
('lisi@scnu.edu.cn', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李四', 88, 0, 0),
('wangwu@scnu.edu.cn', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王五', 75, 0, 0),
('xiaoming@scnu.edu.cn', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '小明', 100, 0, 0),
('xiaohong@scnu.edu.cn', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '小红', 82, 0, 0),
('test001@scnu.edu.cn', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试用户1', 60, 0, 0),
('test002@scnu.edu.cn', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试用户2', 55, 0, 0);

-- 插入模拟物品/需求数据
INSERT INTO `item` (`user_id`, `type`, `category_id`, `campus`, `price`, `content`, `item_status`, `expire_at`) VALUES
-- 求购信息 (type=1)
(2, 1, 1, '南校区', 50.00, '求购一本高等数学第七版上册，最好是全新或者几乎没有使用的，可以面交', 0, DATE_ADD(NOW(), INTERVAL 14 DAY)),
(3, 1, 1, '北校区', 30.00, '需要大学英语四级词汇书，新版最好，价格合适即可', 0, DATE_ADD(NOW(), INTERVAL 14 DAY)),
(4, 1, 2, '南校区', 800.00, '求购一台二手笔记本电脑，配置要求i5以上，8G内存，主要用于学习和编程', 0, DATE_ADD(NOW(), INTERVAL 14 DAY)),
(5, 1, 3, '东校区', NULL, '求购台灯一个，亮度适中，适合宿舍使用，价格面议', 0, DATE_ADD(NOW(), INTERVAL 14 DAY)),
(6, 1, 4, '南校区', 150.00, '求购羽毛球拍一对，品牌不限，质量好就行', 0, DATE_ADD(NOW(), INTERVAL 14 DAY)),
(7, 1, 5, '北校区', 100.00, '求购一件男款运动外套，尺码175/92A左右，颜色不限', 0, DATE_ADD(NOW(), INTERVAL 14 DAY)),
(8, 1, 6, '南校区', 500.00, '求购二手平板电脑，主要用于看网课，安卓或苹果都可以', 0, DATE_ADD(NOW(), INTERVAL 14 DAY)),
(2, 1, 7, '东校区', 20.00, '求购科学计算器一个，卡西欧型号优先', 0, DATE_ADD(NOW(), INTERVAL 14 DAY)),
(3, 1, 1, '南校区', 40.00, '急求线性代数教材，考试复习用，最好带笔记', 0, DATE_ADD(NOW(), INTERVAL 14 DAY)),
(4, 1, 8, '北校区', NULL, '求购宿舍收纳箱若干，大小适中即可，价格面议', 0, DATE_ADD(NOW(), INTERVAL 14 DAY)),

-- 转让信息 (type=2)
(5, 2, 1, '南校区', 35.00, '转让高等数学下册教材，有少量笔记，适合备考使用', 0, DATE_ADD(NOW(), INTERVAL 14 DAY)),
(6, 2, 1, '北校区', 25.00, '转让大学物理教材上下册全套，几乎全新，价格优惠', 0, DATE_ADD(NOW(), INTERVAL 14 DAY)),
(7, 2, 2, '南校区', 1200.00, '转让iPad Pro 11寸一代，128G，屏幕完好，含保护壳和充电器', 0, DATE_ADD(NOW(), INTERVAL 14 DAY)),
(8, 2, 3, '东校区', 80.00, '转让宿舍小冰箱一台，使用一年，制冷效果良好', 0, DATE_ADD(NOW(), INTERVAL 14 DAY)),
(2, 2, 4, '南校区', 200.00, '转让篮球一个，斯伯丁正品，使用不多，质量很好', 0, DATE_ADD(NOW(), INTERVAL 14 DAY)),
(3, 2, 5, '北校区', 150.00, '转让女款连衣裙三条，尺码165/88A，品牌衣服质量好', 0, DATE_ADD(NOW(), INTERVAL 14 DAY)),
(4, 2, 6, '南校区', 3500.00, '转让MacBook Air 13寸，M1芯片，8+256G，电池健康度90%', 0, DATE_ADD(NOW(), INTERVAL 14 DAY)),
(5, 2, 7, '东校区', 15.00, '转让文具套装，含笔袋、各类笔、笔记本等', 0, DATE_ADD(NOW(), INTERVAL 14 DAY)),
(6, 2, 3, '南校区', 50.00, '转让宿舍衣架若干，不锈钢材质，结实耐用', 0, DATE_ADD(NOW(), INTERVAL 14 DAY)),
(7, 2, 1, '北校区', 45.00, '转让C语言程序设计教材，附带习题解答', 0, DATE_ADD(NOW(), INTERVAL 14 DAY)),

-- 已成交的物品 (item_status=1)
(2, 2, 1, '南校区', 30.00, '已转让数据结构教材', 1, DATE_ADD(NOW(), INTERVAL -7 DAY)),
(3, 1, 2, '北校区', 600.00, '已购入二手手机', 1, DATE_ADD(NOW(), INTERVAL -10 DAY)),

-- 已失效的物品 (item_status=2)
(4, 2, 3, '东校区', 100.00, '过期的台灯转让信息', 2, DATE_ADD(NOW(), INTERVAL -30 DAY)),
(5, 1, 4, '南校区', 80.00, '过期的运动鞋求购信息', 2, DATE_ADD(NOW(), INTERVAL -30 DAY));

-- 插入模拟评价数据
INSERT INTO `review` (`item_id`, `reviewer_id`, `reviewee_id`, `rating_type`, `tags`, `content`) VALUES
(21, 3, 2, 1, '爽快,准时,描述相符', '交易很顺利，对方很爽快，物品和描述一致'),
(22, 2, 3, 1, '准时,信誉好', '卖家很守时，价格合理'),
(21, 2, 3, 1, '态度好', '第二次交易，依然很满意'),
(22, 4, 3, 0, '', '一般，物品有点旧但能用'),
(23, 5, 4, 1, '爽快,描述相符', '电子产品交易很放心'),
(24, 6, 5, 1, '准时', '按时面交，不错'),
(25, 7, 6, -1, '描述不符', '物品有些瑕疵没有提前说明'),
(26, 8, 7, 1, '信誉好', '诚信交易');