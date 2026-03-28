#!/bin/bash
# 一键推送家谱项目到独立仓库
# 用法: bash push-to-family-tree.sh

set -e

echo "=== 家谱项目推送脚本 ==="

# 1. 创建临时目录
TEMP_DIR=$(mktemp -d)
echo "1. 创建临时目录: $TEMP_DIR"

# 2. 复制项目文件
echo "2. 复制项目文件..."
cp -r family-tree/* "$TEMP_DIR/"
cp family-tree/.gitignore "$TEMP_DIR/" 2>/dev/null || true

# 3. 初始化Git仓库
cd "$TEMP_DIR"
git init
git checkout -b main

# 4. 添加所有文件并提交
echo "3. 提交代码..."
git add .
git commit -m "init: 家谱管理系统 - 基于芋道云微服务架构的企业级家谱管理平台

功能包括:
- 家谱管理（堂号、郡望、家训、族规）
- 家族成员管理与世系图谱（D3.js可视化）
- 统计分析（ECharts图表）
- 字辈排行、家族大事记、家族相册
- AI助手（Coze大模型智能创作）
- 零依赖独立Demo（JDK 21即可运行）"

# 5. 推送到远程仓库
echo "4. 推送到 GitHub..."
git remote add origin https://github.com/alibabajavalaowang/family-tree.git
git push -u origin main

echo ""
echo "=== 推送完成！==="
echo "仓库地址: https://github.com/alibabajavalaowang/family-tree"

# 清理
cd -
rm -rf "$TEMP_DIR"
