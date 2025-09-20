package org.example.star_town.ai.behavior;

import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 行为库
 * 管理所有可用的行为节点，包括公共行为和专用行为
 */
@Slf4j
public class BehaviorLibrary {
    
    private static final BehaviorLibrary instance = new BehaviorLibrary();
    private final Map<String, BehaviorNode> behaviorNodes = new ConcurrentHashMap<>();
    private final Map<String, BehaviorTemplate> behaviorTemplates = new ConcurrentHashMap<>();
    
    private BehaviorLibrary() {
        initializeCommonBehaviors();
        initializeBehaviorTemplates();
    }
    
    public static BehaviorLibrary getInstance() {
        return instance;
    }
    
    /**
     * 初始化公共行为
     */
    private void initializeCommonBehaviors() {
        // 公共条件节点
        registerConditionNode("IsHungry", context -> {
            Integer hunger = context.get("hunger", Integer.class);
            return hunger != null && hunger > 70;
        });
        
        registerConditionNode("IsVeryHungry", context -> {
            Integer hunger = context.get("hunger", Integer.class);
            return hunger != null && hunger > 90;
        });
        
        registerConditionNode("IsTired", context -> {
            Integer energy = context.get("energy", Integer.class);
            return energy != null && energy < 30;
        });
        
        registerConditionNode("IsVeryTired", context -> {
            Integer energy = context.get("energy", Integer.class);
            return energy != null && energy < 10;
        });
        
        registerConditionNode("HasFood", context -> {
            Boolean hasFood = context.get("hasFood", Boolean.class);
            return hasFood != null && hasFood;
        });
        
        registerConditionNode("HasWorkplace", context -> {
            Boolean hasWorkplace = context.get("hasWorkplace", Boolean.class);
            return hasWorkplace != null && hasWorkplace;
        });
        
        registerConditionNode("HasNearbyAgents", context -> {
            List<Object> nearbyAgents = context.get("nearbyAgents", List.class);
            return nearbyAgents != null && !nearbyAgents.isEmpty();
        });
        
        registerConditionNode("LowHappiness", context -> {
            Integer happiness = context.get("happiness", Integer.class);
            return happiness != null && happiness < 40;
        });
        
        registerConditionNode("ShouldWork", context -> {
            Integer energy = context.get("energy", Integer.class);
            Integer hunger = context.get("hunger", Integer.class);
            return energy != null && energy > 40 && hunger != null && hunger < 60;
        });
        
        // 公共动作节点
        registerActionNode("Eat", context -> {
            log.debug("Executing Eat action");
            Integer hunger = context.get("hunger", Integer.class);
            if (hunger != null) {
                context.put("hunger", Math.max(0, hunger - 30));
            }
            return BehaviorNode.Status.SUCCESS;
        });
        
        registerActionNode("EmergencyEat", context -> {
            log.debug("Executing Emergency Eat action");
            context.put("hunger", 20);
            return BehaviorNode.Status.SUCCESS;
        });
        
        registerActionNode("Sleep", context -> {
            log.debug("Executing Sleep action");
            Integer energy = context.get("energy", Integer.class);
            if (energy != null) {
                context.put("energy", Math.min(100, energy + 50));
            }
            return BehaviorNode.Status.SUCCESS;
        });
        
        registerActionNode("EmergencySleep", context -> {
            log.debug("Executing Emergency Sleep action");
            context.put("energy", 80);
            return BehaviorNode.Status.SUCCESS;
        });
        
        registerActionNode("Work", context -> {
            log.debug("Executing Work action");
            Integer energy = context.get("energy", Integer.class);
            Integer income = context.get("income", Integer.class);
            
            if (energy != null) {
                context.put("energy", Math.max(0, energy - 20));
            }
            if (income != null) {
                context.put("income", income + 10);
            }
            
            return BehaviorNode.Status.SUCCESS;
        });
        
        registerActionNode("Socialize", context -> {
            log.debug("Executing Socialize action");
            Integer happiness = context.get("happiness", Integer.class);
            if (happiness != null) {
                context.put("happiness", Math.min(100, happiness + 15));
            }
            return BehaviorNode.Status.SUCCESS;
        });
        
        registerActionNode("Rest", context -> {
            log.debug("Executing Rest action");
            Integer energy = context.get("energy", Integer.class);
            if (energy != null) {
                context.put("energy", Math.min(100, energy + 25));
            }
            return BehaviorNode.Status.SUCCESS;
        });
    }
    
