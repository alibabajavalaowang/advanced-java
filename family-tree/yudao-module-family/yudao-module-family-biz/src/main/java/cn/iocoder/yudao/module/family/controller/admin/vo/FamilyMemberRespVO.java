package cn.iocoder.yudao.module.family.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 家族成员 Response VO")
@Data
public class FamilyMemberRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "家谱ID")
    private Long treeId;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "出生日期")
    private LocalDate birthDate;

    @Schema(description = "逝世日期")
    private LocalDate deathDate;

    @Schema(description = "是否在世")
    private Boolean isAlive;

    @Schema(description = "世代")
    private Integer generation;

    @Schema(description = "字辈名")
    private String generationName;

    @Schema(description = "出生地")
    private String birthplace;

    @Schema(description = "现居地")
    private String residence;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "学历")
    private String education;

    @Schema(description = "职业")
    private String occupation;

    @Schema(description = "成就")
    private String achievements;

    @Schema(description = "简介")
    private String biography;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "父亲ID")
    private Long parentId;

    @Schema(description = "母亲ID")
    private Long motherId;

    @Schema(description = "配偶ID")
    private Long spouseId;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "关联用户ID")
    private Long userId;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "父亲姓名")
    private String parentName;

    @Schema(description = "配偶姓名")
    private String spouseName;

}
