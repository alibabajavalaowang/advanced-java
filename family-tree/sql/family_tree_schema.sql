-- =============================================
-- 家谱管理系统 - 数据库表结构
-- 基于芋道云(yudao-cloud)框架规范
-- MySQL 8.0+ / UTF8MB4
-- =============================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 家谱表
-- ----------------------------
DROP TABLE IF EXISTS `family_tree`;
CREATE TABLE `family_tree` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '家谱ID',
    `name`             VARCHAR(100) NOT NULL COMMENT '家谱名称',
    `surname`          VARCHAR(20)  NOT NULL COMMENT '姓氏',
    `hall_name`        VARCHAR(50)           DEFAULT NULL COMMENT '堂号',
    `origin`           VARCHAR(200)          DEFAULT NULL COMMENT '郡望/发源地',
    `motto`            TEXT                  DEFAULT NULL COMMENT '家训',
    `clan_rules`       TEXT                  DEFAULT NULL COMMENT '族规',
    `description`      TEXT                  DEFAULT NULL COMMENT '家族简介',
    `ancestor_name`    VARCHAR(50)           DEFAULT NULL COMMENT '始祖姓名',
    `ancestor_story`   TEXT                  DEFAULT NULL COMMENT '始祖故事',
    `cover_image`      VARCHAR(500)          DEFAULT NULL COMMENT '封面图URL',
    `visibility`       TINYINT      NOT NULL DEFAULT 0 COMMENT '可见性：0私有 1公开',
    `member_count`     INT                   DEFAULT 0 COMMENT '成员总数',
    `generation_count` INT                   DEFAULT 0 COMMENT '世代数',
    `status`           TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0正常 1停用',
    -- 芋道云通用字段
    `creator`          VARCHAR(64)           DEFAULT '' COMMENT '创建者',
    `create_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`          VARCHAR(64)           DEFAULT '' COMMENT '更新者',
    `update_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`          BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`        BIGINT       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '家谱表';

-- ----------------------------
-- 2. 家族成员表
-- ----------------------------
DROP TABLE IF EXISTS `family_member`;
CREATE TABLE `family_member` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '成员ID',
    `tree_id`         BIGINT       NOT NULL COMMENT '所属家谱ID',
    `name`            VARCHAR(50)  NOT NULL COMMENT '姓名',
    `gender`          TINYINT      NOT NULL DEFAULT 1 COMMENT '性别：0女 1男',
    `birth_date`      DATE                  DEFAULT NULL COMMENT '出生日期',
    `death_date`      DATE                  DEFAULT NULL COMMENT '去世日期',
    `is_alive`        TINYINT      NOT NULL DEFAULT 1 COMMENT '是否在世：0否 1是',
    `generation`      INT                   DEFAULT NULL COMMENT '第几世（辈分序号）',
    `generation_name` VARCHAR(20)           DEFAULT NULL COMMENT '字辈',
    `birthplace`      VARCHAR(200)          DEFAULT NULL COMMENT '出生地',
    `residence`       VARCHAR(200)          DEFAULT NULL COMMENT '现居住地',
    `phone`           VARCHAR(20)           DEFAULT NULL COMMENT '联系电话',
    `education`       VARCHAR(50)           DEFAULT NULL COMMENT '学历',
    `occupation`      VARCHAR(100)          DEFAULT NULL COMMENT '职业',
    `achievements`    TEXT                  DEFAULT NULL COMMENT '主要成就/事迹',
    `biography`       TEXT                  DEFAULT NULL COMMENT '生平简介',
    `avatar`          VARCHAR(500)          DEFAULT NULL COMMENT '头像URL',
    `parent_id`       BIGINT                DEFAULT NULL COMMENT '父亲ID（邻接表模型）',
    `mother_id`       BIGINT                DEFAULT NULL COMMENT '母亲ID',
    `spouse_id`       BIGINT                DEFAULT NULL COMMENT '配偶ID',
    `sort_order`      INT          NOT NULL DEFAULT 0 COMMENT '排序（长幼顺序）',
    `user_id`         BIGINT                DEFAULT NULL COMMENT '关联系统用户ID',
    `status`          TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0正常 1停用',
    -- 芋道云通用字段
    `creator`         VARCHAR(64)           DEFAULT '' COMMENT '创建者',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`         VARCHAR(64)           DEFAULT '' COMMENT '更新者',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`       BIGINT       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    KEY `idx_tree_id` (`tree_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_spouse_id` (`spouse_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '家族成员表';

-- ----------------------------
-- 3. 成员关系表（补充复杂关系）
-- ----------------------------
DROP TABLE IF EXISTS `family_relation`;
CREATE TABLE `family_relation` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '关系ID',
    `tree_id`        BIGINT       NOT NULL COMMENT '所属家谱ID',
    `from_member_id` BIGINT       NOT NULL COMMENT '关系发起方',
    `to_member_id`   BIGINT       NOT NULL COMMENT '关系接收方',
    `relation_type`  TINYINT      NOT NULL COMMENT '关系类型：1父子 2母子 3夫妻 4兄弟 5姐妹 6兄妹',
    `description`    VARCHAR(200)          DEFAULT NULL COMMENT '关系说明',
    -- 芋道云通用字段
    `creator`        VARCHAR(64)           DEFAULT '' COMMENT '创建者',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`        VARCHAR(64)           DEFAULT '' COMMENT '更新者',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`      BIGINT       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_relation` (`tree_id`, `from_member_id`, `to_member_id`, `relation_type`),
    KEY `idx_from_member` (`from_member_id`),
    KEY `idx_to_member` (`to_member_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '成员关系表';

