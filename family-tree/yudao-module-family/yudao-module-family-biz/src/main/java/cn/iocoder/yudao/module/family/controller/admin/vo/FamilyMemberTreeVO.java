package cn.iocoder.yudao.module.family.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "家谱树节点 VO")
@Data
public class FamilyMemberTreeVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "出生日期")
    private String birthDate;

    @Schema(description = "逝世日期")
    private String deathDate;

    @Schema(description = "是否在世")
    private Boolean isAlive;

    @Schema(description = "世代")
    private Integer generation;

    @Schema(description = "字辈名")
    private String generationName;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "职业")
    private String occupation;

    @Schema(description = "父亲ID")
    private Long parentId;

    @Schema(description = "配偶ID")
    private Long spouseId;

    @Schema(description = "配偶姓名")
    private String spouseName;

    @Schema(description = "配偶性别")
    private Integer spouseGender;

    @Schema(description = "配偶头像")
    private String spouseAvatar;

    @Schema(description = "子女列表")
    private List<FamilyMemberTreeVO> children;

}
