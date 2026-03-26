package cn.iocoder.yudao.module.family.convert;

import cn.iocoder.yudao.module.family.controller.admin.vo.GenerationRankRespVO;
import cn.iocoder.yudao.module.family.controller.admin.vo.GenerationRankSaveReqVO;
import cn.iocoder.yudao.module.family.dal.dataobject.GenerationRankDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface GenerationRankConvert {

    GenerationRankConvert INSTANCE = Mappers.getMapper(GenerationRankConvert.class);

    GenerationRankDO convert(GenerationRankSaveReqVO bean);

    GenerationRankRespVO convert(GenerationRankDO bean);

    List<GenerationRankRespVO> convertList(List<GenerationRankDO> list);

}
