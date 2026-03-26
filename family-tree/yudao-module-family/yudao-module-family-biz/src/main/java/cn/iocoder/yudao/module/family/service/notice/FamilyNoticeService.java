package cn.iocoder.yudao.module.family.service.notice;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyNoticePageReqVO;
import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyNoticeSaveReqVO;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyNoticeDO;

import javax.validation.Valid;

public interface FamilyNoticeService {

    Long createFamilyNotice(@Valid FamilyNoticeSaveReqVO createReqVO);

    void updateFamilyNotice(@Valid FamilyNoticeSaveReqVO updateReqVO);

    void deleteFamilyNotice(Long id);

    FamilyNoticeDO getFamilyNotice(Long id);

    PageResult<FamilyNoticeDO> getFamilyNoticePage(FamilyNoticePageReqVO pageReqVO);

    void publishNotice(Long id);
}
