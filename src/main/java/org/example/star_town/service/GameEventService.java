package org.example.star_town.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.star_town.model.GameEventEntity;
import org.example.star_town.repository.GameEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 游戏事件服务层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GameEventService {
    
    private final GameEventRepository gameEventRepository;
    private final ObjectMapper objectMapper;
    
    /**
     * 记录游戏事件
     */
    @Transactional
    public GameEventEntity recordEvent(String type, String description, Map<String, Object> data) {
        return recordEvent(type, null, null, null, description, data, null, null);
    }
    
    /**
     * 记录智能体事件
     */
    @Transactional
    public GameEventEntity recordAgentEvent(String type, String agentId, String description, Map<String, Object> data) {
        return recordEvent(type, agentId, null, null, description, data, null, null);
    }
    
    /**
     * 记录交互事件
     */
    @Transactional
    public GameEventEntity recordInteractionEvent(String type, String sourceAgentId, String targetAgentId, 
                                                  String description, Map<String, Object> data) {
        return recordEvent(type, sourceAgentId, targetAgentId, null, description, data, null, null);
    }
    
    /**
     * 记录位置事件
     */
    @Transactional
    public GameEventEntity recordLocationEvent(String type, String agentId, double x, double y, 
                                               String description, Map<String, Object> data) {
        return recordEvent(type, agentId, null, null, description, data, x, y);
    }
    
    /**
     * 记录完整事件
     */
    @Transactional
    public GameEventEntity recordEvent(String type, String sourceAgentId, String targetAgentId, 
                                       Long worldObjectId, String description, Map<String, Object> data,
                                       Double positionX, Double positionY) {
        try {
            GameEventEntity event = new GameEventEntity();
            event.setType(type);
            event.setSourceAgentId(sourceAgentId);
            event.setTargetAgentId(targetAgentId);
            event.setWorldObjectId(worldObjectId);
            event.setDescription(description);
            event.setDataJson(data != null ? objectMapper.writeValueAsString(data) : null);
            event.setPositionX(positionX);
            event.setPositionY(positionY);
            event.setTimestamp(LocalDateTime.now());
            event.setSeverity("INFO");
            event.setIsProcessed(false);
            
            GameEventEntity savedEvent = gameEventRepository.save(event);
            log.debug("Recorded event: {} - {}", type, description);
            
            return savedEvent;
        } catch (JsonProcessingException e) {
            log.error("Error serializing event data: {}", e.getMessage());
            throw new RuntimeException("Failed to record event", e);
        }
    }
    
    /**
     * 获取指定类型的事件
     */
    @Transactional(readOnly = true)
    public List<GameEventEntity> getEventsByType(String type) {
        return gameEventRepository.findByType(type);
    }
    
    /**
     * 获取智能体的事件历史
     */
    @Transactional(readOnly = true)
    public List<GameEventEntity> getAgentEvents(String agentId, LocalDateTime start, LocalDateTime end) {
        return gameEventRepository.findAgentEventsInRange(agentId, start, end);
    }
    
    /**
     * 获取最近的事件
     */
    @Transactional(readOnly = true)
    public List<GameEventEntity> getRecentEvents(LocalDateTime since) {
        return gameEventRepository.findRecentEvents(since);
    }
    
    /**
     * 获取未处理的事件
     */
    @Transactional(readOnly = true)
    public List<GameEventEntity> getUnprocessedEvents() {
        return gameEventRepository.findByIsProcessedFalse();
    }
    
    /**
     * 标记事件为已处理
     */
    @Transactional
    public void markEventAsProcessed(Long eventId) {
        gameEventRepository.findById(eventId).ifPresent(event -> {
            event.setIsProcessed(true);
            gameEventRepository.save(event);
        });
    }
    
    /**
     * 批量标记事件为已处理
     */
    @Transactional
    public void markEventsAsProcessed(List<Long> eventIds) {
        List<GameEventEntity> events = gameEventRepository.findAllById(eventIds);
        events.forEach(event -> event.setIsProcessed(true));
        gameEventRepository.saveAll(events);
    }
    
    /**
     * 删除旧事件
     */
    @Transactional
    public void deleteOldEvents(LocalDateTime before) {
        List<GameEventEntity> oldEvents = gameEventRepository.findByTimestampBetween(
                LocalDateTime.of(2020, 1, 1, 0, 0), before);
        gameEventRepository.deleteAll(oldEvents);
        log.info("Deleted {} old events", oldEvents.size());
    }
    
    /**
     * 获取事件统计
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getEventStatistics() {
        return gameEventRepository.countByType()
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Long) result[1]
                ));
    }
    
    /**
     * 获取事件详情
     */
    @Transactional(readOnly = true)
    public Optional<GameEventEntity> getEvent(Long eventId) {
        return gameEventRepository.findById(eventId);
    }
    
    /**
     * 解析事件数据
     */
    public Map<String, Object> parseEventData(GameEventEntity event) {
        try {
            if (event.getDataJson() != null) {
                return objectMapper.readValue(event.getDataJson(), Map.class);
            }
            return Map.of();
        } catch (JsonProcessingException e) {
            log.error("Error parsing event data for event {}: {}", event.getId(), e.getMessage());
            return Map.of();
        }
    }
}
