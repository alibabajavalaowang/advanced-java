package cn.iocoder.yudao.module.family.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.family.controller.admin.vo.*;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyMemberDO;
import cn.iocoder.yudao.module.family.service.member.FamilyMemberService;
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

@Tag(name = "管理后台 - 家族成员")
@RestController
@RequestMapping("/family/member")
@Validated
public class FamilyMemberController {

    @Resource
    private FamilyMemberService familyMemberService;

    @PostMapping("/create")
    @Operation(summary = "创建家族成员")
    @PreAuthorize("@ss.hasPermission('family:member:create')")
    public CommonResult<Long> createFamilyMember(@Valid @RequestBody FamilyMemberSaveReqVO createReqVO) {
        return success(familyMemberService.createFamilyMember(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新家族成员")
    @PreAuthorize("@ss.hasPermission('family:member:update')")
    public CommonResult<Boolean> updateFamilyMember(@Valid @RequestBody FamilyMemberSaveReqVO updateReqVO) {
        familyMemberService.updateFamilyMember(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除家族成员")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('family:member:delete')")
    public CommonResult<Boolean> deleteFamilyMember(@RequestParam("id") Long id) {
        familyMemberService.deleteFamilyMember(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取成员详情")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('family:member:query')")
    public CommonResult<FamilyMemberRespVO> getFamilyMember(@RequestParam("id") Long id) {
        FamilyMemberDO member = familyMemberService.getFamilyMember(id);
        return success(BeanUtils.toBean(member, FamilyMemberRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取成员分页")
    @PreAuthorize("@ss.hasPermission('family:member:query')")
    public CommonResult<PageResult<FamilyMemberRespVO>> getFamilyMemberPage(@Valid FamilyMemberPageReqVO pageReqVO) {
        PageResult<FamilyMemberDO> pageResult = familyMemberService.getFamilyMemberPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, FamilyMemberRespVO.class));
    }

    @GetMapping("/tree")
    @Operation(summary = "获取家谱树数据")
    @Parameter(name = "treeId", description = "家谱编号", required = true)
    @PreAuthorize("@ss.hasPermission('family:member:query')")
    public CommonResult<List<FamilyMemberTreeVO>> getMemberTree(@RequestParam("treeId") Long treeId) {
        return success(familyMemberService.getMemberTree(treeId));
    }

    @GetMapping("/stats")
    @Operation(summary = "获取家族统计数据")
    @Parameter(name = "treeId", description = "家谱编号", required = true)
    @PreAuthorize("@ss.hasPermission('family:member:query')")
    public CommonResult<FamilyTreeStatsVO> getTreeStats(@RequestParam("treeId") Long treeId) {
        return success(familyMemberService.getTreeStats(treeId));
    }

    @GetMapping("/children")
    @Operation(summary = "获取子女列表")
    @Parameter(name = "parentId", description = "父亲编号", required = true)
    public CommonResult<List<FamilyMemberRespVO>> getChildren(@RequestParam("parentId") Long parentId) {
        List<FamilyMemberDO> children = familyMemberService.getChildrenByParentId(parentId);
        return success(BeanUtils.toBean(children, FamilyMemberRespVO.class));
    }
}
