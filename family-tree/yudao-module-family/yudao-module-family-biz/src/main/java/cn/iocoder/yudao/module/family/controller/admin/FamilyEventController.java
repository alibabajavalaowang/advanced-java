package cn.iocoder.yudao.module.family.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.family.controller.admin.vo.*;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyEventDO;
import cn.iocoder.yudao.module.family.service.event.FamilyEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 家族大事记")
@RestController
@RequestMapping("/family/event")
@Validated
public class FamilyEventController {

    @Resource
    private FamilyEventService familyEventService;

    @PostMapping("/create")
    @Operation(summary = "创建大事记")
    @PreAuthorize("@ss.hasPermission('family:event:create')")
    public CommonResult<Long> createFamilyEvent(@Valid @RequestBody FamilyEventSaveReqVO createReqVO) {
        return success(familyEventService.createFamilyEvent(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新大事记")
    @PreAuthorize("@ss.hasPermission('family:event:update')")
    public CommonResult<Boolean> updateFamilyEvent(@Valid @RequestBody FamilyEventSaveReqVO updateReqVO) {
        familyEventService.updateFamilyEvent(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除大事记")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('family:event:delete')")
    public CommonResult<Boolean> deleteFamilyEvent(@RequestParam("id") Long id) {
        familyEventService.deleteFamilyEvent(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取大事记详情")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('family:event:query')")
    public CommonResult<FamilyEventRespVO> getFamilyEvent(@RequestParam("id") Long id) {
        FamilyEventDO event = familyEventService.getFamilyEvent(id);
        return success(BeanUtils.toBean(event, FamilyEventRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取大事记分页")
    @PreAuthorize("@ss.hasPermission('family:event:query')")
    public CommonResult<PageResult<FamilyEventRespVO>> getFamilyEventPage(@Valid FamilyEventPageReqVO pageReqVO) {
        PageResult<FamilyEventDO> pageResult = familyEventService.getFamilyEventPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, FamilyEventRespVO.class));
    }
}
