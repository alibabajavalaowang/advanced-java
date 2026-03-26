package cn.iocoder.yudao.module.family.convert;

import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyTreeRespVO;
import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyTreeSaveReqVO;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyTreeDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface FamilyTreeConvert {

    FamilyTreeConvert INSTANCE = Mappers.getMapper(FamilyTreeConvert.class);

    FamilyTreeDO convert(FamilyTreeSaveReqVO bean);

    FamilyTreeRespVO convert(FamilyTreeDO bean);

    List<FamilyTreeRespVO> convertList(List<FamilyTreeDO> list);

}
