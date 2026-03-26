package cn.iocoder.yudao.module.family.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.family.controller.admin.vo.CozeAiReqVO;
import cn.iocoder.yudao.module.family.controller.admin.vo.CozeAiRespVO;
import cn.iocoder.yudao.module.family.service.ai.CozeAiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - AI助手")
@RestController
@RequestMapping("/family/ai")
@Validated
public class CozeAiController {

    @Resource
    private CozeAiService cozeAiService;

    @PostMapping("/generate")
    @Operation(summary = "AI内容生成")
    @PreAuthorize("@ss.hasPermission('family:ai:generate')")
    public CommonResult<CozeAiRespVO> generateContent(@Valid @RequestBody CozeAiReqVO reqVO) {
        return success(cozeAiService.generateContent(reqVO));
    }
}
