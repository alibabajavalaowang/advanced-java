package cn.iocoder.yudao.module.family.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 家族大事记 Response VO")
@Data
public class FamilyEventRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "家谱ID")
    private Long treeId;

    @Schema(description = "关联成员ID")
    private Long memberId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "事件日期")
    private LocalDate eventDate;

    @Schema(description = "事件类型")
    private String eventType;

    @Schema(description = "农历日期")
    private String lunarDate;

    @Schema(description = "图片")
    private String images;

    @Schema(description = "重要程度")
    private Integer importance;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "关联成员姓名")
    private String memberName;

}
