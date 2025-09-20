package org.example.star_town.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.star_town.agent.Agent;
import org.example.star_town.agent.AgentManager;
import org.example.star_town.agent.AgentType;
import org.example.star_town.world.GameWorld;
import org.example.star_town.world.Position;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 游戏控制器
 * 提供游戏状态控制和监控的REST API
 */
@Slf4j
@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {
    
    private final GameWorld gameWorld;
    private final AgentManager agentManager;
    
    /**
     * 获取游戏状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getGameStatus() {
        Map<String, Object> status = gameWorld.getStatistics();
        return ResponseEntity.ok(status);
    }
    
    /**
     * 暂停/恢复游戏
     */
    @PostMapping("/pause")
    public ResponseEntity<Map<String, Object>> pauseGame(@RequestParam boolean paused) {
        gameWorld.setPaused(paused);
        return ResponseEntity.ok(Map.of(
                "paused", paused,
                "message", "Game " + (paused ? "paused" : "resumed")
        ));
    }
    
    /**
     * 获取所有智能体
     */
    @GetMapping("/agents")
    public ResponseEntity<List<Agent>> getAllAgents() {
        List<Agent> agents = agentManager.getAllAgents();
        return ResponseEntity.ok(agents);
    }
    
    /**
     * 获取指定智能体
     */
    @GetMapping("/agents/{agentId}")
    public ResponseEntity<Agent> getAgent(@PathVariable String agentId) {
        Agent agent = agentManager.getAgent(agentId);
        if (agent != null) {
            return ResponseEntity.ok(agent);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 创建新智能体
     */
    @PostMapping("/agents")
    public ResponseEntity<Agent> createAgent(@RequestBody CreateAgentRequest request) {
        try {
            Agent agent = agentManager.createAgent(
                    request.getId(),
                    request.getName(),
                    request.getType(),
                    request.getConfig()
            );
            
            // 设置初始位置
            if (request.getPosition() != null) {
                agent.setPosition(request.getPosition());
            }
            
            // 设置初始状态
            if (request.getInitialState() != null) {
                agent.setState(request.getInitialState());
            }
            
            return ResponseEntity.ok(agent);
        } catch (Exception e) {
            log.error("Error creating agent: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 移除智能体
     */
    @DeleteMapping("/agents/{agentId}")
    public ResponseEntity<Map<String, Object>> removeAgent(@PathVariable String agentId) {
        if (agentManager.hasAgent(agentId)) {
            agentManager.removeAgent(agentId);
            return ResponseEntity.ok(Map.of("message", "Agent removed successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 移动智能体
     */
    @PostMapping("/agents/{agentId}/move")
    public ResponseEntity<Map<String, Object>> moveAgent(
            @PathVariable String agentId,
            @RequestBody MoveRequest request) {
        
        Agent agent = agentManager.getAgent(agentId);
        if (agent == null) {
            return ResponseEntity.notFound().build();
        }
        
        Position targetPosition = new Position(request.getX(), request.getY());
        boolean success = gameWorld.moveAgent(agent, targetPosition);
        
        if (success) {
            return ResponseEntity.ok(Map.of(
                    "message", "Agent moved successfully",
                    "position", agent.getPosition()
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Failed to move agent to target position"
            ));
        }
    }
    
    /**
     * 获取智能体状态统计
     */
    @GetMapping("/agents/statistics")
    public ResponseEntity<Map<Agent.AgentStatus, Integer>> getAgentStatistics() {
        Map<Agent.AgentStatus, Integer> stats = agentManager.getStatusStatistics();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 根据类型获取智能体
     */
    @GetMapping("/agents/type/{type}")
    public ResponseEntity<List<Agent>> getAgentsByType(@PathVariable AgentType type) {
        List<Agent> agents = agentManager.getAgentsByType(type);
        return ResponseEntity.ok(agents);
    }
    
    /**
     * 获取附近的智能体
     */
    @GetMapping("/agents/nearby")
    public ResponseEntity<List<Agent>> getNearbyAgents(
            @RequestParam double x,
            @RequestParam double y,
            @RequestParam double radius) {
        
        List<Agent> nearbyAgents = gameWorld.getNearbyAgents(x, y, radius);
        return ResponseEntity.ok(nearbyAgents);
    }
    
    /**
     * 重置所有智能体
     */
    @PostMapping("/agents/reset")
    public ResponseEntity<Map<String, Object>> resetAllAgents() {
        agentManager.resetAllAgents();
        return ResponseEntity.ok(Map.of("message", "All agents reset successfully"));
    }
    
    /**
     * 创建智能体请求
     */
    public static class CreateAgentRequest {
        private String id;
        private String name;
        private AgentType type;
        private org.example.star_town.agent.AgentConfig config;
        private Position position;
        private Map<String, Object> initialState;
        
        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public AgentType getType() { return type; }
        public void setType(AgentType type) { this.type = type; }
        
        public org.example.star_town.agent.AgentConfig getConfig() { return config; }
        public void setConfig(org.example.star_town.agent.AgentConfig config) { this.config = config; }
        
        public Position getPosition() { return position; }
        public void setPosition(Position position) { this.position = position; }
        
        public Map<String, Object> getInitialState() { return initialState; }
        public void setInitialState(Map<String, Object> initialState) { this.initialState = initialState; }
    }
    
    /**
     * 移动请求
     */
    public static class MoveRequest {
        private double x;
        private double y;
        
        // Getters and setters
        public double getX() { return x; }
        public void setX(double x) { this.x = x; }
        
        public double getY() { return y; }
        public void setY(double y) { this.y = y; }
    }
}