-- ----------------------------
-- 4. 家族媒体/照片表
-- ----------------------------
DROP TABLE IF EXISTS `family_media`;
CREATE TABLE `family_media` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '媒体ID',
    `tree_id`        BIGINT       NOT NULL COMMENT '所属家谱ID',
    `member_id`      BIGINT                DEFAULT NULL COMMENT '关联成员ID（可选）',
    `title`          VARCHAR(200)          DEFAULT NULL COMMENT '标题',
    `file_name`      VARCHAR(255) NOT NULL COMMENT '文件名',
    `file_url`       VARCHAR(500) NOT NULL COMMENT '文件URL',
    `file_type`      VARCHAR(20)  NOT NULL COMMENT '文件类型：image/video/document',
    `file_size`      BIGINT                DEFAULT NULL COMMENT '文件大小(bytes)',
    `description`    TEXT                  DEFAULT NULL COMMENT '描述',
    `taken_date`     DATE                  DEFAULT NULL COMMENT '拍摄日期',
    `upload_user_id` BIGINT       NOT NULL COMMENT '上传用户ID',
    `sort_order`     INT                   DEFAULT 0 COMMENT '排序',
    -- 芋道云通用字段
    `creator`        VARCHAR(64)           DEFAULT '' COMMENT '创建者',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`        VARCHAR(64)           DEFAULT '' COMMENT '更新者',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`      BIGINT       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    KEY `idx_tree_id` (`tree_id`),
    KEY `idx_member_id` (`member_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '家族媒体表';

-- ----------------------------
-- 5. 家族大事记表
-- ----------------------------
DROP TABLE IF EXISTS `family_event`;
CREATE TABLE `family_event` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '事件ID',
    `tree_id`     BIGINT       NOT NULL COMMENT '所属家谱ID',
    `member_id`   BIGINT                DEFAULT NULL COMMENT '关联成员ID',
    `title`       VARCHAR(200) NOT NULL COMMENT '事件标题',
    `content`     TEXT                  DEFAULT NULL COMMENT '事件内容',
    `event_date`  DATE         NOT NULL COMMENT '事件日期',
    `event_type`  TINYINT      NOT NULL DEFAULT 0 COMMENT '类型：0其他 1婚嫁 2出生 3逝世 4乔迁 5金榜题名 6创业 7荣誉',
    `lunar_date`  VARCHAR(50)           DEFAULT NULL COMMENT '农历日期',
    `images`      VARCHAR(2000)         DEFAULT NULL COMMENT '图片URL,逗号分隔',
    `importance`  TINYINT               DEFAULT 0 COMMENT '重要程度：0普通 1重要 2非常重要',
    -- 芋道云通用字段
    `creator`     VARCHAR(64)           DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     VARCHAR(64)           DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    KEY `idx_tree_id` (`tree_id`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_event_date` (`event_date`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '家族大事记表';

-- ----------------------------
-- 6. 字辈排行表
-- ----------------------------
DROP TABLE IF EXISTS `generation_rank`;
CREATE TABLE `generation_rank` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '字辈ID',
    `tree_id`     BIGINT      NOT NULL COMMENT '所属家谱ID',
    `rank_order`  INT         NOT NULL COMMENT '辈分序号(第几世)',
    `rank_char`   VARCHAR(10) NOT NULL COMMENT '字辈用字',
    `description` VARCHAR(200)         DEFAULT NULL COMMENT '说明',
    -- 芋道云通用字段
    `creator`     VARCHAR(64)          DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     VARCHAR(64)          DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`   BIGINT      NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tree_rank` (`tree_id`, `rank_order`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '字辈排行表';

-- ----------------------------
-- 7. 家族公告表
-- ----------------------------
DROP TABLE IF EXISTS `family_notice`;
CREATE TABLE `family_notice` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    `tree_id`      BIGINT       NOT NULL COMMENT '所属家谱ID',
    `title`        VARCHAR(200) NOT NULL COMMENT '公告标题',
    `content`      TEXT         NOT NULL COMMENT '公告内容',
    `notice_type`  TINYINT               DEFAULT 0 COMMENT '类型：0通知 1公告 2活动',
    `status`       TINYINT               DEFAULT 0 COMMENT '状态：0草稿 1已发布',
    `publish_time` DATETIME              DEFAULT NULL COMMENT '发布时间',
    `publisher_id` BIGINT       NOT NULL COMMENT '发布人ID',
    -- 芋道云通用字段
    `creator`      VARCHAR(64)           DEFAULT '' COMMENT '创建者',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`      VARCHAR(64)           DEFAULT '' COMMENT '更新者',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      BIT(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`    BIGINT       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    KEY `idx_tree_id` (`tree_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '家族公告表';

-- ----------------------------
-- 8. 家谱权限表
-- ----------------------------
DROP TABLE IF EXISTS `tree_permission`;
CREATE TABLE `tree_permission` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    `tree_id`     BIGINT      NOT NULL COMMENT '家谱ID',
    `user_id`     BIGINT      NOT NULL COMMENT '用户ID',
    `role`        VARCHAR(20) NOT NULL COMMENT '角色：admin/editor/viewer',
    -- 芋道云通用字段
    `creator`     VARCHAR(64)          DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     VARCHAR(64)          DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     BIT(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`   BIGINT      NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tree_user` (`tree_id`, `user_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '家谱权限表';

SET FOREIGN_KEY_CHECKS = 1;
