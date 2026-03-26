package cn.iocoder.yudao.module.family.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyEventPageReqVO;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyEventDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FamilyEventMapper extends BaseMapperX<FamilyEventDO> {

    default PageResult<FamilyEventDO> selectPage(FamilyEventPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FamilyEventDO>()
                .eqIfPresent(FamilyEventDO::getTreeId, reqVO.getTreeId())
                .eqIfPresent(FamilyEventDO::getEventType, reqVO.getEventType())
                .eqIfPresent(FamilyEventDO::getMemberId, reqVO.getMemberId())
                .betweenIfPresent(FamilyEventDO::getEventDate, reqVO.getEventDate())
                .orderByDesc(FamilyEventDO::getEventDate));
    }

    default List<FamilyEventDO> selectListByTreeId(Long treeId) {
        return selectList(new LambdaQueryWrapperX<FamilyEventDO>()
                .eq(FamilyEventDO::getTreeId, treeId)
                .orderByDesc(FamilyEventDO::getEventDate));
    }

}
