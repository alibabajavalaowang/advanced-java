# 家谱管理系统 (Family Tree Management System)

基于芋道云(yudao-cloud)微服务架构的企业级家谱管理平台，支持家族成员管理、世系图谱可视化、家族文化传承等功能。

## 项目结构

```
family-tree/
├── demo-standalone/          # 零依赖独立可运行Demo（JDK 21即可运行）
│   └── src/main/
│       ├── java/com/family/
│       │   ├── FamilyTreeApplication.java   # Spring Boot 启动类
│       │   └── FamilyTreeServer.java        # 零依赖独立服务器
│       └── resources/
│           ├── static/index.html            # 完整前端SPA页面
│           └── data.sql                     # 赵氏家谱示例数据
├── yudao-module-family/      # 后端微服务模块（芋道云架构）
│   ├── yudao-module-family-api/   # API层（VO、枚举、常量）
│   └── yudao-module-family-biz/   # 业务层（Controller、Service、Mapper）
├── yudao-ui-admin-vue3/      # 前端Vue3项目（基于芋道云Admin）
│   └── src/
│       ├── api/family/       # API接口层
│       ├── router/modules/   # 路由配置
│       └── views/family/     # 11个功能页面
├── sql/                      # 数据库脚本
│   ├── family_tree_schema.sql     # 建表DDL（8张表）
│   └── family_tree_menu.sql       # 菜单初始化数据
├── docs/                     # 项目文档
├── docker-compose.yml        # Docker编排（MySQL+Redis+Nacos+MinIO）
└── README.md                 # 本文件
```

## 核心功能

- **家谱管理**: 创建和管理多个家谱，记录堂号、郡望、家训、族规
- **成员管理**: 家族成员CRUD，支持父子/配偶关系维护
- **世系图谱**: D3.js交互式家族树可视化（缩放、平移、搜索、导出PNG）
- **统计分析**: ECharts图表展示世代分布、地域分布、年龄分布
- **字辈排行**: 管理和展示家族字辈（辈分字）
- **家族大事记**: 时间轴展示家族重要事件（婚嫁、科举、迁徙等）
- **家族相册**: 图片/视频上传管理
- **家族公告**: 发布和管理族内通知
- **AI助手**: 对接Coze大模型，智能生成家族故事、族训、诗词

## 技术栈

| 层次 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3 + Spring Cloud + yudao-cloud |
| ORM | MyBatis-Plus |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis |
| 注册中心 | Nacos |
| 对象存储 | MinIO |
| 前端框架 | Vue 3 + TypeScript + Element Plus |
| 可视化 | D3.js（家族树）+ ECharts（统计图表）|
| AI | Coze API（智能创作）|
| 容器化 | Docker + Docker Compose |

## 快速体验（零依赖Demo）

无需 Maven、无需数据库，仅需 JDK 21 即可运行：

```bash
cd demo-standalone

# 编译
javac -d out src/main/java/com/family/FamilyTreeServer.java

# 运行（自动加载示例数据，含赵氏家谱5代22位成员）
java -cp out:src/main/resources com.family.FamilyTreeServer
```

浏览器访问 `http://localhost:8080` 即可看到：
- 交互式D3.js家族树（支持缩放/平移/搜索/导出）
- ECharts统计图表（世代/地域/年龄分布）
- 成员列表与详情
- 大事记时间轴
- 字辈排行展示

## 完整版部署

### 1. 启动基础设施

```bash
docker-compose up -d
```

这将启动 MySQL、Redis、Nacos、MinIO 四个服务。

### 2. 初始化数据库

```bash
# 先在 yudao-cloud 框架中执行基础SQL
# 再执行家谱模块SQL
mysql -h127.0.0.1 -P3306 -uroot -proot < sql/family_tree_schema.sql
mysql -h127.0.0.1 -P3306 -uroot -proot < sql/family_tree_menu.sql
```

### 3. 部署后端

将 `yudao-module-family` 目录复制到 yudao-cloud 项目的 `yudao-module-family/` 下，并在根 pom.xml 中添加模块引用：

```xml
<module>yudao-module-family</module>
```

然后构建并启动服务。

### 4. 部署前端

将 `yudao-ui-admin-vue3/src/` 下的文件合并到 yudao-ui-admin-vue3 项目对应目录：

```bash
# 复制API接口
cp -r yudao-ui-admin-vue3/src/api/family/ <your-yudao-ui>/src/api/family/
# 复制路由
cp yudao-ui-admin-vue3/src/router/modules/family.ts <your-yudao-ui>/src/router/modules/
# 复制页面
cp -r yudao-ui-admin-vue3/src/views/family/ <your-yudao-ui>/src/views/family/
```

```bash
npm install d3@7
npm run dev
```

## 数据模型

8张核心表：

| 表名 | 说明 |
|------|------|
| family_tree | 家谱主表（堂号、郡望、家训、族规）|
| family_member | 家族成员（姓名、性别、生卒、世代、职业、简介）|
| family_relation | 亲属关系扩展表 |
| family_media | 家族媒体文件 |
| family_event | 家族大事记 |
| generation_rank | 字辈排行 |
| family_notice | 家族公告 |
| tree_permission | 家谱权限控制 |

## 示例数据

内置赵氏家谱示例，包含：
- 5代22位成员（始祖赵德公 -> 德义礼智信五代）
- 10个字辈（德义礼智信温良恭俭让）
- 6件家族大事记
- 完整的堂号(百忍堂)、郡望(甘肃天水)、家训、族规

## 如何迁移到独立仓库

本项目可完整独立运行，迁移步骤：

```bash
# 1. 创建新仓库
gh repo create your-username/family-tree --public

# 2. 复制项目
cp -r family-tree/ /tmp/family-tree-repo/
cd /tmp/family-tree-repo/

# 3. 初始化并推送
git init
git add .
git commit -m "init: 家谱管理系统"
git remote add origin https://github.com/your-username/family-tree.git
git push -u origin main
```

## License

MIT
