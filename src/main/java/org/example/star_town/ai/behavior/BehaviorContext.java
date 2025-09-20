package org.example.star_town.ai.behavior;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 行为树执行上下文
 * 存储执行过程中的共享数据
 */
@Getter
@Setter
@Slf4j
public class BehaviorContext {
    
    private static final Logger log = LoggerFactory.getLogger(BehaviorContext.class);
    
    private final Map<String, Object> data = new ConcurrentHashMap<>();
    private final long startTime;
    private final String agentId;
    
    public BehaviorContext(String agentId) {
        this.agentId = agentId;
        this.startTime = System.currentTimeMillis();
    }
    
    /**
     * 存储数据
     */
    public void put(String key, Object value) {
        if (value != null) {
            data.put(key, value);
        } else {
            log.warn("Attempted to put null value for key '{}' in BehaviorContext for agent '{}'", key, agentId);
        }
    }
    
    /**
     * 安全存储数据，如果值为null则使用默认值
     */
    public void putSafe(String key, Object value, Object defaultValue) {
        data.put(key, value != null ? value : defaultValue);
    }
    
    /**
     * 获取数据
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        Object value = data.get(key);
        if (value != null && type.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        return null;
    }
    
    /**
     * 获取数据，带默认值
     */
    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(String key, T defaultValue, Class<T> type) {
        T value = get(key, type);
        return value != null ? value : defaultValue;
    }
    
    /**
     * 检查是否包含某个键
     */
    public boolean contains(String key) {
        return data.containsKey(key);
    }
    
    /**
     * 移除数据
     */
    public Object remove(String key) {
        return data.remove(key);
    }
    
    /**
     * 清空所有数据
     */
    public void clear() {
        data.clear();
    }
    
    /**
     * 获取执行时长
     */
    public long getExecutionTime() {
        return System.currentTimeMillis() - startTime;
    }
}
