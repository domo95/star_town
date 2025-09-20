package org.example.star_town.ai.behavior;

import org.example.star_town.ai.behavior.ActionNode;
import org.example.star_town.ai.behavior.ConditionNode;
import org.example.star_town.ai.behavior.SelectorNode;
import org.example.star_town.ai.behavior.SequenceNode;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 行为树测试
 */
class BehaviorTreeTests {

    @Test
    void testSequenceNodeSuccess() {
        SequenceNode sequence = new SequenceNode("TestSequence");
        
        // 添加条件节点
        sequence.addChild(new ConditionNode("Condition1") {
            @Override
            protected boolean checkCondition(BehaviorContext context) {
                return true;
            }
        });
        
        sequence.addChild(new ConditionNode("Condition2") {
            @Override
            protected boolean checkCondition(BehaviorContext context) {
                return true;
            }
        });
        
        BehaviorContext context = new BehaviorContext("test-agent");
        BehaviorNode.Status result = sequence.execute(context);
        
        assertEquals(BehaviorNode.Status.SUCCESS, result);
    }

    @Test
    void testSequenceNodeFailure() {
        SequenceNode sequence = new SequenceNode("TestSequence");
        
        // 添加条件节点
        sequence.addChild(new ConditionNode("Condition1") {
            @Override
            protected boolean checkCondition(BehaviorContext context) {
                return true;
            }
        });
        
        sequence.addChild(new ConditionNode("Condition2") {
            @Override
            protected boolean checkCondition(BehaviorContext context) {
                return false; // 失败
            }
        });
        
        BehaviorContext context = new BehaviorContext("test-agent");
        BehaviorNode.Status result = sequence.execute(context);
        
        assertEquals(BehaviorNode.Status.FAILURE, result);
    }

    @Test
    void testSelectorNodeSuccess() {
        SelectorNode selector = new SelectorNode("TestSelector");
        
        // 添加条件节点
        selector.addChild(new ConditionNode("Condition1") {
            @Override
            protected boolean checkCondition(BehaviorContext context) {
                return false; // 失败
            }
        });
        
        selector.addChild(new ConditionNode("Condition2") {
            @Override
            protected boolean checkCondition(BehaviorContext context) {
                return true; // 成功
            }
        });
        
        BehaviorContext context = new BehaviorContext("test-agent");
        BehaviorNode.Status result = selector.execute(context);
        
        assertEquals(BehaviorNode.Status.SUCCESS, result);
    }

    @Test
    void testSelectorNodeFailure() {
        SelectorNode selector = new SelectorNode("TestSelector");
        
        // 添加条件节点
        selector.addChild(new ConditionNode("Condition1") {
            @Override
            protected boolean checkCondition(BehaviorContext context) {
                return false;
            }
        });
        
        selector.addChild(new ConditionNode("Condition2") {
            @Override
            protected boolean checkCondition(BehaviorContext context) {
                return false;
            }
        });
        
        BehaviorContext context = new BehaviorContext("test-agent");
        BehaviorNode.Status result = selector.execute(context);
        
        assertEquals(BehaviorNode.Status.FAILURE, result);
    }

    @Test
    void testActionNodeExecution() {
        ActionNode action = new ActionNode("TestAction", 1000) {
            private int executionCount = 0;
            
            @Override
            protected Status doAction(BehaviorContext context) {
                executionCount++;
                if (executionCount == 1) {
                    return Status.RUNNING;
                } else {
                    return Status.SUCCESS;
                }
            }
        };
        
        BehaviorContext context = new BehaviorContext("test-agent");
        
        // 第一次执行应该返回RUNNING
        BehaviorNode.Status result1 = action.execute(context);
        assertEquals(BehaviorNode.Status.RUNNING, result1);
        
        // 第二次执行应该返回SUCCESS
        BehaviorNode.Status result2 = action.execute(context);
        assertEquals(BehaviorNode.Status.SUCCESS, result2);
    }

    @Test
    void testBehaviorContext() {
        BehaviorContext context = new BehaviorContext("test-agent");
        
        // 测试数据存储和获取
        context.put("key1", "value1");
        context.put("key2", 42);
        context.put("key3", true);
        
        assertEquals("value1", context.get("key1", String.class));
        assertEquals(42, context.get("key2", Integer.class));
        assertEquals(true, context.get("key3", Boolean.class));
        
        // 测试默认值
        assertEquals("default", context.getOrDefault("nonexistent", "default", String.class));
        
        // 测试包含检查
        assertTrue(context.contains("key1"));
        assertFalse(context.contains("nonexistent"));
        
        // 测试移除
        Object removed = context.remove("key1");
        assertEquals("value1", removed);
        assertFalse(context.contains("key1"));
    }

    @Test
    void testComplexBehaviorTree() {
        // 创建复杂的行为树：如果饿了就吃，否则工作
        SelectorNode root = new SelectorNode("RootSelector");
        
        // 吃分支
        SequenceNode eatBranch = new SequenceNode("EatBranch");
        eatBranch.addChild(new ConditionNode("IsHungry") {
            @Override
            protected boolean checkCondition(BehaviorContext context) {
                Integer hunger = context.get("hunger", Integer.class);
                return hunger != null && hunger > 70;
            }
        });
        
        eatBranch.addChild(new ActionNode("EatAction") {
            @Override
            protected Status doAction(BehaviorContext context) {
                context.put("hunger", 30); // 减少饥饿度
                return Status.SUCCESS;
            }
        });
        
        // 工作分支
        ActionNode workBranch = new ActionNode("WorkAction") {
            @Override
            protected Status doAction(BehaviorContext context) {
                Integer energy = context.get("energy", Integer.class);
                context.put("energy", energy != null ? energy - 10 : 90);
                return Status.SUCCESS;
            }
        };
        
        root.addChild(eatBranch);
        root.addChild(workBranch);
        
        // 测试饥饿情况
        BehaviorContext hungryContext = new BehaviorContext("test-agent");
        hungryContext.put("hunger", 80);
        hungryContext.put("energy", 50);
        
        BehaviorNode.Status result1 = root.execute(hungryContext);
        assertEquals(BehaviorNode.Status.SUCCESS, result1);
        assertEquals(30, hungryContext.get("hunger", Integer.class));
        
        // 测试不饿情况
        BehaviorContext notHungryContext = new BehaviorContext("test-agent");
        notHungryContext.put("hunger", 30);
        notHungryContext.put("energy", 50);
        
        BehaviorNode.Status result2 = root.execute(notHungryContext);
        assertEquals(BehaviorNode.Status.SUCCESS, result2);
        assertEquals(40, notHungryContext.get("energy", Integer.class));
    }
}
