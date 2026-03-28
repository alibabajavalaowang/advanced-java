#!/bin/bash
# 一键推送家谱项目到独立仓库
# 用法: 在 advanced-java 目录下运行 bash push-to-family-tree.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
FAMILY_DIR="$SCRIPT_DIR/family-tree"

if [ ! -d "$FAMILY_DIR" ]; then
    echo "错误: 找不到 $FAMILY_DIR 目录"
    exit 1
fi

echo "=== 家谱项目推送脚本 ==="

TEMP_DIR=$(mktemp -d)
echo "1. 创建临时目录: $TEMP_DIR"

echo "2. 复制项目文件..."
cp -r "$FAMILY_DIR"/* "$TEMP_DIR/"
cp "$FAMILY_DIR/.gitignore" "$TEMP_DIR/" 2>/dev/null || true

echo "3. 初始化并提交..."
(
    cd "$TEMP_DIR"
    git init
    git checkout -b main
    git add .
    git commit -m "init: 家谱管理系统 - 基于芋道云微服务架构的企业级家谱管理平台

功能包括:
- 家谱管理（堂号、郡望、家训、族规）
- 家族成员管理与世系图谱（D3.js可视化）
- 统计分析（ECharts图表）
- 字辈排行、家族大事记、家族相册
- AI助手（Coze大模型智能创作）
- 零依赖独立Demo（JDK 21即可运行）"

    echo "4. 推送到 GitHub..."
    git remote add origin https://github.com/alibabajavalaowang/family-tree.git
    git push -u origin main
)

rm -rf "$TEMP_DIR"

echo ""
echo "=== 推送完成！==="
echo "仓库地址: https://github.com/alibabajavalaowang/family-tree"
