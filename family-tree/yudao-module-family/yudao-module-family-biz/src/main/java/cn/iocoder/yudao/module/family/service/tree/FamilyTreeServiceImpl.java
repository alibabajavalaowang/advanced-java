package cn.iocoder.yudao.module.family.service.tree;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyTreePageReqVO;
import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyTreeSaveReqVO;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyTreeDO;
import cn.iocoder.yudao.module.family.dal.dataobject.TreePermissionDO;
import cn.iocoder.yudao.module.family.dal.mysql.FamilyMemberMapper;
import cn.iocoder.yudao.module.family.dal.mysql.FamilyTreeMapper;
import cn.iocoder.yudao.module.family.dal.mysql.TreePermissionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.family.enums.ErrorCodeConstants.*;

@Service
@Validated
public class FamilyTreeServiceImpl implements FamilyTreeService {

    @Resource
    private FamilyTreeMapper familyTreeMapper;
    @Resource
    private FamilyMemberMapper familyMemberMapper;
    @Resource
    private TreePermissionMapper treePermissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createFamilyTree(FamilyTreeSaveReqVO createReqVO) {
        FamilyTreeDO familyTree = BeanUtils.toBean(createReqVO, FamilyTreeDO.class);
        familyTreeMapper.insert(familyTree);

        // 创建者默认为管理员权限
        TreePermissionDO permission = new TreePermissionDO();
        permission.setTreeId(familyTree.getId());
        permission.setUserId(Long.valueOf(familyTree.getCreator()));
        permission.setRole("admin");
        treePermissionMapper.insert(permission);

        return familyTree.getId();
    }

    @Override
    public void updateFamilyTree(FamilyTreeSaveReqVO updateReqVO) {
        validateFamilyTreeExists(updateReqVO.getId());
        FamilyTreeDO updateObj = BeanUtils.toBean(updateReqVO, FamilyTreeDO.class);
        familyTreeMapper.updateById(updateObj);
    }

    @Override
    public void deleteFamilyTree(Long id) {
        validateFamilyTreeExists(id);
        familyTreeMapper.deleteById(id);
    }

    private void validateFamilyTreeExists(Long id) {
        if (familyTreeMapper.selectById(id) == null) {
            throw exception(FAMILY_TREE_NOT_EXISTS);
        }
    }

    @Override
    public FamilyTreeDO getFamilyTree(Long id) {
        return familyTreeMapper.selectById(id);
    }

    @Override
    public PageResult<FamilyTreeDO> getFamilyTreePage(FamilyTreePageReqVO pageReqVO) {
        return familyTreeMapper.selectPage(pageReqVO);
    }

    @Override
    public List<FamilyTreeDO> getFamilyTreeListByUserId(Long userId) {
        // 查询用户有权限的家谱
        List<TreePermissionDO> permissions = treePermissionMapper.selectListByUserId(userId);
        List<Long> treeIds = permissions.stream()
                .map(TreePermissionDO::getTreeId)
                .collect(Collectors.toList());
        if (treeIds.isEmpty()) {
            return List.of();
        }
        return familyTreeMapper.selectBatchIds(treeIds);
    }

    @Override
    public void updateMemberCount(Long treeId) {
        Long count = familyMemberMapper.selectCountByTreeId(treeId);
        FamilyTreeDO updateObj = new FamilyTreeDO();
        updateObj.setId(treeId);
        updateObj.setMemberCount(count.intValue());
        familyTreeMapper.updateById(updateObj);
    }
}
