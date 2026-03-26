package cn.iocoder.yudao.module.family.service.media;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyMediaPageReqVO;
import cn.iocoder.yudao.module.family.controller.admin.vo.FamilyMediaSaveReqVO;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyMediaDO;
import cn.iocoder.yudao.module.family.dal.mysql.FamilyMediaMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.family.enums.ErrorCodeConstants.*;

@Service
@Validated
public class FamilyMediaServiceImpl implements FamilyMediaService {

    @Resource
    private FamilyMediaMapper familyMediaMapper;

    @Override
    public Long createFamilyMedia(FamilyMediaSaveReqVO createReqVO) {
        FamilyMediaDO media = BeanUtils.toBean(createReqVO, FamilyMediaDO.class);
        familyMediaMapper.insert(media);
        return media.getId();
    }

    @Override
    public void updateFamilyMedia(FamilyMediaSaveReqVO updateReqVO) {
        validateExists(updateReqVO.getId());
        FamilyMediaDO updateObj = BeanUtils.toBean(updateReqVO, FamilyMediaDO.class);
        familyMediaMapper.updateById(updateObj);
    }

    @Override
    public void deleteFamilyMedia(Long id) {
        validateExists(id);
        familyMediaMapper.deleteById(id);
    }

    private void validateExists(Long id) {
        if (familyMediaMapper.selectById(id) == null) {
            throw exception(FAMILY_MEDIA_NOT_EXISTS);
        }
    }

    @Override
    public FamilyMediaDO getFamilyMedia(Long id) {
        return familyMediaMapper.selectById(id);
    }

    @Override
    public PageResult<FamilyMediaDO> getFamilyMediaPage(FamilyMediaPageReqVO pageReqVO) {
        return familyMediaMapper.selectPage(pageReqVO);
    }
}
