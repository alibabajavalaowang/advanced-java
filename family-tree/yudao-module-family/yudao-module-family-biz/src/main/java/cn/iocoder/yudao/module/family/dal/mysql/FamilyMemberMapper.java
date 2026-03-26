package cn.iocoder.yudao.module.family.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyMemberPageReqVO;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyMemberDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FamilyMemberMapper extends BaseMapperX<FamilyMemberDO> {

    default PageResult<FamilyMemberDO> selectPage(FamilyMemberPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FamilyMemberDO>()
                .eqIfPresent(FamilyMemberDO::getTreeId, reqVO.getTreeId())
                .likeIfPresent(FamilyMemberDO::getName, reqVO.getName())
                .eqIfPresent(FamilyMemberDO::getGender, reqVO.getGender())
                .eqIfPresent(FamilyMemberDO::getGeneration, reqVO.getGeneration())
                .eqIfPresent(FamilyMemberDO::getIsAlive, reqVO.getIsAlive())
                .orderByAsc(FamilyMemberDO::getGeneration)
                .orderByAsc(FamilyMemberDO::getSortOrder));
    }

    default List<FamilyMemberDO> selectListByTreeId(Long treeId) {
        return selectList(FamilyMemberDO::getTreeId, treeId);
    }

    default List<FamilyMemberDO> selectListByParentId(Long parentId) {
        return selectList(FamilyMemberDO::getParentId, parentId);
    }

    default Long selectCountByTreeId(Long treeId) {
        return selectCount(FamilyMemberDO::getTreeId, treeId);
    }

}
