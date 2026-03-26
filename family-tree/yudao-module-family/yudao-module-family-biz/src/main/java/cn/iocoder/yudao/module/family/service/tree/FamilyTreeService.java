package cn.iocoder.yudao.module.family.service.tree;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyTreePageReqVO;
import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyTreeSaveReqVO;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyTreeDO;

import javax.validation.Valid;
import java.util.List;

public interface FamilyTreeService {

    Long createFamilyTree(@Valid FamilyTreeSaveReqVO createReqVO);

    void updateFamilyTree(@Valid FamilyTreeSaveReqVO updateReqVO);

    void deleteFamilyTree(Long id);

    FamilyTreeDO getFamilyTree(Long id);

    PageResult<FamilyTreeDO> getFamilyTreePage(FamilyTreePageReqVO pageReqVO);

    List<FamilyTreeDO> getFamilyTreeListByUserId(Long userId);

    void updateMemberCount(Long treeId);
}
