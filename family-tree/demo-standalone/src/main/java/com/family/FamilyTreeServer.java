package com.family;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

public class FamilyTreeServer {

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

    static List<FamilyTree> trees = new ArrayList<>();
    static List<Member> members = new ArrayList<>();
    static List<Event> events = new ArrayList<>();
    static List<Rank> ranks = new ArrayList<>();
    static long eventIdSeq = 1;
    static long rankIdSeq = 1;

    // Province names for stats grouping (Unicode escaped)
    static final String[] PROVINCES = {
        "\u5317\u4eac",   // Beijing
        "\u6c5f\u82cf",   // Jiangsu
        "\u6d59\u6c5f",   // Zhejiang
        "\u5b89\u5fbd",   // Anhui
        "\u7518\u8083"    // Gansu
    };

    public static void main(String[] args) throws Exception {
        loadDataFromSql();

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
        System.out.println("  Family Tree Demo Server Started!");
        System.out.println("  URL: http://localhost:8080");
        System.out.println("  API: http://localhost:8080/api/tree/list");
        System.out.println("==========================================================");
    }

    // ==================== Load data from SQL file ====================
    static void loadDataFromSql() {
        String sql = readResourceUtf8("/data.sql");
        if (sql == null || sql.isEmpty()) {
            System.out.println("Warning: data.sql not found, no demo data loaded.");
            return;
        }

        // Parse family_tree inserts
        Pattern treePattern = Pattern.compile(
            "INSERT INTO family_tree[^V]+VALUES\\s*\\(([^;]+?)\\);", Pattern.DOTALL);
        Matcher tm = treePattern.matcher(sql);
        while (tm.find()) {
            List<String> vals = parseSqlValues(tm.group(1));
            if (vals.size() >= 12) {
                trees.add(new FamilyTree(
                    longVal(vals, 0), strVal(vals, 1), strVal(vals, 2), strVal(vals, 3),
                    strVal(vals, 4), strVal(vals, 5), strVal(vals, 6), strVal(vals, 7),
                    strVal(vals, 8), strVal(vals, 9),
                    intVal(vals, 10), intVal(vals, 11)));
            }
        }

        // Parse family_member inserts
        Pattern memberPattern = Pattern.compile(
            "INSERT INTO family_member[^V]+VALUES\\s*\\(([^;]+?)\\);", Pattern.DOTALL);
        Matcher mm = memberPattern.matcher(sql);
        while (mm.find()) {
            List<String> vals = parseSqlValues(mm.group(1));
            int size = vals.size();
            if (size >= 15) {
                // Two formats: with biography (16 fields) and without (15 fields)
                boolean hasBio = size >= 16;
                int offset = hasBio ? 0 : 0;
                members.add(new Member(
                    longVal(vals, 0), longVal(vals, 1), strVal(vals, 2), intVal(vals, 3),
                    strVal(vals, 4), strVal(vals, 5), intVal(vals, 6), intVal(vals, 7),
                    strVal(vals, 8), strVal(vals, 9), strVal(vals, 10), strVal(vals, 11),
                    hasBio ? strVal(vals, 12) : null,
                    nullLong(vals, hasBio ? 13 : 12), nullLong(vals, hasBio ? 14 : 13),
                    intVal(vals, hasBio ? 15 : 14)));
            }
        }

        // Parse generation_rank inserts
        Pattern rankPattern = Pattern.compile(
            "INSERT INTO generation_rank[^V]+VALUES\\s*\\(([^;]+?)\\);", Pattern.DOTALL);
        Matcher rm = rankPattern.matcher(sql);
        while (rm.find()) {
            List<String> vals = parseSqlValues(rm.group(1));
            if (vals.size() >= 4) {
                ranks.add(new Rank(rankIdSeq++, longVal(vals, 0), intVal(vals, 1),
                    strVal(vals, 2), strVal(vals, 3)));
            }
        }

        // Parse family_event inserts (two formats: with member_id and without)
        Pattern eventPattern = Pattern.compile(
            "INSERT INTO family_event[^V]+VALUES\\s*\\(([^;]+?)\\);", Pattern.DOTALL);
        Matcher em = eventPattern.matcher(sql);
        while (em.find()) {
            String insertLine = em.group(0);
            List<String> vals = parseSqlValues(em.group(1));
            boolean hasMemberId = insertLine.contains("member_id");
            if (hasMemberId && vals.size() >= 7) {
                events.add(new Event(eventIdSeq++, longVal(vals, 0), nullLong(vals, 1),
                    strVal(vals, 2), strVal(vals, 3), strVal(vals, 4),
                    intVal(vals, 5), intVal(vals, 6)));
            } else if (!hasMemberId && vals.size() >= 6) {
                events.add(new Event(eventIdSeq++, longVal(vals, 0), null,
                    strVal(vals, 1), strVal(vals, 2), strVal(vals, 3),
                    intVal(vals, 4), intVal(vals, 5)));
            }
        }

        System.out.println("Loaded: " + trees.size() + " trees, " + members.size() +
            " members, " + events.size() + " events, " + ranks.size() + " ranks");
    }

