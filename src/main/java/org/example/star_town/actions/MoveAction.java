package org.example.star_town.actions;

import lombok.extern.slf4j.Slf4j;
import org.example.star_town.ai.goap.Action;
import org.example.star_town.ai.goap.ActionContext;
import org.example.star_town.world.Position;

/**
 * 移动动作
 * 智能体移动到指定位置
 */
@Slf4j
public class MoveAction extends Action {
    
    private final double speed;
    private Position targetPosition;
    
    public MoveAction() {
        super("Move");
        this.speed = 1.0;
        this.duration = 2000; // 2秒
        this.cost = 2;
    }
    
    public MoveAction(double speed) {
        super("Move");
        this.speed = speed;
        this.duration = 2000;
        this.cost = 2;
    }
    
    @Override
    public boolean execute(ActionContext context) {
        // 从上下文获取目标位置
        targetPosition = context.get("targetPosition", Position.class);
        if (targetPosition == null) {
            log.warn("MoveAction: No target position provided");
            return false;
        }
        
        // 模拟移动过程
        log.debug("Agent {} moving to {}", context.getAgentId(), targetPosition);
        
        // 这里可以添加实际的移动逻辑
        // 例如：检查路径、处理障碍物等
        
        // 移动完成
        context.put("currentPosition", targetPosition);
        return true;
    }
    
    @Override
    public void reset() {
        super.reset();
        targetPosition = null;
    }
}
