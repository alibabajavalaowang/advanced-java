package cn.iocoder.yudao.module.family.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RelationTypeEnum {

    FATHER_CHILD(1, "父子"),
    MOTHER_CHILD(2, "母子"),
    SPOUSE(3, "夫妻"),
    BROTHER(4, "兄弟"),
    SISTER(5, "姐妹"),
    SIBLING(6, "兄妹");

    private final Integer value;
    private final String name;

}