    static String readResourceUtf8(String path) {
        // Try classpath first
        InputStream is = FamilyTreeServer.class.getResourceAsStream(path);
        if (is == null) {
            // Try file system
            try {
                Path filePath = Path.of("src/main/resources" + path);
                if (Files.exists(filePath)) {
                    is = Files.newInputStream(filePath);
                }
            } catch (Exception e) { /* ignore */ }
        }
        if (is == null) return null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return br.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) { return null; }
    }

    // Simple SQL VALUES parser - handles quoted strings with commas inside
    static List<String> parseSqlValues(String raw) {
        List<String> result = new ArrayList<>();
        raw = raw.trim();
        int i = 0;
        while (i < raw.length()) {
            char c = raw.charAt(i);
            if (c == ' ' || c == '\n' || c == '\r' || c == '\t' || c == ',') { i++; continue; }
            if (c == '\'') {
                // Quoted string
                StringBuilder sb = new StringBuilder();
                i++;
                while (i < raw.length()) {
                    char ch = raw.charAt(i);
                    if (ch == '\\' && i + 1 < raw.length() && raw.charAt(i + 1) == 'n') {
                        sb.append('\n'); i += 2; continue;
                    }
                    if (ch == '\'' && i + 1 < raw.length() && raw.charAt(i + 1) == '\'') {
                        sb.append('\''); i += 2; continue;
                    }
                    if (ch == '\'') { i++; break; }
                    sb.append(ch); i++;
                }
                result.add("'" + sb + "'");
            } else {
                // Unquoted value (number or NULL)
                StringBuilder sb = new StringBuilder();
                while (i < raw.length() && raw.charAt(i) != ',' && raw.charAt(i) != ')') {
                    sb.append(raw.charAt(i)); i++;
                }
                result.add(sb.toString().trim());
            }
        }
        return result;
    }

    static String strVal(List<String> vals, int idx) {
        if (idx >= vals.size()) return null;
        String v = vals.get(idx);
        if (v.equalsIgnoreCase("NULL")) return null;
        if (v.startsWith("'") && v.endsWith("'")) return v.substring(1, v.length() - 1);
        return v;
    }

    static long longVal(List<String> vals, int idx) {
        try { return Long.parseLong(vals.get(idx).trim()); } catch (Exception e) { return 0; }
    }

    static int intVal(List<String> vals, int idx) {
        try { return Integer.parseInt(vals.get(idx).trim()); } catch (Exception e) { return 0; }
    }

    static Long nullLong(List<String> vals, int idx) {
        if (idx >= vals.size()) return null;
        String v = vals.get(idx).trim();
        if (v.equalsIgnoreCase("NULL")) return null;
        try { return Long.parseLong(v); } catch (Exception e) { return null; }
    }

    // ==================== HTTP ====================
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

        InputStream is = FamilyTreeServer.class.getResourceAsStream("/static" + path);
        if (is == null) {
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

    // ==================== JSON serialization ====================
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

    // ==================== Tree building ====================
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

    // ==================== Stats ====================
    static String statsJson(long treeId) {
        List<Member> all = members.stream().filter(m -> m.treeId == treeId).toList();
        int total = all.size();
        int alive = (int) all.stream().filter(m -> m.isAlive == 1).count();
        int male = (int) all.stream().filter(m -> m.gender == 1).count();
        int female = (int) all.stream().filter(m -> m.gender == 0).count();

        // "\u7b2c" = "Di"(No.), "\u4e16" = "Shi"(generation)
        Map<String, Integer> genDist = new LinkedHashMap<>();
        all.stream().collect(Collectors.groupingBy(
                m -> "\u7b2c" + m.generation + "\u4e16", Collectors.summingInt(m -> 1)))
                .entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e -> genDist.put(e.getKey(), e.getValue()));

        Map<String, Integer> regionDist = new LinkedHashMap<>();
        all.stream().filter(m -> m.residence != null && !m.residence.isEmpty())
                .collect(Collectors.groupingBy(m -> {
                    for (String p : PROVINCES) {
                        if (m.residence.contains(p)) return p;
                    }
                    return m.residence.length() > 4 ? m.residence.substring(0, 4) : m.residence;
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
}
