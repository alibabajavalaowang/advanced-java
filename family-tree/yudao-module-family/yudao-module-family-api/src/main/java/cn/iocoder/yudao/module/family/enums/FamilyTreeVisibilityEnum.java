package cn.iocoder.yudao.module.family.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FamilyTreeVisibilityEnum {

    PRIVATE(0, "私有"),
    PUBLIC(1, "公开");

    private final Integer value;
    private final String name;

}
