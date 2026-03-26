package cn.iocoder.yudao.module.family.service.member;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.family.controller.admin.vo.*;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyMemberDO;

import javax.validation.Valid;
import java.util.List;

public interface FamilyMemberService {

    Long createFamilyMember(@Valid FamilyMemberSaveReqVO createReqVO);

    void updateFamilyMember(@Valid FamilyMemberSaveReqVO updateReqVO);

    void deleteFamilyMember(Long id);

    FamilyMemberDO getFamilyMember(Long id);

    PageResult<FamilyMemberDO> getFamilyMemberPage(FamilyMemberPageReqVO pageReqVO);

    List<FamilyMemberDO> getMemberListByTreeId(Long treeId);

    List<FamilyMemberTreeVO> getMemberTree(Long treeId);

    FamilyTreeStatsVO getTreeStats(Long treeId);

    List<FamilyMemberDO> getChildrenByParentId(Long parentId);
}
