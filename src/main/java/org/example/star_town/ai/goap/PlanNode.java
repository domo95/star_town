package org.example.star_town.ai.goap;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 计划节点
 * 用于A*搜索算法构建计划树
 */
@Getter
@Setter
public class PlanNode {
    
    private final Map<String, Object> state;
    private final Action action;
    private final PlanNode parent;
    private final int gCost; // 从起点到当前节点的实际成本
    private final int hCost; // 从当前节点到目标的启发式成本
    private final List<Action> remainingActions;
    
    public PlanNode(Map<String, Object> state, Action action, PlanNode parent, 
                   int gCost, int hCost, List<Action> remainingActions) {
        this.state = new HashMap<>(state);
        this.action = action;
        this.parent = parent;
        this.gCost = gCost;
        this.hCost = hCost;
        this.remainingActions = new ArrayList<>(remainingActions);
    }
    
    /**
     * 计算总成本 f = g + h
     */
    public int getFCost() {
        return gCost + hCost;
    }
    
    /**
     * 构建从当前节点到根节点的动作序列
     */
    public List<Action> buildActionSequence() {
        List<Action> sequence = new ArrayList<>();
        PlanNode current = this;
        
        while (current.getParent() != null) {
            sequence.add(0, current.getAction());
            current = current.getParent();
        }
        
        return sequence;
    }
    
    /**
     * 检查状态是否满足目标
     */
    public boolean satisfiesGoal(Goal goal) {
        return goal.isSatisfied(state);
    }
    
    /**
     * 计算启发式成本（简化版本）
     */
    public static int calculateHeuristic(Map<String, Object> currentState, Goal goal) {
        int cost = 0;
        for (Map.Entry<String, Object> entry : goal.getDesiredState().entrySet()) {
            Object currentValue = currentState.get(entry.getKey());
            if (!entry.getValue().equals(currentValue)) {
                cost++;
            }
        }
        return cost;
    }
    
    @Override
    public String toString() {
        return String.format("PlanNode{gCost=%d, hCost=%d, fCost=%d, action=%s}", 
                gCost, hCost, getFCost(), action != null ? action.getName() : "START");
    }
}
