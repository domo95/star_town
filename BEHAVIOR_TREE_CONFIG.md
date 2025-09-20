# 行为树配置指南

## 概述

Star Town游戏使用行为树系统来控制智能体的决策逻辑。行为树是一种层次化的决策结构，用于定义智能体在不同情况下的行为模式。

## 行为树结构

### 节点类型

1. **选择器节点 (Selector)**
   - 按顺序执行子节点，直到有一个成功就返回成功
   - 只有当所有子节点都失败时才返回失败

2. **顺序节点 (Sequence)**
   - 按顺序执行子节点，只有当所有子节点都成功时才返回成功
   - 如果任何一个子节点失败，则立即返回失败

3. **条件节点 (Condition)**
   - 检查某个条件是否满足
   - 返回SUCCESS或FAILURE

4. **动作节点 (Action)**
   - 执行具体的动作逻辑
   - 可以返回RUNNING、SUCCESS或FAILURE

### 内置条件节点

- `IsHungry`: 检查是否饥饿（饥饿度 > 70）
- `IsTired`: 检查是否疲劳（精力 < 30）
- `HasFood`: 检查是否有食物
- `HasWorkplace`: 检查是否有工作场所
- `HasNearbyAgents`: 检查附近是否有其他智能体

### 内置动作节点

- `Eat`: 进食，减少饥饿度
- `Sleep`: 睡眠，恢复精力
- `Work`: 工作，消耗精力，增加收入
- `Socialize`: 社交，增加幸福感

## 配置方法

### 1. 使用JSON配置

```json
{
  "name": "ResidentTree",
  "description": "居民行为树",
  "rootNode": {
    "name": "RootSelector",
    "type": "selector",
    "children": [
      {
        "name": "EatBranch",
        "type": "sequence",
        "children": [
          {
            "name": "IsHungry",
            "type": "condition"
          },
          {
            "name": "Eat",
            "type": "action"
          }
        ]
      },
      {
        "name": "WorkBranch",
        "type": "sequence",
        "children": [
          {
            "name": "HasWorkplace",
            "type": "condition"
          },
          {
            "name": "Work",
            "type": "action"
          }
        ]
      }
    ]
  }
}
```

### 2. 使用代码配置

```java
// 创建行为树构建器
BehaviorTreeBuilder builder = new BehaviorTreeBuilder();

// 注册自定义条件节点
builder.registerConditionNode("IsVeryHungry", context -> {
    Integer hunger = context.get("hunger", Integer.class);
    return hunger != null && hunger > 90;
});

// 注册自定义动作节点
builder.registerActionNode("EmergencyEat", context -> {
    context.put("hunger", 20);
    return BehaviorNode.Status.SUCCESS;
});

// 构建行为树
BehaviorNode tree = builder.buildDefaultTree();
```

### 3. 通过API配置

```bash
# 获取示例配置
curl -X GET http://localhost:8080/api/behavior-tree/example/RESIDENT

# 创建自定义配置
curl -X POST http://localhost:8080/api/behavior-tree/config \
  -H "Content-Type: application/json" \
  -d '{
    "name": "CustomTree",
    "description": "自定义行为树"
  }'

# 从JSON加载配置
curl -X POST http://localhost:8080/api/behavior-tree/load-from-json \
  -H "Content-Type: application/json" \
  -d '{
    "jsonConfig": "{\"name\":\"TestTree\",...}"
  }'
```

## 智能体类型对应的默认行为树

### 居民 (RESIDENT)
- 优先级：紧急需求 > 基本需求 > 社交需求
- 包含饥饿、疲劳、社交等基础行为

### 工人 (WORKER)
- 优先级：工作 > 休息 > 进食
- 注重工作效率和体力管理

### 商人 (MERCHANT)
- 优先级：交易 > 社交 > 休息
- 注重商业活动和人际关系

### 艺术家 (ARTIST)
- 优先级：创作 > 社交 > 基本需求
- 注重创意和灵感

### 科学家 (SCIENTIST)
- 优先级：研究 > 基本需求
- 注重知识积累和实验

### 守卫 (GUARD)
- 优先级：巡逻 > 警戒 > 基本需求
- 注重安全和秩序

## 自定义行为树

### 1. 创建自定义条件节点

```java
// 在BehaviorTreeBuilder中注册
builder.registerConditionNode("IsWeekend", context -> {
    // 检查是否是周末
    return isWeekend();
});

builder.registerConditionNode("HasMoney", context -> {
    Integer money = context.get("money", Integer.class);
    return money != null && money > 100;
});
```

### 2. 创建自定义动作节点

```java
// 注册自定义动作
builder.registerActionNode("BuyFood", context -> {
    Integer money = context.get("money", Integer.class);
    if (money != null && money >= 50) {
        context.put("money", money - 50);
        context.put("hasFood", true);
        return BehaviorNode.Status.SUCCESS;
    }
    return BehaviorNode.Status.FAILURE;
});

builder.registerActionNode("CreateArt", context -> {
    Integer creativity = context.get("creativity", Integer.class);
    if (creativity != null && creativity > 70) {
        context.put("happiness", 90);
        context.put("income", 200);
        return BehaviorNode.Status.SUCCESS;
    }
    return BehaviorNode.Status.FAILURE;
});
```

### 3. 构建复杂行为树

```java
// 创建复杂的行为树逻辑
SelectorNode root = new SelectorNode("ComplexRoot");

// 紧急情况分支
SequenceNode emergencyBranch = new SequenceNode("Emergency");
emergencyBranch.addChild(builder.getRegisteredNode("IsVeryHungry"));
emergencyBranch.addChild(builder.getRegisteredNode("EmergencyEat"));

// 创作分支
SequenceNode creativeBranch = new SequenceNode("Creative");
creativeBranch.addChild(builder.getRegisteredNode("IsWeekend"));
creativeBranch.addChild(builder.getRegisteredNode("HasMoney"));
creativeBranch.addChild(builder.getRegisteredNode("CreateArt"));

// 购买分支
SequenceNode shoppingBranch = new SequenceNode("Shopping");
shoppingBranch.addChild(builder.getRegisteredNode("HasMoney"));
shoppingBranch.addChild(builder.getRegisteredNode("BuyFood"));

root.addChild(emergencyBranch);
root.addChild(creativeBranch);
root.addChild(shoppingBranch);
```

## 行为树执行流程

1. **初始化**: 智能体启动时加载对应的行为树
2. **执行**: 每个游戏循环都会执行行为树
3. **状态更新**: 根据行为树执行结果更新智能体状态
4. **缓存**: 行为树会被缓存以提高性能

## 调试和监控

### 1. 查看智能体行为树状态
```bash
curl -X GET http://localhost:8080/api/behavior-tree/agent/{agentId}
```

### 2. 清除行为树缓存
```bash
curl -X POST http://localhost:8080/api/behavior-tree/clear-cache
```

### 3. 查看日志
行为树的执行过程会在日志中记录，可以通过日志分析智能体的决策过程。

## 最佳实践

1. **层次化设计**: 使用选择器和顺序节点创建清晰的决策层次
2. **优先级管理**: 将紧急需求放在高层，日常需求放在低层
3. **状态检查**: 在动作执行前检查必要的状态条件
4. **错误处理**: 为异常情况提供备选行为
5. **性能优化**: 避免过深的行为树层次，合理使用缓存

## 示例配置文件

参考 `src/main/resources/behavior-tree-examples.json` 文件中的完整示例配置。

通过这些配置，您可以创建出复杂而有趣的智能体行为模式，让Star Town中的每个智能体都有独特的个性和行为特征。
