package cn.iocoder.yudao.module.family.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GenderEnum {

    FEMALE(0, "女"),
    MALE(1, "男");

    private final Integer value;
    private final String name;

}
