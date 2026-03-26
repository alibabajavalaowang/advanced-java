package cn.iocoder.yudao.module.family.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 家族关系 DO
 *
 * @author family
 */
@TableName("family_relation")
@KeySequence("family_relation_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FamilyRelationDO extends BaseDO {

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
     * 起始成员 ID
     */
    private Long fromMemberId;
    /**
     * 目标成员 ID
     */
    private Long toMemberId;
    /**
     * 关系类型
     *
     * 枚举 {@link cn.iocoder.yudao.module.family.enums.RelationTypeEnum}
     */
    private Integer relationType;
    /**
     * 描述
     */
    private String description;
    /**
     * 租户编号
     */
    private Long tenantId;

}
