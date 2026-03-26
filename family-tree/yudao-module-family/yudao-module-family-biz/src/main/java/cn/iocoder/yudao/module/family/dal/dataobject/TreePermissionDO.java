package cn.iocoder.yudao.module.family.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 家谱权限 DO
 *
 * @author family
 */
@TableName("tree_permission")
@KeySequence("tree_permission_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TreePermissionDO extends BaseDO {

    @TableId
    private Long id;
    private Long treeId;
    private Long userId;
    private String role;
    private Long tenantId;

}
