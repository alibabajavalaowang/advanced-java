package cn.iocoder.yudao.module.family.service.notice;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyNoticePageReqVO;
import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyNoticeSaveReqVO;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyNoticeDO;
import cn.iocoder.yudao.module.family.dal.mysql.FamilyNoticeMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.family.enums.ErrorCodeConstants.*;

@Service
@Validated
public class FamilyNoticeServiceImpl implements FamilyNoticeService {

    @Resource
    private FamilyNoticeMapper familyNoticeMapper;

    @Override
    public Long createFamilyNotice(FamilyNoticeSaveReqVO createReqVO) {
        FamilyNoticeDO notice = BeanUtils.toBean(createReqVO, FamilyNoticeDO.class);
        familyNoticeMapper.insert(notice);
        return notice.getId();
    }

    @Override
    public void updateFamilyNotice(FamilyNoticeSaveReqVO updateReqVO) {
        validateExists(updateReqVO.getId());
        FamilyNoticeDO updateObj = BeanUtils.toBean(updateReqVO, FamilyNoticeDO.class);
        familyNoticeMapper.updateById(updateObj);
    }

    @Override
    public void deleteFamilyNotice(Long id) {
        validateExists(id);
        familyNoticeMapper.deleteById(id);
    }

    private void validateExists(Long id) {
        if (familyNoticeMapper.selectById(id) == null) {
            throw exception(FAMILY_NOTICE_NOT_EXISTS);
        }
    }

    @Override
    public FamilyNoticeDO getFamilyNotice(Long id) {
        return familyNoticeMapper.selectById(id);
    }

    @Override
    public PageResult<FamilyNoticeDO> getFamilyNoticePage(FamilyNoticePageReqVO pageReqVO) {
        return familyNoticeMapper.selectPage(pageReqVO);
    }

    @Override
    public void publishNotice(Long id) {
        FamilyNoticeDO notice = familyNoticeMapper.selectById(id);
        if (notice == null) {
            throw exception(FAMILY_NOTICE_NOT_EXISTS);
        }
        FamilyNoticeDO updateObj = new FamilyNoticeDO();
        updateObj.setId(id);
        updateObj.setStatus(1); // 已发布
        updateObj.setPublishTime(LocalDateTime.now());
        familyNoticeMapper.updateById(updateObj);
    }
}
