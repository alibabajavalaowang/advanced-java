package com.family;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 家谱管理系统 - 独立演示服务器
 * 零依赖，使用 JDK 内置 HTTP 服务器 + 内存数据
 * 启动后访问 http://localhost:8080
 */
public class FamilyTreeServer {

    // ==================== 数据模型 ====================
    record FamilyTree(long id, String name, String surname, String hallName, String origin,
                      String motto, String clanRules, String description,
                      String ancestorName, String ancestorStory, int memberCount, int generationCount) {}

    record Member(long id, long treeId, String name, int gender, String birthDate, String deathDate,
                  int isAlive, int generation, String generationName, String birthplace,
                  String residence, String occupation, String biography,
                  Long parentId, Long spouseId, int sortOrder) {}

    record Event(long id, long treeId, Long memberId, String title, String content,
                 String eventDate, int eventType, int importance) {}

    record Rank(long id, long treeId, int rankOrder, String rankChar, String description) {}

    // ==================== 内存数据库 ====================
    static List<FamilyTree> trees = new ArrayList<>();
    static List<Member> members = new ArrayList<>();
    static List<Event> events = new ArrayList<>();
    static List<Rank> ranks = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        initDemoData();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/tree/list", ex -> json(ex, treesJson()));
        server.createContext("/api/tree/", ex -> json(ex, treeDetailJson(ex.getRequestURI().getPath())));
        server.createContext("/api/member/list", ex -> json(ex, membersJson(param(ex, "treeId"))));
        server.createContext("/api/member/tree", ex -> json(ex, memberTreeJson(param(ex, "treeId"))));
        server.createContext("/api/event/list", ex -> json(ex, eventsJson(param(ex, "treeId"))));
        server.createContext("/api/rank/list", ex -> json(ex, ranksJson(param(ex, "treeId"))));
        server.createContext("/", ex -> serveStatic(ex));
        server.setExecutor(null);
        server.start();

