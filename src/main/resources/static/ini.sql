create table agents
(
    id          varchar(255)                                                                                                   not null comment '智能体ID'
        primary key,
    name        varchar(255)                                                                                                   not null comment '智能体名称',
    type        enum ('RESIDENT', 'WORKER', 'MERCHANT', 'GUARD', 'ARTIST', 'SCIENTIST', 'LEADER', 'CHILD', 'ELDER', 'VISITOR') not null comment '智能体类型',
    position_x  decimal(10, 2)                                             default 0.00                                        null comment 'X坐标',
    position_y  decimal(10, 2)                                             default 0.00                                        null comment 'Y坐标',
    state_json  json                                                                                                           null comment '智能体状态数据',
    memory_json json                                                                                                           null comment '智能体记忆数据',
    config_json json                                                                                                           null comment '智能体配置数据',
    status      enum ('IDLE', 'THINKING', 'EXECUTING', 'WAITING', 'ERROR') default 'IDLE'                                      null comment '智能体状态',
    created_at  timestamp                                                  default CURRENT_TIMESTAMP                           null comment '创建时间',
    updated_at  timestamp                                                  default CURRENT_TIMESTAMP                           null on update CURRENT_TIMESTAMP comment '更新时间',
    last_active timestamp                                                  default CURRENT_TIMESTAMP                           null comment '最后活跃时间',
    is_active   tinyint(1)                                                 default 1                                           null comment '是否活跃'
)
    comment '智能体表' collate = utf8mb4_unicode_ci;

create index idx_is_active
    on agents (is_active);

create index idx_last_active
    on agents (last_active);

create index idx_position
    on agents (position_x, position_y);

create index idx_status
    on agents (status);

create index idx_type
    on agents (type);

create table behavior_node_templates
(
    id              bigint auto_increment comment '模板ID'
        primary key,
    name            varchar(255)                                         not null comment '节点名称',
    type            enum ('CONDITION', 'ACTION', 'SEQUENCE', 'SELECTOR') not null comment '节点类型',
    description     text                                                 null comment '节点描述',
    parameters_json json                                                 null comment '节点参数',
    script_content  text                                                 null comment '脚本内容（对于条件/动作节点）',
    is_system       tinyint(1) default 0                                 null comment '是否为系统节点',
    is_active       tinyint(1) default 1                                 null comment '是否激活',
    created_at      timestamp  default CURRENT_TIMESTAMP                 null comment '创建时间',
    updated_at      timestamp  default CURRENT_TIMESTAMP                 null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint name
        unique (name)
)
    comment '行为节点模板表' collate = utf8mb4_unicode_ci;

create index idx_is_active
    on behavior_node_templates (is_active);

create index idx_is_system
    on behavior_node_templates (is_system);

create index idx_name
    on behavior_node_templates (name);

create index idx_type
    on behavior_node_templates (type);

create table behavior_templates
(
    id            bigint auto_increment comment '模板ID'
        primary key,
    name          varchar(255)                                                                   not null comment '模板名称',
    description   text                                                                           null comment '模板描述',
    template_type enum ('URGENT_NEEDS', 'BASIC_NEEDS', 'SOCIAL_NEEDS', 'WORK', 'REST', 'CUSTOM') not null comment '模板类型',
    conditions    json                                                                           null comment '条件节点列表',
    actions       json                                                                           null comment '动作节点列表',
    is_system     tinyint(1) default 0                                                           null comment '是否为系统模板',
    is_active     tinyint(1) default 1                                                           null comment '是否激活',
    created_at    timestamp  default CURRENT_TIMESTAMP                                           null comment '创建时间',
    updated_at    timestamp  default CURRENT_TIMESTAMP                                           null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint name
        unique (name)
)
    comment '行为模板表' collate = utf8mb4_unicode_ci;

create index idx_is_active
    on behavior_templates (is_active);

create index idx_is_system
    on behavior_templates (is_system);

create index idx_name
    on behavior_templates (name);

create index idx_template_type
    on behavior_templates (template_type);

