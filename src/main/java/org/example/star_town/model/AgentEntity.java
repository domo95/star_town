package org.example.star_town.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.example.star_town.agent.AgentType;
import org.example.star_town.agent.Agent;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 智能体实体
 * 数据库持久化模型
 */
@Entity
@Table(name = "agents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentEntity {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgentType type;
    
    @Column(name = "position_x")
    private Double positionX;
    
    @Column(name = "position_y")
    private Double positionY;
    
    @Column(columnDefinition = "TEXT")
    private String stateJson;
    
    @Column(columnDefinition = "TEXT")
    private String memoryJson;
    
    @Column(columnDefinition = "TEXT")
    private String configJson;
    
    @Enumerated(EnumType.STRING)
    private Agent.AgentStatus status;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "last_active")
    private LocalDateTime lastActive;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        lastActive = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        lastActive = LocalDateTime.now();
    }
}