    /**
     * 初始化行为模板
     */
    private void initializeBehaviorTemplates() {
        // 紧急需求模板
        BehaviorTemplate urgentNeeds = new BehaviorTemplate("UrgentNeeds", "紧急需求处理");
        urgentNeeds.addCondition("IsVeryHungry");
        urgentNeeds.addAction("EmergencyEat");
        urgentNeeds.addCondition("IsVeryTired");
        urgentNeeds.addAction("EmergencySleep");
        registerTemplate(urgentNeeds);
        
        // 基本需求模板
        BehaviorTemplate basicNeeds = new BehaviorTemplate("BasicNeeds", "基本需求处理");
        basicNeeds.addCondition("IsHungry");
        basicNeeds.addAction("Eat");
        basicNeeds.addCondition("IsTired");
        basicNeeds.addAction("Sleep");
        registerTemplate(basicNeeds);
        
        // 社交需求模板
        BehaviorTemplate socialNeeds = new BehaviorTemplate("SocialNeeds", "社交需求处理");
        socialNeeds.addCondition("HasNearbyAgents");
        socialNeeds.addCondition("LowHappiness");
        socialNeeds.addAction("Socialize");
        registerTemplate(socialNeeds);
        
        // 工作模板
        BehaviorTemplate workTemplate = new BehaviorTemplate("Work", "工作行为");
        workTemplate.addCondition("ShouldWork");
        workTemplate.addCondition("HasWorkplace");
        workTemplate.addAction("Work");
        registerTemplate(workTemplate);
        
        // 休息模板
        BehaviorTemplate restTemplate = new BehaviorTemplate("Rest", "休息行为");
        restTemplate.addCondition("IsTired");
        restTemplate.addAction("Rest");
        registerTemplate(restTemplate);
    }
    
    /**
     * 注册条件节点
     */
    public void registerConditionNode(String name, java.util.function.Function<BehaviorContext, Boolean> condition) {
        ConditionNode node = new ConditionNode(name) {
            @Override
            protected boolean checkCondition(BehaviorContext context) {
                return condition.apply(context);
            }
        };
        behaviorNodes.put(name, node);
    }
    
    /**
     * 注册动作节点
     */
    public void registerActionNode(String name, java.util.function.Function<BehaviorContext, BehaviorNode.Status> action) {
        ActionNode node = new ActionNode(name) {
            @Override
            protected Status doAction(BehaviorContext context) {
                return action.apply(context);
            }
        };
        behaviorNodes.put(name, node);
    }
    
    /**
     * 注册行为模板
     */
    public void registerTemplate(BehaviorTemplate template) {
        behaviorTemplates.put(template.getName(), template);
    }
    
    /**
     * 获取行为节点
     */
    public BehaviorNode getBehaviorNode(String name) {
        return behaviorNodes.get(name);
    }
    
    /**
     * 获取行为模板
     */
    public BehaviorTemplate getTemplate(String name) {
        return behaviorTemplates.get(name);
    }
    
    /**
     * 获取所有公共行为节点名称
     */
    public Set<String> getCommonBehaviorNames() {
        return new HashSet<>(behaviorNodes.keySet());
    }
    
    /**
     * 获取所有行为模板名称
     */
    public Set<String> getTemplateNames() {
        return new HashSet<>(behaviorTemplates.keySet());
    }
    
