package cn.iocoder.yudao.module.family.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "管理后台 - 家谱创建/修改 Request VO")
@Data
public class FamilyTreeSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "家谱名称", requiredMode = REQUIRED)
    @NotEmpty(message = "家谱名称不能为空")
    private String name;

    @Schema(description = "姓氏", requiredMode = REQUIRED)
    @NotEmpty(message = "姓氏不能为空")
    private String surname;

    @Schema(description = "堂号")
    private String hallName;

    @Schema(description = "发源地")
    private String origin;

    @Schema(description = "家训")
    private String motto;

    @Schema(description = "族规")
    private String clanRules;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "始祖姓名")
    private String ancestorName;

    @Schema(description = "始祖故事")
    private String ancestorStory;

    @Schema(description = "封面图片")
    private String coverImage;

    @Schema(description = "可见性", example = "0")
    private Integer visibility;

    @Schema(description = "状态", example = "0")
    private Integer status;

}
