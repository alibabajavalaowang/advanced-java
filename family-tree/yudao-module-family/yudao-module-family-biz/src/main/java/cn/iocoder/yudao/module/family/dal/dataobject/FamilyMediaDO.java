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
 * 家族媒体资料 DO
 *
 * @author family
 */
@TableName("family_media")
@KeySequence("family_media_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FamilyMediaDO extends BaseDO {

    @TableId
    private Long id;
    private Long treeId;
    private Long memberId;
    private String title;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String description;
    private LocalDate takenDate;
    private Integer sortOrder;
    private Long tenantId;

}
