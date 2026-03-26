package cn.iocoder.yudao.module.family.service.ai;

import cn.iocoder.yudao.module.family.controller.admin.vo.CozeAiReqVO;
import cn.iocoder.yudao.module.family.controller.admin.vo.CozeAiRespVO;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyMemberDO;
import cn.iocoder.yudao.module.family.dal.dataobject.FamilyTreeDO;
import cn.iocoder.yudao.module.family.service.member.FamilyMemberService;
import cn.iocoder.yudao.module.family.service.tree.FamilyTreeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Validated
@Slf4j
public class CozeAiServiceImpl implements CozeAiService {

    @Value("${family.coze.api-key:}")
    private String apiKey;

    @Value("${family.coze.bot-id:}")
    private String botId;

    @Value("${family.coze.api-url:https://api.coze.cn/open_api/v2/chat}")
    private String apiUrl;

    @Resource
    private FamilyTreeService familyTreeService;
    @Resource
    private FamilyMemberService familyMemberService;
    @Resource
    private RestTemplate restTemplate;

    @Override
    public CozeAiRespVO generateContent(CozeAiReqVO reqVO) {
        FamilyTreeDO tree = familyTreeService.getFamilyTree(reqVO.getTreeId());
        List<FamilyMemberDO> members = familyMemberService.getMemberListByTreeId(reqVO.getTreeId());

        // 构建家族上下文信息
        String familyContext = buildFamilyContext(tree, members);

        // 根据类型构建不同的 prompt
        String systemPrompt = buildSystemPrompt(reqVO.getType(), familyContext);
        String userPrompt = reqVO.getPrompt();

        // 调用 Coze API
        String aiContent = callCozeApi(systemPrompt, userPrompt);

        CozeAiRespVO resp = new CozeAiRespVO();
        resp.setContent(aiContent);
        resp.setType(reqVO.getType());
        resp.setTreeId(reqVO.getTreeId());
        return resp;
    }

    private String buildFamilyContext(FamilyTreeDO tree, List<FamilyMemberDO> members) {
        StringBuilder sb = new StringBuilder();
        sb.append("【家族信息】\n");
        sb.append("家谱名称：").append(tree.getName()).append("\n");
        sb.append("姓氏：").append(tree.getSurname()).append("\n");
        if (tree.getHallName() != null) sb.append("堂号：").append(tree.getHallName()).append("\n");
        if (tree.getOrigin() != null) sb.append("郡望：").append(tree.getOrigin()).append("\n");
        if (tree.getAncestorName() != null) sb.append("始祖：").append(tree.getAncestorName()).append("\n");
        if (tree.getMotto() != null) sb.append("家训：").append(tree.getMotto()).append("\n");
        sb.append("成员总数：").append(members.size()).append("人\n");
        sb.append("世代数：").append(tree.getGenerationCount()).append("代\n\n");

        sb.append("【部分成员】\n");
        members.stream().limit(20).forEach(m -> {
            sb.append("- ").append(m.getName());
            if (m.getGeneration() != null) sb.append("(第").append(m.getGeneration()).append("世)");
            if (m.getOccupation() != null) sb.append(" 职业:").append(m.getOccupation());
            if (m.getAchievements() != null) sb.append(" 成就:").append(m.getAchievements());
            sb.append("\n");
        });

        return sb.toString();
    }

    private String buildSystemPrompt(String type, String familyContext) {
        String basePrompt = "你是一位精通中华传统文化和家谱文化的文学大师。以下是一个家族的基本信息：\n\n" + familyContext + "\n\n";

        switch (type != null ? type : "story") {
            case "story":
                return basePrompt + "请根据以上家族信息，用优美的文笔撰写一篇家族传承故事。要求：文风典雅，富有感情，体现家族的传承精神和文化底蕴，适当引用诗词典故。字数800-1200字。";
            case "motto":
                return basePrompt + "请根据这个家族的特点，创作几条家训建议。要求：言简意赅，朗朗上口，符合中华传统美德，每条家训附上简短释义。提供5-8条。";
            case "poem":
                return basePrompt + "请根据这个家族的信息，创作一首关于该家族传承的诗词。可以是七言律诗、五言律诗或者词的形式。要求：意境优美，表达家族的根脉传承和美好愿望。";
            case "summary":
                return basePrompt + "请根据以上信息，撰写一份家族概况总结。要求：条理清晰，涵盖家族渊源、堂号来历、世系传承、人才辈出等方面，500-800字。";
            default:
                return basePrompt + "请根据用户的要求，结合以上家族信息进行创作。";
        }
    }

