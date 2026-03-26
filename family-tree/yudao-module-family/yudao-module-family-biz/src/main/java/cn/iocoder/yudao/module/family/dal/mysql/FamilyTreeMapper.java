package cn.iocoder.yudao.module.family.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyTreePageReqVO;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyTreeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FamilyTreeMapper extends BaseMapperX<FamilyTreeDO> {

    default PageResult<FamilyTreeDO> selectPage(FamilyTreePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FamilyTreeDO>()
                .likeIfPresent(FamilyTreeDO::getName, reqVO.getName())
                .eqIfPresent(FamilyTreeDO::getSurname, reqVO.getSurname())
                .eqIfPresent(FamilyTreeDO::getStatus, reqVO.getStatus())
                .eqIfPresent(FamilyTreeDO::getVisibility, reqVO.getVisibility())
                .betweenIfPresent(FamilyTreeDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(FamilyTreeDO::getId));
    }

    default List<FamilyTreeDO> selectListByUserId(Long userId) {
        // 通过 tree_permission 关联查询，这里简化为查询所有
        return selectList();
    }

}
