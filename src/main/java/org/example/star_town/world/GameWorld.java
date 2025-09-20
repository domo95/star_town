package org.example.star_town.world;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.star_town.agent.Agent;
import org.example.star_town.agent.AgentManager;
import org.example.star_town.model.WorldObjectEntity;
import org.example.star_town.service.GameEventService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 游戏世界
 * 管理游戏世界的状态和对象
 */
@Slf4j
@Component
@Getter
@Setter
public class GameWorld {
    
    private final AgentManager agentManager;
    private final GameEventService gameEventService;
    
    private double worldWidth = 1000.0;
    private double worldHeight = 1000.0;
    private long gameTime = 0; // 游戏时间（游戏tick数）
    private boolean paused = false;
    
    // 世界对象映射
    private final Map<Long, WorldObjectEntity> worldObjects = new ConcurrentHashMap<>();
    
    // 位置索引（用于快速查找附近对象）
    private final Map<String, List<Long>> spatialIndex = new ConcurrentHashMap<>();
    
    public GameWorld(AgentManager agentManager, GameEventService gameEventService) {
        this.agentManager = agentManager;
        this.gameEventService = gameEventService;
    }
    
    /**
     * 更新游戏世界
     */
    public void update() {
        if (paused) {
            return;
        }
        
        gameTime++;
        
        // 更新所有智能体
        agentManager.updateAllAgents();
        
        // 处理空间索引更新
        updateSpatialIndex();
        
        // 处理碰撞检测
        handleCollisions();
        
        // 处理交互
        handleInteractions();
        
        log.debug("Game world updated, time: {}", gameTime);
    }
    
    /**
     * 添加世界对象
     */
    public void addWorldObject(WorldObjectEntity object) {
        worldObjects.put(object.getId(), object);
        updateObjectInSpatialIndex(object);
        
        gameEventService.recordEvent("OBJECT_CREATED", 
                "World object created: " + object.getName(), 
                Map.of("objectId", object.getId(), "type", object.getType()));
        
        log.debug("Added world object: {} at ({}, {})", object.getName(), 
                object.getPositionX(), object.getPositionY());
    }
    
    /**
     * 移除世界对象
     */
    public void removeWorldObject(Long objectId) {
        WorldObjectEntity object = worldObjects.remove(objectId);
        if (object != null) {
            removeObjectFromSpatialIndex(object);
            
            gameEventService.recordEvent("OBJECT_REMOVED", 
                    "World object removed: " + object.getName(), 
                    Map.of("objectId", objectId, "type", object.getType()));
            
            log.debug("Removed world object: {}", object.getName());
        }
    }
    
    /**
     * 获取世界对象
     */
    public WorldObjectEntity getWorldObject(Long objectId) {
        return worldObjects.get(objectId);
    }
    
    /**
     * 获取附近的世界对象
     */
    public List<WorldObjectEntity> getNearbyObjects(double x, double y, double radius) {
        List<WorldObjectEntity> nearby = new ArrayList<>();
        
        // 使用空间索引快速查找
        String key = getSpatialKey(x, y);
        List<String> nearbyKeys = getNearbySpatialKeys(x, y, radius);
        
        for (String spatialKey : nearbyKeys) {
            List<Long> objectIds = spatialIndex.get(spatialKey);
            if (objectIds != null) {
                for (Long objectId : objectIds) {
                    WorldObjectEntity object = worldObjects.get(objectId);
                    if (object != null && isWithinRadius(object, x, y, radius)) {
                        nearby.add(object);
                    }
                }
            }
        }
        
        return nearby;
    }
    
    /**
     * 获取附近的智能体
     */
    public List<Agent> getNearbyAgents(double x, double y, double radius) {
        return agentManager.getAllAgents().stream()
                .filter(agent -> agent.getPosition().distanceTo(new Position(x, y)) <= radius)
                .toList();
    }
    
