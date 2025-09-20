package org.example.star_town.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.star_town.agent.Agent;
import org.example.star_town.agent.AgentManager;
import org.example.star_town.ai.goap.Goal;
import org.example.star_town.ai.goap.Action;
import org.example.star_town.service.AgentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 智能体控制器
 * 提供智能体管理的REST API
 */
@Slf4j
@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
public class AgentController {
    
    private final AgentManager agentManager;
    private final AgentService agentService;
    
    /**
     * 获取智能体详细信息
     */
    @GetMapping("/{agentId}/details")
    public ResponseEntity<Map<String, Object>> getAgentDetails(@PathVariable String agentId) {
        Agent agent = agentManager.getAgent(agentId);
        if (agent == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> details = new HashMap<>();
        details.put("id", agent.getId());
        details.put("name", agent.getName());
        details.put("type", agent.getType());
        details.put("position", agent.getPosition());
        details.put("status", agent.getStatus());
        details.put("state", agent.getState());
        details.put("memory", agent.getMemory());
        details.put("goals", agent.getGoals());
        details.put("availableActions", agent.getAvailableActions().size());
        details.put("currentPlan", agent.getCurrentPlan().size());
        details.put("lastUpdateTime", agent.getLastUpdateTime());
        
        return ResponseEntity.ok(details);
    }
    
    /**
     * 添加目标到智能体
     */
    @PostMapping("/{agentId}/goals")
    public ResponseEntity<Map<String, Object>> addGoal(
            @PathVariable String agentId,
            @RequestBody GoalRequest request) {
        
        Agent agent = agentManager.getAgent(agentId);
        if (agent == null) {
            return ResponseEntity.notFound().build();
        }
        
        Goal goal = new Goal(request.getName(), request.getDesiredState(), request.getPriority());
        goal.setPersistent(request.isPersistent());
        agent.addGoal(goal);
        
        return ResponseEntity.ok(Map.of(
                "message", "Goal added successfully",
                "goal", goal
        ));
    }
    
    /**
     * 移除智能体的目标
     */
    @DeleteMapping("/{agentId}/goals/{goalName}")
    public ResponseEntity<Map<String, Object>> removeGoal(
            @PathVariable String agentId,
            @PathVariable String goalName) {
        
        Agent agent = agentManager.getAgent(agentId);
        if (agent == null) {
            return ResponseEntity.notFound().build();
        }
        
        agent.removeGoal(goalName);
        
        return ResponseEntity.ok(Map.of("message", "Goal removed successfully"));
    }
    
    /**
     * 获取智能体的目标列表
     */
    @GetMapping("/{agentId}/goals")
    public ResponseEntity<List<Goal>> getAgentGoals(@PathVariable String agentId) {
        Agent agent = agentManager.getAgent(agentId);
        if (agent == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(agent.getGoals());
    }
    
    /**
     * 设置智能体状态值
     */
    @PostMapping("/{agentId}/state")
    public ResponseEntity<Map<String, Object>> setAgentState(
            @PathVariable String agentId,
            @RequestBody Map<String, Object> stateUpdates) {
        
        Agent agent = agentManager.getAgent(agentId);
        if (agent == null) {
            return ResponseEntity.notFound().build();
        }
        
        for (Map.Entry<String, Object> entry : stateUpdates.entrySet()) {
            agent.setState(entry.getKey(), entry.getValue());
        }
        
        return ResponseEntity.ok(Map.of(
                "message", "Agent state updated successfully",
                "state", agent.getState()
        ));
    }
    
    /**
     * 获取智能体状态
     */
    @GetMapping("/{agentId}/state")
    public ResponseEntity<Map<String, Object>> getAgentState(@PathVariable String agentId) {
        Agent agent = agentManager.getAgent(agentId);
        if (agent == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(agent.getState());
    }
    
    /**
     * 设置智能体记忆
     */
    @PostMapping("/{agentId}/memory")
    public ResponseEntity<Map<String, Object>> setAgentMemory(
            @PathVariable String agentId,
            @RequestBody Map<String, Object> memoryUpdates) {
        
        Agent agent = agentManager.getAgent(agentId);
        if (agent == null) {
            return ResponseEntity.notFound().build();
        }
        
        for (Map.Entry<String, Object> entry : memoryUpdates.entrySet()) {
            agent.setMemory(entry.getKey(), entry.getValue());
        }
        
        return ResponseEntity.ok(Map.of(
                "message", "Agent memory updated successfully",
                "memory", agent.getMemory()
        ));
    }
    
    /**
     * 获取智能体记忆
     */
    @GetMapping("/{agentId}/memory")
    public ResponseEntity<Map<String, Object>> getAgentMemory(@PathVariable String agentId) {
        Agent agent = agentManager.getAgent(agentId);
        if (agent == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(agent.getMemory());
    }
    
    /**
     * 获取智能体当前计划
     */
    @GetMapping("/{agentId}/plan")
    public ResponseEntity<List<Action>> getAgentPlan(@PathVariable String agentId) {
        Agent agent = agentManager.getAgent(agentId);
        if (agent == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(agent.getCurrentPlan());
    }
    
    /**
     * 重置智能体
     */
    @PostMapping("/{agentId}/reset")
    public ResponseEntity<Map<String, Object>> resetAgent(@PathVariable String agentId) {
        Agent agent = agentManager.getAgent(agentId);
        if (agent == null) {
            return ResponseEntity.notFound().build();
        }
        
        agent.reset();
        
        return ResponseEntity.ok(Map.of("message", "Agent reset successfully"));
    }
    
    /**
     * 强制智能体重新规划
     */
    @PostMapping("/{agentId}/replan")
    public ResponseEntity<Map<String, Object>> forceReplan(@PathVariable String agentId) {
        Agent agent = agentManager.getAgent(agentId);
        if (agent == null) {
            return ResponseEntity.notFound().build();
        }
        
        agent.setStatus(Agent.AgentStatus.THINKING);
        
        return ResponseEntity.ok(Map.of("message", "Agent replanning initiated"));
    }
    
    /**
     * 保存智能体到数据库
     */
    @PostMapping("/{agentId}/save")
    public ResponseEntity<Map<String, Object>> saveAgent(@PathVariable String agentId) {
        Agent agent = agentManager.getAgent(agentId);
        if (agent == null) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            agentService.saveAgent(agent);
            return ResponseEntity.ok(Map.of("message", "Agent saved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Failed to save agent: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 从数据库加载智能体
     */
    @PostMapping("/{agentId}/load")
    public ResponseEntity<Map<String, Object>> loadAgent(@PathVariable String agentId) {
        try {
            var agentOpt = agentService.loadAgent(agentId);
            if (agentOpt.isPresent()) {
                Agent agent = agentOpt.get();
                agent.setBlackboard(agentManager.getBlackboard());
                agentManager.getAllAgents().add(agent); // 添加到管理器
                
                return ResponseEntity.ok(Map.of(
                        "message", "Agent loaded successfully",
                        "agent", agent
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Failed to load agent: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 目标请求
     */
    public static class GoalRequest {
        private String name;
        private Map<String, Object> desiredState;
        private int priority;
        private boolean persistent;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public Map<String, Object> getDesiredState() { return desiredState; }
        public void setDesiredState(Map<String, Object> desiredState) { this.desiredState = desiredState; }
        
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        
        public boolean isPersistent() { return persistent; }
        public void setPersistent(boolean persistent) { this.persistent = persistent; }
    }
}
