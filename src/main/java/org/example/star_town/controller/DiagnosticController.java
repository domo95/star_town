package org.example.star_town.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.star_town.ai.behavior.BehaviorLibrary;
import org.example.star_town.ai.behavior.BehaviorNode;
import org.example.star_town.agent.Agent;
import org.example.star_town.agent.AgentManager;
import org.example.star_town.agent.AgentType;
import org.example.star_town.service.BehaviorTreeConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 诊断控制器 - 用于调试和诊断系统问题
 */
@RestController
@RequestMapping("/api/diagnostic")
@RequiredArgsConstructor
@Slf4j
public class DiagnosticController {

    private final AgentManager agentManager;
    private final BehaviorTreeConfigService behaviorTreeService;
    private final BehaviorLibrary behaviorLibrary = BehaviorLibrary.getInstance();

    /**
     * 获取系统诊断信息
     */
    @GetMapping("/system")
    public ResponseEntity<Map<String, Object>> getSystemDiagnostics() {
        Map<String, Object> diagnostics = new HashMap<>();
        
        // 智能体信息
        List<Agent> agents = agentManager.getAllAgents();
        diagnostics.put("totalAgents", agents.size());
        diagnostics.put("agentTypes", agents.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Agent::getType, 
                        java.util.stream.Collectors.counting())));
        
        // 行为库信息
        Set<String> behaviorNodes = behaviorLibrary.getCommonBehaviorNames();
        Set<String> templates = behaviorLibrary.getTemplateNames();
        diagnostics.put("behaviorNodes", behaviorNodes);
        diagnostics.put("behaviorTemplates", templates);
        
        // 行为树配置信息
        diagnostics.put("commonBehaviors", behaviorTreeService.getCommonBehaviorNames());
        diagnostics.put("templateNames", behaviorTreeService.getTemplateNames());
        
        return ResponseEntity.ok(diagnostics);
    }

    /**
     * 测试特定智能体的行为树
     */
    @GetMapping("/agent/{agentId}/behavior-tree")
    public ResponseEntity<Map<String, Object>> testAgentBehaviorTree(@PathVariable String agentId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Agent agent = agentManager.getAgent(agentId);
            if (agent == null) {
                result.put("error", "Agent not found: " + agentId);
                return ResponseEntity.notFound().build();
            }
            
            // 获取行为树
            BehaviorNode tree = behaviorTreeService.getBehaviorTree(agent);
            if (tree != null) {
                result.put("treeName", tree.getName());
                result.put("treeType", tree.getClass().getSimpleName());
                result.put("hasChildren", tree instanceof org.example.star_town.ai.behavior.CompositeNode);
                
                if (tree instanceof org.example.star_town.ai.behavior.CompositeNode compositeNode) {
                    result.put("childrenCount", compositeNode.getChildren().size());
                }
                
                result.put("success", true);
            } else {
                result.put("error", "No behavior tree found for agent");
                result.put("success", false);
            }
            
        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("success", false);
            log.error("Error testing behavior tree for agent {}: {}", agentId, e.getMessage(), e);
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * 测试行为节点
     */
    @GetMapping("/behavior-node/{nodeName}")
    public ResponseEntity<Map<String, Object>> testBehaviorNode(@PathVariable String nodeName) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            BehaviorNode node = behaviorLibrary.getBehaviorNode(nodeName);
            if (node != null) {
                result.put("nodeName", node.getName());
                result.put("nodeType", node.getClass().getSimpleName());
                result.put("exists", true);
                result.put("success", true);
            } else {
                result.put("exists", false);
                result.put("error", "Node not found: " + nodeName);
                result.put("success", false);
            }
        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("success", false);
            log.error("Error testing behavior node {}: {}", nodeName, e.getMessage(), e);
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * 测试行为模板
     */
    @GetMapping("/template/{templateName}")
    public ResponseEntity<Map<String, Object>> testBehaviorTemplate(@PathVariable String templateName) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            BehaviorLibrary.BehaviorTemplate template = behaviorLibrary.getTemplate(templateName);
            if (template != null) {
                result.put("templateName", template.getName());
                result.put("description", template.getDescription());
                result.put("conditions", template.getConditions());
                result.put("actions", template.getActions());
                result.put("exists", true);
                result.put("success", true);
                
                // 测试构建树
                try {
                    BehaviorNode tree = template.buildTree(behaviorLibrary);
                    result.put("treeBuilt", tree != null);
                    if (tree != null) {
                        result.put("treeName", tree.getName());
                        result.put("treeType", tree.getClass().getSimpleName());
                    }
                } catch (Exception e) {
                    result.put("treeBuildError", e.getMessage());
                }
            } else {
                result.put("exists", false);
                result.put("error", "Template not found: " + templateName);
                result.put("success", false);
            }
        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("success", false);
            log.error("Error testing behavior template {}: {}", templateName, e.getMessage(), e);
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * 重新构建所有行为树缓存
     */
    @PostMapping("/rebuild-behavior-trees")
    public ResponseEntity<Map<String, Object>> rebuildBehaviorTrees() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 这里需要访问BehaviorTreeConfigService的内部缓存
            // 由于缓存是私有的，我们需要通过重新获取行为树来刷新缓存
            List<Agent> agents = agentManager.getAllAgents();
            int rebuiltCount = 0;
            
            for (Agent agent : agents) {
                try {
                    BehaviorNode tree = behaviorTreeService.getBehaviorTree(agent);
                    if (tree != null) {
                        rebuiltCount++;
                    }
                } catch (Exception e) {
                    log.warn("Failed to rebuild behavior tree for agent {}: {}", agent.getId(), e.getMessage());
                }
            }
            
            result.put("rebuiltCount", rebuiltCount);
            result.put("totalAgents", agents.size());
            result.put("success", true);
            
        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("success", false);
            log.error("Error rebuilding behavior trees: {}", e.getMessage(), e);
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * 获取智能体状态详情
     */
    @GetMapping("/agent/{agentId}/state")
    public ResponseEntity<Map<String, Object>> getAgentState(@PathVariable String agentId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Agent agent = agentManager.getAgent(agentId);
            if (agent == null) {
                result.put("error", "Agent not found: " + agentId);
                return ResponseEntity.notFound().build();
            }
            
            result.put("agentId", agent.getId());
            result.put("agentName", agent.getName());
            result.put("agentType", agent.getType());
            result.put("status", agent.getStatus());
            result.put("state", agent.getState());
            result.put("memory", agent.getMemory());
            result.put("goals", agent.getGoals());
            result.put("availableActions", agent.getAvailableActions());
            result.put("currentPlan", agent.getCurrentPlan());
            result.put("success", true);
            
        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("success", false);
            log.error("Error getting agent state for {}: {}", agentId, e.getMessage(), e);
        }
        
        return ResponseEntity.ok(result);
    }
}
