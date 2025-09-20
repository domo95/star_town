package org.example.star_town.repository;

import org.example.star_town.model.AgentEntity;
import org.example.star_town.agent.AgentType;
import org.example.star_town.agent.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 智能体数据访问层
 */
@Repository
public interface AgentRepository extends JpaRepository<AgentEntity, String> {
    
    /**
     * 根据类型查找智能体
     */
    List<AgentEntity> findByType(AgentType type);
    
    /**
     * 根据状态查找智能体
     */
    List<AgentEntity> findByStatus(Agent.AgentStatus status);
    
    /**
     * 查找活跃的智能体
     */
    List<AgentEntity> findByIsActiveTrue();
    
    /**
     * 根据类型和状态查找智能体
     */
    List<AgentEntity> findByTypeAndStatus(AgentType type, Agent.AgentStatus status);
    
    /**
     * 查找最近活跃的智能体
     */
    @Query("SELECT a FROM AgentEntity a WHERE a.lastActive >= :since AND a.isActive = true")
    List<AgentEntity> findRecentlyActive(@Param("since") LocalDateTime since);
    
    /**
     * 根据名称查找智能体
     */
    Optional<AgentEntity> findByName(String name);
    
    /**
     * 统计各类型智能体数量
     */
    @Query("SELECT a.type, COUNT(a) FROM AgentEntity a WHERE a.isActive = true GROUP BY a.type")
    List<Object[]> countByType();
    
    /**
     * 查找在指定位置附近的智能体
     */
    @Query("SELECT a FROM AgentEntity a WHERE " +
           "SQRT(POWER(a.positionX - :x, 2) + POWER(a.positionY - :y, 2)) <= :radius " +
           "AND a.isActive = true")
    List<AgentEntity> findNearbyAgents(@Param("x") Double x, @Param("y") Double y, @Param("radius") Double radius);
}
