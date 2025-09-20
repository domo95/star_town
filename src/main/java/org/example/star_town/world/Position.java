package org.example.star_town.world;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 位置类
 * 表示游戏世界中的坐标位置
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    
    private double x;
    private double y;
    
    /**
     * 计算到另一个位置的距离
     */
    public double distanceTo(Position other) {
        double dx = x - other.x;
        double dy = y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * 移动到指定位置
     */
    public void moveTo(Position target, double speed) {
        double distance = distanceTo(target);
        if (distance > 0) {
            double ratio = Math.min(speed, distance) / distance;
            x = x + (target.x - x) * ratio;
            y = y + (target.y - y) * ratio;
        }
    }
    
    /**
     * 创建副本
     */
    public Position copy() {
        return new Position(x, y);
    }
    
    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", x, y);
    }
}
