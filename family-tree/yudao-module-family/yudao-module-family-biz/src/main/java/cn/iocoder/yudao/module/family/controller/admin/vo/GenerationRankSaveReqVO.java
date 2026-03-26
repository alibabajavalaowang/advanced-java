package cn.iocoder.yudao.module.family.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "管理后台 - 字辈信息创建/修改 Request VO")
@Data
public class GenerationRankSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "家谱ID", requiredMode = REQUIRED, example = "1")
    @NotNull(message = "家谱ID不能为空")
    private Long treeId;

    @Schema(description = "辈分序号", requiredMode = REQUIRED, example = "1")
    @NotNull(message = "辈分序号不能为空")
    private Integer rankOrder;

    @Schema(description = "辈分字", requiredMode = REQUIRED)
    @NotEmpty(message = "辈分字不能为空")
    private String rankChar;

    @Schema(description = "描述")
    private String description;

}
