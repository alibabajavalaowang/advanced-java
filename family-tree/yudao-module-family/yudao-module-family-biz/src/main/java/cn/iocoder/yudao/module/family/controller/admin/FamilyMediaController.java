package cn.iocoder.yudao.module.family.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.family.controller.admin.vo.*;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyMediaDO;
import cn.iocoder.yudao.module.family.service.media.FamilyMediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 家族相册")
@RestController
@RequestMapping("/family/media")
@Validated
public class FamilyMediaController {

    @Resource
    private FamilyMediaService familyMediaService;

    @PostMapping("/create")
    @Operation(summary = "创建媒体记录")
    @PreAuthorize("@ss.hasPermission('family:media:create')")
    public CommonResult<Long> createFamilyMedia(@Valid @RequestBody FamilyMediaSaveReqVO createReqVO) {
        return success(familyMediaService.createFamilyMedia(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新媒体记录")
    @PreAuthorize("@ss.hasPermission('family:media:update')")
    public CommonResult<Boolean> updateFamilyMedia(@Valid @RequestBody FamilyMediaSaveReqVO updateReqVO) {
        familyMediaService.updateFamilyMedia(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除媒体记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('family:media:delete')")
    public CommonResult<Boolean> deleteFamilyMedia(@RequestParam("id") Long id) {
        familyMediaService.deleteFamilyMedia(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取媒体详情")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('family:media:query')")
    public CommonResult<FamilyMediaRespVO> getFamilyMedia(@RequestParam("id") Long id) {
        FamilyMediaDO media = familyMediaService.getFamilyMedia(id);
        return success(BeanUtils.toBean(media, FamilyMediaRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取媒体分页")
    @PreAuthorize("@ss.hasPermission('family:media:query')")
    public CommonResult<PageResult<FamilyMediaRespVO>> getFamilyMediaPage(@Valid FamilyMediaPageReqVO pageReqVO) {
        PageResult<FamilyMediaDO> pageResult = familyMediaService.getFamilyMediaPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, FamilyMediaRespVO.class));
    }
}