    /**
     * 构建复合行为树
     */
    public BehaviorNode buildCompositeTree(List<String> templateNames, String rootType) {
        BehaviorNode root;
        
        if ("selector".equalsIgnoreCase(rootType)) {
            root = new SelectorNode("CompositeRoot");
        } else {
            root = new SequenceNode("CompositeRoot");
        }
        
        for (String templateName : templateNames) {
            BehaviorTemplate template = getTemplate(templateName);
            if (template != null) {
                try {
                    BehaviorNode templateTree = template.buildTree(this);
                    if (templateTree != null && root instanceof CompositeNode compositeNode) {
                        compositeNode.addChild(templateTree);
                    } else {
                        log.warn("Failed to build tree for template: {}", templateName);
                    }
                } catch (Exception e) {
                    log.error("Error building tree for template {}: {}", templateName, e.getMessage(), e);
                }
            } else {
                log.warn("Template not found: {}", templateName);
            }
        }
        
        return root;
    }
    
    /**
     * 行为模板类
     */
    public static class BehaviorTemplate {
        private final String name;
        private final String description;
        private final List<String> conditions = new ArrayList<>();
        private final List<String> actions = new ArrayList<>();
        
        public BehaviorTemplate(String name, String description) {
            this.name = name;
            this.description = description;
        }
        
        public void addCondition(String conditionName) {
            conditions.add(conditionName);
        }
        
        public void addAction(String actionName) {
            actions.add(actionName);
        }
        
        public BehaviorNode buildTree(BehaviorLibrary library) {
            try {
                if (conditions.size() == 1 && actions.size() == 1) {
                    // 简单序列：条件 -> 动作
                    SequenceNode sequence = new SequenceNode(name);
                    BehaviorNode conditionNode = library.getBehaviorNode(conditions.get(0));
                    BehaviorNode actionNode = library.getBehaviorNode(actions.get(0));
                    
                    if (conditionNode != null && actionNode != null) {
                        sequence.addChild(conditionNode);
                        sequence.addChild(actionNode);
                        return sequence;
                    }
                } else if (conditions.size() > 1 && actions.size() == 1) {
                    // 多条件 -> 单动作
                    SequenceNode sequence = new SequenceNode(name);
                    boolean allValid = true;
                    
                    for (String condition : conditions) {
                        BehaviorNode conditionNode = library.getBehaviorNode(condition);
                        if (conditionNode != null) {
                            sequence.addChild(conditionNode);
                        } else {
                            allValid = false;
                            break;
                        }
                    }
                    
                    if (allValid) {
                        BehaviorNode actionNode = library.getBehaviorNode(actions.get(0));
                        if (actionNode != null) {
                            sequence.addChild(actionNode);
                            return sequence;
                        }
                    }
                } else if (conditions.size() == 1 && actions.size() > 1) {
                    // 单条件 -> 多动作
                    SelectorNode selector = new SelectorNode(name);
                    BehaviorNode conditionNode = library.getBehaviorNode(conditions.get(0));
                    
                    if (conditionNode != null) {
                        selector.addChild(conditionNode);
                        for (String action : actions) {
                            BehaviorNode actionNode = library.getBehaviorNode(action);
                            if (actionNode != null) {
                                selector.addChild(actionNode);
                            }
                        }
                        return selector;
                    }
                } else {
                    // 复杂情况：使用选择器
                    SelectorNode selector = new SelectorNode(name);
                    for (String condition : conditions) {
                        BehaviorNode conditionNode = library.getBehaviorNode(condition);
                        if (conditionNode != null) {
                            selector.addChild(conditionNode);
                        }
                    }
                    for (String action : actions) {
                        BehaviorNode actionNode = library.getBehaviorNode(action);
                        if (actionNode != null) {
                            selector.addChild(actionNode);
                        }
                    }
                    return selector;
                }
            } catch (Exception e) {
                log.error("Error building tree for template {}: {}", name, e.getMessage(), e);
            }
            
            // 如果所有情况都失败，返回一个空的选择器
            return new SelectorNode(name + "_Empty");
        }
        
        // Getters
        public String getName() { return name; }
        public String getDescription() { return description; }
        public List<String> getConditions() { return conditions; }
        public List<String> getActions() { return actions; }
    }
}
