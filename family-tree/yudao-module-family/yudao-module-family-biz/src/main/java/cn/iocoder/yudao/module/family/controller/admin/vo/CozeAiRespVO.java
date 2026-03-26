package cn.iocoder.yudao.module.family.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - AI 内容生成 Response VO")
@Data
public class CozeAiRespVO {

    @Schema(description = "生成内容")
    private String content;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "家谱ID")
    private Long treeId;

}
