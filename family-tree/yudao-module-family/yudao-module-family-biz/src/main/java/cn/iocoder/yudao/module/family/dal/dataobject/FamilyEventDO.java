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
 * 家族大事记 DO
 *
 * @author family
 */
@TableName("family_event")
@KeySequence("family_event_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FamilyEventDO extends BaseDO {

    @TableId
    private Long id;
    private Long treeId;
    private Long memberId;
    private String title;
    private String content;
    private LocalDate eventDate;
    private String eventType;
    private String lunarDate;
    private String images;
    private Integer importance;
    private Long tenantId;

}
