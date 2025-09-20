package org.example.star_town.ai.blackboard;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 黑板系统
 * 智能体之间共享信息的中央存储
 */
@Slf4j
@Getter
public class Blackboard {
    
    private final Map<String, Object> globalData = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> agentData = new ConcurrentHashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    /**
     * 设置全局数据
     */
    public void setGlobal(String key, Object value) {
        lock.writeLock().lock();
        try {
            globalData.put(key, value);
            log.debug("Set global data: {} = {}", key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * 获取全局数据
     */
    @SuppressWarnings("unchecked")
    public <T> T getGlobal(String key, Class<T> type) {
        lock.readLock().lock();
        try {
            Object value = globalData.get(key);
            if (value != null && type.isAssignableFrom(value.getClass())) {
                return (T) value;
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * 设置智能体特定数据
     */
    public void setAgent(String agentId, String key, Object value) {
        lock.writeLock().lock();
        try {
            agentData.computeIfAbsent(agentId, k -> new ConcurrentHashMap<>()).put(key, value);
            log.debug("Set agent data for {}: {} = {}", agentId, key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * 获取智能体特定数据
     */
    @SuppressWarnings("unchecked")
    public <T> T getAgent(String agentId, String key, Class<T> type) {
        lock.readLock().lock();
        try {
            Map<String, Object> agent = agentData.get(agentId);
            if (agent != null) {
                Object value = agent.get(key);
                if (value != null && type.isAssignableFrom(value.getClass())) {
                    return (T) value;
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * 移除智能体数据
     */
    public void removeAgent(String agentId) {
        lock.writeLock().lock();
        try {
            agentData.remove(agentId);
            log.debug("Removed agent data for: {}", agentId);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * 检查全局数据是否存在
     */
    public boolean hasGlobal(String key) {
        lock.readLock().lock();
        try {
            return globalData.containsKey(key);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * 检查智能体数据是否存在
     */
    public boolean hasAgent(String agentId, String key) {
        lock.readLock().lock();
        try {
            Map<String, Object> agent = agentData.get(agentId);
            return agent != null && agent.containsKey(key);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * 获取所有全局数据
     */
    public Map<String, Object> getAllGlobal() {
        lock.readLock().lock();
        try {
            return new ConcurrentHashMap<>(globalData);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * 获取智能体的所有数据
     */
    public Map<String, Object> getAllAgent(String agentId) {
        lock.readLock().lock();
        try {
            Map<String, Object> agent = agentData.get(agentId);
            return agent != null ? new ConcurrentHashMap<>(agent) : new ConcurrentHashMap<>();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * 清空所有数据
     */
    public void clear() {
        lock.writeLock().lock();
        try {
            globalData.clear();
            agentData.clear();
            log.debug("Cleared all blackboard data");
        } finally {
            lock.writeLock().unlock();
        }
    }
}
