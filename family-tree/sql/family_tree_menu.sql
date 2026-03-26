-- =============================================
-- 家谱管理系统 - 芋道云系统菜单数据
-- 插入 system_menu 表
-- 菜单ID从 3000 开始
-- =============================================

-- ----------------------------
-- 顶级目录：家谱管理
-- ----------------------------
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (3000, '家谱管理', '', 1, 50, 0, '/family', 'ep:house', NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0');

-- ----------------------------
-- 1. 家谱列表
-- ----------------------------
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (3001, '家谱列表', '', 2, 1, 3000, 'tree', 'ep:notebook', 'family/tree/index', 'FamilyTree', 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0');

-- 家谱列表按钮
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (3010, '家谱查询', 'family:tree:query',   3, 1, 3001, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0'),
       (3011, '家谱创建', 'family:tree:create',  3, 2, 3001, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0'),
       (3012, '家谱更新', 'family:tree:update',  3, 3, 3001, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0'),
       (3013, '家谱删除', 'family:tree:delete',  3, 4, 3001, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0');

-- ----------------------------
-- 2. 成员管理
-- ----------------------------
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (3002, '成员管理', '', 2, 2, 3000, 'member', 'ep:user', 'family/member/index', 'FamilyMember', 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0');

-- 成员管理按钮
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (3020, '成员查询', 'family:member:query',   3, 1, 3002, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0'),
       (3021, '成员创建', 'family:member:create',  3, 2, 3002, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0'),
       (3022, '成员更新', 'family:member:update',  3, 3, 3002, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0'),
       (3023, '成员删除', 'family:member:delete',  3, 4, 3002, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0');

-- ----------------------------
-- 3. 世系图谱
-- ----------------------------
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (3003, '世系图谱', 'family:genealogy:query', 2, 3, 3000, 'genealogy', 'ep:share', 'family/genealogy/index', 'FamilyGenealogy', 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0');

-- ----------------------------
-- 4. 字辈管理
-- ----------------------------
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (3004, '字辈管理', '', 2, 4, 3000, 'generation', 'ep:sort', 'family/generation/index', 'FamilyGeneration', 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0');

-- 字辈管理按钮
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (3040, '字辈查询', 'family:generation:query',   3, 1, 3004, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0'),
       (3041, '字辈创建', 'family:generation:create',  3, 2, 3004, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0'),
       (3042, '字辈更新', 'family:generation:update',  3, 3, 3004, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0'),
       (3043, '字辈删除', 'family:generation:delete',  3, 4, 3004, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0');

-- ----------------------------
-- 5. 大事记
-- ----------------------------
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (3005, '大事记', '', 2, 5, 3000, 'event', 'ep:calendar', 'family/event/index', 'FamilyEvent', 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0');

-- 大事记按钮
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (3050, '大事记查询', 'family:event:query',   3, 1, 3005, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0'),
       (3051, '大事记创建', 'family:event:create',  3, 2, 3005, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0'),
       (3052, '大事记更新', 'family:event:update',  3, 3, 3005, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0'),
       (3053, '大事记删除', 'family:event:delete',  3, 4, 3005, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0');

-- ----------------------------
-- 6. 家族相册
-- ----------------------------
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (3006, '家族相册', '', 2, 6, 3000, 'media', 'ep:picture', 'family/media/index', 'FamilyMedia', 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0');

-- 家族相册按钮
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (3060, '相册查询', 'family:media:query',   3, 1, 3006, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0'),
       (3061, '相册上传', 'family:media:create',  3, 2, 3006, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0'),
       (3062, '相册更新', 'family:media:update',  3, 3, 3006, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0'),
       (3063, '相册删除', 'family:media:delete',  3, 4, 3006, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0');

-- ----------------------------
-- 7. 家族公告
-- ----------------------------
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (3007, '家族公告', '', 2, 7, 3000, 'notice', 'ep:bell', 'family/notice/index', 'FamilyNotice', 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0');

-- 家族公告按钮
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (3070, '公告查询', 'family:notice:query',   3, 1, 3007, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0'),
       (3071, '公告创建', 'family:notice:create',  3, 2, 3007, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0'),
       (3072, '公告更新', 'family:notice:update',  3, 3, 3007, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0'),
       (3073, '公告删除', 'family:notice:delete',  3, 4, 3007, '', '', '', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0');

-- ----------------------------
-- 8. 权限设置
-- ----------------------------
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (3008, '权限设置', 'family:permission:query', 2, 8, 3000, 'permission', 'ep:lock', 'family/permission/index', 'FamilyPermission', 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0');

-- ----------------------------
-- 9. 家族统计
-- ----------------------------
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (3009, '家族统计', 'family:statistics:query', 2, 9, 3000, 'statistics', 'ep:data-analysis', 'family/statistics/index', 'FamilyStatistics', 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0');

-- ----------------------------
-- 10. AI助手
-- ----------------------------
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES (3100, 'AI助手', 'family:ai:query', 2, 10, 3000, 'ai', 'ep:magic-stick', 'family/ai/index', 'FamilyAI', 0, 1, 1, 1, '1', NOW(), '1', NOW(), b'0');
