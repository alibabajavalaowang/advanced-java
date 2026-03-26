package cn.iocoder.yudao.module.family.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.family.controller.admin.vo.*;
import cn.iocoder.yudao.module.family.dal.dataobject.GenerationRankDO;
import cn.iocoder.yudao.module.family.service.rank.GenerationRankService;
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

@Tag(name = "管理后台 - 字辈管理")
@RestController
@RequestMapping("/family/generation-rank")
@Validated
public class GenerationRankController {

    @Resource
    private GenerationRankService generationRankService;

    @PostMapping("/create")
    @Operation(summary = "创建字辈")
    @PreAuthorize("@ss.hasPermission('family:generation-rank:create')")
    public CommonResult<Long> createGenerationRank(@Valid @RequestBody GenerationRankSaveReqVO createReqVO) {
        return success(generationRankService.createGenerationRank(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新字辈")
    @PreAuthorize("@ss.hasPermission('family:generation-rank:update')")
    public CommonResult<Boolean> updateGenerationRank(@Valid @RequestBody GenerationRankSaveReqVO updateReqVO) {
        generationRankService.updateGenerationRank(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除字辈")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('family:generation-rank:delete')")
    public CommonResult<Boolean> deleteGenerationRank(@RequestParam("id") Long id) {
        generationRankService.deleteGenerationRank(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取字辈详情")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('family:generation-rank:query')")
    public CommonResult<GenerationRankRespVO> getGenerationRank(@RequestParam("id") Long id) {
        GenerationRankDO rank = generationRankService.getGenerationRank(id);
        return success(BeanUtils.toBean(rank, GenerationRankRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取字辈分页")
    @PreAuthorize("@ss.hasPermission('family:generation-rank:query')")
    public CommonResult<PageResult<GenerationRankRespVO>> getGenerationRankPage(@Valid GenerationRankPageReqVO pageReqVO) {
        PageResult<GenerationRankDO> pageResult = generationRankService.getGenerationRankPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, GenerationRankRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获取家谱字辈列表")
    @Parameter(name = "treeId", description = "家谱编号", required = true)
    public CommonResult<List<GenerationRankRespVO>> getRankListByTreeId(@RequestParam("treeId") Long treeId) {
        List<GenerationRankDO> list = generationRankService.getRankListByTreeId(treeId);
        return success(BeanUtils.toBean(list, GenerationRankRespVO.class));
    }
}
