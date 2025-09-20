package org.example.star_town.agent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 智能体类型枚举
 * 定义不同类型的智能体及其特性
 */
@Getter
@RequiredArgsConstructor
public enum AgentType {
    
    RESIDENT("居民", "普通居民，负责日常活动"),
    WORKER("工人", "工人，负责生产和工作"),
    MERCHANT("商人", "商人，负责交易和商业活动"),
    GUARD("守卫", "守卫，负责安全和秩序"),
    ARTIST("艺术家", "艺术家，负责创作和文化活动"),
    SCIENTIST("科学家", "科学家，负责研究和发明"),
    LEADER("领导者", "领导者，负责管理和决策"),
    CHILD("儿童", "儿童，负责学习和游戏"),
    ELDER("长者", "长者，负责传授经验和智慧"),
    VISITOR("访客", "访客，临时访问者");
    
    private final String displayName;
    private final String description;
    
    /**
     * 获取默认属性
     */
    public AgentProperties getDefaultProperties() {
        return switch (this) {
            case RESIDENT -> new AgentProperties(50, 30, 40, 20, 30);
            case WORKER -> new AgentProperties(60, 50, 30, 15, 25);
            case MERCHANT -> new AgentProperties(40, 70, 60, 80, 40);
            case GUARD -> new AgentProperties(80, 40, 50, 20, 60);
            case ARTIST -> new AgentProperties(30, 60, 80, 70, 50);
            case SCIENTIST -> new AgentProperties(50, 90, 40, 30, 70);
            case LEADER -> new AgentProperties(60, 70, 80, 90, 60);
            case CHILD -> new AgentProperties(20, 30, 50, 10, 40);
            case ELDER -> new AgentProperties(40, 80, 60, 50, 70);
            case VISITOR -> new AgentProperties(30, 40, 30, 20, 30);
        };
    }
    
    /**
     * 获取智能体属性类
     */
    @Getter
    @lombok.AllArgsConstructor
    public static class AgentProperties {
        private final int health;      // 健康值
        private final int intelligence; // 智力
        private final int creativity;   // 创造力
        private final int social;      // 社交能力
        private final int energy;      // 精力
    }
}
