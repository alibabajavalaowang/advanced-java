package cn.iocoder.yudao.module.family.service.media;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyMediaPageReqVO;
import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyMediaSaveReqVO;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyMediaDO;

import javax.validation.Valid;

public interface FamilyMediaService {

    Long createFamilyMedia(@Valid FamilyMediaSaveReqVO createReqVO);

    void updateFamilyMedia(@Valid FamilyMediaSaveReqVO updateReqVO);

    void deleteFamilyMedia(Long id);

    FamilyMediaDO getFamilyMedia(Long id);

    PageResult<FamilyMediaDO> getFamilyMediaPage(FamilyMediaPageReqVO pageReqVO);
}
