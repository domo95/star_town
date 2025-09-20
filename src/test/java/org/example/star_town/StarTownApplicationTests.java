package org.example.star_town;

import org.example.star_town.agent.Agent;
import org.example.star_town.agent.AgentConfig;
import org.example.star_town.agent.AgentManager;
import org.example.star_town.agent.AgentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.star_town.ai.goap.Goal;
import org.example.star_town.actions.EatAction;
import org.example.star_town.actions.MoveAction;
import org.example.star_town.actions.WorkAction;
import org.example.star_town.service.BehaviorTreeConfigService;
import org.example.star_town.world.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 应用程序集成测试
 */
@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class StarTownApplicationTests {

    private BehaviorTreeConfigService behaviorTreeService;
    private AgentManager agentManager;

    @BeforeEach
    void setUp() {
        // 创建行为树配置服务
        ObjectMapper objectMapper = new ObjectMapper();
        behaviorTreeService = new BehaviorTreeConfigService(objectMapper);
        
        // 创建智能体管理器
        agentManager = new AgentManager(behaviorTreeService);
    }

    @Test
    void contextLoads() {
        // 测试Spring上下文是否能正常加载
    }

    @Test
    void testAgentCreation() {
        // 创建智能体配置
        AgentConfig config = new AgentConfig("test-agent-1", "Test Agent", AgentType.RESIDENT);
        config.setProperty("energy", 100);
        config.setProperty("hunger", 50);
        
        // 创建智能体
        Agent agent = agentManager.createAgent("test-agent-1", "Test Agent", AgentType.RESIDENT, config);
        
        assertNotNull(agent);
        assertEquals("test-agent-1", agent.getId());
        assertEquals("Test Agent", agent.getName());
        assertEquals(AgentType.RESIDENT, agent.getType());
        assertNotNull(agent.getPosition());
    }

    @Test
    void testAgentGoalsAndActions() {
        AgentConfig config = new AgentConfig("test-agent-2", "Test Agent 2", AgentType.WORKER);
        
        Agent agent = agentManager.createAgent("test-agent-2", "Test Agent 2", AgentType.WORKER, config);
        
        // 添加目标
        Map<String, Object> desiredState = new HashMap<>();
        desiredState.put("energy", 80);
        Goal goal = new Goal("Rest", desiredState, 5);
        agent.addGoal(goal);
        
        // 添加动作
        agent.addAction(new MoveAction());
        agent.addAction(new WorkAction());
        agent.addAction(new EatAction());
        
        assertEquals(1, agent.getGoals().size());
        assertEquals(3, agent.getAvailableActions().size());
        assertEquals("Rest", agent.getGoals().get(0).getName());
    }

    @Test
    void testAgentStateManagement() {
        AgentConfig config = new AgentConfig("test-agent-3", "Test Agent 3", AgentType.RESIDENT);
        
        Agent agent = agentManager.createAgent("test-agent-3", "Test Agent 3", AgentType.RESIDENT, config);
        
        // 设置状态
        agent.setState("energy", 75);
        agent.setState("hunger", 30);
        agent.setState("health", 90);
        
        // 验证状态
        assertEquals(75, agent.getState("energy", Integer.class));
        assertEquals(30, agent.getState("hunger", Integer.class));
        assertEquals(90, agent.getState("health", Integer.class));
        
        // 设置记忆
        agent.setMemory("lastWorkTime", System.currentTimeMillis());
        agent.setMemory("favoritePlace", "Library");
        
        // 验证记忆
        assertNotNull(agent.getMemory("lastWorkTime", Long.class));
        assertEquals("Library", agent.getMemory("favoritePlace", String.class));
    }

    @Test
    void testAgentPositionMovement() {
        AgentConfig config = new AgentConfig("test-agent-4", "Test Agent 4", AgentType.RESIDENT);
        
        Agent agent = agentManager.createAgent("test-agent-4", "Test Agent 4", AgentType.RESIDENT, config);
        
        // 设置初始位置
        Position initialPosition = new Position(0, 0);
        agent.setPosition(initialPosition);
        
        assertEquals(0, agent.getPosition().getX());
        assertEquals(0, agent.getPosition().getY());
        
        // 移动到新位置
        Position newPosition = new Position(10, 15);
        agent.setPosition(newPosition);
        
        assertEquals(10, agent.getPosition().getX());
        assertEquals(15, agent.getPosition().getY());
        
        // 计算距离
        double distance = initialPosition.distanceTo(newPosition);
        assertEquals(18.03, distance, 0.1);
    }

    @Test
    void testGoalSatisfaction() {
        // 创建目标
        Map<String, Object> desiredState = new HashMap<>();
        desiredState.put("energy", 80);
        desiredState.put("hunger", 20);
        
        Goal goal = new Goal("WellFed", desiredState, 3);
        
        // 测试状态满足目标
        Map<String, Object> satisfiedState = new HashMap<>();
        satisfiedState.put("energy", 85);
        satisfiedState.put("hunger", 15);
        
        assertTrue(goal.isSatisfied(satisfiedState));
        
        // 测试状态不满足目标
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
        
        // 测试无效状态
        Map<String, Object> invalidState = new HashMap<>();
        invalidState.put("hasWorkplace", false);
        invalidState.put("energy", 10);
        
        assertFalse(workAction.checkPreconditions(invalidState));
        assertFalse(workAction.canExecute(invalidState));
        
        // 测试效果应用
        Map<String, Object> resultState = workAction.applyEffects(validState);
        assertEquals(10, resultState.get("income"));
        assertEquals(10, resultState.get("energy")); // 25 - 15
        assertEquals(true, resultState.get("workCompleted"));
    }
}