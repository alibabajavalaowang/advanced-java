package cn.iocoder.yudao.module.family.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "管理后台 - 家族成员分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FamilyMemberPageReqVO extends PageParam {

    @Schema(description = "家谱ID", requiredMode = REQUIRED, example = "1")
    @NotNull(message = "家谱ID不能为空")
    private Long treeId;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "世代")
    private Integer generation;

    @Schema(description = "是否在世")
    private Boolean isAlive;

}
