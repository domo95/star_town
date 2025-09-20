package org.example.star_town.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.star_town.agent.Agent;
import org.example.star_town.agent.AgentConfig;
import org.example.star_town.agent.AgentManager;
import org.example.star_town.agent.AgentType;
import org.example.star_town.ai.goap.Goal;
import org.example.star_town.actions.*;
import org.example.star_town.world.Position;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 游戏初始化服务
 * 在应用启动时创建示例智能体和世界
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GameInitializationService implements CommandLineRunner {
    
    private final AgentManager agentManager;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing Star Town game...");
        
        // 启动智能体管理器
        agentManager.start();
        
        // 创建示例智能体
        createSampleAgents();
        
        log.info("Star Town game initialized with {} agents", agentManager.getAgentCount());
    }
    
    /**
     * 创建示例智能体
     */
    private void createSampleAgents() {
        // 创建居民
        createResident("alice", "Alice", 100, 200);
        createResident("bob", "Bob", 300, 150);
        
        // 创建工人
        createWorker("charlie", "Charlie", 200, 100);
        
        // 创建商人
        createMerchant("diana", "Diana", 400, 250);
        
        // 创建艺术家
        createArtist("eve", "Eve", 150, 300);
        
        // 创建科学家
        createScientist("frank", "Frank", 350, 100);
    }
    
    /**
     * 创建居民智能体
     */
    private void createResident(String id, String name, double x, double y) {
        AgentConfig config = new AgentConfig(id, name, AgentType.RESIDENT);
        config.setProperty("energy", 80);
        config.setProperty("hunger", 30);
        config.setProperty("health", 90);
        config.setProperty("happiness", 70);
        config.setProperty("social", 60);
        
        Agent agent = agentManager.createAgent(id, name, AgentType.RESIDENT, config);
        agent.setPosition(new Position(x, y));
        
        // 添加基础动作
        agent.addAction(new MoveAction());
        agent.addAction(new EatAction());
        agent.addAction(new SleepAction());
        agent.addAction(new SocializeAction());
        
        // 添加基础目标
        agent.addGoal(createBasicNeedsGoal());
        agent.addGoal(createSocialGoal());
        
        log.info("Created resident agent: {} at ({}, {})", name, x, y);
    }
    
    /**
     * 创建工人智能体
     */
    private void createWorker(String id, String name, double x, double y) {
        AgentConfig config = new AgentConfig(id, name, AgentType.WORKER);
        config.setProperty("energy", 90);
        config.setProperty("hunger", 20);
        config.setProperty("health", 85);
        config.setProperty("income", 0);
        config.setProperty("hasWorkplace", true);
        
        Agent agent = agentManager.createAgent(id, name, AgentType.WORKER, config);
        agent.setPosition(new Position(x, y));
        
        // 添加动作
        agent.addAction(new MoveAction());
        agent.addAction(new WorkAction("construction"));
        agent.addAction(new EatAction());
        agent.addAction(new SleepAction());
        
        // 添加目标
        agent.addGoal(createWorkGoal());
        agent.addGoal(createBasicNeedsGoal());
        
        log.info("Created worker agent: {} at ({}, {})", name, x, y);
    }
    
    /**
     * 创建商人智能体
     */
    private void createMerchant(String id, String name, double x, double y) {
        AgentConfig config = new AgentConfig(id, name, AgentType.MERCHANT);
        config.setProperty("energy", 70);
        config.setProperty("hunger", 40);
        config.setProperty("health", 80);
        config.setProperty("money", 100);
        config.setProperty("hasShop", true);
        
        Agent agent = agentManager.createAgent(id, name, AgentType.MERCHANT, config);
        agent.setPosition(new Position(x, y));
        
        // 添加动作
        agent.addAction(new MoveAction());
        agent.addAction(new WorkAction("trading"));
        agent.addAction(new EatAction());
        agent.addAction(new SleepAction());
        agent.addAction(new SocializeAction());
        
        // 添加目标
        agent.addGoal(createBusinessGoal());
        agent.addGoal(createBasicNeedsGoal());
        
        log.info("Created merchant agent: {} at ({}, {})", name, x, y);
    }
    
    /**
     * 创建艺术家智能体
     */
    private void createArtist(String id, String name, double x, double y) {
        AgentConfig config = new AgentConfig(id, name, AgentType.ARTIST);
        config.setProperty("energy", 60);
        config.setProperty("hunger", 50);
        config.setProperty("health", 75);
        config.setProperty("creativity", 95);
        config.setProperty("inspiration", 40);
        
        Agent agent = agentManager.createAgent(id, name, AgentType.ARTIST, config);
        agent.setPosition(new Position(x, y));
        
        // 添加动作
        agent.addAction(new MoveAction());
        agent.addAction(new WorkAction("art"));
        agent.addAction(new EatAction());
        agent.addAction(new SleepAction());
        agent.addAction(new SocializeAction());
        
        // 添加目标
        agent.addGoal(createCreativeGoal());
        agent.addGoal(createBasicNeedsGoal());
        
        log.info("Created artist agent: {} at ({}, {})", name, x, y);
    }
    
    /**
     * 创建科学家智能体
     */
    private void createScientist(String id, String name, double x, double y) {
        AgentConfig config = new AgentConfig(id, name, AgentType.SCIENTIST);
        config.setProperty("energy", 85);
        config.setProperty("hunger", 25);
        config.setProperty("health", 90);
        config.setProperty("knowledge", 95);
        config.setProperty("hasLab", true);
        
        Agent agent = agentManager.createAgent(id, name, AgentType.SCIENTIST, config);
        agent.setPosition(new Position(x, y));
        
        // 添加动作
        agent.addAction(new MoveAction());
        agent.addAction(new WorkAction("research"));
        agent.addAction(new EatAction());
        agent.addAction(new SleepAction());
        
        // 添加目标
        agent.addGoal(createResearchGoal());
        agent.addGoal(createBasicNeedsGoal());
        
        log.info("Created scientist agent: {} at ({}, {})", name, x, y);
    }
    
    /**
     * 创建基本需求目标
     */
    private Goal createBasicNeedsGoal() {
        Map<String, Object> desiredState = new HashMap<>();
        desiredState.put("energy", 60);
        desiredState.put("hunger", 40);
        desiredState.put("health", 70);
        
        Goal goal = new Goal("BasicNeeds", desiredState, 8);
        goal.setPersistent(true);
        return goal;
    }
    
    /**
     * 创建工作目标
     */
    private Goal createWorkGoal() {
        Map<String, Object> desiredState = new HashMap<>();
        desiredState.put("income", 50);
        desiredState.put("workCompleted", true);
        
        Goal goal = new Goal("WorkProductivity", desiredState, 6);
        goal.setPersistent(true);
        return goal;
    }
    
    /**
     * 创建社交目标
     */
    private Goal createSocialGoal() {
        Map<String, Object> desiredState = new HashMap<>();
        desiredState.put("happiness", 70);
        desiredState.put("social", 60);
        
        Goal goal = new Goal("SocialWellbeing", desiredState, 4);
        goal.setPersistent(true);
        return goal;
    }
    
    /**
     * 创建商业目标
     */
    private Goal createBusinessGoal() {
        Map<String, Object> desiredState = new HashMap<>();
        desiredState.put("money", 200);
        desiredState.put("income", 30);
        
        Goal goal = new Goal("BusinessSuccess", desiredState, 7);
        goal.setPersistent(true);
        return goal;
    }
    
    /**
     * 创建创作目标
     */
    private Goal createCreativeGoal() {
        Map<String, Object> desiredState = new HashMap<>();
        desiredState.put("inspiration", 80);
        desiredState.put("creativity", 90);
        
        Goal goal = new Goal("CreativeExpression", desiredState, 5);
        goal.setPersistent(true);
        return goal;
    }
    
    /**
     * 创建研究目标
     */
    private Goal createResearchGoal() {
        Map<String, Object> desiredState = new HashMap<>();
        desiredState.put("knowledge", 95);
        desiredState.put("researchCompleted", true);
        
        Goal goal = new Goal("ScientificDiscovery", desiredState, 9);
        goal.setPersistent(true);
        return goal;
    }
}
