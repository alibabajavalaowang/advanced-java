package cn.iocoder.yudao.module.family.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Schema(description = "管理后台 - 家族媒体资料创建/修改 Request VO")
@Data
public class FamilyMediaSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "家谱ID", example = "1")
    private Long treeId;

    @Schema(description = "关联成员ID", example = "1")
    private Long memberId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "文件地址")
    private String fileUrl;

    @Schema(description = "文件类型")
    private String fileType;

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "拍摄日期")
    private LocalDate takenDate;

    @Schema(description = "排序")
    private Integer sortOrder;

}
