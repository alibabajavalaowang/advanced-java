package cn.iocoder.yudao.module.family.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 字辈信息 DO
 *
 * @author family
 */
@TableName("generation_rank")
@KeySequence("generation_rank_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GenerationRankDO extends BaseDO {

    @TableId
    private Long id;
    private Long treeId;
    private Integer rankOrder;
    private String rankChar;
    private String description;
    private Long tenantId;

}
