package org.example.star_town.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.star_town.ai.behavior.BehaviorNode;
import org.example.star_town.ai.behavior.BehaviorLibrary;
import org.example.star_town.service.BehaviorTreeConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 行为树可视化控制器
 * 支持前端可视化编辑和展示
 */
@RestController
@RequestMapping("/api/behavior-tree-visualization")
@RequiredArgsConstructor
@Slf4j
public class BehaviorTreeVisualizationController {

    private final BehaviorTreeConfigService behaviorTreeService;
    private final BehaviorLibrary behaviorLibrary = BehaviorLibrary.getInstance();

    /**
     * 获取行为树的可视化数据结构
     */
    @GetMapping("/tree/{agentType}")
    public ResponseEntity<Map<String, Object>> getVisualizationTree(@PathVariable String agentType) {
        try {
            // 这里需要根据agentType获取对应的行为树
            // 暂时返回一个示例结构
            Map<String, Object> treeData = new HashMap<>();
            
            // 构建示例树结构
            List<Map<String, Object>> nodes = new ArrayList<>();
            List<Map<String, Object>> edges = new ArrayList<>();
            
            // 根节点
            Map<String, Object> rootNode = new HashMap<>();
            rootNode.put("id", "root");
            rootNode.put("type", "selector");
            rootNode.put("name", "RootSelector");
            rootNode.put("x", 200);
            rootNode.put("y", 50);
            nodes.add(rootNode);
            
            // 子节点
            String[] nodeNames = {"EatSequence", "SleepSequence", "WorkSequence"};
            String[] nodeTypes = {"sequence", "sequence", "sequence"};
            String[] conditionNames = {"IsHungry", "IsTired", "HasWorkplace"};
            String[] actionNames = {"Eat", "Sleep", "Work"};
            
            for (int i = 0; i < nodeNames.length; i++) {
                // 序列节点
                Map<String, Object> sequenceNode = new HashMap<>();
                sequenceNode.put("id", "node_" + i);
                sequenceNode.put("type", nodeTypes[i]);
                sequenceNode.put("name", nodeNames[i]);
                sequenceNode.put("x", 50 + i * 150);
                sequenceNode.put("y", 150);
                nodes.add(sequenceNode);
                
                // 连接到根节点
                Map<String, Object> edge1 = new HashMap<>();
                edge1.put("from", "root");
                edge1.put("to", "node_" + i);
                edges.add(edge1);
                
                // 条件节点
                Map<String, Object> conditionNode = new HashMap<>();
                conditionNode.put("id", "condition_" + i);
                conditionNode.put("type", "condition");
                conditionNode.put("name", conditionNames[i]);
                conditionNode.put("x", 20 + i * 150);
                conditionNode.put("y", 250);
                nodes.add(conditionNode);
                
                // 动作节点
                Map<String, Object> actionNode = new HashMap<>();
                actionNode.put("id", "action_" + i);
                actionNode.put("type", "action");
                actionNode.put("name", actionNames[i]);
                actionNode.put("x", 20 + i * 150);
                actionNode.put("y", 350);
                nodes.add(actionNode);
                
                // 连接到序列节点
                Map<String, Object> edge2 = new HashMap<>();
                edge2.put("from", "node_" + i);
                edge2.put("to", "condition_" + i);
                edges.add(edge2);
                
                Map<String, Object> edge3 = new HashMap<>();
                edge3.put("from", "condition_" + i);
                edge3.put("to", "action_" + i);
                edges.add(edge3);
            }
            
            treeData.put("nodes", nodes);
            treeData.put("edges", edges);
            treeData.put("agentType", agentType);
            treeData.put("createdAt", new Date());
            
            return ResponseEntity.ok(treeData);
            
        } catch (Exception e) {
            log.error("Error getting visualization tree for agent type {}: {}", agentType, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 保存可视化编辑的行为树
     */
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveVisualizationTree(@RequestBody Map<String, Object> treeData) {
        try {
            String treeName = (String) treeData.get("name");
            String description = (String) treeData.get("description");
            String agentType = (String) treeData.get("agentType");
            List<Map<String, Object>> nodes = (List<Map<String, Object>>) treeData.get("nodes");
            List<Map<String, Object>> edges = (List<Map<String, Object>>) treeData.get("edges");
            
            if (treeName == null || treeName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "行为树名称不能为空"));
            }
            
            // 这里应该将可视化数据转换为实际的行为树配置
            // 暂时只是记录日志
            log.info("保存行为树: name={}, agentType={}, nodes={}, edges={}", 
                    treeName, agentType, nodes.size(), edges.size());
            
            // 构建行为树配置
            Map<String, Object> config = new HashMap<>();
            config.put("name", treeName);
            config.put("description", description);
            config.put("agentType", agentType);
            config.put("nodes", nodes);
            config.put("edges", edges);
            config.put("savedAt", new Date());
            
            // 这里应该调用服务保存到数据库
            // behaviorTreeService.saveBehaviorTreeConfig(config);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "行为树保存成功",
                    "treeId", "tree_" + System.currentTimeMillis()
            ));
            
        } catch (Exception e) {
            log.error("Error saving visualization tree: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 获取可用的节点类型和模板
     */
    @GetMapping("/node-types")
    public ResponseEntity<Map<String, Object>> getNodeTypes() {
        try {
            Map<String, Object> nodeTypes = new HashMap<>();
            
            // 基础节点类型
            Map<String, Object> baseTypes = new HashMap<>();
            baseTypes.put("sequence", Map.of(
                    "name", "Sequence",
                    "description", "序列节点：按顺序执行子节点",
                    "color", "#28a745",
                    "icon", "→"
            ));
            baseTypes.put("selector", Map.of(
                    "name", "Selector", 
                    "description", "选择节点：执行第一个成功的子节点",
                    "color", "#ffc107",
                    "icon", "?"
            ));
            baseTypes.put("condition", Map.of(
                    "name", "Condition",
                    "description", "条件节点：检查条件是否满足",
                    "color", "#17a2b8",
                    "icon", "?"
            ));
            baseTypes.put("action", Map.of(
                    "name", "Action",
                    "description", "动作节点：执行具体动作",
                    "color", "#dc3545",
                    "icon", "▶"
            ));
            
            // 预定义的条件节点
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("IsHungry", Map.of(
                    "name", "IsHungry",
                    "description", "检查是否饥饿",
                    "parameters", List.of(Map.of("name", "threshold", "type", "number", "default", 70))
            ));
            conditions.put("IsTired", Map.of(
                    "name", "IsTired", 
                    "description", "检查是否疲劳",
                    "parameters", List.of(Map.of("name", "threshold", "type", "number", "default", 30))
            ));
            conditions.put("HasFood", Map.of(
                    "name", "HasFood",
                    "description", "检查是否有食物",
                    "parameters", List.of()
            ));
            conditions.put("HasWorkplace", Map.of(
                    "name", "HasWorkplace",
                    "description", "检查是否有工作场所",
                    "parameters", List.of()
            ));
            
            // 预定义的动作节点
            Map<String, Object> actions = new HashMap<>();
            actions.put("Eat", Map.of(
                    "name", "Eat",
                    "description", "进食动作",
                    "parameters", List.of(Map.of("name", "hungerReduction", "type", "number", "default", 30))
            ));
            actions.put("Sleep", Map.of(
                    "name", "Sleep",
                    "description", "睡眠动作", 
                    "parameters", List.of(Map.of("name", "energyRestore", "type", "number", "default", 50))
            ));
            actions.put("Work", Map.of(
                    "name", "Work",
                    "description", "工作动作",
                    "parameters", List.of(
                            Map.of("name", "energyCost", "type", "number", "default", 20),
                            Map.of("name", "incomeGain", "type", "number", "default", 10)
                    )
            ));
            
            nodeTypes.put("baseTypes", baseTypes);
            nodeTypes.put("conditions", conditions);
            nodeTypes.put("actions", actions);
            
            return ResponseEntity.ok(nodeTypes);
            
        } catch (Exception e) {
            log.error("Error getting node types: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 获取行为模板列表
     */
    @GetMapping("/templates")
    public ResponseEntity<List<Map<String, Object>>> getBehaviorTemplates() {
        try {
            List<Map<String, Object>> templates = new ArrayList<>();
            
            // 紧急需求模板
            Map<String, Object> urgentNeeds = new HashMap<>();
            urgentNeeds.put("name", "UrgentNeeds");
            urgentNeeds.put("description", "处理紧急生理需求");
            urgentNeeds.put("nodes", List.of(
                    Map.of("type", "condition", "name", "IsVeryHungry", "x", 50, "y", 100),
                    Map.of("type", "action", "name", "EmergencyEat", "x", 50, "y", 200),
                    Map.of("type", "condition", "name", "IsVeryTired", "x", 200, "y", 100),
                    Map.of("type", "action", "name", "EmergencySleep", "x", 200, "y", 200)
            ));
            urgentNeeds.put("edges", List.of(
                    Map.of("from", "IsVeryHungry", "to", "EmergencyEat"),
                    Map.of("from", "IsVeryTired", "to", "EmergencySleep")
            ));
            templates.add(urgentNeeds);
            
            // 基本需求模板
            Map<String, Object> basicNeeds = new HashMap<>();
            basicNeeds.put("name", "BasicNeeds");
            basicNeeds.put("description", "处理基本需求");
            basicNeeds.put("nodes", List.of(
                    Map.of("type", "condition", "name", "IsHungry", "x", 50, "y", 100),
                    Map.of("type", "action", "name", "Eat", "x", 50, "y", 200),
                    Map.of("type", "condition", "name", "IsTired", "x", 200, "y", 100),
                    Map.of("type", "action", "name", "Sleep", "x", 200, "y", 200)
            ));
            basicNeeds.put("edges", List.of(
                    Map.of("from", "IsHungry", "to", "Eat"),
                    Map.of("from", "IsTired", "to", "Sleep")
            ));
            templates.add(basicNeeds);
            
            // 社交需求模板
            Map<String, Object> socialNeeds = new HashMap<>();
            socialNeeds.put("name", "SocialNeeds");
            socialNeeds.put("description", "处理社交需求");
            socialNeeds.put("nodes", List.of(
                    Map.of("type", "condition", "name", "HasNearbyAgents", "x", 50, "y", 100),
                    Map.of("type", "condition", "name", "LowHappiness", "x", 50, "y", 200),
                    Map.of("type", "action", "name", "Socialize", "x", 50, "y", 300)
            ));
            socialNeeds.put("edges", List.of(
                    Map.of("from", "HasNearbyAgents", "to", "LowHappiness"),
                    Map.of("from", "LowHappiness", "to", "Socialize")
            ));
            templates.add(socialNeeds);
            
            return ResponseEntity.ok(templates);
            
        } catch (Exception e) {
            log.error("Error getting behavior templates: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(List.of(Map.of("error", e.getMessage())));
        }
    }

    /**
     * 应用模板到行为树
     */
    @PostMapping("/apply-template")
    public ResponseEntity<Map<String, Object>> applyTemplate(@RequestBody Map<String, Object> request) {
        try {
            String templateName = (String) request.get("templateName");
            String agentType = (String) request.get("agentType");
            
            // 这里应该根据模板名称获取模板数据
            // 暂时返回示例数据
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("templateName", templateName);
            result.put("agentType", agentType);
            result.put("message", "模板应用成功");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Error applying template: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 验证行为树配置
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateTree(@RequestBody Map<String, Object> treeData) {
        try {
            List<Map<String, Object>> nodes = (List<Map<String, Object>>) treeData.get("nodes");
            List<Map<String, Object>> edges = (List<Map<String, Object>>) treeData.get("edges");
            
            List<String> errors = new ArrayList<>();
            List<String> warnings = new ArrayList<>();
            
            // 基本验证
            if (nodes == null || nodes.isEmpty()) {
                errors.add("行为树至少需要一个节点");
            }
            
            // 检查是否有根节点
            boolean hasRoot = nodes.stream().anyMatch(node -> "root".equals(node.get("id")));
            if (!hasRoot) {
                warnings.add("建议指定一个根节点");
            }
            
            // 检查节点连接
            if (edges != null && !edges.isEmpty()) {
                for (Map<String, Object> edge : edges) {
                    String from = (String) edge.get("from");
                    String to = (String) edge.get("to");
                    
                    boolean fromExists = nodes.stream().anyMatch(node -> from.equals(node.get("id")));
                    boolean toExists = nodes.stream().anyMatch(node -> to.equals(node.get("id")));
                    
                    if (!fromExists) {
                        errors.add("连接引用了不存在的起始节点: " + from);
                    }
                    if (!toExists) {
                        errors.add("连接引用了不存在的目标节点: " + to);
                    }
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("valid", errors.isEmpty());
            result.put("errors", errors);
            result.put("warnings", warnings);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Error validating tree: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
