package org.example.star_town.agent;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.star_town.ai.behavior.BehaviorContext;
import org.example.star_town.ai.behavior.BehaviorNode;
import org.example.star_town.ai.blackboard.Blackboard;
import org.example.star_town.ai.goap.*;
import org.example.star_town.service.BehaviorTreeConfigService;
import org.example.star_town.world.Position;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 智能体
 * 集成行为树、黑板系统和GOAP计划系统
 */
@Slf4j
@Getter
@Setter
public class Agent {
    
    private String id;
    private String name;
    private AgentType type;
    private Position position;
    private Map<String, Object> state;
    private List<Goal> goals;
    private List<Action> availableActions;
    private BehaviorNode behaviorTree;
    private Blackboard blackboard;
    private GoapPlanner planner;
    private List<Action> currentPlan;
    private int currentPlanIndex;
    private AgentStatus status;
    private AgentConfig config;
    private long lastUpdateTime;
    private Map<String, Object> memory;
    private BehaviorTreeConfigService behaviorTreeService;
    
    public enum AgentStatus {
        IDLE,       // 空闲
        THINKING,   // 思考中（规划）
        EXECUTING,  // 执行中
        WAITING,    // 等待中
        ERROR       // 错误状态
    }
    
    public Agent(String id, String name, AgentType type, AgentConfig config) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.config = config;
        this.state = new ConcurrentHashMap<>();
        this.goals = new ArrayList<>();
        this.availableActions = new ArrayList<>();
        this.planner = new GoapPlanner();
        this.currentPlan = new ArrayList<>();
        this.currentPlanIndex = 0;
        this.status = AgentStatus.IDLE;
        this.lastUpdateTime = System.currentTimeMillis();
        this.memory = new ConcurrentHashMap<>();
        this.position = new Position(0, 0);
        
