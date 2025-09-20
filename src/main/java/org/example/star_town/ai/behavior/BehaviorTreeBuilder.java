package org.example.star_town.ai.behavior;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 行为树构建器
 * 用于动态构建和配置行为树
 */
@Slf4j
public class BehaviorTreeBuilder {
    
    private BehaviorNode rootNode;
    private final BehaviorLibrary behaviorLibrary;
    
    public BehaviorTreeBuilder() {
        this.behaviorLibrary = BehaviorLibrary.getInstance();
    }
    
    /**
     * 注册自定义条件节点
     */
    public BehaviorTreeBuilder registerConditionNode(String name, java.util.function.Function<BehaviorContext, Boolean> condition) {
        behaviorLibrary.registerConditionNode(name, condition);
        return this;
    }
    
    /**
     * 注册自定义动作节点
     */
    public BehaviorTreeBuilder registerActionNode(String name, java.util.function.Function<BehaviorContext, BehaviorNode.Status> action) {
        behaviorLibrary.registerActionNode(name, action);
        return this;
    }
    
    /**
     * 从配置构建行为树
     */
    public BehaviorNode buildFromConfig(BehaviorTreeConfig config) {
        rootNode = buildNode(config.getRootNode());
        return rootNode;
    }
    
    /**
     * 构建节点
     */
    private BehaviorNode buildNode(BehaviorTreeConfig.BehaviorTreeNodeConfig nodeConfig) {
        String type = nodeConfig.getType();
        String name = nodeConfig.getName();
        
        BehaviorNode node = switch (type.toLowerCase()) {
            case "sequence" -> new SequenceNode(name);
            case "selector" -> new SelectorNode(name);
            case "condition" -> behaviorLibrary.getBehaviorNode(name);
            case "action" -> behaviorLibrary.getBehaviorNode(name);
            default -> throw new IllegalArgumentException("Unknown node type: " + type);
        };
        
        if (node == null) {
            throw new IllegalArgumentException("Node not found: " + name);
        }
        
        // 如果是复合节点，构建子节点
        if (node instanceof CompositeNode compositeNode) {
            for (BehaviorTreeConfig.BehaviorTreeNodeConfig childConfig : nodeConfig.getChildren()) {
                BehaviorNode childNode = buildNode(childConfig);
                compositeNode.addChild(childNode);
            }
        }
        
        return node;
    }
    
    /**
     * 构建默认的行为树
     */
    public BehaviorNode buildDefaultTree() {
        // 创建根选择器：如果饿了就吃，如果累了就睡，否则工作
        SelectorNode root = new SelectorNode("RootSelector");
        
        // 饥饿分支
        SequenceNode eatBranch = new SequenceNode("EatBranch");
        eatBranch.addChild(behaviorLibrary.getBehaviorNode("IsHungry"));
        eatBranch.addChild(behaviorLibrary.getBehaviorNode("Eat"));
        
        // 疲劳分支
        SequenceNode sleepBranch = new SequenceNode("SleepBranch");
        sleepBranch.addChild(behaviorLibrary.getBehaviorNode("IsTired"));
        sleepBranch.addChild(behaviorLibrary.getBehaviorNode("Sleep"));
        
        // 工作分支
        SequenceNode workBranch = new SequenceNode("WorkBranch");
        workBranch.addChild(behaviorLibrary.getBehaviorNode("HasWorkplace"));
        workBranch.addChild(behaviorLibrary.getBehaviorNode("Work"));
        
        // 社交分支
        SequenceNode socialBranch = new SequenceNode("SocialBranch");
        socialBranch.addChild(behaviorLibrary.getBehaviorNode("HasNearbyAgents"));
        socialBranch.addChild(behaviorLibrary.getBehaviorNode("Socialize"));
        
        root.addChild(eatBranch);
        root.addChild(sleepBranch);
        root.addChild(socialBranch);
        root.addChild(workBranch);
        
        return root;
    }
    
