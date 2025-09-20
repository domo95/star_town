package org.example.star_town.agent;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;
import java.util.HashMap;

/**
 * 智能体配置
 * 支持配置化的智能体参数设置
 */
@Getter
@Setter
public class AgentConfig {
    
    private String id;
    private String name;
    private AgentType type;
    private Map<String, Object> properties;
    private Map<String, Object> behaviorSettings;
    private Map<String, Object> goalSettings;
    private Map<String, Object> actionSettings;
    private boolean enabled;
    private int priority;
    
    public AgentConfig() {
        this.properties = new HashMap<>();
        this.behaviorSettings = new HashMap<>();
        this.goalSettings = new HashMap<>();
        this.actionSettings = new HashMap<>();
        this.enabled = true;
        this.priority = 0;
    }
    
    public AgentConfig(String id, String name, AgentType type) {
        this();
        this.id = id;
        this.name = name;
        this.type = type;
    }
    
    /**
     * 设置属性
     */
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }
    
    /**
     * 获取属性
     */
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key, Class<T> type) {
        Object value = properties.get(key);
        if (value != null && type.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        return null;
    }
    
    /**
     * 获取属性，带默认值
     */
    @SuppressWarnings("unchecked")
    public <T> T getPropertyOrDefault(String key, T defaultValue, Class<T> type) {
        T value = getProperty(key, type);
        return value != null ? value : defaultValue;
    }
    
    /**
     * 设置行为设置
     */
    public void setBehaviorSetting(String key, Object value) {
        behaviorSettings.put(key, value);
    }
    
    /**
     * 获取行为设置
     */
    @SuppressWarnings("unchecked")
    public <T> T getBehaviorSetting(String key, Class<T> type) {
        Object value = behaviorSettings.get(key);
        if (value != null && type.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        return null;
    }
    
    /**
     * 设置目标设置
     */
    public void setGoalSetting(String key, Object value) {
        goalSettings.put(key, value);
    }
    
    /**
     * 获取目标设置
     */
    @SuppressWarnings("unchecked")
    public <T> T getGoalSetting(String key, Class<T> type) {
        Object value = goalSettings.get(key);
        if (value != null && type.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        return null;
    }
    
    /**
     * 设置动作设置
     */
    public void setActionSetting(String key, Object value) {
        actionSettings.put(key, value);
    }
    
    /**
     * 获取动作设置
     */
    @SuppressWarnings("unchecked")
    public <T> T getActionSetting(String key, Class<T> type) {
        Object value = actionSettings.get(key);
        if (value != null && type.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        return null;
    }
    
    /**
     * 获取更新频率（毫秒）
     */
    public long getUpdateFrequency() {
        return getBehaviorSettingOrDefault("updateFrequency", 1000L, Long.class);
    }
    
    /**
     * 获取最大计划长度
     */
    public int getMaxPlanLength() {
        return getBehaviorSettingOrDefault("maxPlanLength", 10, Integer.class);
    }
    
    /**
     * 获取目标优先级
     */
    public int getGoalPriority() {
        return getGoalSettingOrDefault("priority", 1, Integer.class);
    }
    
    /**
     * 是否启用AI
     */
    public boolean isAiEnabled() {
        return getBehaviorSettingOrDefault("aiEnabled", true, Boolean.class);
    }
    
    /**
     * 获取行为设置，带默认值
     */
    @SuppressWarnings("unchecked")
    public <T> T getBehaviorSettingOrDefault(String key, T defaultValue, Class<T> type) {
        T value = getBehaviorSetting(key, type);
        return value != null ? value : defaultValue;
    }
    
    /**
     * 获取目标设置，带默认值
     */
    @SuppressWarnings("unchecked")
    public <T> T getGoalSettingOrDefault(String key, T defaultValue, Class<T> type) {
        T value = getGoalSetting(key, type);
        return value != null ? value : defaultValue;
    }
    
    /**
     * 获取动作设置，带默认值
     */
    @SuppressWarnings("unchecked")
    public <T> T getActionSettingOrDefault(String key, T defaultValue, Class<T> type) {
        T value = getActionSetting(key, type);
        return value != null ? value : defaultValue;
    }
}
