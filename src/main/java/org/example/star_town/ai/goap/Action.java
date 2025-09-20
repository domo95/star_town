package org.example.star_town.ai.goap;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;
import java.util.HashMap;

/**
 * GOAP动作
 * 定义智能体可以执行的动作及其前置条件和效果
 */
@Getter
@Setter
public abstract class Action {
    
    protected String name;
    protected String description;
    protected Map<String, Object> preconditions;
    protected Map<String, Object> effects;
    protected int cost;
    protected long duration; // 执行时长（毫秒）
    
    public Action(String name) {
        this.name = name;
        this.preconditions = new HashMap<>();
        this.effects = new HashMap<>();
        this.cost = 1;
        this.duration = 1000; // 默认1秒
    }
    
    /**
     * 添加前置条件
     */
    public Action addPrecondition(String key, Object value) {
        preconditions.put(key, value);
        return this;
    }
    
    /**
     * 添加效果
     */
    public Action addEffect(String key, Object value) {
        effects.put(key, value);
        return this;
    }
    
    /**
     * 检查前置条件是否满足
     */
    public boolean checkPreconditions(Map<String, Object> currentState) {
        for (Map.Entry<String, Object> entry : preconditions.entrySet()) {
            Object currentValue = currentState.get(entry.getKey());
            if (!entry.getValue().equals(currentValue)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 应用动作效果到状态
     */
    public Map<String, Object> applyEffects(Map<String, Object> currentState) {
        Map<String, Object> newState = new HashMap<>(currentState);
        newState.putAll(effects);
        return newState;
    }
    
    /**
     * 计算动作成本（子类可以重写以提供更精确的成本计算）
     */
    public int calculateCost(Map<String, Object> currentState) {
        return cost;
    }
    
    /**
     * 检查动作是否可以在当前状态下执行
     */
    public boolean canExecute(Map<String, Object> currentState) {
        return checkPreconditions(currentState);
    }
    
    /**
     * 执行动作（子类必须实现）
     */
    public abstract boolean execute(ActionContext context);
    
    /**
     * 重置动作状态
     */
    public void reset() {
        // 子类可以重写以重置内部状态
    }
    
    @Override
    public String toString() {
        return String.format("Action{name='%s', cost=%d, duration=%d, preconditions=%s, effects=%s}", 
                name, cost, duration, preconditions, effects);
    }
}
