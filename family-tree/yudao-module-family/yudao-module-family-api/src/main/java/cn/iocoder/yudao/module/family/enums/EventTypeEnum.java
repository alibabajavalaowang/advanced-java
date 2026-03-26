package cn.iocoder.yudao.module.family.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventTypeEnum {

    OTHER(0, "其他"),
    MARRIAGE(1, "婚嫁"),
    BIRTH(2, "出生"),
    DEATH(3, "逝世"),
    MOVE(4, "乔迁"),
    EDUCATION(5, "金榜题名"),
    BUSINESS(6, "创业"),
    HONOR(7, "荣誉");

    private final Integer value;
    private final String name;

}
