package org.example.star_town.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.star_town.ai.behavior.BehaviorLibrary;
import org.example.star_town.ai.behavior.BehaviorNode;
import org.example.star_town.ai.behavior.BehaviorTreeBuilder;
import org.example.star_town.ai.behavior.BehaviorTreeConfig;
import org.example.star_town.agent.Agent;
import org.example.star_town.agent.AgentType;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 行为树配置服务
 * 管理不同类型智能体的行为树配置
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BehaviorTreeConfigService {
    
    private final ObjectMapper objectMapper;
    private final Map<AgentType, BehaviorNode> behaviorTreeCache = new HashMap<>();
    private final BehaviorTreeBuilder treeBuilder = new BehaviorTreeBuilder();
    
    /**
     * 获取智能体的行为树
     */
    public BehaviorNode getBehaviorTree(Agent agent) {
        AgentType type = agent.getType();
        
        // 从缓存获取
        if (behaviorTreeCache.containsKey(type)) {
            return behaviorTreeCache.get(type);
        }
        
        // 构建新的行为树
        BehaviorNode tree = buildBehaviorTreeForType(type);
        behaviorTreeCache.put(type, tree);
        
        return tree;
    }
    
    /**
     * 根据智能体类型构建行为树
     */
    private BehaviorNode buildBehaviorTreeForType(AgentType type) {
        return switch (type) {
            case RESIDENT -> buildResidentTree();
            case WORKER -> buildWorkerTree();
            case MERCHANT -> buildMerchantTree();
            case ARTIST -> buildArtistTree();
            case SCIENTIST -> buildScientistTree();
            case GUARD -> buildGuardTree();
            case LEADER -> buildLeaderTree();
            case CHILD -> buildChildTree();
            case ELDER -> buildElderTree();
            case VISITOR -> buildVisitorTree();
        };
    }
    
    /**
     * 构建居民行为树
     */
    private BehaviorNode buildResidentTree() {
        // 使用模板构建：紧急需求 -> 基本需求 -> 社交需求
        List<String> templates = List.of("UrgentNeeds", "BasicNeeds", "SocialNeeds");
        return treeBuilder.buildTreeFromTemplates(templates, "selector");
    }
    
    /**
     * 构建工人行为树
     */
    private BehaviorNode buildWorkerTree() {
        // 工人：工作优先，然后基本需求
        List<String> templates = List.of("Work", "BasicNeeds");
        return treeBuilder.buildTreeFromTemplates(templates, "selector");
    }
    
    /**
     * 构建商人行为树
     */
    private BehaviorNode buildMerchantTree() {
        // 商人：社交需求优先，然后是基本需求
        List<String> templates = List.of("SocialNeeds", "BasicNeeds");
        return treeBuilder.buildTreeFromTemplates(templates, "selector");
    }
    
    /**
     * 构建艺术家行为树
     */
    private BehaviorNode buildArtistTree() {
        // 艺术家：社交和创作并重
        List<String> templates = List.of("SocialNeeds", "BasicNeeds");
        return treeBuilder.buildTreeFromTemplates(templates, "selector");
    }
    
    /**
     * 构建科学家行为树
     */
    private BehaviorNode buildScientistTree() {
        // 科学家：工作优先，基本需求次之
        List<String> templates = List.of("Work", "BasicNeeds");
        return treeBuilder.buildTreeFromTemplates(templates, "selector");
    }
    
    /**
     * 构建守卫行为树
     */
    private BehaviorNode buildGuardTree() {
        // 守卫：工作（巡逻）优先，然后是基本需求
        List<String> templates = List.of("Work", "BasicNeeds");
        return treeBuilder.buildTreeFromTemplates(templates, "selector");
    }
    
    /**
     * 构建领导者行为树
     */
    private BehaviorNode buildLeaderTree() {
        // 领导者：社交和基本需求并重
        List<String> templates = List.of("SocialNeeds", "BasicNeeds");
        return treeBuilder.buildTreeFromTemplates(templates, "selector");
    }
    
    /**
     * 构建儿童行为树
     */
    private BehaviorNode buildChildTree() {
        // 儿童：社交优先，然后是基本需求
        List<String> templates = List.of("SocialNeeds", "BasicNeeds");
        return treeBuilder.buildTreeFromTemplates(templates, "selector");
    }
    
    /**
     * 构长者行为树
     */
    private BehaviorNode buildElderTree() {
        // 长者：社交优先，然后是基本需求
        List<String> templates = List.of("SocialNeeds", "BasicNeeds");
        return treeBuilder.buildTreeFromTemplates(templates, "selector");
    }
    
    /**
     * 构建访客行为树
     */
    private BehaviorNode buildVisitorTree() {
        // 访客：社交优先，然后是基本需求
        List<String> templates = List.of("SocialNeeds", "BasicNeeds");
        return treeBuilder.buildTreeFromTemplates(templates, "selector");
    }
    
    /**
     * 从JSON配置加载行为树
     */
    public BehaviorNode loadBehaviorTreeFromJson(String jsonConfig) {
        try {
            BehaviorTreeConfig config = objectMapper.readValue(jsonConfig, BehaviorTreeConfig.class);
            return treeBuilder.buildFromConfig(config);
        } catch (Exception e) {
            log.error("Error loading behavior tree from JSON: {}", e.getMessage());
            return treeBuilder.buildDefaultTree();
        }
    }
    
    /**
     * 保存行为树配置到JSON
     */
    public String saveBehaviorTreeToJson(BehaviorTreeConfig config) {
        try {
            return objectMapper.writeValueAsString(config);
        } catch (Exception e) {
            log.error("Error saving behavior tree to JSON: {}", e.getMessage());
            return "{}";
        }
    }
    
    /**
     * 清除行为树缓存
     */
    public void clearCache() {
        behaviorTreeCache.clear();
        log.info("Behavior tree cache cleared");
    }
    
    /**
     * 获取所有支持的行为树类型
     */
    public List<String> getSupportedTreeTypes() {
        return List.of(
            "default", "advanced", "resident", "worker", "merchant", 
            "artist", "scientist", "guard", "leader", "child", "elder", "visitor"
        );
    }
    
    /**
     * 创建自定义行为树配置
     */
    public BehaviorTreeConfig createCustomTreeConfig(String name, String description) {
        BehaviorTreeConfig config = new BehaviorTreeConfig();
        config.setName(name);
        config.setDescription(description);
        
        // 创建默认的根节点
        BehaviorTreeConfig.BehaviorTreeNodeConfig root = new BehaviorTreeConfig.BehaviorTreeNodeConfig("Root", "selector");
        config.setRootNode(root);
        
        return config;
    }
    
    /**
     * 获取所有公共行为节点名称
     */
    public Set<String> getCommonBehaviorNames() {
        return treeBuilder.getCommonBehaviorNames();
    }
    
    /**
     * 获取所有行为模板名称
     */
    public Set<String> getTemplateNames() {
        return treeBuilder.getTemplateNames();
    }
    
    /**
     * 获取行为模板
     */
    public BehaviorLibrary.BehaviorTemplate getTemplate(String name) {
        return treeBuilder.getTemplate(name);
    }
    
    /**
     * 基于模板创建行为树
     */
    public BehaviorNode createTreeFromTemplates(List<String> templateNames, String rootType) {
        return treeBuilder.buildTreeFromTemplates(templateNames, rootType);
    }
}
