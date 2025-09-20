package org.example.star_town.ai.behavior;

/**
 * 选择节点
 * 按顺序执行子节点，直到有一个子节点成功就返回成功
 * 只有当所有子节点都失败时才返回失败
 */
public class SelectorNode extends CompositeNode {
    
    public SelectorNode(String name) {
        super(name);
    }
    
    @Override
    public Status execute(BehaviorContext context) {
        if (children.isEmpty()) {
            return Status.FAILURE;
        }
        
        while (currentChildIndex < children.size()) {
            BehaviorNode currentChild = getCurrentChild();
            if (currentChild == null) {
                return Status.FAILURE;
            }
            
            Status childStatus = currentChild.execute(context);
            
            switch (childStatus) {
                case RUNNING:
                    status = Status.RUNNING;
                    return Status.RUNNING;
                    
                case SUCCESS:
                    status = Status.SUCCESS;
                    return Status.SUCCESS;
                    
                case FAILURE:
                    moveToNextChild();
                    break;
            }
        }
        
        // 所有子节点都失败
        status = Status.FAILURE;
        return Status.FAILURE;
    }
}
