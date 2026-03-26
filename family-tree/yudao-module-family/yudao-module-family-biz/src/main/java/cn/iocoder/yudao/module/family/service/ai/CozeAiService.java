package cn.iocoder.yudao.module.family.service.ai;

import cn.iocoder.yudao.module.family.controller.admin.vo.CozeAiReqVO;
import cn.iocoder.yudao.module.family.controller.admin.vo.CozeAiRespVO;

import javax.validation.Valid;

public interface CozeAiService {

    CozeAiRespVO generateContent(@Valid CozeAiReqVO reqVO);
}
