package cn.iocoder.yudao.module.family.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyMediaPageReqVO;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyMediaDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FamilyMediaMapper extends BaseMapperX<FamilyMediaDO> {

    default PageResult<FamilyMediaDO> selectPage(FamilyMediaPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FamilyMediaDO>()
                .eqIfPresent(FamilyMediaDO::getTreeId, reqVO.getTreeId())
                .eqIfPresent(FamilyMediaDO::getMemberId, reqVO.getMemberId())
                .eqIfPresent(FamilyMediaDO::getFileType, reqVO.getFileType())
                .orderByAsc(FamilyMediaDO::getSortOrder));
    }

}
