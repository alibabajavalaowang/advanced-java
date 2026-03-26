package cn.iocoder.yudao.module.family.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.family.controller.admin.vo.*;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyNoticeDO;
import cn.iocoder.yudao.module.family.service.notice.FamilyNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 家族公告")
@RestController
@RequestMapping("/family/notice")
@Validated
public class FamilyNoticeController {

    @Resource
    private FamilyNoticeService familyNoticeService;

    @PostMapping("/create")
    @Operation(summary = "创建公告")
    @PreAuthorize("@ss.hasPermission('family:notice:create')")
    public CommonResult<Long> createFamilyNotice(@Valid @RequestBody FamilyNoticeSaveReqVO createReqVO) {
        return success(familyNoticeService.createFamilyNotice(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新公告")
    @PreAuthorize("@ss.hasPermission('family:notice:update')")
    public CommonResult<Boolean> updateFamilyNotice(@Valid @RequestBody FamilyNoticeSaveReqVO updateReqVO) {
        familyNoticeService.updateFamilyNotice(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除公告")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('family:notice:delete')")
    public CommonResult<Boolean> deleteFamilyNotice(@RequestParam("id") Long id) {
        familyNoticeService.deleteFamilyNotice(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取公告详情")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('family:notice:query')")
    public CommonResult<FamilyNoticeRespVO> getFamilyNotice(@RequestParam("id") Long id) {
        FamilyNoticeDO notice = familyNoticeService.getFamilyNotice(id);
        return success(BeanUtils.toBean(notice, FamilyNoticeRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取公告分页")
    @PreAuthorize("@ss.hasPermission('family:notice:query')")
    public CommonResult<PageResult<FamilyNoticeRespVO>> getFamilyNoticePage(@Valid FamilyNoticePageReqVO pageReqVO) {
        PageResult<FamilyNoticeDO> pageResult = familyNoticeService.getFamilyNoticePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, FamilyNoticeRespVO.class));
    }

    @PutMapping("/publish")
    @Operation(summary = "发布公告")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('family:notice:update')")
    public CommonResult<Boolean> publishNotice(@RequestParam("id") Long id) {
        familyNoticeService.publishNotice(id);
        return success(true);
    }
}
