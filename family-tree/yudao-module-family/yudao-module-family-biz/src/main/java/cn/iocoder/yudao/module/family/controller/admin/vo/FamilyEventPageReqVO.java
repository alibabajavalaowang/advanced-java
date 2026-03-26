package cn.iocoder.yudao.module.family.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - 家族大事记分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FamilyEventPageReqVO extends PageParam {

    @Schema(description = "家谱ID", example = "1")
    private Long treeId;

    @Schema(description = "事件类型")
    private String eventType;

    @Schema(description = "关联成员ID", example = "1")
    private Long memberId;

    @Schema(description = "事件日期范围")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate[] eventDate;

}
