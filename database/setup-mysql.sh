#!/bin/bash

# Star Town MySQL 9.4 数据库设置脚本

echo "🚀 开始设置 Star Town MySQL 数据库..."

# 检查MySQL是否运行
if ! pgrep -x "mysqld" > /dev/null; then
    echo "❌ MySQL服务未运行，请先启动MySQL服务"
    echo "Windows: net start mysql"
    echo "Linux: sudo systemctl start mysql"
    exit 1
fi

echo "✅ MySQL服务正在运行"

# 数据库连接信息
DB_HOST="localhost"
DB_PORT="3306"
DB_USER="root"
DB_PASSWORD="123456"
DB_NAME="star_town"

echo "📋 数据库配置信息:"
echo "   主机: $DB_HOST"
echo "   端口: $DB_PORT"
echo "   用户: $DB_USER"
echo "   数据库: $DB_NAME"

# 测试连接
echo "🔍 测试MySQL连接..."
mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASSWORD -e "SELECT 1;" 2>/dev/null

if [ $? -eq 0 ]; then
    echo "✅ MySQL连接成功"
else
    echo "❌ MySQL连接失败，请检查用户名和密码"
    echo "请修改脚本中的DB_USER和DB_PASSWORD变量"
    exit 1
fi

# 创建数据库
echo "📦 创建数据库 $DB_NAME..."
mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASSWORD -e "CREATE DATABASE IF NOT EXISTS $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null

if [ $? -eq 0 ]; then
    echo "✅ 数据库 $DB_NAME 创建成功"
else
    echo "❌ 数据库创建失败"
    exit 1
fi

# 执行建表脚本
echo "🏗️ 执行建表脚本..."
mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASSWORD $DB_NAME < schema.sql

if [ $? -eq 0 ]; then
    echo "✅ 表结构创建成功"
else
    echo "❌ 表结构创建失败"
    exit 1
fi

# 插入初始数据
echo "📊 插入初始数据..."
mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASSWORD $DB_NAME < init.sql

if [ $? -eq 0 ]; then
    echo "✅ 初始数据插入成功"
else
    echo "❌ 初始数据插入失败"
    exit 1
fi

# 验证数据
echo "🔍 验证数据库设置..."
mysql -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASSWORD $DB_NAME -e "
SELECT '智能体表' as table_name, COUNT(*) as record_count FROM agents
UNION ALL
SELECT '世界对象表', COUNT(*) FROM world_objects
UNION ALL
SELECT '游戏事件表', COUNT(*) FROM game_events
UNION ALL
SELECT '行为树配置表', COUNT(*) FROM behavior_tree_configs
UNION ALL
SELECT '行为模板表', COUNT(*) FROM behavior_templates;
"

echo ""
echo "🎉 Star Town 数据库设置完成！"
echo ""
echo "📝 下一步操作："
echo "1. 启动Spring Boot应用: mvn spring-boot:run"
echo "2. 访问应用: http://localhost:8080"
echo "3. 访问行为树管理器: 打开 frontend/behavior-tree-manager.html"
echo ""
echo "🔧 如需修改数据库配置，请编辑:"
echo "   - src/main/resources/application.properties"
echo "   - database/setup-mysql.sh"