    /**
     * 检查位置是否可用（没有碰撞）
     */
    public boolean isPositionAvailable(double x, double y, double width, double height) {
        // 检查边界
        if (x < 0 || y < 0 || x + width > worldWidth || y + height > worldHeight) {
            return false;
        }
        
        // 检查与其他对象的碰撞
        List<WorldObjectEntity> nearby = getNearbyObjects(x + width/2, y + height/2, Math.max(width, height));
        
        for (WorldObjectEntity object : nearby) {
            if (object.getWidth() != null && object.getHeight() != null) {
                if (isRectangleCollision(x, y, width, height, 
                        object.getPositionX(), object.getPositionY(), 
                        object.getWidth(), object.getHeight())) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * 移动智能体到指定位置
     */
    public boolean moveAgent(Agent agent, Position targetPosition) {
        // 检查目标位置是否可用
        if (isPositionAvailable(targetPosition.getX(), targetPosition.getY(), 1, 1)) {
            agent.setPosition(targetPosition);
            
            gameEventService.recordAgentEvent("AGENT_MOVED", agent.getId(),
                    "Agent moved to " + targetPosition,
                    Map.of("fromX", agent.getPosition().getX(), 
                           "fromY", agent.getPosition().getY(),
                           "toX", targetPosition.getX(), 
                           "toY", targetPosition.getY()));
            
            return true;
        }
        
        return false;
    }
    
    /**
     * 暂停/恢复游戏
     */
    public void setPaused(boolean paused) {
        this.paused = paused;
        gameEventService.recordEvent("GAME_PAUSED", 
                "Game " + (paused ? "paused" : "resumed"), 
                Map.of("paused", paused, "gameTime", gameTime));
        
        log.info("Game {}", paused ? "paused" : "resumed");
    }
    
    /**
     * 获取游戏统计信息
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("gameTime", gameTime);
        stats.put("worldSize", worldWidth + "x" + worldHeight);
        stats.put("objectCount", worldObjects.size());
        stats.put("agentCount", agentManager.getAgentCount());
        stats.put("paused", paused);
        stats.put("agentStatusStats", agentManager.getStatusStatistics());
        
        return stats;
    }
    
    /**
     * 更新空间索引
     */
    private void updateSpatialIndex() {
        // 这里可以实现增量更新逻辑
        // 为了简化，我们重新构建整个索引
        spatialIndex.clear();
        for (WorldObjectEntity object : worldObjects.values()) {
            updateObjectInSpatialIndex(object);
        }
    }
    
    /**
     * 将对象添加到空间索引
     */
    private void updateObjectInSpatialIndex(WorldObjectEntity object) {
        String key = getSpatialKey(object.getPositionX(), object.getPositionY());
        spatialIndex.computeIfAbsent(key, k -> new ArrayList<>()).add(object.getId());
    }
    
    /**
     * 从空间索引移除对象
     */
    private void removeObjectFromSpatialIndex(WorldObjectEntity object) {
        String key = getSpatialKey(object.getPositionX(), object.getPositionY());
        List<Long> objects = spatialIndex.get(key);
        if (objects != null) {
            objects.remove(object.getId());
            if (objects.isEmpty()) {
                spatialIndex.remove(key);
            }
        }
    }
    
    /**
     * 获取空间索引键
     */
    private String getSpatialKey(double x, double y) {
        // 使用网格划分空间，每个网格大小为100x100
        int gridX = (int) (x / 100);
        int gridY = (int) (y / 100);
        return gridX + "," + gridY;
    }
    
    /**
     * 获取附近的空间索引键
     */
    private List<String> getNearbySpatialKeys(double x, double y, double radius) {
        List<String> keys = new ArrayList<>();
        int gridRadius = (int) Math.ceil(radius / 100) + 1;
        
        int centerX = (int) (x / 100);
        int centerY = (int) (y / 100);
        
        for (int dx = -gridRadius; dx <= gridRadius; dx++) {
            for (int dy = -gridRadius; dy <= gridRadius; dy++) {
                keys.add((centerX + dx) + "," + (centerY + dy));
            }
        }
        
        return keys;
    }
    
    /**
     * 检查是否在半径内
     */
    private boolean isWithinRadius(WorldObjectEntity object, double x, double y, double radius) {
        double distance = Math.sqrt(Math.pow(object.getPositionX() - x, 2) + Math.pow(object.getPositionY() - y, 2));
        return distance <= radius;
    }
    
    /**
     * 检查矩形碰撞
     */
    private boolean isRectangleCollision(double x1, double y1, double w1, double h1,
                                       double x2, double y2, double w2, double h2) {
        return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
    }
    
    /**
     * 处理碰撞
     */
    private void handleCollisions() {
        // 这里可以实现碰撞处理逻辑
        // 例如：智能体之间的碰撞、智能体与物体的碰撞等
    }
    
    /**
     * 处理交互
     */
    private void handleInteractions() {
        // 这里可以实现交互处理逻辑
        // 例如：智能体与物体的交互、智能体之间的交互等
    }
}
