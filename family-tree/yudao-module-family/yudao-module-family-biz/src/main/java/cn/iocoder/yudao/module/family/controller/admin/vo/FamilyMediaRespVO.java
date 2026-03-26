package cn.iocoder.yudao.module.family.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 家族媒体资料 Response VO")
@Data
public class FamilyMediaRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "家谱ID")
    private Long treeId;

    @Schema(description = "关联成员ID")
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

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
