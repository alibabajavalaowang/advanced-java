package cn.iocoder.yudao.module.family.service.rank;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.family.controller.admin.vo.GenerationRankPageReqVO;
import cn.iocoder.yudao.module.family.controller.admin.vo.GenerationRankSaveReqVO;
import cn.iocoder.yudao.module.family.dal.dataobject.GenerationRankDO;

import javax.validation.Valid;
import java.util.List;

public interface GenerationRankService {

    Long createGenerationRank(@Valid GenerationRankSaveReqVO createReqVO);

    void updateGenerationRank(@Valid GenerationRankSaveReqVO updateReqVO);

    void deleteGenerationRank(Long id);

    GenerationRankDO getGenerationRank(Long id);

    PageResult<GenerationRankDO> getGenerationRankPage(GenerationRankPageReqVO pageReqVO);

    List<GenerationRankDO> getRankListByTreeId(Long treeId);
}
