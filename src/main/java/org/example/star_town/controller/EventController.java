package org.example.star_town.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.star_town.model.GameEventEntity;
import org.example.star_town.service.GameEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 事件控制器
 * 提供游戏事件查询和管理的REST API
 */
@Slf4j
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    
    private final GameEventService gameEventService;
    
    /**
     * 获取最近的事件
     */
    @GetMapping("/recent")
    public ResponseEntity<List<GameEventEntity>> getRecentEvents(
            @RequestParam(defaultValue = "1") int hours) {
        
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        List<GameEventEntity> events = gameEventService.getRecentEvents(since);
        
        return ResponseEntity.ok(events);
    }
    
    /**
     * 根据类型获取事件
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<GameEventEntity>> getEventsByType(@PathVariable String type) {
        List<GameEventEntity> events = gameEventService.getEventsByType(type);
        return ResponseEntity.ok(events);
    }
    
    /**
     * 获取智能体的事件历史
     */
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<GameEventEntity>> getAgentEvents(
            @PathVariable String agentId,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {
        
        LocalDateTime startTime = start != null ? LocalDateTime.parse(start) : 
                LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = end != null ? LocalDateTime.parse(end) : 
                LocalDateTime.now();
        
        List<GameEventEntity> events = gameEventService.getAgentEvents(agentId, startTime, endTime);
        return ResponseEntity.ok(events);
    }
    
    /**
     * 获取未处理的事件
     */
    @GetMapping("/unprocessed")
    public ResponseEntity<List<GameEventEntity>> getUnprocessedEvents() {
        List<GameEventEntity> events = gameEventService.getUnprocessedEvents();
        return ResponseEntity.ok(events);
    }
    
    /**
     * 标记事件为已处理
     */
    @PostMapping("/{eventId}/process")
    public ResponseEntity<Map<String, Object>> markEventAsProcessed(@PathVariable Long eventId) {
        gameEventService.markEventAsProcessed(eventId);
        return ResponseEntity.ok(Map.of("message", "Event marked as processed"));
    }
    
    /**
     * 批量标记事件为已处理
     */
    @PostMapping("/batch/process")
    public ResponseEntity<Map<String, Object>> markEventsAsProcessed(
            @RequestBody List<Long> eventIds) {
        
        gameEventService.markEventsAsProcessed(eventIds);
        return ResponseEntity.ok(Map.of(
                "message", "Events marked as processed",
                "count", eventIds.size()
        ));
    }
    
    /**
     * 获取事件统计
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getEventStatistics() {
        Map<String, Long> stats = gameEventService.getEventStatistics();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 获取事件详情
     */
    @GetMapping("/{eventId}")
    public ResponseEntity<GameEventEntity> getEvent(@PathVariable Long eventId) {
        return gameEventService.getEvent(eventId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 获取事件数据
     */
    @GetMapping("/{eventId}/data")
    public ResponseEntity<Map<String, Object>> getEventData(@PathVariable Long eventId) {
        return gameEventService.getEvent(eventId)
                .map(event -> ResponseEntity.ok(gameEventService.parseEventData(event)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 记录新事件
     */
    @PostMapping("/record")
    public ResponseEntity<GameEventEntity> recordEvent(@RequestBody EventRequest request) {
        GameEventEntity event = gameEventService.recordEvent(
                request.getType(),
                request.getDescription(),
                request.getData()
        );
        
        return ResponseEntity.ok(event);
    }
    
    /**
     * 记录智能体事件
     */
    @PostMapping("/record/agent")
    public ResponseEntity<GameEventEntity> recordAgentEvent(@RequestBody AgentEventRequest request) {
        GameEventEntity event = gameEventService.recordAgentEvent(
                request.getType(),
                request.getAgentId(),
                request.getDescription(),
                request.getData()
        );
        
        return ResponseEntity.ok(event);
    }
    
    /**
     * 记录交互事件
     */
    @PostMapping("/record/interaction")
    public ResponseEntity<GameEventEntity> recordInteractionEvent(@RequestBody InteractionEventRequest request) {
        GameEventEntity event = gameEventService.recordInteractionEvent(
                request.getType(),
                request.getSourceAgentId(),
                request.getTargetAgentId(),
                request.getDescription(),
                request.getData()
        );
        
        return ResponseEntity.ok(event);
    }
    
    /**
     * 清理旧事件
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupOldEvents(
            @RequestParam(defaultValue = "7") int days) {
        
        LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
        gameEventService.deleteOldEvents(cutoff);
        
        return ResponseEntity.ok(Map.of(
                "message", "Old events cleaned up",
                "cutoff", cutoff
        ));
    }
    
    /**
     * 事件请求
     */
    public static class EventRequest {
        private String type;
        private String description;
        private Map<String, Object> data;
        
        // Getters and setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Map<String, Object> getData() { return data; }
        public void setData(Map<String, Object> data) { this.data = data; }
    }
    
    /**
     * 智能体事件请求
     */
    public static class AgentEventRequest {
        private String type;
        private String agentId;
        private String description;
        private Map<String, Object> data;
        
        // Getters and setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getAgentId() { return agentId; }
        public void setAgentId(String agentId) { this.agentId = agentId; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Map<String, Object> getData() { return data; }
        public void setData(Map<String, Object> data) { this.data = data; }
    }
    
    /**
     * 交互事件请求
     */
    public static class InteractionEventRequest {
        private String type;
        private String sourceAgentId;
        private String targetAgentId;
        private String description;
        private Map<String, Object> data;
        
        // Getters and setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getSourceAgentId() { return sourceAgentId; }
        public void setSourceAgentId(String sourceAgentId) { this.sourceAgentId = sourceAgentId; }
        
        public String getTargetAgentId() { return targetAgentId; }
        public void setTargetAgentId(String targetAgentId) { this.targetAgentId = targetAgentId; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Map<String, Object> getData() { return data; }
        public void setData(Map<String, Object> data) { this.data = data; }
    }
}
