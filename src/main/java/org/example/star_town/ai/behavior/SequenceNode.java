package org.example.star_town.ai.behavior;

/**
 * 顺序节点
 * 按顺序执行子节点，只有当所有子节点都成功时才返回成功
 * 如果任何一个子节点失败，则立即返回失败
 */
public class SequenceNode extends CompositeNode {
    
    public SequenceNode(String name) {
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
                    
                case FAILURE:
                    status = Status.FAILURE;
                    return Status.FAILURE;
                    
                case SUCCESS:
                    moveToNextChild();
                    break;
            }
        }
        
        // 所有子节点都成功
        status = Status.SUCCESS;
        return Status.SUCCESS;
    }
}
