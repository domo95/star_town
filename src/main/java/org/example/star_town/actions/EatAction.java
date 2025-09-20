package org.example.star_town.actions;

import lombok.extern.slf4j.Slf4j;
import org.example.star_town.ai.goap.Action;
import org.example.star_town.ai.goap.ActionContext;

/**
 * 进食动作
 * 智能体进食以恢复精力和饥饿度
 */
@Slf4j
public class EatAction extends Action {
    
    private final int hungerReduction;
    private final int energyIncrease;
    
    public EatAction() {
        super("Eat");
        this.hungerReduction = 30;
        this.energyIncrease = 20;
        this.duration = 3000; // 3秒
        this.cost = 1;
        
        // 前置条件：有食物
        addPrecondition("hasFood", true);
        
        // 效果：减少饥饿，增加精力
        addEffect("hunger", -hungerReduction);
        addEffect("energy", energyIncrease);
        addEffect("needsFood", false);
    }
    
    public EatAction(int hungerReduction, int energyIncrease) {
        super("Eat");
        this.hungerReduction = hungerReduction;
        this.energyIncrease = energyIncrease;
        this.duration = 3000;
        this.cost = 1;
        
        addPrecondition("hasFood", true);
        
        addEffect("hunger", -hungerReduction);
        addEffect("energy", energyIncrease);
        addEffect("needsFood", false);
    }
    
    @Override
    public boolean execute(ActionContext context) {
        log.debug("Agent {} eating", context.getAgentId());
        
        // 模拟进食过程
        try {
            Thread.sleep(50); // 模拟进食耗时
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        
        // 记录进食信息
        context.put("foodConsumed", hungerReduction);
        context.put("eatTime", System.currentTimeMillis());
        
        return true;
    }
    
    @Override
    public void reset() {
        super.reset();
    }
}
