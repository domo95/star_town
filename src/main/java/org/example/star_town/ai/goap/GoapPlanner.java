package org.example.star_town.ai.goap;

import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GOAP规划器
 * 使用A*算法寻找从当前状态到目标状态的最优动作序列
 */
@Slf4j
public class GoapPlanner {
    
    private static final int MAX_ITERATIONS = 1000;
    private static final int MAX_PLAN_LENGTH = 20;
    
    /**
     * 创建计划
     * @param currentState 当前状态
     * @param goal 目标
     * @param availableActions 可用动作列表
     * @return 动作序列，如果无法找到计划则返回空列表
     */
    public List<Action> createPlan(Map<String, Object> currentState, Goal goal, 
                                  List<Action> availableActions) {
        
        log.debug("Creating plan for goal: {} from state: {}", goal.getName(), currentState);
        
        // 如果目标已经满足，返回空计划
        if (goal.isSatisfied(currentState)) {
            log.debug("Goal already satisfied, returning empty plan");
            return new ArrayList<>();
        }
        
        // 初始化搜索
        PriorityQueue<PlanNode> openList = new PriorityQueue<>(Comparator.comparingInt(PlanNode::getFCost));
        Set<String> closedSet = new HashSet<>();
        
        // 创建根节点
        List<Action> filteredActions = availableActions.stream()
                .filter(action -> action.canExecute(currentState))
                .collect(Collectors.toList());
        
        PlanNode startNode = new PlanNode(currentState, null, null, 0, 
                PlanNode.calculateHeuristic(currentState, goal), filteredActions);
        openList.offer(startNode);
        
        int iterations = 0;
        
        while (!openList.isEmpty() && iterations < MAX_ITERATIONS) {
            iterations++;
            
            PlanNode currentNode = openList.poll();
            
            // 创建状态标识符
            String stateKey = createStateKey(currentNode.getState());
            
            // 如果已经访问过这个状态，跳过
            if (closedSet.contains(stateKey)) {
                continue;
            }
            
            closedSet.add(stateKey);
            
            // 检查是否达到目标
            if (currentNode.satisfiesGoal(goal)) {
                List<Action> plan = currentNode.buildActionSequence();
                log.debug("Found plan with {} actions after {} iterations", plan.size(), iterations);
                return plan;
            }
            
            // 如果计划太长，跳过
            if (currentNode.buildActionSequence().size() >= MAX_PLAN_LENGTH) {
                continue;
            }
            
            // 扩展节点
            expandNode(currentNode, openList, closedSet, goal);
        }
        
        log.warn("Failed to find plan for goal: {} after {} iterations", goal.getName(), iterations);
        return new ArrayList<>();
    }
    
    /**
     * 扩展节点，生成所有可能的后续节点
     */
    private void expandNode(PlanNode currentNode, PriorityQueue<PlanNode> openList, 
                           Set<String> closedSet, Goal goal) {
        
        Map<String, Object> currentState = currentNode.getState();
        List<Action> remainingActions = currentNode.getRemainingActions();
        
        for (Action action : remainingActions) {
            // 检查动作的前置条件
            if (!action.checkPreconditions(currentState)) {
                continue;
            }
            
            // 应用动作效果
            Map<String, Object> newState = action.applyEffects(currentState);
            String stateKey = createStateKey(newState);
            
            // 如果状态已经访问过，跳过
            if (closedSet.contains(stateKey)) {
                continue;
            }
            
            // 计算成本
            int gCost = currentNode.getGCost() + action.calculateCost(currentState);
            int hCost = PlanNode.calculateHeuristic(newState, goal);
            
            // 创建新节点
            List<Action> newRemainingActions = new ArrayList<>(remainingActions);
            newRemainingActions.remove(action);
            
            PlanNode newNode = new PlanNode(newState, action, currentNode, 
                    gCost, hCost, newRemainingActions);
            
            openList.offer(newNode);
        }
    }
    
    /**
     * 创建状态的唯一标识符
     */
    private String createStateKey(Map<String, Object> state) {
        return state.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(","));
    }
    
    /**
     * 验证计划的可行性
     */
    public boolean validatePlan(List<Action> plan, Map<String, Object> initialState) {
        Map<String, Object> currentState = new HashMap<>(initialState);
        
        for (Action action : plan) {
            if (!action.checkPreconditions(currentState)) {
                log.warn("Plan validation failed: action {} preconditions not met", action.getName());
                return false;
            }
            currentState = action.applyEffects(currentState);
        }
        
        return true;
    }
}
