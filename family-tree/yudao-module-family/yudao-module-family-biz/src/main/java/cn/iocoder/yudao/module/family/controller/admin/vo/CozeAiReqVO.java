package cn.iocoder.yudao.module.family.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - AI 内容生成 Request VO")
@Data
public class CozeAiReqVO {

    @Schema(description = "家谱ID", example = "1")
    @NotNull(message = "家谱ID不能为空")
    private Long treeId;

    @Schema(description = "提示词")
    @NotEmpty(message = "提示词不能为空")
    private String prompt;

    @Schema(description = "类型: story/motto/poem/summary")
    private String type;

}
