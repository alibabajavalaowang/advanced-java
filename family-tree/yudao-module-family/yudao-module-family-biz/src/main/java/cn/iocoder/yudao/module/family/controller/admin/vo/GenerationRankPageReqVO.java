package cn.iocoder.yudao.module.family.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 字辈信息分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GenerationRankPageReqVO extends PageParam {

    @Schema(description = "家谱ID", example = "1")
    private Long treeId;

}
