package org.example.star_town.repository;

import org.example.star_town.model.WorldObjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 世界对象数据访问层
 */
@Repository
public interface WorldObjectRepository extends JpaRepository<WorldObjectEntity, Long> {
    
    /**
     * 根据类型查找世界对象
     */
    List<WorldObjectEntity> findByType(String type);
    
    /**
     * 查找活跃的世界对象
     */
    List<WorldObjectEntity> findByIsActiveTrue();
    
    /**
     * 根据所有者查找世界对象
     */
    List<WorldObjectEntity> findByOwnerAgentId(String ownerAgentId);
    
    /**
     * 查找交互式对象
     */
    List<WorldObjectEntity> findByIsInteractiveTrue();
    
    /**
     * 查找在指定位置附近的世界对象
     */
    @Query("SELECT w FROM WorldObjectEntity w WHERE " +
           "SQRT(POWER(w.positionX - :x, 2) + POWER(w.positionY - :y, 2)) <= :radius " +
           "AND w.isActive = true")
    List<WorldObjectEntity> findNearbyObjects(@Param("x") Double x, @Param("y") Double y, @Param("radius") Double radius);
    
    /**
     * 查找可用的建筑（未满员）
     */
    @Query("SELECT w FROM WorldObjectEntity w WHERE " +
           "w.type = :type AND " +
           "w.capacity IS NOT NULL AND " +
           "w.currentOccupancy < w.capacity AND " +
           "w.isActive = true")
    List<WorldObjectEntity> findAvailableBuildings(@Param("type") String type);
    
    /**
     * 统计各类型世界对象数量
     */
    @Query("SELECT w.type, COUNT(w) FROM WorldObjectEntity w WHERE w.isActive = true GROUP BY w.type")
    List<Object[]> countByType();
}
