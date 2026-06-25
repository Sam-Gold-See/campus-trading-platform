### 1. `user` (用户表)

存储用户核心身份信息与信用分。

| 字段名             | 数据类型         | 约束/默认值                | 描述说明                |
|:----------------|:-------------|:----------------------|:--------------------|
| `id`            | BIGINT       | PRIMARY KEY, AUTO_INC | 用户唯一标识              |
| `edu_email`     | VARCHAR(100) | UNIQUE, NOT NULL      | 教育邮箱（实名认证凭证）        |
| `password_hash` | VARCHAR(255) | NOT NULL              | 密码哈希值               |
| `nickname`      | VARCHAR(50)  | NOT NULL              | 昵称                  |
| `avatar_url`    | VARCHAR(255) | NULL                  | 头像（采用系统默认或淡化处理的URL） |
| `credit_score`  | INT          | DEFAULT 100           | **信用分**（初始100）      |
| `user_status`   | TINYINT      | DEFAULT 0             | 账号状态 (0正常, 1禁言拦截发布) |
| `is_admin`      | TINYINT      | DEFAULT 0             | 是否管理员 (0普通用户, 1管理员) |
| `last_nickname_change` | DATETIME | NULL              | 上次昵称修改时间 |
| `created_at`    | DATETIME     | CURRENT_TIMESTAMP     | 注册时间                |
| `updated_at`    | DATETIME     | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

### 2. `category` (分类字典表)

用于维护物品或需求的结构化分类。

| 字段名          | 数据类型        | 约束/默认值                | 描述说明              |
|:-------------|:------------|:----------------------|:------------------|
| `id`         | INT         | PRIMARY KEY, AUTO_INC | 分类ID              |
| `name`       | VARCHAR(50) | NOT NULL              | 分类名称（如：书籍教材、二手数码） |
| `sort_order` | INT         | DEFAULT 0             | 排序权重（供前端展示排序用）    |

### 3. `item` (需求/物品表 - 核心交易池)

承载需求墙的全部文本流，由于不涉及在线支付，这里直接加入 `matched_user_id` 代替繁琐的订单表结构。

| 字段名               | 数据类型          | 约束/默认值                | 描述说明                        |
|:------------------|:--------------|:----------------------|:----------------------------|
| `id`              | BIGINT        | PRIMARY KEY, AUTO_INC | 帖文ID                        |
| `user_id`         | BIGINT        | NOT NULL              | 发布者ID                       |
| `type`            | TINYINT       | NOT NULL              | **交易类型 (1求购, 2转让)**         |
| `category_id`     | INT           | NOT NULL              | 分类ID                        |
| `campus`          | VARCHAR(50)   | NOT NULL              | 校区/楼栋位置（如：南校区）              |
| `price`           | DECIMAL(10,2) | NULL                  | 期望价格（NULL代表 面议）             |
| `content`         | VARCHAR(500)  | NOT NULL              | **纯文本描述**（限制字数内）            |
| `image_url`       | VARCHAR(255)  | NULL                  | 补充实物图（仅限1张缩略图URL）           |
| `item_status`     | TINYINT       | DEFAULT 0             | 状态 (0展示中, 1已成交, 2已失效, 3已删除) |
| `matched_user_id` | BIGINT        | NULL                  | **成交方用户ID**（状态转为1时必须填入该值）   |
| `created_at`      | DATETIME      | CURRENT_TIMESTAMP     | 发布时间（“擦亮”功能即更新此字段）          |
| `expire_at`       | DATETIME      | NOT NULL              | 过期时间（配合自动失效逻辑，通常+14天）       |

* **核心索引建议**：
    * 联合索引 `idx_search (item_status, type, category_id, campus)`：用于多维组合筛选的极速响应。
    * 全文索引 `FULLTEXT(content)`（MySQL 5.7+支持）：用于文本的模糊模糊搜索。

### 4. `message` (站内信/私信表)

支持 WebSocket 离线消息拉取与历史记录漫游。

| 字段名           | 数据类型         | 约束/默认值                | 描述说明                   |
|:--------------|:-------------|:----------------------|:-----------------------|
| `id`          | BIGINT       | PRIMARY KEY, AUTO_INC | 消息唯一ID                 |
| `sender_id`   | BIGINT       | NOT NULL              | 发送方ID                  |
| `receiver_id` | BIGINT       | NOT NULL              | 接收方ID                  |
| `item_id`     | BIGINT       | NOT NULL              | **意向物品ID**（带入的商品卡片上下文） |
| `content`     | VARCHAR(500) | NOT NULL              | 聊天文本内容                 |
| `is_read`     | TINYINT      | DEFAULT 0             | 是否已读 (0未读, 1已读)        |
| `created_at`  | DATETIME     | CURRENT_TIMESTAMP     | 消息发送时间                 |

* **核心索引建议**：联合索引 `idx_conversation (sender_id, receiver_id)` 或根据业务逻辑建立接收方未读消息索引。

### 5. `review` (交易评价与信控表)

当 `item` 状态变为“已成交”后，买卖双方互评的载体。

| 字段名           | 数据类型         | 约束/默认值                | 描述说明                             |
|:--------------|:-------------|:----------------------|:---------------------------------|
| `id`          | BIGINT       | PRIMARY KEY, AUTO_INC | 评价流水ID                           |
| `item_id`     | BIGINT       | NOT NULL              | 关联的交易物品/需求帖文ID                   |
| `reviewer_id` | BIGINT       | NOT NULL              | 评价人ID                            |
| `reviewee_id` | BIGINT       | NOT NULL              | 被评价人ID                           |
| `rating_type` | TINYINT      | NOT NULL              | **评价性质 (1好评加分, 0中评无影响, -1差评扣分)** |
| `tags`        | VARCHAR(100) | NULL                  | 结构化标签（逗号分隔，如：爽快,准时跨校区）           |
| `content`     | VARCHAR(255) | NULL                  | 图文无关的文字短评                        |
| `created_at`  | DATETIME     | CURRENT_TIMESTAMP     | 评价时间                             |

### 数据库设计亮点与业务映射：

1. **极简的交易流转**：摒弃传统电商系统的 `Order` 表。只要发布者将 `item.item_status` 修改为 `1 (已成交)`，并选中系统提供的
   `matched_user_id`，一个撮合闭环即可成立，极大降低数据库联表查询成本。
2. **消息上下文不丢**：`message` 表中关联了 `item_id`，完美支持PRD中“聊天界面顶部带入商品卡片防误解”的前端需求。
3. **信用分自动计算接口预留**：通过监听对 `review` 表的 `INSERT` 操作（或使用简单的定时任务/Mybatis后置插件），实时统计
   `rating_type`，反哺更新 `user.credit_score` 字段。