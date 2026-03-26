package cn.iocoder.yudao.module.family.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "管理后台 - 家族大事记创建/修改 Request VO")
@Data
public class FamilyEventSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "家谱ID", example = "1")
    private Long treeId;

    @Schema(description = "关联成员ID", example = "1")
    private Long memberId;

    @Schema(description = "标题", requiredMode = REQUIRED)
    @NotEmpty(message = "标题不能为空")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "事件日期", requiredMode = REQUIRED)
    @NotNull(message = "事件日期不能为空")
    private LocalDate eventDate;

    @Schema(description = "事件类型")
    private String eventType;

    @Schema(description = "农历日期")
    private String lunarDate;

    @Schema(description = "图片")
    private String images;

    @Schema(description = "重要程度", example = "1")
    private Integer importance;

}
