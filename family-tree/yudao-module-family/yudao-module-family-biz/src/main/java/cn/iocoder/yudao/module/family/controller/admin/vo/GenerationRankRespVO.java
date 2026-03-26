package cn.iocoder.yudao.module.family.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 字辈信息 Response VO")
@Data
public class GenerationRankRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "家谱ID")
    private Long treeId;

    @Schema(description = "辈分序号")
    private Integer rankOrder;

    @Schema(description = "辈分字")
    private String rankChar;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
