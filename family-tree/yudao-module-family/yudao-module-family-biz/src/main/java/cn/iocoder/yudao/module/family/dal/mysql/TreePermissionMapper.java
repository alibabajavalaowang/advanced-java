package cn.iocoder.yudao.module.family.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.family.dal.dataobject.TreePermissionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TreePermissionMapper extends BaseMapperX<TreePermissionDO> {

    default List<TreePermissionDO> selectListByUserId(Long userId) {
        return selectList(TreePermissionDO::getUserId, userId);
    }

}
