package cn.iocoder.yudao.module.family.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.family.controller.admin.vo.GenerationRankPageReqVO;
import cn.iocoder.yudao.module.family.dal.dataobject.GenerationRankDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GenerationRankMapper extends BaseMapperX<GenerationRankDO> {

    default PageResult<GenerationRankDO> selectPage(GenerationRankPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<GenerationRankDO>()
                .eqIfPresent(GenerationRankDO::getTreeId, reqVO.getTreeId())
                .orderByAsc(GenerationRankDO::getRankOrder));
    }

    default List<GenerationRankDO> selectListByTreeId(Long treeId) {
        return selectList(new LambdaQueryWrapperX<GenerationRankDO>()
                .eq(GenerationRankDO::getTreeId, treeId)
                .orderByAsc(GenerationRankDO::getRankOrder));
    }

    default GenerationRankDO selectByTreeIdAndRankOrder(Long treeId, Integer rankOrder) {
        return selectOne(new LambdaQueryWrapperX<GenerationRankDO>()
                .eq(GenerationRankDO::getTreeId, treeId)
                .eq(GenerationRankDO::getRankOrder, rankOrder));
    }

}
