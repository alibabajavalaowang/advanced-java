package cn.iocoder.yudao.module.family.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyRelationDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FamilyRelationMapper extends BaseMapperX<FamilyRelationDO> {

    default PageResult<FamilyRelationDO> selectPage(PageParam pageParam, Long treeId) {
        return selectPage(pageParam, new LambdaQueryWrapperX<FamilyRelationDO>()
                .eqIfPresent(FamilyRelationDO::getTreeId, treeId)
                .orderByDesc(FamilyRelationDO::getId));
    }

    default List<FamilyRelationDO> selectByTreeId(Long treeId) {
        return selectList(new LambdaQueryWrapperX<FamilyRelationDO>()
                .eq(FamilyRelationDO::getTreeId, treeId));
    }

}
