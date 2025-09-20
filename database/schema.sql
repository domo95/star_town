-- Star Town 游戏数据库表结构
-- MySQL 9.4 兼容

-- 智能体表
CREATE TABLE IF NOT EXISTS agents (
    id VARCHAR(255) PRIMARY KEY COMMENT '智能体ID',
    name VARCHAR(255) NOT NULL COMMENT '智能体名称',
    type ENUM('RESIDENT', 'WORKER', 'MERCHANT', 'GUARD', 'ARTIST', 'SCIENTIST', 'LEADER', 'CHILD', 'ELDER', 'VISITOR') NOT NULL COMMENT '智能体类型',
    position_x DECIMAL(10,2) DEFAULT 0.00 COMMENT 'X坐标',
    position_y DECIMAL(10,2) DEFAULT 0.00 COMMENT 'Y坐标',
    state_json JSON COMMENT '智能体状态数据',
    memory_json JSON COMMENT '智能体记忆数据',
    config_json JSON COMMENT '智能体配置数据',
    status ENUM('IDLE', 'THINKING', 'EXECUTING', 'WAITING', 'ERROR') DEFAULT 'IDLE' COMMENT '智能体状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_active TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '最后活跃时间',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否活跃',
    
    INDEX idx_type (type),
    INDEX idx_status (status),
    INDEX idx_position (position_x, position_y),
    INDEX idx_last_active (last_active),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='智能体表';

-- 世界对象表
CREATE TABLE IF NOT EXISTS world_objects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '对象ID',
    name VARCHAR(255) NOT NULL COMMENT '对象名称',
    type VARCHAR(100) NOT NULL COMMENT '对象类型',
    position_x DECIMAL(10,2) NOT NULL COMMENT 'X坐标',
    position_y DECIMAL(10,2) NOT NULL COMMENT 'Y坐标',
    width DECIMAL(10,2) COMMENT '宽度',
    height DECIMAL(10,2) COMMENT '高度',
    properties_json JSON COMMENT '对象属性',
    owner_agent_id VARCHAR(255) COMMENT '所有者智能体ID',
    is_interactive BOOLEAN DEFAULT FALSE COMMENT '是否可交互',
    capacity INT COMMENT '容量',
    current_occupancy INT DEFAULT 0 COMMENT '当前占用数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否活跃',
    
    INDEX idx_type (type),
    INDEX idx_position (position_x, position_y),
    INDEX idx_owner (owner_agent_id),
    INDEX idx_interactive (is_interactive),
    INDEX idx_is_active (is_active),
    FOREIGN KEY (owner_agent_id) REFERENCES agents(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='世界对象表';

-- 游戏事件表
CREATE TABLE IF NOT EXISTS game_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '事件ID',
    type VARCHAR(100) NOT NULL COMMENT '事件类型',
    source_agent_id VARCHAR(255) COMMENT '源智能体ID',
    target_agent_id VARCHAR(255) COMMENT '目标智能体ID',
    world_object_id BIGINT COMMENT '世界对象ID',
    description TEXT COMMENT '事件描述',
    data_json JSON COMMENT '事件数据',
    position_x DECIMAL(10,2) COMMENT 'X坐标',
    position_y DECIMAL(10,2) COMMENT 'Y坐标',
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '事件时间',
    severity ENUM('INFO', 'WARNING', 'ERROR', 'CRITICAL') DEFAULT 'INFO' COMMENT '严重程度',
    is_processed BOOLEAN DEFAULT FALSE COMMENT '是否已处理',
    
    INDEX idx_type (type),
    INDEX idx_source_agent (source_agent_id),
    INDEX idx_target_agent (target_agent_id),
    INDEX idx_world_object (world_object_id),
    INDEX idx_timestamp (timestamp),
    INDEX idx_severity (severity),
    INDEX idx_processed (is_processed),
    INDEX idx_position (position_x, position_y),
    FOREIGN KEY (source_agent_id) REFERENCES agents(id) ON DELETE SET NULL,
    FOREIGN KEY (target_agent_id) REFERENCES agents(id) ON DELETE SET NULL,
    FOREIGN KEY (world_object_id) REFERENCES world_objects(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='游戏事件表';

-- 行为树配置表
CREATE TABLE IF NOT EXISTS behavior_tree_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '配置ID',
    name VARCHAR(255) NOT NULL UNIQUE COMMENT '配置名称',
    description TEXT COMMENT '配置描述',
    agent_type ENUM('RESIDENT', 'WORKER', 'MERCHANT', 'GUARD', 'ARTIST', 'SCIENTIST', 'LEADER', 'CHILD', 'ELDER', 'VISITOR') COMMENT '适用的智能体类型',
    config_json JSON NOT NULL COMMENT '行为树配置JSON',
    is_default BOOLEAN DEFAULT FALSE COMMENT '是否为默认配置',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by VARCHAR(255) COMMENT '创建者',
    
    INDEX idx_name (name),
    INDEX idx_agent_type (agent_type),
    INDEX idx_is_default (is_default),
    INDEX idx_is_active (is_active),
    INDEX idx_created_by (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='行为树配置表';

-- 行为节点模板表
CREATE TABLE IF NOT EXISTS behavior_node_templates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '模板ID',
    name VARCHAR(255) NOT NULL UNIQUE COMMENT '节点名称',
    type ENUM('CONDITION', 'ACTION', 'SEQUENCE', 'SELECTOR') NOT NULL COMMENT '节点类型',
    description TEXT COMMENT '节点描述',
    parameters_json JSON COMMENT '节点参数',
    script_content TEXT COMMENT '脚本内容（对于条件/动作节点）',
    is_system BOOLEAN DEFAULT FALSE COMMENT '是否为系统节点',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_name (name),
    INDEX idx_type (type),
    INDEX idx_is_system (is_system),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='行为节点模板表';

-- 行为模板表
CREATE TABLE IF NOT EXISTS behavior_templates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '模板ID',
    name VARCHAR(255) NOT NULL UNIQUE COMMENT '模板名称',
    description TEXT COMMENT '模板描述',
    template_type ENUM('URGENT_NEEDS', 'BASIC_NEEDS', 'SOCIAL_NEEDS', 'WORK', 'REST', 'CUSTOM') NOT NULL COMMENT '模板类型',
    conditions JSON COMMENT '条件节点列表',
    actions JSON COMMENT '动作节点列表',
    is_system BOOLEAN DEFAULT FALSE COMMENT '是否为系统模板',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_name (name),
    INDEX idx_template_type (template_type),
    INDEX idx_is_system (is_system),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='行为模板表';

-- 插入默认的行为树配置数据
INSERT INTO behavior_tree_configs (name, description, agent_type, config_json, is_default, is_active) VALUES
('Default Resident Tree', '默认居民行为树', 'RESIDENT', '{"name":"ResidentTree","description":"普通居民的行为树","rootNode":{"name":"RootSelector","type":"selector","children":[{"name":"UrgentNeeds","type":"template"},{"name":"BasicNeeds","type":"template"},{"name":"SocialNeeds","type":"template"}]}}', TRUE, TRUE),
('Default Worker Tree', '默认工人行为树', 'WORKER', '{"name":"WorkerTree","description":"工人的行为树","rootNode":{"name":"RootSelector","type":"selector","children":[{"name":"Work","type":"template"},{"name":"BasicNeeds","type":"template"}]}}', TRUE, TRUE),
('Default Merchant Tree', '默认商人行为树', 'MERCHANT', '{"name":"MerchantTree","description":"商人的行为树","rootNode":{"name":"RootSelector","type":"selector","children":[{"name":"SocialNeeds","type":"template"},{"name":"BasicNeeds","type":"template"}]}}', TRUE, TRUE);

-- 插入默认的行为节点模板
INSERT INTO behavior_node_templates (name, type, description, is_system, is_active) VALUES
('IsHungry', 'CONDITION', '检查是否饥饿（饥饿度 > 70）', TRUE, TRUE),
('IsVeryHungry', 'CONDITION', '检查是否极度饥饿（饥饿度 > 90）', TRUE, TRUE),
('IsTired', 'CONDITION', '检查是否疲劳（精力 < 30）', TRUE, TRUE),
('IsVeryTired', 'CONDITION', '检查是否极度疲劳（精力 < 10）', TRUE, TRUE),
('HasFood', 'CONDITION', '检查是否有食物', TRUE, TRUE),
('HasWorkplace', 'CONDITION', '检查是否有工作场所', TRUE, TRUE),
('HasNearbyAgents', 'CONDITION', '检查附近是否有其他智能体', TRUE, TRUE),
('LowHappiness', 'CONDITION', '检查是否幸福感低（< 40）', TRUE, TRUE),
('ShouldWork', 'CONDITION', '检查是否应该工作', TRUE, TRUE),
('Eat', 'ACTION', '进食，减少饥饿度30点', TRUE, TRUE),
('EmergencyEat', 'ACTION', '紧急进食，将饥饿度设为20', TRUE, TRUE),
('Sleep', 'ACTION', '睡眠，恢复精力50点', TRUE, TRUE),
('EmergencySleep', 'ACTION', '紧急睡眠，将精力设为80', TRUE, TRUE),
('Work', 'ACTION', '工作，消耗精力20点，增加收入10点', TRUE, TRUE),
('Socialize', 'ACTION', '社交，增加幸福感15点', TRUE, TRUE),
('Rest', 'ACTION', '休息，恢复精力25点', TRUE, TRUE);

-- 插入默认的行为模板
INSERT INTO behavior_templates (name, description, template_type, conditions, actions, is_system, is_active) VALUES
('UrgentNeeds', '紧急需求处理', 'URGENT_NEEDS', '["IsVeryHungry", "IsVeryTired"]', '["EmergencyEat", "EmergencySleep"]', TRUE, TRUE),
('BasicNeeds', '基本需求处理', 'BASIC_NEEDS', '["IsHungry", "IsTired"]', '["Eat", "Sleep"]', TRUE, TRUE),
('SocialNeeds', '社交需求处理', 'SOCIAL_NEEDS', '["HasNearbyAgents", "LowHappiness"]', '["Socialize"]', TRUE, TRUE),
('Work', '工作行为', 'WORK', '["ShouldWork", "HasWorkplace"]', '["Work"]', TRUE, TRUE),
('Rest', '休息行为', 'REST', '["IsTired"]', '["Rest"]', TRUE, TRUE);
