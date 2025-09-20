package org.example.star_town.ai.goap;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动作执行上下文
 * 包含动作执行所需的所有信息
 */
@Getter
@Setter
public class ActionContext {
    
    private final String agentId;
    private final Map<String, Object> data;
    private final long startTime;
    
    public ActionContext(String agentId) {
        this.agentId = agentId;
        this.data = new ConcurrentHashMap<>();
        this.startTime = System.currentTimeMillis();
    }
    
    /**
     * 存储数据
     */
    public void put(String key, Object value) {
        data.put(key, value);
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
     * 获取执行时长
     */
    public long getExecutionTime() {
        return System.currentTimeMillis() - startTime;
    }
}
