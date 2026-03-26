package cn.iocoder.yudao.module.family.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 家谱 DO
 *
 * @author family
 */
@TableName("family_tree")
@KeySequence("family_tree_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FamilyTreeDO extends BaseDO {

    /**
     * 主键 ID
     */
    @TableId
    private Long id;
    /**
     * 家谱名称
     */
    private String name;
    /**
     * 姓氏
     */
    private String surname;
    /**
     * 堂号
     */
    private String hallName;
    /**
     * 发源地
     */
    private String origin;
    /**
     * 家训
     */
    private String motto;
    /**
     * 族规
     */
    private String clanRules;
    /**
     * 描述
     */
    private String description;
    /**
     * 始祖姓名
     */
    private String ancestorName;
    /**
     * 始祖故事
     */
    private String ancestorStory;
    /**
     * 封面图片
     */
    private String coverImage;
    /**
     * 可见性
     *
     * 枚举 {@link cn.iocoder.yudao.module.family.enums.FamilyTreeVisibilityEnum}
     */
    private Integer visibility;
    /**
     * 成员数量
     */
    private Integer memberCount;
    /**
     * 世代数量
     */
    private Integer generationCount;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 租户编号
     */
    private Long tenantId;

}