    /**
     * 构建高级行为树（包含更多复杂逻辑）
     */
    public BehaviorNode buildAdvancedTree() {
        SelectorNode root = new SelectorNode("AdvancedRoot");
        
        // 紧急需求分支（高优先级）
        SelectorNode urgentBranch = new SelectorNode("UrgentBranch");
        
        // 极度饥饿
        SequenceNode veryHungryBranch = new SequenceNode("VeryHungryBranch");
        veryHungryBranch.addChild(new ConditionNode("VeryHungry") {
            @Override
            protected boolean checkCondition(BehaviorContext context) {
                Integer hunger = context.get("hunger", Integer.class);
                return hunger != null && hunger > 90;
            }
        });
        veryHungryBranch.addChild(new ActionNode("EmergencyEat") {
            @Override
            protected Status doAction(BehaviorContext context) {
                log.debug("Emergency eating - very hungry!");
                context.put("hunger", 20);
                return Status.SUCCESS;
            }
        });
        
        // 极度疲劳
        SequenceNode veryTiredBranch = new SequenceNode("VeryTiredBranch");
        veryTiredBranch.addChild(new ConditionNode("VeryTired") {
            @Override
            protected boolean checkCondition(BehaviorContext context) {
                Integer energy = context.get("energy", Integer.class);
                return energy != null && energy < 10;
            }
        });
        veryTiredBranch.addChild(new ActionNode("EmergencySleep") {
            @Override
            protected Status doAction(BehaviorContext context) {
                log.debug("Emergency sleeping - very tired!");
                context.put("energy", 80);
                return Status.SUCCESS;
            }
        });
        
        urgentBranch.addChild(veryHungryBranch);
        urgentBranch.addChild(veryTiredBranch);
        
        // 正常需求分支
        SelectorNode normalBranch = new SelectorNode("NormalBranch");
        
        // 基本需求
        SequenceNode basicNeedsBranch = new SequenceNode("BasicNeedsBranch");
        basicNeedsBranch.addChild(behaviorLibrary.getBehaviorNode("IsHungry"));
        basicNeedsBranch.addChild(behaviorLibrary.getBehaviorNode("Eat"));
        
        SequenceNode restBranch = new SequenceNode("RestBranch");
        restBranch.addChild(behaviorLibrary.getBehaviorNode("IsTired"));
        restBranch.addChild(behaviorLibrary.getBehaviorNode("Sleep"));
        
        // 社交需求
        SequenceNode socialNeedsBranch = new SequenceNode("SocialNeedsBranch");
        socialNeedsBranch.addChild(new ConditionNode("LowHappiness") {
            @Override
            protected boolean checkCondition(BehaviorContext context) {
                Integer happiness = context.get("happiness", Integer.class);
                return happiness != null && happiness < 40;
            }
        });
        socialNeedsBranch.addChild(behaviorLibrary.getBehaviorNode("Socialize"));
        
        // 工作分支
        SequenceNode workBranch = new SequenceNode("WorkBranch");
        workBranch.addChild(new ConditionNode("ShouldWork") {
            @Override
            protected boolean checkCondition(BehaviorContext context) {
                Integer energy = context.get("energy", Integer.class);
                Integer hunger = context.get("hunger", Integer.class);
                return energy != null && energy > 40 && hunger != null && hunger < 60;
            }
        });
        workBranch.addChild(behaviorLibrary.getBehaviorNode("Work"));
        
        normalBranch.addChild(basicNeedsBranch);
        normalBranch.addChild(restBranch);
        normalBranch.addChild(socialNeedsBranch);
        normalBranch.addChild(workBranch);
        
        root.addChild(urgentBranch);
        root.addChild(normalBranch);
        
        return root;
    }
    
    /**
     * 基于模板构建行为树
     */
    public BehaviorNode buildTreeFromTemplates(List<String> templateNames, String rootType) {
        return behaviorLibrary.buildCompositeTree(templateNames, rootType);
    }
    
    /**
     * 获取公共行为节点
     */
    public BehaviorNode getBehaviorNode(String name) {
        return behaviorLibrary.getBehaviorNode(name);
    }
    
    /**
     * 获取所有公共行为节点名称
     */
    public Set<String> getCommonBehaviorNames() {
        return behaviorLibrary.getCommonBehaviorNames();
    }
    
    /**
     * 获取所有行为模板名称
     */
    public Set<String> getTemplateNames() {
        return behaviorLibrary.getTemplateNames();
    }
    
    /**
     * 获取行为模板
     */
    public BehaviorLibrary.BehaviorTemplate getTemplate(String name) {
        return behaviorLibrary.getTemplate(name);
    }
}
