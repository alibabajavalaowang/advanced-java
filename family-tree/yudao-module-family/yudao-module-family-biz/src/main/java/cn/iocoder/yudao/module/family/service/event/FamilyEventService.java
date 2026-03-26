package cn.iocoder.yudao.module.family.service.event;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyEventPageReqVO;
import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyEventSaveReqVO;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyEventDO;

import javax.validation.Valid;
import java.util.List;

public interface FamilyEventService {

    Long createFamilyEvent(@Valid FamilyEventSaveReqVO createReqVO);

    void updateFamilyEvent(@Valid FamilyEventSaveReqVO updateReqVO);

    void deleteFamilyEvent(Long id);

    FamilyEventDO getFamilyEvent(Long id);

    PageResult<FamilyEventDO> getFamilyEventPage(FamilyEventPageReqVO pageReqVO);

    List<FamilyEventDO> getEventListByTreeId(Long treeId);
}
