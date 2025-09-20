package org.example.star_town.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.star_town.ai.behavior.BehaviorNode;
import org.example.star_town.ai.behavior.BehaviorTreeConfig;
import org.example.star_town.agent.Agent;
import org.example.star_town.agent.AgentManager;
import org.example.star_town.agent.AgentType;
import org.example.star_town.service.BehaviorTreeConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 行为树配置控制器
 * 提供行为树管理和配置的REST API
 */
@Slf4j
@RestController
@RequestMapping("/api/behavior-tree")
@RequiredArgsConstructor
public class BehaviorTreeController {
    
    private final BehaviorTreeConfigService behaviorTreeService;
    private final AgentManager agentManager;
    
    /**
     * 获取所有支持的行为树类型
     */
    @GetMapping("/types")
    public ResponseEntity<List<String>> getSupportedTreeTypes() {
        List<String> types = behaviorTreeService.getSupportedTreeTypes();
        return ResponseEntity.ok(types);
    }
    
    /**
     * 获取所有公共行为节点
     */
    @GetMapping("/common-behaviors")
    public ResponseEntity<Set<String>> getCommonBehaviors() {
        Set<String> behaviors = behaviorTreeService.getCommonBehaviorNames();
        return ResponseEntity.ok(behaviors);
    }
    
    /**
     * 获取所有行为模板
     */
    @GetMapping("/templates")
    public ResponseEntity<Set<String>> getBehaviorTemplates() {
        Set<String> templates = behaviorTreeService.getTemplateNames();
        return ResponseEntity.ok(templates);
    }
    
    /**
     * 获取行为模板详情
     */
    @GetMapping("/templates/{templateName}")
    public ResponseEntity<Map<String, Object>> getTemplateDetails(@PathVariable String templateName) {
        var template = behaviorTreeService.getTemplate(templateName);
        if (template != null) {
            Map<String, Object> details = Map.of(
                    "name", template.getName(),
                    "description", template.getDescription(),
                    "conditions", template.getConditions(),
                    "actions", template.getActions()
            );
            return ResponseEntity.ok(details);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 基于模板创建行为树
     */
    @PostMapping("/create-from-templates")
    public ResponseEntity<Map<String, Object>> createTreeFromTemplates(@RequestBody CreateFromTemplatesRequest request) {
        try {
            BehaviorNode tree = behaviorTreeService.createTreeFromTemplates(request.getTemplateNames(), request.getRootType());
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "treeName", tree.getName(),
                    "message", "Behavior tree created successfully from templates"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to create behavior tree: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 获取智能体的行为树信息
     */
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<Map<String, Object>> getAgentBehaviorTree(@PathVariable String agentId) {
        Agent agent = agentManager.getAgent(agentId);
        if (agent == null) {
            return ResponseEntity.notFound().build();
        }
        
        BehaviorNode tree = behaviorTreeService.getBehaviorTree(agent);
        
        Map<String, Object> treeInfo = Map.of(
                "agentId", agentId,
                "agentType", agent.getType(),
                "treeName", tree != null ? tree.getName() : "No Tree",
                "treeStatus", tree != null ? "Active" : "Inactive"
        );
        
        return ResponseEntity.ok(treeInfo);
    }
    
    /**
     * 创建自定义行为树配置
     */
    @PostMapping("/config")
    public ResponseEntity<BehaviorTreeConfig> createCustomTreeConfig(@RequestBody CreateTreeConfigRequest request) {
        BehaviorTreeConfig config = behaviorTreeService.createCustomTreeConfig(
                request.getName(), 
                request.getDescription()
        );
        
        return ResponseEntity.ok(config);
    }
    
    /**
     * 从JSON加载行为树配置
     */
    @PostMapping("/load-from-json")
    public ResponseEntity<Map<String, Object>> loadTreeFromJson(@RequestBody LoadTreeRequest request) {
        try {
            BehaviorNode tree = behaviorTreeService.loadBehaviorTreeFromJson(request.getJsonConfig());
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "treeName", tree.getName(),
                    "message", "Behavior tree loaded successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to load behavior tree: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 保存行为树配置为JSON
     */
    @PostMapping("/save-to-json")
    public ResponseEntity<Map<String, Object>> saveTreeToJson(@RequestBody BehaviorTreeConfig config) {
        try {
            String json = behaviorTreeService.saveBehaviorTreeToJson(config);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "jsonConfig", json,
                    "message", "Behavior tree saved successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to save behavior tree: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 清除行为树缓存
     */
    @PostMapping("/clear-cache")
    public ResponseEntity<Map<String, Object>> clearCache() {
        behaviorTreeService.clearCache();
        
        return ResponseEntity.ok(Map.of(
                "message", "Behavior tree cache cleared successfully"
        ));
    }
    
    /**
     * 获取特定类型智能体的行为树配置示例
     */
    @GetMapping("/example/{agentType}")
    public ResponseEntity<BehaviorTreeConfig> getExampleTreeConfig(@PathVariable AgentType agentType) {
        // 创建示例配置
        BehaviorTreeConfig config = new BehaviorTreeConfig();
        config.setName(agentType.name() + "Tree");
        config.setDescription("Example behavior tree for " + agentType.getDisplayName());
        
        // 创建根节点
        BehaviorTreeConfig.BehaviorTreeNodeConfig root = new BehaviorTreeConfig.BehaviorTreeNodeConfig("Root", "selector");
        
        // 根据智能体类型添加不同的节点
        switch (agentType) {
            case RESIDENT -> {
                root.getChildren().add(new BehaviorTreeConfig.BehaviorTreeNodeConfig("BasicNeeds", "sequence"));
                root.getChildren().add(new BehaviorTreeConfig.BehaviorTreeNodeConfig("Social", "sequence"));
            }
            case WORKER -> {
                root.getChildren().add(new BehaviorTreeConfig.BehaviorTreeNodeConfig("Work", "sequence"));
                root.getChildren().add(new BehaviorTreeConfig.BehaviorTreeNodeConfig("Rest", "sequence"));
            }
            case MERCHANT -> {
                root.getChildren().add(new BehaviorTreeConfig.BehaviorTreeNodeConfig("Trade", "sequence"));
                root.getChildren().add(new BehaviorTreeConfig.BehaviorTreeNodeConfig("Rest", "sequence"));
            }
            default -> {
                root.getChildren().add(new BehaviorTreeConfig.BehaviorTreeNodeConfig("Default", "sequence"));
            }
        }
        
        config.setRootNode(root);
        
        return ResponseEntity.ok(config);
    }
    
    /**
     * 创建树配置请求
     */
    public static class CreateTreeConfigRequest {
        private String name;
        private String description;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    /**
     * 加载树请求
     */
    public static class LoadTreeRequest {
        private String jsonConfig;
        
        // Getters and setters
        public String getJsonConfig() { return jsonConfig; }
        public void setJsonConfig(String jsonConfig) { this.jsonConfig = jsonConfig; }
    }
    
    /**
     * 基于模板创建请求
     */
    public static class CreateFromTemplatesRequest {
        private List<String> templateNames;
        private String rootType = "selector";
        
        // Getters and setters
        public List<String> getTemplateNames() { return templateNames; }
        public void setTemplateNames(List<String> templateNames) { this.templateNames = templateNames; }
        
        public String getRootType() { return rootType; }
        public void setRootType(String rootType) { this.rootType = rootType; }
    }
}
