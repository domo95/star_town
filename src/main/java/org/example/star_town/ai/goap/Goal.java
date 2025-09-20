package org.example.star_town.ai.goap;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;
import java.util.HashMap;

/**
 * GOAP目标
 * 定义智能体想要达到的状态
 */
@Getter
@Setter
public class Goal {
    
    private String name;
    private String description;
    private Map<String, Object> desiredState;
    private int priority;
    private boolean persistent; // 是否持续目标
    
    public Goal(String name) {
        this.name = name;
        this.desiredState = new HashMap<>();
        this.priority = 0;
        this.persistent = false;
    }
    
    public Goal(String name, Map<String, Object> desiredState) {
        this.name = name;
        this.desiredState = new HashMap<>(desiredState);
        this.priority = 0;
        this.persistent = false;
    }
    
    public Goal(String name, Map<String, Object> desiredState, int priority) {
        this.name = name;
        this.desiredState = new HashMap<>(desiredState);
        this.priority = priority;
        this.persistent = false;
    }
    
    /**
     * 添加期望状态
     */
    public Goal addDesiredState(String key, Object value) {
        desiredState.put(key, value);
        return this;
    }
    
    /**
     * 检查当前状态是否满足目标
     */
    public boolean isSatisfied(Map<String, Object> currentState) {
        for (Map.Entry<String, Object> entry : desiredState.entrySet()) {
            Object currentValue = currentState.get(entry.getKey());
            if (!entry.getValue().equals(currentValue)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 计算目标的重要程度（用于优先级排序）
     */
    public float calculateImportance() {
        return priority * 1.0f;
    }
    
    @Override
    public String toString() {
        return String.format("Goal{name='%s', priority=%d, persistent=%s, desiredState=%s}", 
                name, priority, persistent, desiredState);
    }
}
