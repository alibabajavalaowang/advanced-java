package cn.iocoder.yudao.module.family.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 家族公告 DO
 *
 * @author family
 */
@TableName("family_notice")
@KeySequence("family_notice_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FamilyNoticeDO extends BaseDO {

    @TableId
    private Long id;
    private Long treeId;
    private String title;
    private String content;
    private String noticeType;
    private Integer status;
    private Long publisherId;
    private LocalDateTime publishTime;
    private Long tenantId;

}
