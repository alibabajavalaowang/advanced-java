package cn.iocoder.yudao.module.family.convert;

import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyMemberRespVO;
import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyMemberSaveReqVO;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyMemberDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface FamilyMemberConvert {

    FamilyMemberConvert INSTANCE = Mappers.getMapper(FamilyMemberConvert.class);

    FamilyMemberDO convert(FamilyMemberSaveReqVO bean);

    FamilyMemberRespVO convert(FamilyMemberDO bean);

    List<FamilyMemberRespVO> convertList(List<FamilyMemberDO> list);

}
