package cn.iocoder.yudao.module.family.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 家族公告 Response VO")
@Data
public class FamilyNoticeRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "家谱ID")
    private Long treeId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "公告类型")
    private String noticeType;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "发布人ID")
    private Long publisherId;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "发布人姓名")
    private String publisherName;

}
