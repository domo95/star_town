package org.example.star_town.actions;

import lombok.extern.slf4j.Slf4j;
import org.example.star_town.ai.goap.Action;
import org.example.star_town.ai.goap.ActionContext;

/**
 * 睡眠动作
 * 智能体睡眠以恢复精力和健康
 */
@Slf4j
public class SleepAction extends Action {
    
    private final int energyIncrease;
    private final int healthIncrease;
    
    public SleepAction() {
        super("Sleep");
        this.energyIncrease = 50;
        this.healthIncrease = 10;
        this.duration = 8000; // 8秒
        this.cost = 1;
        
        // 前置条件：有床铺或休息场所
        addPrecondition("hasBed", true);
        
        // 效果：大幅增加精力，增加健康
        addEffect("energy", energyIncrease);
        addEffect("health", healthIncrease);
        addEffect("isRested", true);
    }
    
    public SleepAction(int energyIncrease, int healthIncrease) {
        super("Sleep");
        this.energyIncrease = energyIncrease;
        this.healthIncrease = healthIncrease;
        this.duration = 8000;
        this.cost = 1;
        
        addPrecondition("hasBed", true);
        
        addEffect("energy", energyIncrease);
        addEffect("health", healthIncrease);
        addEffect("isRested", true);
    }
    
    @Override
    public boolean execute(ActionContext context) {
        log.debug("Agent {} sleeping", context.getAgentId());
        
        // 模拟睡眠过程
        try {
            Thread.sleep(100); // 模拟睡眠耗时
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        
        // 记录睡眠信息
        context.put("sleepDuration", duration);
        context.put("sleepTime", System.currentTimeMillis());
        
        return true;
    }
    
    @Override
    public void reset() {
        super.reset();
    }
}