    private String callCozeApi(String systemPrompt, String userPrompt) {
        if (apiKey == null || apiKey.isEmpty()) {
            // 未配置 API Key 时返回模拟内容
            return generateMockContent(systemPrompt);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("bot_id", botId);
            body.put("user", "family_tree_user");
            body.put("query", userPrompt);
            body.put("stream", false);

            List<Map<String, String>> chatHistory = new ArrayList<>();
            Map<String, String> sysMsg = new HashMap<>();
            sysMsg.put("role", "system");
            sysMsg.put("content", systemPrompt);
            chatHistory.add(sysMsg);
            body.put("chat_history", chatHistory);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode messages = root.path("messages");
            if (messages.isArray()) {
                for (JsonNode msg : messages) {
                    if ("answer".equals(msg.path("type").asText())) {
                        return msg.path("content").asText();
                    }
                }
            }
            return root.path("msg").asText("AI生成失败，请稍后重试");
        } catch (Exception e) {
            log.error("[callCozeApi] 调用 Coze API 异常", e);
            return "AI服务暂时不可用，请检查配置或稍后重试。错误信息：" + e.getMessage();
        }
    }

    private String generateMockContent(String prompt) {
        if (prompt.contains("故事")) {
            return "# 家族传承故事\n\n"
                    + "源远流长，薪火相传。吾族自始祖开基立业以来，历经数代风雨，始终秉承\"耕读传家\"之训，"
                    + "族中子弟勤勉好学，忠厚传家，人才辈出。\n\n"
                    + "始祖创业之初，筚路蓝缕，艰苦卓绝。凭借坚韧不拔之志，终成一方基业。"
                    + "传至后世，子孙繁衍，枝繁叶茂。族人或从政，或经商，或治学，各有所成。\n\n"
                    + "家训曰：\"忠孝仁义，礼智信廉。\"此八字箴言，代代相传，为族中子弟修身立命之本。\n\n"
                    + "> 注：此为 AI 模拟生成内容。请配置 Coze API Key 获取更精准的个性化内容。";
        } else if (prompt.contains("家训")) {
            return "# 家训建议\n\n"
                    + "1. **忠孝为本，仁义为先** — 以忠孝作为做人的根本，仁义作为处事的准则\n"
                    + "2. **耕读传家，诗书继世** — 以农耕读书为传家之道，以诗书教化传承后世\n"
                    + "3. **勤俭持家，和睦兴邦** — 勤劳节俭治理家业，和睦相处兴旺家族\n"
                    + "4. **尊老爱幼，扶危济困** — 尊敬长辈疼爱晚辈，扶助危难接济困苦\n"
                    + "5. **谦虚谨慎，戒骄戒躁** — 保持谦逊谨慎之心，戒除骄傲浮躁之气\n\n"
                    + "> 注：此为 AI 模拟生成内容。配置 Coze API 后可获取基于家族特点的个性化家训。";
        } else if (prompt.contains("诗词")) {
            return "# 家族颂\n\n"
                    + "```\n源远流长百代传，\n祖德宗功耀人间。\n"
                    + "耕读继世家声振，\n忠孝持身族望延。\n"
                    + "堂号千秋昭日月，\n家风万古焕山川。\n"
                    + "子孙繁盛如松柏，\n一脉相承福绵绵。\n```\n\n"
                    + "> 注：此为 AI 模拟生成。配置 Coze API 后将根据家族实际信息创作。";
        } else {
            return "# 家族概况\n\n"
                    + "吾族历史悠久，根脉深远。自始祖以来，代代相传，至今已有数代之久。"
                    + "族中人才辈出，遍布各地，从事各行各业。\n\n"
                    + "家族秉承优良传统，以忠孝仁义为做人之本，以耕读传家为兴族之道。"
                    + "族人团结和睦，互帮互助，共同发展。\n\n"
                    + "> 注：此为 AI 模拟生成。请配置 Coze API Key 以获取详细个性化内容。";
        }
    }
}
