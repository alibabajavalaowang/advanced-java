package cn.iocoder.yudao.module.family.service.member;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.family.controller.admin.vo.*;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyEventDO;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyMemberDO;
import cn.iocoder.yudao.module.family.dal.mysql.FamilyEventMapper;
import cn.iocoder.yudao.module.family.dal.mysql.FamilyMemberMapper;
import cn.iocoder.yudao.module.family.service.tree.FamilyTreeService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.family.enums.ErrorCodeConstants.*;

@Service
@Validated
public class FamilyMemberServiceImpl implements FamilyMemberService {

    @Resource
    private FamilyMemberMapper familyMemberMapper;
    @Resource
    private FamilyEventMapper familyEventMapper;
    @Resource
    @Lazy
    private FamilyTreeService familyTreeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createFamilyMember(FamilyMemberSaveReqVO createReqVO) {
        FamilyMemberDO member = BeanUtils.toBean(createReqVO, FamilyMemberDO.class);
        familyMemberMapper.insert(member);
        // 更新家谱成员计数
        familyTreeService.updateMemberCount(member.getTreeId());
        return member.getId();
    }

    @Override
    public void updateFamilyMember(FamilyMemberSaveReqVO updateReqVO) {
        validateFamilyMemberExists(updateReqVO.getId());
        FamilyMemberDO updateObj = BeanUtils.toBean(updateReqVO, FamilyMemberDO.class);
        familyMemberMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFamilyMember(Long id) {
        FamilyMemberDO member = validateFamilyMemberExists(id);
        // 校验是否有子女
        List<FamilyMemberDO> children = familyMemberMapper.selectListByParentId(id);
        if (!children.isEmpty()) {
            throw exception(FAMILY_MEMBER_HAS_CHILDREN);
        }
        familyMemberMapper.deleteById(id);
        familyTreeService.updateMemberCount(member.getTreeId());
    }

    private FamilyMemberDO validateFamilyMemberExists(Long id) {
        FamilyMemberDO member = familyMemberMapper.selectById(id);
        if (member == null) {
            throw exception(FAMILY_MEMBER_NOT_EXISTS);
        }
        return member;
    }

    @Override
    public FamilyMemberDO getFamilyMember(Long id) {
        return familyMemberMapper.selectById(id);
    }

    @Override
    public PageResult<FamilyMemberDO> getFamilyMemberPage(FamilyMemberPageReqVO pageReqVO) {
        return familyMemberMapper.selectPage(pageReqVO);
    }

    @Override
    public List<FamilyMemberDO> getMemberListByTreeId(Long treeId) {
        return familyMemberMapper.selectListByTreeId(treeId);
    }

    /**
     * 构建家谱树（核心算法）
     * 1. 查询所有成员
     * 2. 建立 parentId -> children 映射
     * 3. 找根节点（parentId 为 null）
     * 4. 递归构建树
     * 5. 附加配偶信息
     */
    @Override
    public List<FamilyMemberTreeVO> getMemberTree(Long treeId) {
        List<FamilyMemberDO> allMembers = familyMemberMapper.selectListByTreeId(treeId);
        if (allMembers.isEmpty()) {
            return List.of();
        }

        // 建立 ID -> Member 映射
        Map<Long, FamilyMemberDO> memberMap = allMembers.stream()
                .collect(Collectors.toMap(FamilyMemberDO::getId, m -> m));

        // 建立 parentId -> children 映射
        Map<Long, List<FamilyMemberDO>> childrenMap = allMembers.stream()
                .filter(m -> m.getParentId() != null)
                .collect(Collectors.groupingBy(FamilyMemberDO::getParentId));

        // 找根节点（parentId 为 null 的成员）
        List<FamilyMemberDO> roots = allMembers.stream()
                .filter(m -> m.getParentId() == null)
                .sorted(Comparator.comparingInt(FamilyMemberDO::getSortOrder))
                .collect(Collectors.toList());

        // 递归构建树
        return roots.stream()
                .map(root -> buildTreeNode(root, childrenMap, memberMap))
                .collect(Collectors.toList());
    }

    private FamilyMemberTreeVO buildTreeNode(FamilyMemberDO member,
                                              Map<Long, List<FamilyMemberDO>> childrenMap,
                                              Map<Long, FamilyMemberDO> memberMap) {
        FamilyMemberTreeVO node = new FamilyMemberTreeVO();
        node.setId(member.getId());
        node.setName(member.getName());
        node.setGender(member.getGender());
        node.setBirthDate(member.getBirthDate() != null ? member.getBirthDate().toString() : null);
        node.setDeathDate(member.getDeathDate() != null ? member.getDeathDate().toString() : null);
        node.setIsAlive(member.getIsAlive() != null && member.getIsAlive() == 1);
        node.setGeneration(member.getGeneration());
        node.setGenerationName(member.getGenerationName());
        node.setAvatar(member.getAvatar());
        node.setOccupation(member.getOccupation());
        node.setParentId(member.getParentId());
        node.setSpouseId(member.getSpouseId());

        // 配偶信息
        if (member.getSpouseId() != null) {
            FamilyMemberDO spouse = memberMap.get(member.getSpouseId());
            if (spouse != null) {
                node.setSpouseName(spouse.getName());
                node.setSpouseGender(spouse.getGender());
                node.setSpouseAvatar(spouse.getAvatar());
            }
        }

        // 递归构建子节点
        List<FamilyMemberDO> children = childrenMap.getOrDefault(member.getId(), List.of());
        if (!children.isEmpty()) {
            List<FamilyMemberTreeVO> childNodes = children.stream()
                    .sorted(Comparator.comparingInt(FamilyMemberDO::getSortOrder))
                    .map(child -> buildTreeNode(child, childrenMap, memberMap))
                    .collect(Collectors.toList());
            node.setChildren(childNodes);
        }

        return node;
    }

    /**
     * 家族统计数据
     */
    @Override
    public FamilyTreeStatsVO getTreeStats(Long treeId) {
        List<FamilyMemberDO> members = familyMemberMapper.selectListByTreeId(treeId);
        FamilyTreeStatsVO stats = new FamilyTreeStatsVO();

        stats.setTotalMembers(members.size());
        stats.setAliveMembers((int) members.stream().filter(m -> m.getIsAlive() != null && m.getIsAlive() == 1).count());
        stats.setMaleCount((int) members.stream().filter(m -> m.getGender() != null && m.getGender() == 1).count());
        stats.setFemaleCount((int) members.stream().filter(m -> m.getGender() != null && m.getGender() == 0).count());

        // 世代数
        OptionalInt maxGen = members.stream()
                .filter(m -> m.getGeneration() != null)
                .mapToInt(FamilyMemberDO::getGeneration)
                .max();
        stats.setGenerationCount(maxGen.orElse(0));

        // 平均年龄
        LocalDate now = LocalDate.now();
        List<Integer> ages = members.stream()
                .filter(m -> m.getBirthDate() != null && m.getIsAlive() != null && m.getIsAlive() == 1)
                .map(m -> Period.between(m.getBirthDate(), now).getYears())
                .collect(Collectors.toList());
        stats.setAverageAge(ages.isEmpty() ? 0.0 : ages.stream().mapToInt(Integer::intValue).average().orElse(0));

        // 各代人数分布
        Map<String, Integer> genDist = new LinkedHashMap<>();
        members.stream()
                .filter(m -> m.getGeneration() != null)
                .collect(Collectors.groupingBy(m -> "第" + m.getGeneration() + "世", Collectors.counting()))
                .forEach((k, v) -> genDist.put(k, v.intValue()));
        stats.setGenerationDistribution(genDist);

        // 地域分布（从 residence 提取省份）
        Map<String, Integer> provinceDist = new LinkedHashMap<>();
        members.stream()
                .filter(m -> m.getResidence() != null && !m.getResidence().isEmpty())
                .map(m -> extractProvince(m.getResidence()))
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()))
                .forEach((k, v) -> provinceDist.put(k, v.intValue()));
        stats.setProvinceDistribution(provinceDist);

