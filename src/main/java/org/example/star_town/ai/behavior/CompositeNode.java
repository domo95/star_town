package org.example.star_town.ai.behavior;

import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

/**
 * 复合节点基类
 * 可以包含多个子节点的行为节点
 */
@Getter
public abstract class CompositeNode extends BehaviorNode {
    
    protected final List<BehaviorNode> children = new ArrayList<>();
    protected int currentChildIndex = 0;
    
    public CompositeNode(String name) {
        super(name);
    }
    
    /**
     * 添加子节点
     */
    public void addChild(BehaviorNode child) {
        children.add(child);
    }
    
    /**
     * 移除子节点
     */
    public void removeChild(BehaviorNode child) {
        children.remove(child);
    }
    
    /**
     * 清空所有子节点
     */
    public void clearChildren() {
        children.clear();
        currentChildIndex = 0;
    }
    
    /**
     * 获取当前子节点
     */
    protected BehaviorNode getCurrentChild() {
        if (currentChildIndex < children.size()) {
            return children.get(currentChildIndex);
        }
        return null;
    }
    
    /**
     * 移动到下一个子节点
     */
    protected void moveToNextChild() {
        currentChildIndex++;
    }
    
    /**
     * 重置到第一个子节点
     */
    protected void resetToFirstChild() {
        currentChildIndex = 0;
    }
    
    @Override
    public void reset() {
        super.reset();
        currentChildIndex = 0;
        children.forEach(BehaviorNode::reset);
    }
    
    @Override
    public void initialize() {
        super.initialize();
        currentChildIndex = 0;
        children.forEach(BehaviorNode::initialize);
    }
}
