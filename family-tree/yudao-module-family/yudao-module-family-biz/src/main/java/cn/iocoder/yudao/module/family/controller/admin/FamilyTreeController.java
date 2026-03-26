package cn.iocoder.yudao.module.family.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.family.controller.admin.vo.*;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyTreeDO;
import cn.iocoder.yudao.module.family.service.tree.FamilyTreeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 家谱管理")
@RestController
@RequestMapping("/family/tree")
@Validated
public class FamilyTreeController {

    @Resource
    private FamilyTreeService familyTreeService;

    @PostMapping("/create")
    @Operation(summary = "创建家谱")
    @PreAuthorize("@ss.hasPermission('family:tree:create')")
    public CommonResult<Long> createFamilyTree(@Valid @RequestBody FamilyTreeSaveReqVO createReqVO) {
        return success(familyTreeService.createFamilyTree(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新家谱")
    @PreAuthorize("@ss.hasPermission('family:tree:update')")
    public CommonResult<Boolean> updateFamilyTree(@Valid @RequestBody FamilyTreeSaveReqVO updateReqVO) {
        familyTreeService.updateFamilyTree(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除家谱")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('family:tree:delete')")
    public CommonResult<Boolean> deleteFamilyTree(@RequestParam("id") Long id) {
        familyTreeService.deleteFamilyTree(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取家谱详情")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('family:tree:query')")
    public CommonResult<FamilyTreeRespVO> getFamilyTree(@RequestParam("id") Long id) {
        FamilyTreeDO familyTree = familyTreeService.getFamilyTree(id);
        return success(BeanUtils.toBean(familyTree, FamilyTreeRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取家谱分页")
    @PreAuthorize("@ss.hasPermission('family:tree:query')")
    public CommonResult<PageResult<FamilyTreeRespVO>> getFamilyTreePage(@Valid FamilyTreePageReqVO pageReqVO) {
        PageResult<FamilyTreeDO> pageResult = familyTreeService.getFamilyTreePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, FamilyTreeRespVO.class));
    }

    @GetMapping("/my-list")
    @Operation(summary = "获取我的家谱列表")
    public CommonResult<List<FamilyTreeRespVO>> getMyFamilyTreeList() {
        // TODO: 从 SecurityContext 获取当前用户 ID
        Long userId = 1L;
        List<FamilyTreeDO> list = familyTreeService.getFamilyTreeListByUserId(userId);
        return success(BeanUtils.toBean(list, FamilyTreeRespVO.class));
    }
}