        // 年龄段分布
        Map<String, Integer> ageDist = new LinkedHashMap<>();
        ageDist.put("0-18岁", 0);
        ageDist.put("19-35岁", 0);
        ageDist.put("36-55岁", 0);
        ageDist.put("56-75岁", 0);
        ageDist.put("75岁以上", 0);
        for (Integer age : ages) {
            if (age <= 18) ageDist.merge("0-18岁", 1, Integer::sum);
            else if (age <= 35) ageDist.merge("19-35岁", 1, Integer::sum);
            else if (age <= 55) ageDist.merge("36-55岁", 1, Integer::sum);
            else if (age <= 75) ageDist.merge("56-75岁", 1, Integer::sum);
            else ageDist.merge("75岁以上", 1, Integer::sum);
        }
        stats.setAgeDistribution(ageDist);

        // 最近事件
        List<FamilyEventDO> events = familyEventMapper.selectListByTreeId(treeId);
        List<FamilyEventRespVO> recentEvents = events.stream()
                .sorted(Comparator.comparing(FamilyEventDO::getEventDate).reversed())
                .limit(5)
                .map(e -> BeanUtils.toBean(e, FamilyEventRespVO.class))
                .collect(Collectors.toList());
        stats.setRecentEvents(recentEvents);

        return stats;
    }

    private String extractProvince(String residence) {
        String[] provinces = {"北京", "天津", "上海", "重庆", "河北", "山西", "辽宁", "吉林",
                "黑龙江", "江苏", "浙江", "安徽", "福建", "江西", "山东", "河南", "湖北",
                "湖南", "广东", "海南", "四川", "贵州", "云南", "陕西", "甘肃", "青海",
                "台湾", "内蒙古", "广西", "西藏", "宁夏", "新疆", "香港", "澳门"};
        for (String province : provinces) {
            if (residence.contains(province)) {
                return province;
            }
        }
        return "其他";
    }

    @Override
    public List<FamilyMemberDO> getChildrenByParentId(Long parentId) {
        return familyMemberMapper.selectListByParentId(parentId);
    }
}
