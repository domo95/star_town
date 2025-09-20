package org.example.star_town.ai.behavior;

import lombok.Getter;
import lombok.Setter;

/**
 * 行为树节点基类
 * 支持行为树的执行状态和结果
 */
@Getter
@Setter
public abstract class BehaviorNode {
    
    public enum Status {
        READY,    // 准备执行
        RUNNING,  // 正在执行
        SUCCESS,  // 执行成功
        FAILURE   // 执行失败
    }
    
    protected String name;
    protected Status status = Status.READY;
    
    public BehaviorNode(String name) {
        this.name = name;
    }
    
    /**
     * 执行行为节点
     * @param context 执行上下文
     * @return 执行结果状态
     */
    public abstract Status execute(BehaviorContext context);
    
    /**
     * 重置节点状态
     */
    public void reset() {
        this.status = Status.READY;
    }
    
    /**
     * 初始化节点
     */
    public void initialize() {
        this.status = Status.READY;
    }
    
    /**
     * 节点是否完成执行
     */
    public boolean isFinished() {
        return status == Status.SUCCESS || status == Status.FAILURE;
    }
}
