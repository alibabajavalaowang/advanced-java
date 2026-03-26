package cn.iocoder.yudao.module.family.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * Family 模块错误码枚举类
 *
 * family 系统，使用 1-050-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 家谱 1-050-001-000 ==========
    ErrorCode FAMILY_TREE_NOT_EXISTS = new ErrorCode(1_050_001_000, "家谱不存在");

    // ========== 家族成员 1-050-002-000 ==========
    ErrorCode FAMILY_MEMBER_NOT_EXISTS = new ErrorCode(1_050_002_000, "家族成员不存在");
    ErrorCode FAMILY_MEMBER_HAS_CHILDREN = new ErrorCode(1_050_002_001, "该成员存在子女，无法删除");

    // ========== 字辈信息 1-050-003-000 ==========
    ErrorCode GENERATION_RANK_NOT_EXISTS = new ErrorCode(1_050_003_000, "字辈信息不存在");
    ErrorCode GENERATION_RANK_DUPLICATE = new ErrorCode(1_050_003_001, "辈分序号已存在");

    // ========== 大事记 1-050-004-000 ==========
    ErrorCode FAMILY_EVENT_NOT_EXISTS = new ErrorCode(1_050_004_000, "大事记不存在");

    // ========== 媒体资料 1-050-005-000 ==========
    ErrorCode FAMILY_MEDIA_NOT_EXISTS = new ErrorCode(1_050_005_000, "媒体资料不存在");

    // ========== 公告 1-050-006-000 ==========
    ErrorCode FAMILY_NOTICE_NOT_EXISTS = new ErrorCode(1_050_006_000, "公告不存在");

    // ========== 关系记录 1-050-007-000 ==========
    ErrorCode FAMILY_RELATION_NOT_EXISTS = new ErrorCode(1_050_007_000, "关系记录不存在");

    // ========== 权限记录 1-050-008-000 ==========
    ErrorCode TREE_PERMISSION_NOT_EXISTS = new ErrorCode(1_050_008_000, "权限记录不存在");

}
