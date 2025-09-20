package org.example.star_town.ai.behavior;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * 行为树配置
 * 用于定义行为树的结构
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BehaviorTreeConfig {
    
    private String name;
    private String description;
    private BehaviorTreeNodeConfig rootNode;
    
    /**
     * 行为树节点配置
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BehaviorTreeNodeConfig {
        private String name;
        private String type; // sequence, selector, condition, action
        private List<BehaviorTreeNodeConfig> children;
        private String conditionExpression; // 对于条件节点
        private String actionScript; // 对于动作节点
        private Map<String, Object> parameters;
        
        public BehaviorTreeNodeConfig(String name, String type) {
            this.name = name;
            this.type = type;
            this.children = new ArrayList<>();
        }
        
        public BehaviorTreeNodeConfig(String name, String type, List<BehaviorTreeNodeConfig> children) {
            this.name = name;
            this.type = type;
            this.children = children != null ? children : new ArrayList<>();
        }
    }
}
