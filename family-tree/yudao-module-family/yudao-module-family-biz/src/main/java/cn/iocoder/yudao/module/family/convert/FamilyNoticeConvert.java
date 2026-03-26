package cn.iocoder.yudao.module.family.convert;

import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyNoticeRespVO;
import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyNoticeSaveReqVO;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyNoticeDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface FamilyNoticeConvert {

    FamilyNoticeConvert INSTANCE = Mappers.getMapper(FamilyNoticeConvert.class);

    FamilyNoticeDO convert(FamilyNoticeSaveReqVO bean);

    FamilyNoticeRespVO convert(FamilyNoticeDO bean);

    List<FamilyNoticeRespVO> convertList(List<FamilyNoticeDO> list);

}
