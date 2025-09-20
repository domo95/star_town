package org.example.star_town.ai.behavior;

/**
 * 条件节点
 * 检查某个条件是否满足
 */
public abstract class ConditionNode extends BehaviorNode {
    
    public ConditionNode(String name) {
        super(name);
    }
    
    @Override
    public Status execute(BehaviorContext context) {
        boolean conditionMet = checkCondition(context);
        status = conditionMet ? Status.SUCCESS : Status.FAILURE;
        return status;
    }
    
    /**
     * 子类实现具体的条件检查逻辑
     */
    protected abstract boolean checkCondition(BehaviorContext context);
}