        System.out.println("==========================================================");
        System.out.println("  家谱管理系统 Demo 已启动!");
        System.out.println("  访问地址: http://localhost:8080");
        System.out.println("  API 文档: http://localhost:8080/api/tree/list");
        System.out.println("==========================================================");
    }

    // ==================== HTTP 工具 ====================
    static String param(HttpExchange ex, String name) {
        String query = ex.getRequestURI().getQuery();
        if (query == null) return "";
        for (String p : query.split("&")) {
            String[] kv = p.split("=");
            if (kv.length == 2 && kv[0].equals(name)) return kv[1];
        }
        return "";
    }

    static void json(HttpExchange ex, String body) throws IOException {
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(200, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.close();
    }

    static void serveStatic(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        if (path.equals("/")) path = "/index.html";

        // 尝试从 classpath 读取
        InputStream is = FamilyTreeServer.class.getResourceAsStream("/static" + path);
        if (is == null) {
            // 尝试从文件系统读取
            Path filePath = Path.of("src/main/resources/static" + path);
            if (Files.exists(filePath)) {
                is = Files.newInputStream(filePath);
            }
        }

        if (is != null) {
            byte[] bytes = is.readAllBytes();
            is.close();
            String contentType = path.endsWith(".html") ? "text/html; charset=utf-8"
                    : path.endsWith(".js") ? "application/javascript"
                    : path.endsWith(".css") ? "text/css"
                    : "application/octet-stream";
            ex.getResponseHeaders().set("Content-Type", contentType);
            ex.sendResponseHeaders(200, bytes.length);
            ex.getResponseBody().write(bytes);
        } else {
            String msg = "Not Found";
            ex.sendResponseHeaders(404, msg.length());
            ex.getResponseBody().write(msg.getBytes());
        }
        ex.close();
    }

    // ==================== JSON 序列化（手写，零依赖） ====================
    static String esc(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "") + "\"";
    }

    static String treesJson() {
        return wrap(trees.stream().map(t -> String.format(
                "{\"id\":%d,\"name\":%s,\"surname\":%s,\"hallName\":%s,\"origin\":%s,\"motto\":%s,\"clanRules\":%s,\"description\":%s,\"ancestorName\":%s,\"ancestorStory\":%s,\"memberCount\":%d,\"generationCount\":%d}",
                t.id, esc(t.name), esc(t.surname), esc(t.hallName), esc(t.origin), esc(t.motto), esc(t.clanRules), esc(t.description), esc(t.ancestorName), esc(t.ancestorStory), t.memberCount, t.generationCount
        )).collect(Collectors.joining(",", "[", "]")));
    }

    static String treeDetailJson(String path) {
        // /api/tree/1/stats or /api/tree/1
        String[] parts = path.split("/");
        long id = 1;
        for (String p : parts) { try { id = Long.parseLong(p); break; } catch (Exception e) {} }
        boolean isStats = path.contains("stats");

        if (isStats) return wrap(statsJson(id));

        long fid = id;
        return trees.stream().filter(t -> t.id == fid).findFirst()
                .map(t -> wrap(String.format(
                        "{\"id\":%d,\"name\":%s,\"surname\":%s,\"hallName\":%s,\"origin\":%s,\"motto\":%s,\"clanRules\":%s,\"description\":%s,\"ancestorName\":%s,\"ancestorStory\":%s,\"memberCount\":%d,\"generationCount\":%d}",
                        t.id, esc(t.name), esc(t.surname), esc(t.hallName), esc(t.origin), esc(t.motto), esc(t.clanRules), esc(t.description), esc(t.ancestorName), esc(t.ancestorStory), t.memberCount, t.generationCount
                ))).orElse("{\"code\":200,\"data\":null}");
    }

    static String memberJson(Member m) {
        return String.format(
                "{\"id\":%d,\"treeId\":%d,\"name\":%s,\"gender\":%d,\"birthDate\":%s,\"deathDate\":%s,\"isAlive\":%d,\"generation\":%d,\"generationName\":%s,\"birthplace\":%s,\"residence\":%s,\"occupation\":%s,\"biography\":%s,\"parentId\":%s,\"spouseId\":%s,\"sortOrder\":%d}",
                m.id, m.treeId, esc(m.name), m.gender, esc(m.birthDate), esc(m.deathDate), m.isAlive, m.generation, esc(m.generationName), esc(m.birthplace), esc(m.residence), esc(m.occupation), esc(m.biography),
                m.parentId != null ? m.parentId : "null", m.spouseId != null ? m.spouseId : "null", m.sortOrder);
    }

    static String membersJson(String treeIdStr) {
        long treeId = parseLong(treeIdStr, 1);
        return wrap(members.stream().filter(m -> m.treeId == treeId)
                .sorted(Comparator.comparingInt(Member::generation).thenComparingInt(Member::sortOrder))
                .map(FamilyTreeServer::memberJson)
                .collect(Collectors.joining(",", "[", "]")));
    }

    // ==================== 家谱树构建 ====================
    static String memberTreeJson(String treeIdStr) {
        long treeId = parseLong(treeIdStr, 1);
        List<Member> all = members.stream().filter(m -> m.treeId == treeId).toList();
        Map<Long, Member> memberMap = all.stream().collect(Collectors.toMap(Member::id, m -> m));
        Map<Long, List<Member>> childrenMap = all.stream().filter(m -> m.parentId != null)
                .collect(Collectors.groupingBy(Member::parentId));
        Set<Long> spouseIds = all.stream().map(Member::spouseId).filter(Objects::nonNull).collect(Collectors.toSet());

        List<Member> roots = all.stream()
                .filter(m -> m.parentId == null && !spouseIds.contains(m.id))
                .sorted(Comparator.comparingInt(Member::sortOrder))
                .toList();

        String result = roots.stream()
                .map(r -> buildTreeNodeJson(r, childrenMap, memberMap))
                .collect(Collectors.joining(",", "[", "]"));
        return wrap(result);
    }

    static String buildTreeNodeJson(Member m, Map<Long, List<Member>> childrenMap, Map<Long, Member> memberMap) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"id\":").append(m.id);
        sb.append(",\"name\":").append(esc(m.name));
        sb.append(",\"gender\":").append(m.gender);
        sb.append(",\"birthDate\":").append(esc(m.birthDate));
        sb.append(",\"deathDate\":").append(esc(m.deathDate));
        sb.append(",\"alive\":").append(m.isAlive == 1);
        sb.append(",\"generation\":").append(m.generation);
        sb.append(",\"generationName\":").append(esc(m.generationName));
        sb.append(",\"occupation\":").append(esc(m.occupation));
        sb.append(",\"residence\":").append(esc(m.residence));
        sb.append(",\"parentId\":").append(m.parentId != null ? m.parentId : "null");
        sb.append(",\"spouseId\":").append(m.spouseId != null ? m.spouseId : "null");

        if (m.spouseId != null && memberMap.containsKey(m.spouseId)) {
            Member spouse = memberMap.get(m.spouseId);
            sb.append(",\"spouseName\":").append(esc(spouse.name));
            sb.append(",\"spouseGender\":").append(spouse.gender);
            sb.append(",\"spouseOccupation\":").append(esc(spouse.occupation));
        }

        List<Member> children = new ArrayList<>(childrenMap.getOrDefault(m.id, List.of()));
        Set<Long> childIds = children.stream().map(Member::id).collect(Collectors.toSet());
        if (m.spouseId != null) {
            childrenMap.getOrDefault(m.spouseId, List.of()).stream()
                    .filter(c -> !childIds.contains(c.id))
                    .forEach(children::add);
        }

        Set<Long> allSpouseIds = memberMap.values().stream().map(Member::spouseId).filter(Objects::nonNull).collect(Collectors.toSet());
        String childrenJson = children.stream()
                .filter(c -> !allSpouseIds.contains(c.id))
                .sorted(Comparator.comparingInt(Member::sortOrder))
                .map(c -> buildTreeNodeJson(c, childrenMap, memberMap))
                .collect(Collectors.joining(",", "[", "]"));
        sb.append(",\"children\":").append(childrenJson);
        sb.append("}");
        return sb.toString();
    }

    // ==================== 统计 ====================
    static String statsJson(long treeId) {
        List<Member> all = members.stream().filter(m -> m.treeId == treeId).toList();
        int total = all.size();
        int alive = (int) all.stream().filter(m -> m.isAlive == 1).count();
        int male = (int) all.stream().filter(m -> m.gender == 1).count();
        int female = (int) all.stream().filter(m -> m.gender == 0).count();

        Map<String, Integer> genDist = new LinkedHashMap<>();
        all.stream().collect(Collectors.groupingBy(m -> "第" + m.generation + "世", Collectors.summingInt(m -> 1)))
                .entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e -> genDist.put(e.getKey(), e.getValue()));

        Map<String, Integer> regionDist = new LinkedHashMap<>();
        all.stream().filter(m -> m.residence != null && !m.residence.isEmpty())
                .collect(Collectors.groupingBy(m -> {
                    for (String p : new String[]{"北京","江苏","浙江","安徽","甘肃"}) {
                        if (m.residence.contains(p)) return p;
                    }
                    return m.residence.length() > 4 ? m.residence.substring(0,4) : m.residence;
                }, Collectors.summingInt(m -> 1)))
                .forEach(regionDist::put);

        Map<String, Integer> occDist = new LinkedHashMap<>();
        all.stream().filter(m -> m.occupation != null && !m.occupation.isEmpty())
                .collect(Collectors.groupingBy(Member::occupation, Collectors.summingInt(m -> 1)))
                .forEach(occDist::put);

        List<Event> evts = events.stream().filter(e -> e.treeId == treeId)
                .sorted(Comparator.comparing(Event::eventDate).reversed()).limit(10).toList();

        String eventsStr = evts.stream().map(e -> String.format(
                "{\"id\":%d,\"title\":%s,\"content\":%s,\"eventDate\":%s,\"eventType\":%d,\"importance\":%d}",
                e.id, esc(e.title), esc(e.content), esc(e.eventDate), e.eventType, e.importance
        )).collect(Collectors.joining(",", "[", "]"));

        return String.format(
                "{\"totalMembers\":%d,\"aliveMembers\":%d,\"deceasedMembers\":%d,\"maleCount\":%d,\"femaleCount\":%d,\"generationCount\":%d,\"generationDistribution\":%s,\"regionDistribution\":%s,\"occupationDistribution\":%s,\"recentEvents\":%s}",
                total, alive, total - alive, male, female, genDist.size(), mapToJson(genDist), mapToJson(regionDist), mapToJson(occDist), eventsStr);
    }

    static String mapToJson(Map<String, Integer> map) {
        return map.entrySet().stream()
                .map(e -> esc(e.getKey()) + ":" + e.getValue())
                .collect(Collectors.joining(",", "{", "}"));
    }

    static String eventsJson(String treeIdStr) {
        long treeId = parseLong(treeIdStr, 1);
        return wrap(events.stream().filter(e -> e.treeId == treeId)
                .sorted(Comparator.comparing(Event::eventDate).reversed())
                .map(e -> String.format(
                        "{\"id\":%d,\"treeId\":%d,\"memberId\":%s,\"title\":%s,\"content\":%s,\"eventDate\":%s,\"eventType\":%d,\"importance\":%d}",
                        e.id, e.treeId, e.memberId != null ? e.memberId : "null", esc(e.title), esc(e.content), esc(e.eventDate), e.eventType, e.importance))
                .collect(Collectors.joining(",", "[", "]")));
    }

    static String ranksJson(String treeIdStr) {
        long treeId = parseLong(treeIdStr, 1);
        return wrap(ranks.stream().filter(r -> r.treeId == treeId)
                .sorted(Comparator.comparingInt(Rank::rankOrder))
                .map(r -> String.format("{\"id\":%d,\"treeId\":%d,\"rankOrder\":%d,\"rankChar\":%s,\"description\":%s}",
                        r.id, r.treeId, r.rankOrder, esc(r.rankChar), esc(r.description)))
                .collect(Collectors.joining(",", "[", "]")));
    }

    static String wrap(String data) { return "{\"code\":200,\"message\":\"success\",\"data\":" + data + "}"; }
    static long parseLong(String s, long def) { try { return Long.parseLong(s); } catch (Exception e) { return def; } }

    // ==================== 示例数据 ====================
    static void initDemoData() {
        trees.add(new FamilyTree(1, "赵氏家谱", "赵", "百忍堂", "甘肃天水",
                "忠孝传家久，诗书继世长。勤俭持家远，和睦万事兴。",
                "一、尊祖敬宗，孝顺父母\n二、兄友弟恭，和睦邻里\n三、勤读诗书，耕读传家\n四、克勤克俭，不事奢华\n五、诚实守信，乐善好施",
                "赵氏一族源自天水，始祖赵德公于明洪武年间迁居江南，历经六百余年，繁衍至今已逾二十余世。族人秉承祖训，耕读传家，人才辈出，为国为民多有建树。",
                "赵德公",
                "始祖赵德公，字润之，原籍甘肃天水。明洪武三年（1370年），奉诏南迁，携家眷定居于江苏南京。",
                22, 5));

        // 第一世
        members.add(new Member(1, 1, "赵德公", 1, "1340-03-15", "1420-11-08", 0, 1, "德", "甘肃天水", "江苏南京", "乡绅", "始祖德公，明初南迁江南，开基创业，为赵氏南方一脉之始。", null, 2L, 0));
        members.add(new Member(2, 1, "李氏", 0, "1342-06-20", "1418-09-12", 0, 1, null, "江苏南京", "江苏南京", null, null, null, null, 0));
        // 第二世
        members.add(new Member(3, 1, "赵义忠", 1, "1368-02-10", "1445-07-22", 0, 2, "义", "江苏南京", "江苏南京", "县丞", "义忠公，德公长子，少年聪颖，中举后任县丞，为官清廉。", 1L, 4L, 0));
        members.add(new Member(4, 1, "王氏", 0, "1370-08-05", "1442-03-18", 0, 2, null, "江苏南京", "江苏南京", null, null, null, null, 0));
        members.add(new Member(5, 1, "赵义信", 1, "1372-09-18", "1450-12-03", 0, 2, "义", "江苏南京", "浙江杭州", "商人", "义信公，德公次子，经商有道，在杭州开设丝绸铺。", 1L, 6L, 1));
        members.add(new Member(6, 1, "张氏", 0, "1374-04-12", "1455-08-20", 0, 2, null, "浙江杭州", "浙江杭州", null, null, null, null, 0));
        // 第三世
        members.add(new Member(7, 1, "赵礼文", 1, "1395-05-20", "1478-10-15", 0, 3, "礼", "江苏南京", "江苏南京", "教书先生", "礼文公，义忠公长子，饱读诗书，一生教书育人，桃李满天下。", 3L, 8L, 0));
        members.add(new Member(8, 1, "陈氏", 0, "1398-03-08", "1475-06-30", 0, 3, null, "江苏南京", "江苏南京", null, null, null, null, 0));
        members.add(new Member(9, 1, "赵礼武", 1, "1398-11-03", "1480-04-25", 0, 3, "礼", "江苏南京", "安徽合肥", "武官", "礼武公，义忠公次子，从军报国，官至千户。", 3L, 10L, 1));
        members.add(new Member(10, 1, "刘氏", 0, "1400-07-14", "1482-11-20", 0, 3, null, "安徽合肥", "安徽合肥", null, null, null, null, 0));
        members.add(new Member(11, 1, "赵礼商", 1, "1400-01-25", "1485-09-10", 0, 3, "礼", "浙江杭州", "浙江杭州", "丝绸商人", "礼商公，义信公之子，继承父业，将丝绸生意做到苏州、扬州。", 5L, 12L, 0));
        members.add(new Member(12, 1, "周氏", 0, "1402-12-08", "1488-05-15", 0, 3, null, "浙江杭州", "浙江杭州", null, null, null, null, 0));
        // 第四世
        members.add(new Member(13, 1, "赵智远", 1, "1425-08-12", "1510-02-28", 0, 4, "智", "江苏南京", "江苏南京", "进士/知府", "智远公，礼文公长子，明正统年间中进士，官至知府。", 7L, 14L, 0));
        members.add(new Member(14, 1, "孙氏", 0, "1428-04-18", "1508-10-05", 0, 4, null, "江苏南京", "江苏南京", null, null, null, null, 0));
        members.add(new Member(15, 1, "赵智明", 1, "1428-12-05", "1505-06-18", 0, 4, "智", "江苏南京", "江苏南京", "医者", "智明公，礼文公次子，精通岐黄之术，悬壶济世。", 7L, 16L, 1));
        members.add(new Member(16, 1, "马氏", 0, "1430-09-22", "1502-12-10", 0, 4, null, "江苏南京", "江苏南京", null, null, null, null, 0));
        members.add(new Member(17, 1, "赵智勇", 1, "1430-06-30", "1512-08-14", 0, 4, "智", "安徽合肥", "安徽合肥", "武举人", "智勇公，礼武公之子，继承父志从军，中武举。", 9L, null, 0));
        // 第五世
        members.add(new Member(18, 1, "赵信达", 1, "1455-03-08", "1540-11-20", 0, 5, "信", "江苏南京", "北京", "翰林院编修", "信达公，智远公长子，才华横溢，入翰林院编修国史。", 13L, 19L, 0));
        members.add(new Member(19, 1, "黄氏", 0, "1458-07-15", "1538-04-25", 0, 5, null, "北京", "北京", null, null, null, null, 0));
        members.add(new Member(20, 1, "赵信义", 1, "1458-10-22", "1535-05-08", 0, 5, "信", "江苏南京", "江苏南京", "教书先生", "信义公，智远公次子，继承祖父遗风，教书育人。", 13L, null, 1));
        members.add(new Member(21, 1, "赵信和", 1, "1460-04-18", "1542-09-30", 0, 5, "信", "江苏南京", "浙江杭州", "药铺掌柜", "信和公，智明公之子，将医术与经商结合，开设药铺。", 15L, 22L, 0));
        members.add(new Member(22, 1, "吴氏", 0, "1462-11-05", "1540-07-18", 0, 5, null, "浙江杭州", "浙江杭州", null, null, null, null, 0));

        // 大事记
        events.add(new Event(1, 1, 1L, "始祖南迁", "始祖赵德公奉诏从甘肃天水南迁至江苏南京，开创赵氏南方基业。", "1370-03-15", 4, 2));
        events.add(new Event(2, 1, 3L, "义忠公中举", "二世祖赵义忠参加乡试，高中举人，后任县丞。", "1390-09-01", 5, 1));
        events.add(new Event(3, 1, 13L, "智远公中进士", "四世祖赵智远于明正统年间高中进士，官至知府，光耀门楣。", "1450-03-20", 5, 2));
        events.add(new Event(4, 1, 18L, "信达公入翰林", "五世祖赵信达入翰林院任编修，参与编修国史，为族中最高文职。", "1480-06-15", 7, 2));
        events.add(new Event(5, 1, 11L, "礼商公扩业", "三世赵礼商将丝绸生意扩展至苏州、扬州，赵氏商号名扬江南。", "1435-08-10", 6, 1));
        events.add(new Event(6, 1, null, "修建赵氏宗祠", "赵氏族人集资在南京修建宗祠，供奉列祖列宗，每年春秋两祭。", "1460-10-01", 0, 2));

        // 字辈
        String[] chars = {"德","义","礼","智","信","温","良","恭","俭","让"};
        String[] descs = {"以德立身，厚德载物","义薄云天，见义勇为","知书达礼，礼贤下士","智勇双全，智慧通达","诚实守信，言而有信","温文尔雅，温润如玉","良善正直，积善成德","恭敬谦让，敬业乐群","勤俭持家，克勤克俭","谦让为先，礼让三分"};
        for (int i = 0; i < 10; i++) {
            ranks.add(new Rank(i + 1, 1, i + 1, chars[i], descs[i]));
        }
    }
}
