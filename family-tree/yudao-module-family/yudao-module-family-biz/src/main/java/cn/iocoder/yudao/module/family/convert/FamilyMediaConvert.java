package cn.iocoder.yudao.module.family.convert;

import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyMediaRespVO;
import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyMediaSaveReqVO;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyMediaDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface FamilyMediaConvert {

    FamilyMediaConvert INSTANCE = Mappers.getMapper(FamilyMediaConvert.class);

    FamilyMediaDO convert(FamilyMediaSaveReqVO bean);

    FamilyMediaRespVO convert(FamilyMediaDO bean);

    List<FamilyMediaRespVO> convertList(List<FamilyMediaDO> list);

}
