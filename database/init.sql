-- Star Town 数据库初始化脚本
-- MySQL 9.4

-- 创建数据库
CREATE DATABASE IF NOT EXISTS star_town 
CHARACTER SET utf8 
COLLATE utf8_general_ci;

USE star_town;

-- 设置MySQL 9.4兼容性
SET sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_DATE,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO';

-- 执行建表脚本
SOURCE schema.sql;

-- 插入示例数据
INSERT INTO agents (id, name, type, position_x, position_y, state_json, memory_json, config_json, status) VALUES
('alice', 'Alice', 'RESIDENT', 100.0, 200.0, 
 '{"energy": 80, "hunger": 30, "health": 90, "happiness": 70, "social": 60}', 
 '{"lastWorkTime": 1640995200000, "favoritePlace": "Library"}', 
 '{"energy": 80, "hunger": 30, "health": 90, "happiness": 70, "social": 60}', 
 'IDLE'),

('bob', 'Bob', 'RESIDENT', 300.0, 150.0, 
 '{"energy": 75, "hunger": 40, "health": 85, "happiness": 65, "social": 55}', 
 '{"lastWorkTime": 1640995800000, "favoritePlace": "Park"}', 
 '{"energy": 75, "hunger": 40, "health": 85, "happiness": 65, "social": 55}', 
 'IDLE'),

('charlie', 'Charlie', 'WORKER', 200.0, 100.0, 
 '{"energy": 90, "hunger": 20, "health": 85, "income": 0, "hasWorkplace": true}', 
 '{"lastWorkTime": 1640995200000, "workType": "construction"}', 
 '{"energy": 90, "hunger": 20, "health": 85, "income": 0, "hasWorkplace": true}', 
 'IDLE'),

('diana', 'Diana', 'MERCHANT', 400.0, 250.0, 
 '{"energy": 70, "hunger": 40, "health": 80, "money": 100, "hasShop": true}', 
 '{"lastWorkTime": 1640995200000, "businessType": "trading"}', 
 '{"energy": 70, "hunger": 40, "health": 80, "money": 100, "hasShop": true}', 
 'IDLE'),

('eve', 'Eve', 'ARTIST', 150.0, 300.0, 
 '{"energy": 60, "hunger": 50, "health": 75, "creativity": 95, "inspiration": 40}', 
 '{"lastWorkTime": 1640995200000, "artType": "painting"}', 
 '{"energy": 60, "hunger": 50, "health": 75, "creativity": 95, "inspiration": 40}', 
 'IDLE'),

('frank', 'Frank', 'SCIENTIST', 350.0, 100.0, 
 '{"energy": 85, "hunger": 25, "health": 90, "knowledge": 95, "hasLab": true}', 
 '{"lastWorkTime": 1640995200000, "researchType": "physics"}', 
 '{"energy": 85, "hunger": 25, "health": 90, "knowledge": 95, "hasLab": true}', 
 'IDLE');

-- 插入示例世界对象
INSERT INTO world_objects (name, type, position_x, position_y, width, height, properties_json, is_interactive, capacity) VALUES
('中央公园', 'PARK', 250.0, 250.0, 100.0, 100.0, '{"amenities": ["bench", "fountain", "garden"]}', TRUE, 50),
('图书馆', 'LIBRARY', 150.0, 150.0, 80.0, 60.0, '{"books": 1000, "study_areas": 20}', TRUE, 30),
('市场', 'MARKET', 400.0, 200.0, 120.0, 80.0, '{"stalls": 15, "food_variety": "high"}', TRUE, 40),
('工厂', 'FACTORY', 200.0, 50.0, 100.0, 80.0, '{"production_rate": "high", "workers_needed": 10}', TRUE, 20),
('艺术工作室', 'ART_STUDIO', 100.0, 300.0, 60.0, 40.0, '{"art_supplies": true, "inspiration_level": "high"}', TRUE, 5),
('实验室', 'LABORATORY', 350.0, 50.0, 80.0, 60.0, '{"equipment": "advanced", "research_capability": "high"}', TRUE, 8);

-- 插入示例游戏事件
INSERT INTO game_events (type, source_agent_id, description, data_json, position_x, position_y, severity) VALUES
('AGENT_CREATED', 'alice', '智能体Alice被创建', '{"agentType": "RESIDENT", "location": "Town Center"}', 100.0, 200.0, 'INFO'),
('AGENT_CREATED', 'charlie', '智能体Charlie被创建', '{"agentType": "WORKER", "location": "Factory"}', 200.0, 100.0, 'INFO'),
('WORLD_OBJECT_CREATED', NULL, '中央公园被创建', '{"objectType": "PARK", "capacity": 50}', 250.0, 250.0, 'INFO'),
('AGENT_MOVED', 'alice', 'Alice移动到新位置', '{"fromX": 100, "fromY": 200, "toX": 150, "toY": 250}', 150.0, 250.0, 'INFO');

-- 显示创建结果
SELECT 'Database initialized successfully!' as message;
SELECT COUNT(*) as agent_count FROM agents;
SELECT COUNT(*) as object_count FROM world_objects;
SELECT COUNT(*) as event_count FROM game_events;
SELECT COUNT(*) as config_count FROM behavior_tree_configs;
SELECT COUNT(*) as template_count FROM behavior_templates;
