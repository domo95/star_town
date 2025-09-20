package org.example.star_town.actions;

import lombok.extern.slf4j.Slf4j;
import org.example.star_town.ai.goap.Action;
import org.example.star_town.ai.goap.ActionContext;

/**
 * 社交动作
 * 智能体与其他智能体进行社交互动
 */
@Slf4j
public class SocializeAction extends Action {
    
    private final int happinessIncrease;
    private final int socialIncrease;
    
    public SocializeAction() {
        super("Socialize");
        this.happinessIncrease = 15;
        this.socialIncrease = 10;
        this.duration = 4000; // 4秒
        this.cost = 2;
        
        // 前置条件：附近有其他智能体
        addPrecondition("hasNearbyAgents", true);
        addPrecondition("energy", 10);
        
        // 效果：增加幸福感和社交能力
        addEffect("happiness", happinessIncrease);
        addEffect("social", socialIncrease);
        addEffect("socialNeedMet", true);
    }
    
    public SocializeAction(int happinessIncrease, int socialIncrease) {
        super("Socialize");
        this.happinessIncrease = happinessIncrease;
        this.socialIncrease = socialIncrease;
        this.duration = 4000;
        this.cost = 2;
        
        addPrecondition("hasNearbyAgents", true);
        addPrecondition("energy", 10);
        
        addEffect("happiness", happinessIncrease);
        addEffect("social", socialIncrease);
        addEffect("socialNeedMet", true);
    }
    
    @Override
    public boolean execute(ActionContext context) {
        String targetAgentId = context.get("targetAgentId", String.class);
        
        if (targetAgentId != null) {
            log.debug("Agent {} socializing with agent {}", context.getAgentId(), targetAgentId);
        } else {
            log.debug("Agent {} socializing with nearby agents", context.getAgentId());
        }
        
        // 模拟社交过程
        try {
            Thread.sleep(50); // 模拟社交耗时
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        
        // 记录社交信息
        context.put("socialPartner", targetAgentId);
        context.put("socialTime", System.currentTimeMillis());
        
        return true;
    }
    
    @Override
    public void reset() {
        super.reset();
    }
}
