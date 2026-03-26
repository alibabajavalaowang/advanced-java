package cn.iocoder.yudao.module.family.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 家族媒体资料分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FamilyMediaPageReqVO extends PageParam {

    @Schema(description = "家谱ID", example = "1")
    private Long treeId;

    @Schema(description = "关联成员ID", example = "1")
    private Long memberId;

    @Schema(description = "文件类型")
    private String fileType;

}
