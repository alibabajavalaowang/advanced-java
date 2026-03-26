package cn.iocoder.yudao.module.family.service.event;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyEventPageReqVO;
import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyEventSaveReqVO;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyEventDO;
import cn.iocoder.yudao.module.family.dal.mysql.FamilyEventMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.family.enums.ErrorCodeConstants.*;

@Service
@Validated
public class FamilyEventServiceImpl implements FamilyEventService {

    @Resource
    private FamilyEventMapper familyEventMapper;

    @Override
    public Long createFamilyEvent(FamilyEventSaveReqVO createReqVO) {
        FamilyEventDO event = BeanUtils.toBean(createReqVO, FamilyEventDO.class);
        familyEventMapper.insert(event);
        return event.getId();
    }

    @Override
    public void updateFamilyEvent(FamilyEventSaveReqVO updateReqVO) {
        validateExists(updateReqVO.getId());
        FamilyEventDO updateObj = BeanUtils.toBean(updateReqVO, FamilyEventDO.class);
        familyEventMapper.updateById(updateObj);
    }

    @Override
    public void deleteFamilyEvent(Long id) {
        validateExists(id);
        familyEventMapper.deleteById(id);
    }

    private void validateExists(Long id) {
        if (familyEventMapper.selectById(id) == null) {
            throw exception(FAMILY_EVENT_NOT_EXISTS);
        }
    }

    @Override
    public FamilyEventDO getFamilyEvent(Long id) {
        return familyEventMapper.selectById(id);
    }

    @Override
    public PageResult<FamilyEventDO> getFamilyEventPage(FamilyEventPageReqVO pageReqVO) {
        return familyEventMapper.selectPage(pageReqVO);
    }

    @Override
    public List<FamilyEventDO> getEventListByTreeId(Long treeId) {
        return familyEventMapper.selectListByTreeId(treeId);
    }
}
