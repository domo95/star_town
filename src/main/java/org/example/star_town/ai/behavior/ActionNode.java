package org.example.star_town.ai.behavior;

import lombok.Getter;

/**
 * 动作节点
 * 执行具体的动作逻辑
 */
@Getter
public abstract class ActionNode extends BehaviorNode {
    
    private final long timeout;
    private long startTime;
    
    public ActionNode(String name) {
        this(name, 5000); // 默认5秒超时
    }
    
    public ActionNode(String name, long timeout) {
        super(name);
        this.timeout = timeout;
    }
    
    @Override
    public Status execute(BehaviorContext context) {
        // 如果是第一次执行，记录开始时间
        if (status == Status.READY) {
            startTime = System.currentTimeMillis();
            status = Status.RUNNING;
        }
        
        // 检查超时
        if (System.currentTimeMillis() - startTime > timeout) {
            status = Status.FAILURE;
            return Status.FAILURE;
        }
        
        // 执行具体动作
        return doAction(context);
    }
    
    /**
     * 子类实现具体的动作逻辑
     */
    protected abstract Status doAction(BehaviorContext context);
    
    @Override
    public void reset() {
        super.reset();
        startTime = 0;
    }
    
    @Override
    public void initialize() {
        super.initialize();
        startTime = 0;
    }
}