create table behavior_tree_configs
(
    id          bigint auto_increment comment '配置ID'
        primary key,
    name        varchar(255)                                                                                                   not null comment '配置名称',
    description text                                                                                                           null comment '配置描述',
    agent_type  enum ('RESIDENT', 'WORKER', 'MERCHANT', 'GUARD', 'ARTIST', 'SCIENTIST', 'LEADER', 'CHILD', 'ELDER', 'VISITOR') null comment '适用的智能体类型',
    config_json json                                                                                                           not null comment '行为树配置JSON',
    is_default  tinyint(1) default 0                                                                                           null comment '是否为默认配置',
    is_active   tinyint(1) default 1                                                                                           null comment '是否激活',
    created_at  timestamp  default CURRENT_TIMESTAMP                                                                           null comment '创建时间',
    updated_at  timestamp  default CURRENT_TIMESTAMP                                                                           null on update CURRENT_TIMESTAMP comment '更新时间',
    created_by  varchar(255)                                                                                                   null comment '创建者',
    constraint name
        unique (name)
)
    comment '行为树配置表' collate = utf8mb4_unicode_ci;

create index idx_agent_type
    on behavior_tree_configs (agent_type);

create index idx_created_by
    on behavior_tree_configs (created_by);

create index idx_is_active
    on behavior_tree_configs (is_active);

create index idx_is_default
    on behavior_tree_configs (is_default);

create index idx_name
    on behavior_tree_configs (name);

create table world_objects
(
    id                bigint auto_increment comment '对象ID'
        primary key,
    name              varchar(255)                         not null comment '对象名称',
    type              varchar(100)                         not null comment '对象类型',
    position_x        decimal(10, 2)                       not null comment 'X坐标',
    position_y        decimal(10, 2)                       not null comment 'Y坐标',
    width             decimal(10, 2)                       null comment '宽度',
    height            decimal(10, 2)                       null comment '高度',
    properties_json   json                                 null comment '对象属性',
    owner_agent_id    varchar(255)                         null comment '所有者智能体ID',
    is_interactive    tinyint(1) default 0                 null comment '是否可交互',
    capacity          int                                  null comment '容量',
    current_occupancy int        default 0                 null comment '当前占用数',
    created_at        timestamp  default CURRENT_TIMESTAMP null comment '创建时间',
    updated_at        timestamp  default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_active         tinyint(1) default 1                 null comment '是否活跃',
    constraint world_objects_ibfk_1
        foreign key (owner_agent_id) references agents (id)
            on delete set null
)
    comment '世界对象表' collate = utf8mb4_unicode_ci;

create table game_events
(
    id              bigint auto_increment comment '事件ID'
        primary key,
    type            varchar(100)                                                            not null comment '事件类型',
    source_agent_id varchar(255)                                                            null comment '源智能体ID',
    target_agent_id varchar(255)                                                            null comment '目标智能体ID',
    world_object_id bigint                                                                  null comment '世界对象ID',
    description     text                                                                    null comment '事件描述',
    data_json       json                                                                    null comment '事件数据',
    position_x      decimal(10, 2)                                                          null comment 'X坐标',
    position_y      decimal(10, 2)                                                          null comment 'Y坐标',
    timestamp       timestamp                                     default CURRENT_TIMESTAMP null comment '事件时间',
    severity        enum ('INFO', 'WARNING', 'ERROR', 'CRITICAL') default 'INFO'            null comment '严重程度',
    is_processed    tinyint(1)                                    default 0                 null comment '是否已处理',
    constraint game_events_ibfk_1
        foreign key (source_agent_id) references agents (id)
            on delete set null,
    constraint game_events_ibfk_2
        foreign key (target_agent_id) references agents (id)
            on delete set null,
    constraint game_events_ibfk_3
        foreign key (world_object_id) references world_objects (id)
            on delete set null
)
    comment '游戏事件表' collate = utf8mb4_unicode_ci;

create index idx_position
    on game_events (position_x, position_y);

create index idx_processed
    on game_events (is_processed);

create index idx_severity
    on game_events (severity);

create index idx_source_agent
    on game_events (source_agent_id);

create index idx_target_agent
    on game_events (target_agent_id);

create index idx_timestamp
    on game_events (timestamp);

create index idx_type
    on game_events (type);

create index idx_world_object
    on game_events (world_object_id);

create index idx_interactive
    on world_objects (is_interactive);

create index idx_is_active
    on world_objects (is_active);

create index idx_owner
    on world_objects (owner_agent_id);

create index idx_position
    on world_objects (position_x, position_y);

create index idx_type
    on world_objects (type);

