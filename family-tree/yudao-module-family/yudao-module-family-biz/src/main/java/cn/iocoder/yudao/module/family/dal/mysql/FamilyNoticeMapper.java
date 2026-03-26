package cn.iocoder.yudao.module.family.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyNoticePageReqVO;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyNoticeDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FamilyNoticeMapper extends BaseMapperX<FamilyNoticeDO> {

    default PageResult<FamilyNoticeDO> selectPage(FamilyNoticePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FamilyNoticeDO>()
                .eqIfPresent(FamilyNoticeDO::getTreeId, reqVO.getTreeId())
                .eqIfPresent(FamilyNoticeDO::getNoticeType, reqVO.getNoticeType())
                .eqIfPresent(FamilyNoticeDO::getStatus, reqVO.getStatus())
                .orderByDesc(FamilyNoticeDO::getId));
    }

}
