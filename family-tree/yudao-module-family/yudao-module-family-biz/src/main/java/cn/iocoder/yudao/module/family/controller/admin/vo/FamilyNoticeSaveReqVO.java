package cn.iocoder.yudao.module.family.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "管理后台 - 家族公告创建/修改 Request VO")
@Data
public class FamilyNoticeSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "家谱ID", example = "1")
    private Long treeId;

    @Schema(description = "标题", requiredMode = REQUIRED)
    @NotEmpty(message = "标题不能为空")
    private String title;

    @Schema(description = "内容", requiredMode = REQUIRED)
    @NotEmpty(message = "内容不能为空")
    private String content;

    @Schema(description = "公告类型")
    private String noticeType;

    @Schema(description = "状态")
    private Integer status;

}
