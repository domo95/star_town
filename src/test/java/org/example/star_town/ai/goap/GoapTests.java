package org.example.star_town.ai.goap;

import org.example.star_town.actions.EatAction;
import org.example.star_town.actions.MoveAction;
import org.example.star_town.actions.WorkAction;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GOAP框架测试
 */
class GoapTests {

    @Test
    void testGoalCreation() {
        Map<String, Object> desiredState = new HashMap<>();
        desiredState.put("energy", 80);
        desiredState.put("hunger", 20);
        
        Goal goal = new Goal("WellFed", desiredState, 5);
        
        assertEquals("WellFed", goal.getName());
        assertEquals(5, goal.getPriority());
        assertEquals(80, goal.getDesiredState().get("energy"));
        assertEquals(20, goal.getDesiredState().get("hunger"));
    }

    @Test
    void testGoalSatisfaction() {
        Map<String, Object> desiredState = new HashMap<>();
        desiredState.put("energy", 80);
        desiredState.put("hunger", 20);
        
        Goal goal = new Goal("WellFed", desiredState, 3);
        
        // 满足的状态
        Map<String, Object> satisfiedState = new HashMap<>();
        satisfiedState.put("energy", 85);
        satisfiedState.put("hunger", 15);
        
        assertTrue(goal.isSatisfied(satisfiedState));
        
        // 不满足的状态
        Map<String, Object> unsatisfiedState = new HashMap<>();
        unsatisfiedState.put("energy", 70);
        unsatisfiedState.put("hunger", 30);
        
        assertFalse(goal.isSatisfied(unsatisfiedState));
    }

    @Test
    void testActionPreconditionsAndEffects() {
        WorkAction workAction = new WorkAction();
        
        // 测试前置条件
        Map<String, Object> validState = new HashMap<>();
        validState.put("hasWorkplace", true);
        validState.put("energy", 25);
        
        assertTrue(workAction.checkPreconditions(validState));
        assertTrue(workAction.canExecute(validState));
        
        // 测试效果应用
        Map<String, Object> newState = workAction.applyEffects(validState);
        assertEquals(10, newState.get("income"));
        assertEquals(10, newState.get("energy")); // 25 - 15
        assertEquals(true, newState.get("workCompleted"));
    }

    @Test
    void testSimplePlanCreation() {
        GoapPlanner planner = new GoapPlanner();
        
        // 当前状态
        Map<String, Object> currentState = new HashMap<>();
        currentState.put("energy", 50);
        currentState.put("hunger", 80);
        currentState.put("hasFood", true);
        currentState.put("hasWorkplace", true);
        
        // 目标状态
        Map<String, Object> desiredState = new HashMap<>();
        desiredState.put("energy", 80);
        desiredState.put("hunger", 20);
        
        Goal goal = new Goal("WellFed", desiredState, 5);
        
        // 可用动作
        List<Action> availableActions = Arrays.asList(
                new EatAction(),
                new WorkAction(),
                new MoveAction()
        );
        
        // 创建计划
        List<Action> plan = planner.createPlan(currentState, goal, availableActions);
        
        assertNotNull(plan);
        assertFalse(plan.isEmpty());
        
        // 验证计划
        boolean validPlan = planner.validatePlan(plan, currentState);
        assertTrue(validPlan);
        
        // 验证计划包含进食动作
        boolean hasEatAction = plan.stream()
                .anyMatch(action -> action.getName().equals("Eat"));
        assertTrue(hasEatAction);
    }

    @Test
    void testComplexPlanCreation() {
        GoapPlanner planner = new GoapPlanner();
        
        // 复杂当前状态
        Map<String, Object> currentState = new HashMap<>();
        currentState.put("energy", 20);
        currentState.put("hunger", 90);
        currentState.put("health", 60);
        currentState.put("hasFood", false);
        currentState.put("hasBed", true);
        currentState.put("hasWorkplace", true);
        currentState.put("money", 0);
        
        // 复杂目标状态
        Map<String, Object> desiredState = new HashMap<>();
        desiredState.put("energy", 70);
        desiredState.put("hunger", 30);
        desiredState.put("health", 80);
        desiredState.put("money", 50);
        
        Goal goal = new Goal("HealthyAndRich", desiredState, 8);
        
        // 可用动作
        List<Action> availableActions = Arrays.asList(
                new EatAction(),
                new WorkAction(),
                new MoveAction()
        );
        
        // 创建计划
        List<Action> plan = planner.createPlan(currentState, goal, availableActions);
        
        assertNotNull(plan);
        
        // 验证计划
        boolean validPlan = planner.validatePlan(plan, currentState);
        assertTrue(validPlan);
        
        System.out.println("Generated plan:");
        for (int i = 0; i < plan.size(); i++) {
            System.out.println((i + 1) + ". " + plan.get(i).getName());
        }
    }

    @Test
    void testActionContext() {
        ActionContext context = new ActionContext("test-agent");
        
        // 测试数据存储
        context.put("target", "library");
        context.put("speed", 2.5);
        context.put("completed", false);
        
        assertEquals("library", context.get("target", String.class));
        assertEquals(2.5, context.get("speed", Double.class));
        assertEquals(false, context.get("completed", Boolean.class));
        
        // 测试默认值
        assertEquals("default", context.getOrDefault("nonexistent", "default", String.class));
        
        // 测试包含检查
        assertTrue(context.contains("target"));
        assertFalse(context.contains("nonexistent"));
    }

    @Test
    void testPlanNode() {
        Map<String, Object> state = new HashMap<>();
        state.put("energy", 50);
        state.put("hunger", 30);
        
        WorkAction action = new WorkAction();
        List<Action> remainingActions = Arrays.asList(new EatAction());
        
        PlanNode node = new PlanNode(state, action, null, 5, 3, remainingActions);
        
        assertEquals(8, node.getFCost()); // 5 + 3
        assertEquals(5, node.getGCost());
        assertEquals(3, node.getHCost());
        assertEquals(1, node.buildActionSequence().size());
        assertEquals("Work", node.buildActionSequence().get(0).getName());
    }

    @Test
    void testPlanNodeHeuristic() {
        Map<String, Object> currentState = new HashMap<>();
        currentState.put("energy", 50);
        currentState.put("hunger", 70);
        
        Map<String, Object> desiredState = new HashMap<>();
        desiredState.put("energy", 80);
        desiredState.put("hunger", 20);
        
        Goal goal = new Goal("TestGoal", desiredState, 1);
        
        int heuristic = PlanNode.calculateHeuristic(currentState, goal);
        
        // 应该返回2，因为energy和hunger都不匹配
        assertEquals(2, heuristic);
    }
}
