package org.example.star_town.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 游戏事件实体
 * 记录游戏中发生的各种事件
 */
@Entity
@Table(name = "game_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameEventEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String type;
    
    @Column(name = "source_agent_id")
    private String sourceAgentId;
    
    @Column(name = "target_agent_id")
    private String targetAgentId;
    
    @Column(name = "world_object_id")
    private Long worldObjectId;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String dataJson;
    
    @Column(name = "position_x")
    private Double positionX;
    
    @Column(name = "position_y")
    private Double positionY;
    
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    
    @Column(name = "severity")
    private String severity = "INFO";
    
    @Column(name = "is_processed")
    private Boolean isProcessed = false;
    
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
