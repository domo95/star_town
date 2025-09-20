package org.example.star_town.repository;

import org.example.star_town.model.GameEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 游戏事件数据访问层
 */
@Repository
public interface GameEventRepository extends JpaRepository<GameEventEntity, Long> {
    
    /**
     * 根据类型查找事件
     */
    List<GameEventEntity> findByType(String type);
    
    /**
     * 根据源智能体查找事件
     */
    List<GameEventEntity> findBySourceAgentId(String sourceAgentId);
    
    /**
     * 根据目标智能体查找事件
     */
    List<GameEventEntity> findByTargetAgentId(String targetAgentId);
    
    /**
     * 查找未处理的事件
     */
    List<GameEventEntity> findByIsProcessedFalse();
    
    /**
     * 查找指定时间范围内的事件
     */
    List<GameEventEntity> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * 查找最近的事件
     */
    @Query("SELECT e FROM GameEventEntity e WHERE e.timestamp >= :since ORDER BY e.timestamp DESC")
    List<GameEventEntity> findRecentEvents(@Param("since") LocalDateTime since);
    
    /**
     * 根据严重程度查找事件
     */
    List<GameEventEntity> findBySeverity(String severity);
    
    /**
     * 统计各类型事件数量
     */
    @Query("SELECT e.type, COUNT(e) FROM GameEventEntity e GROUP BY e.type")
    List<Object[]> countByType();
    
    /**
     * 查找智能体在指定时间范围内的事件
     */
    @Query("SELECT e FROM GameEventEntity e WHERE " +
           "(e.sourceAgentId = :agentId OR e.targetAgentId = :agentId) AND " +
           "e.timestamp BETWEEN :start AND :end " +
           "ORDER BY e.timestamp DESC")
    List<GameEventEntity> findAgentEventsInRange(@Param("agentId") String agentId, 
                                                 @Param("start") LocalDateTime start, 
                                                 @Param("end") LocalDateTime end);
}
