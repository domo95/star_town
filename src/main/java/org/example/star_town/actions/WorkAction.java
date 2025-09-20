package org.example.star_town.actions;

import lombok.extern.slf4j.Slf4j;
import org.example.star_town.ai.goap.Action;
import org.example.star_town.ai.goap.ActionContext;

/**
 * 工作动作
 * 智能体执行工作相关活动
 */
@Slf4j
public class WorkAction extends Action {
    
    private final String workType;
    
    public WorkAction() {
        super("Work");
        this.workType = "general";
        this.duration = 5000; // 5秒
        this.cost = 3;
        
        // 设置前置条件：需要工作场所
        addPrecondition("hasWorkplace", true);
        addPrecondition("energy", 20); // 需要最低精力
        
        // 设置效果：增加收入，消耗精力
        addEffect("income", 10);
        addEffect("energy", -15);
        addEffect("workCompleted", true);
    }
    
    public WorkAction(String workType) {
        super("Work_" + workType);
        this.workType = workType;
        this.duration = 5000;
        this.cost = 3;
        
        addPrecondition("hasWorkplace", true);
        addPrecondition("energy", 20);
        
        addEffect("income", 15);
        addEffect("energy", -15);
        addEffect("workCompleted", true);
    }
    
    @Override
    public boolean execute(ActionContext context) {
        log.debug("Agent {} working as {}", context.getAgentId(), workType);
        
        // 模拟工作过程
        try {
            Thread.sleep(100); // 模拟工作耗时
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        
        // 记录工作完成
        context.put("workType", workType);
        context.put("workTime", System.currentTimeMillis());
        
        return true;
    }
    
    @Override
    public void reset() {
        super.reset();
    }
}
