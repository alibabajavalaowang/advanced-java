package cn.iocoder.yudao.module.family.service.rank;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.family.controller.admin.vo.GenerationRankPageReqVO;
import cn.iocoder.yudao.module.family.controller.admin.vo.GenerationRankSaveReqVO;
import cn.iocoder.yudao.module.family.dal.dataobject.GenerationRankDO;
import cn.iocoder.yudao.module.family.dal.mysql.GenerationRankMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.family.enums.ErrorCodeConstants.*;

@Service
@Validated
public class GenerationRankServiceImpl implements GenerationRankService {

    @Resource
    private GenerationRankMapper generationRankMapper;

    @Override
    public Long createGenerationRank(GenerationRankSaveReqVO createReqVO) {
        // 校验辈分序号不重复
        GenerationRankDO existing = generationRankMapper.selectByTreeIdAndRankOrder(
                createReqVO.getTreeId(), createReqVO.getRankOrder());
        if (existing != null) {
            throw exception(GENERATION_RANK_DUPLICATE);
        }
        GenerationRankDO rank = BeanUtils.toBean(createReqVO, GenerationRankDO.class);
        generationRankMapper.insert(rank);
        return rank.getId();
    }

    @Override
    public void updateGenerationRank(GenerationRankSaveReqVO updateReqVO) {
        validateExists(updateReqVO.getId());
        GenerationRankDO updateObj = BeanUtils.toBean(updateReqVO, GenerationRankDO.class);
        generationRankMapper.updateById(updateObj);
    }

    @Override
    public void deleteGenerationRank(Long id) {
        validateExists(id);
        generationRankMapper.deleteById(id);
    }

    private void validateExists(Long id) {
        if (generationRankMapper.selectById(id) == null) {
            throw exception(GENERATION_RANK_NOT_EXISTS);
        }
    }

    @Override
    public GenerationRankDO getGenerationRank(Long id) {
        return generationRankMapper.selectById(id);
    }

    @Override
    public PageResult<GenerationRankDO> getGenerationRankPage(GenerationRankPageReqVO pageReqVO) {
        return generationRankMapper.selectPage(pageReqVO);
    }

    @Override
    public List<GenerationRankDO> getRankListByTreeId(Long treeId) {
        return generationRankMapper.selectListByTreeId(treeId);
    }
}
