package cn.iocoder.yudao.module.family.convert;

import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyEventRespVO;
import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyEventSaveReqVO;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyEventDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface FamilyEventConvert {

    FamilyEventConvert INSTANCE = Mappers.getMapper(FamilyEventConvert.class);

    FamilyEventDO convert(FamilyEventSaveReqVO bean);

    FamilyEventRespVO convert(FamilyEventDO bean);

    List<FamilyEventRespVO> convertList(List<FamilyEventDO> list);

}
