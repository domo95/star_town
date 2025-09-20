package org.example.star_town.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 世界对象实体
 * 表示游戏世界中的各种对象（建筑、物品等）
 */
@Entity
@Table(name = "world_objects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorldObjectEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String type;
    
    @Column(name = "position_x", nullable = false)
    private Double positionX;
    
    @Column(name = "position_y", nullable = false)
    private Double positionY;
    
    @Column(name = "width")
    private Double width;
    
    @Column(name = "height")
    private Double height;
    
    @Column(columnDefinition = "TEXT")
    private String propertiesJson;
    
    @Column(name = "owner_agent_id")
    private String ownerAgentId;
    
    @Column(name = "is_interactive")
    private Boolean isInteractive = false;
    
    @Column(name = "capacity")
    private Integer capacity;
    
    @Column(name = "current_occupancy")
    private Integer currentOccupancy = 0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