        // 初始化默认状态值
        initializeDefaultState();
    }
    
    /**
     * 初始化默认状态值
     */
    private void initializeDefaultState() {
        setState("hunger", 50);
        setState("energy", 70);
        setState("happiness", 60);
        setState("health", 90);
        setState("hasFood", false);
        setState("hasWorkplace", false);
        setState("nearbyAgents", new ArrayList<>());
        setState("income", 0);
        setState("money", 100);
    }
    
    /**
     * 更新智能体状态
     */
    public void update() {
        long currentTime = System.currentTimeMillis();
        
        try {
            // 首先执行行为树
            if (behaviorTreeService != null && config.isAiEnabled()) {
                executeBehaviorTree();
            }
            
            // 然后执行GOAP逻辑
            switch (status) {
                case IDLE:
                    handleIdleState();
                    break;
                case THINKING:
                    handleThinkingState();
                    break;
                case EXECUTING:
                    handleExecutingState();
                    break;
                case WAITING:
                    handleWaitingState();
                    break;
                case ERROR:
                    handleErrorState();
                    break;
            }
        } catch (Exception e) {
            log.error("Error updating agent {}: {}", id, e.getMessage(), e);
            status = AgentStatus.ERROR;
        }
        
        lastUpdateTime = currentTime;
    }
    
    /**
     * 执行行为树
     */
    private void executeBehaviorTree() {
        try {
            BehaviorNode tree = behaviorTreeService.getBehaviorTree(this);
            if (tree != null) {
                BehaviorContext context = new BehaviorContext(id);
                
                // 安全地设置上下文数据，避免null值
                context.putSafe("hunger", getState("hunger", Integer.class), 50);
                context.putSafe("energy", getState("energy", Integer.class), 70);
                context.putSafe("happiness", getState("happiness", Integer.class), 60);
                context.putSafe("hasFood", getState("hasFood", Boolean.class), false);
                context.putSafe("hasWorkplace", getState("hasWorkplace", Boolean.class), false);
                context.putSafe("nearbyAgents", getState("nearbyAgents", List.class), new java.util.ArrayList<>());
                

                
                BehaviorNode.Status result = tree.execute(context);
                
                log.debug("Agent {} behavior tree result: {}", id, result);
                
                // 根据行为树结果更新状态
                if (result == BehaviorNode.Status.RUNNING) {
                    status = AgentStatus.EXECUTING;
                } else if (result == BehaviorNode.Status.SUCCESS) {
                    // 更新状态值
                    updateStateFromContext(context);
                }
            } else {
                log.warn("No behavior tree found for agent {}", id);
            }
        } catch (Exception e) {
            log.error("Error executing behavior tree for agent {}: {}", id, e.getMessage(), e);
        }
    }
    
    /**
     * 从行为树上下文更新状态
     */
    private void updateStateFromContext(BehaviorContext context) {
        // 更新饥饿度
        Integer hunger = context.get("hunger", Integer.class);
        if (hunger != null) {
            setState("hunger", hunger);
        }
        
        // 更新精力
        Integer energy = context.get("energy", Integer.class);
        if (energy != null) {
            setState("energy", energy);
        }
        
        // 更新幸福感
        Integer happiness = context.get("happiness", Integer.class);
        if (happiness != null) {
            setState("happiness", happiness);
        }
        
        // 更新收入
        Integer income = context.get("income", Integer.class);
        if (income != null) {
            setState("income", income);
        }
    }
    
    /**
     * 处理空闲状态
     */
    private void handleIdleState() {
        // 检查是否有未完成的目标
        Goal nextGoal = findNextGoal();
        if (nextGoal != null) {
            log.debug("Agent {} found goal to pursue: {}", id, nextGoal.getName());
            status = AgentStatus.THINKING;
        }
    }
    
    /**
     * 处理思考状态
     */
    private void handleThinkingState() {
        Goal nextGoal = findNextGoal();
        if (nextGoal == null) {
            status = AgentStatus.IDLE;
            return;
        }
        
        // 创建计划
        List<Action> plan = planner.createPlan(state, nextGoal, availableActions);
        if (plan.isEmpty()) {
            log.warn("Agent {} could not create plan for goal: {}", id, nextGoal.getName());
            status = AgentStatus.IDLE;
            return;
        }
        
        currentPlan = plan;
        currentPlanIndex = 0;
        status = AgentStatus.EXECUTING;
        log.debug("Agent {} created plan with {} actions", id, plan.size());
    }
    
    /**
     * 处理执行状态
     */
    private void handleExecutingState() {
        if (currentPlanIndex >= currentPlan.size()) {
            // 计划完成
            status = AgentStatus.IDLE;
            return;
        }
        
        Action currentAction = currentPlan.get(currentPlanIndex);
        ActionContext context = new ActionContext(id);
        
        // 执行当前动作
        boolean success = currentAction.execute(context);
        
        if (success) {
            // 应用动作效果到状态
            state = currentAction.applyEffects(state);
            currentPlanIndex++;
            
            // 更新黑板
            updateBlackboard();
            
            log.debug("Agent {} completed action: {}", id, currentAction.getName());
        } else {
            // 动作失败，重新规划
            log.warn("Agent {} failed to execute action: {}, replanning", id, currentAction.getName());
            status = AgentStatus.THINKING;
        }
    }
    
    /**
     * 处理等待状态
     */
    private void handleWaitingState() {
        // 检查等待条件是否满足
        // 这里可以添加具体的等待逻辑
        status = AgentStatus.IDLE;
    }
    
    /**
     * 处理错误状态
     */
    private void handleErrorState() {
        // 尝试恢复
        log.info("Agent {} attempting to recover from error state", id);
        status = AgentStatus.IDLE;
        currentPlan.clear();
        currentPlanIndex = 0;
    }
    
    /**
     * 查找下一个要追求的目标
     */
    private Goal findNextGoal() {
        return goals.stream()
                .filter(goal -> !goal.isSatisfied(state))
                .max(Comparator.comparing(Goal::calculateImportance))
                .orElse(null);
    }
    
    /**
     * 更新黑板信息
     */
    private void updateBlackboard() {
        if (blackboard != null) {
            blackboard.setAgent(id, "position", position);
            blackboard.setAgent(id, "status", status);
            blackboard.setAgent(id, "state", new HashMap<>(state));
            blackboard.setAgent(id, "lastUpdate", lastUpdateTime);
        }
    }
    
    /**
     * 添加目标
     */
    public void addGoal(Goal goal) {
        goals.add(goal);
        log.debug("Agent {} added goal: {}", id, goal.getName());
    }
    
    /**
     * 移除目标
     */
    public void removeGoal(String goalName) {
        goals.removeIf(goal -> goal.getName().equals(goalName));
        log.debug("Agent {} removed goal: {}", id, goalName);
    }
    
    /**
     * 添加可用动作
     */
    public void addAction(Action action) {
        availableActions.add(action);
        log.debug("Agent {} added action: {}", id, action.getName());
    }
    
    /**
     * 设置状态值
     */
    public void setState(String key, Object value) {
        state.put(key, value);
    }
    
    /**
     * 获取状态值
     */
    @SuppressWarnings("unchecked")
    public <T> T getState(String key, Class<T> type) {
        Object value = state.get(key);
        if (value != null && type.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        return null;
    }
    
    /**
     * 设置记忆
     */
    public void setMemory(String key, Object value) {
        memory.put(key, value);
    }
    
    /**
     * 获取记忆
     */
    @SuppressWarnings("unchecked")
    public <T> T getMemory(String key, Class<T> type) {
        Object value = memory.get(key);
        if (value != null && type.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        return null;
    }
    
    /**
     * 重置智能体
     */
    public void reset() {
        status = AgentStatus.IDLE;
        currentPlan.clear();
        currentPlanIndex = 0;
        state.clear();
        memory.clear();
        goals.forEach(goal -> goal.setPersistent(false));
        goals.clear();
        log.debug("Agent {} reset", id);
    }
}
