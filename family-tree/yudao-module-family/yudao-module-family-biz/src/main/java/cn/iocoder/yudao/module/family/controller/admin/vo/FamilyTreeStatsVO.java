package cn.iocoder.yudao.module.family.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "家族统计 VO")
@Data
public class FamilyTreeStatsVO {

    @Schema(description = "总成员数")
    private Integer totalMembers;

    @Schema(description = "在世成员数")
    private Integer aliveMembers;

    @Schema(description = "男性数量")
    private Integer maleCount;

    @Schema(description = "女性数量")
    private Integer femaleCount;

    @Schema(description = "世代数量")
    private Integer generationCount;

    @Schema(description = "平均年龄")
    private Double averageAge;

    @Schema(description = "各代人数分布")
    private Map<String, Integer> generationDistribution;

    @Schema(description = "地域分布")
    private Map<String, Integer> provinceDistribution;

    @Schema(description = "年龄段分布")
    private Map<String, Integer> ageDistribution;

    @Schema(description = "最近事件")
    private List<FamilyEventRespVO> recentEvents;

}
