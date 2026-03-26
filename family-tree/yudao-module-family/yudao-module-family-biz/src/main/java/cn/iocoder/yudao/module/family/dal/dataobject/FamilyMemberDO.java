package cn.iocoder.yudao.module.family.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

/**
 * 家族成员 DO
 *
 * @author family
 */
@TableName("family_member")
@KeySequence("family_member_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FamilyMemberDO extends BaseDO {

    /**
     * 主键 ID
     */
    @TableId
    private Long id;
    /**
     * 家谱 ID
     */
    private Long treeId;
    /**
     * 姓名
     */
    private String name;
    /**
     * 性别
     *
     * 枚举 {@link cn.iocoder.yudao.module.family.enums.GenderEnum}
     */
    private Integer gender;
    /**
     * 出生日期
     */
    private LocalDate birthDate;
    /**
     * 逝世日期
     */
    private LocalDate deathDate;
    /**
     * 是否在世
     */
    private Boolean isAlive;
    /**
     * 世代（第几代）
     */
    private Integer generation;
    /**
     * 字辈名
     */
    private String generationName;
    /**
     * 出生地
     */
    private String birthplace;
    /**
     * 现居地
     */
    private String residence;
    /**
     * 联系电话
     */
    private String phone;
    /**
     * 学历
     */
    private String education;
    /**
     * 职业
     */
    private String occupation;
    /**
     * 成就
     */
    private String achievements;
    /**
     * 简介
     */
    private String biography;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 父亲 ID
     */
    private Long parentId;
    /**
     * 母亲 ID
     */
    private Long motherId;
    /**
     * 配偶 ID
     */
    private Long spouseId;
    /**
     * 排序
     */
    private Integer sortOrder;
    /**
     * 关联用户 ID
     */
    private Long userId;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 租户编号
     */
    private Long tenantId;

}
