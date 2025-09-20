# Star Town 数据库配置指南

## 📋 概述

本项目使用 MySQL 9.4 作为主数据库，支持完整的智能体行为树管理系统。

## 🚀 快速开始

### 1. 环境要求

- MySQL 9.4 或更高版本
- Java 17+
- Maven 3.6+

### 2. 数据库设置

#### Windows 用户
```bash
# 1. 确保MySQL服务运行
net start mysql

# 2. 运行设置脚本
database\setup-mysql.bat
```

#### Linux/Mac 用户
```bash
# 1. 确保MySQL服务运行
sudo systemctl start mysql

# 2. 给脚本执行权限
chmod +x database/setup-mysql.sh

# 3. 运行设置脚本
./database/setup-mysql.sh
```

### 3. 手动设置（如果脚本失败）

```sql
-- 1. 登录MySQL
mysql -u root -p

-- 2. 创建数据库
CREATE DATABASE star_town CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE star_town;

-- 3. 执行建表脚本
SOURCE database/schema.sql;

-- 4. 插入初始数据
SOURCE database/init.sql;
```

## ⚙️ 配置说明

### 数据库连接配置

编辑 `src/main/resources/application.properties`:

```properties
# MySQL 9.4 连接配置
spring.datasource.url=jdbc:mysql://localhost:3306/star_town?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8mb4
spring.datasource.username=root
spring.datasource.password=你的密码

# 连接池配置
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

### 修改默认密码

如果您的MySQL密码不是 `123456`，请修改以下文件：

1. `src/main/resources/application.properties` - 第11行
2. `database/setup-mysql.sh` - 第12行
3. `database/setup-mysql.bat` - 第17行

## 📊 数据库结构

### 核心表

| 表名 | 描述 |
|------|------|
| `agents` | 智能体信息 |
| `world_objects` | 世界对象 |
| `game_events` | 游戏事件 |
| `behavior_tree_configs` | 行为树配置 |
| `behavior_node_templates` | 行为节点模板 |
| `behavior_templates` | 行为模板 |

### 示例数据

数据库初始化后会包含：

- 6个示例智能体（居民、工人、商人等）
- 6个世界对象（公园、图书馆、市场等）
- 4个示例游戏事件
- 3个默认行为树配置
- 16个行为节点模板
- 5个行为模板

## 🔧 常用操作

### 查看数据

```sql
-- 查看所有智能体
SELECT id, name, type, status FROM agents;

-- 查看行为树配置
SELECT name, agent_type, is_default FROM behavior_tree_configs;

-- 查看行为模板
SELECT name, template_type, is_system FROM behavior_templates;
```

### 重置数据库

```sql
-- 删除所有数据（保留表结构）
DELETE FROM game_events;
DELETE FROM world_objects;
DELETE FROM agents;
DELETE FROM behavior_tree_configs;
DELETE FROM behavior_templates;
DELETE FROM behavior_node_templates;

-- 重新插入初始数据
SOURCE database/init.sql;
```

### 备份数据库

```bash
# 备份整个数据库
mysqldump -u root -p star_town > star_town_backup.sql

# 备份特定表
mysqldump -u root -p star_town agents world_objects > core_tables_backup.sql
```

## 🐛 故障排除

### 连接失败

1. **检查MySQL服务状态**
   ```bash
   # Windows
   sc query mysql
   
   # Linux
   sudo systemctl status mysql
   ```

2. **检查端口占用**
   ```bash
   # Windows
   netstat -an | findstr :3306
   
   # Linux
   netstat -tlnp | grep :3306
   ```

3. **验证用户权限**
   ```sql
   SHOW GRANTS FOR 'root'@'localhost';
   ```

### 字符编码问题

如果遇到中文乱码，确保：

1. 数据库使用 `utf8mb4` 字符集
2. 连接URL包含 `characterEncoding=utf8mb4`
3. MySQL配置文件 `my.cnf` 设置：
   ```ini
   [mysql]
   default-character-set = utf8mb4
   
   [mysqld]
   character-set-server = utf8mb4
   collation-server = utf8mb4_unicode_ci
   ```

### 性能优化

1. **调整连接池大小**
   ```properties
   spring.datasource.hikari.maximum-pool-size=50
   spring.datasource.hikari.minimum-idle=10
   ```

2. **启用查询缓存**
   ```sql
   SET GLOBAL query_cache_size = 67108864;
   SET GLOBAL query_cache_type = ON;
   ```

## 📞 支持

如果遇到问题，请检查：

1. MySQL 9.4 是否正确安装和运行
2. 用户权限是否正确设置
3. 防火墙是否阻止了3306端口
4. 字符编码配置是否正确

更多帮助请参考 [MySQL 9.4 官方文档](https://dev.mysql.com/doc/refman/9.4/en/)。
