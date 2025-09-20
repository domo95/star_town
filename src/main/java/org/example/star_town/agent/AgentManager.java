package org.example.star_town.agent;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.star_town.ai.blackboard.Blackboard;
import org.example.star_town.service.BehaviorTreeConfigService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 智能体管理器
 * 负责管理所有智能体的生命周期和更新
 */
@Slf4j
@Component
@Getter
public class AgentManager {
    
    private final Map<String, Agent> agents = new ConcurrentHashMap<>();
    private final Blackboard blackboard;
    private final ScheduledExecutorService scheduler;
    private final BehaviorTreeConfigService behaviorTreeService;
    private boolean running = false;
    private long updateInterval = 1000; // 默认1秒更新一次
    
    public AgentManager(BehaviorTreeConfigService behaviorTreeService) {
        this.blackboard = new Blackboard();
        this.scheduler = Executors.newScheduledThreadPool(4);
        this.behaviorTreeService = behaviorTreeService;
    }
    
    /**
     * 启动智能体管理器
     */
    public void start() {
        if (running) {
            log.warn("AgentManager is already running");
            return;
        }
        
        running = true;
        scheduler.scheduleAtFixedRate(this::updateAllAgents, 0, updateInterval, TimeUnit.MILLISECONDS);
        log.info("AgentManager started with {} agents", agents.size());
    }
    
    /**
     * 停止智能体管理器
     */
    public void stop() {
        if (!running) {
            log.warn("AgentManager is not running");
            return;
        }
        
        running = false;
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("AgentManager stopped");
    }
    
    /**
     * 更新所有智能体
     */
    public void updateAllAgents() {
        if (!running) {
            return;
        }
        
        try {
            for (Agent agent : agents.values()) {
                if (agent.getConfig().isEnabled()) {
                    agent.update();
                }
            }
        } catch (Exception e) {
            log.error("Error updating agents: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 创建智能体
     */
    public Agent createAgent(String id, String name, AgentType type, AgentConfig config) {
        if (agents.containsKey(id)) {
            log.warn("Agent with id {} already exists", id);
            return agents.get(id);
        }
        
        Agent agent = new Agent(id, name, type, config);
        agent.setBlackboard(blackboard);
        agent.setBehaviorTreeService(behaviorTreeService);
        agents.put(id, agent);
        
        log.info("Created agent: {} ({})", name, type.getDisplayName());
        return agent;
    }
    
    /**
     * 移除智能体
     */
    public void removeAgent(String agentId) {
        Agent agent = agents.remove(agentId);
        if (agent != null) {
            blackboard.removeAgent(agentId);
            log.info("Removed agent: {}", agentId);
        }
    }
    
    /**
     * 获取智能体
     */
    public Agent getAgent(String agentId) {
        return agents.get(agentId);
    }
    
    /**
     * 获取所有智能体
     */
    public List<Agent> getAllAgents() {
        return new ArrayList<>(agents.values());
    }
    
    /**
     * 根据类型获取智能体
     */
    public List<Agent> getAgentsByType(AgentType type) {
        return agents.values().stream()
                .filter(agent -> agent.getType() == type)
                .toList();
    }
    
    /**
     * 获取智能体状态统计
     */
    public Map<Agent.AgentStatus, Integer> getStatusStatistics() {
        Map<Agent.AgentStatus, Integer> stats = new HashMap<>();
        
        for (Agent.AgentStatus status : Agent.AgentStatus.values()) {
            stats.put(status, 0);
        }
        
        for (Agent agent : agents.values()) {
            Agent.AgentStatus status = agent.getStatus();
            stats.put(status, stats.get(status) + 1);
        }
        
        return stats;
    }
    
    /**
     * 设置更新间隔
     */
    public void setUpdateInterval(long intervalMs) {
        this.updateInterval = intervalMs;
        log.info("Update interval set to {} ms", intervalMs);
    }
    
    /**
     * 重置所有智能体
     */
    public void resetAllAgents() {
        for (Agent agent : agents.values()) {
            agent.reset();
        }
        log.info("Reset all agents");
    }
    
    /**
     * 获取黑板
     */
    public Blackboard getBlackboard() {
        return blackboard;
    }
    
    /**
     * 批量创建智能体
     */
    public void createAgentsFromConfigs(List<AgentConfig> configs) {
        for (AgentConfig config : configs) {
            createAgent(config.getId(), config.getName(), config.getType(), config);
        }
        log.info("Created {} agents from configurations", configs.size());
    }
    
    /**
     * 获取智能体数量
     */
    public int getAgentCount() {
        return agents.size();
    }
    
    /**
     * 检查智能体是否存在
     */
    public boolean hasAgent(String agentId) {
        return agents.containsKey(agentId);
    }
}
