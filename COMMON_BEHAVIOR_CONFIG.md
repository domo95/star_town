# 公共行为配置指南

## 概述

Star Town游戏现在支持公共行为配置系统，让您可以轻松地配置和重用通用的行为模式。这个系统解决了不同智能体类型之间行为重复的问题。

## 公共行为库

### 内置公共行为节点

#### 条件节点
- `IsHungry`: 检查是否饥饿（饥饿度 > 70）
- `IsVeryHungry`: 检查是否极度饥饿（饥饿度 > 90）
- `IsTired`: 检查是否疲劳（精力 < 30）
- `IsVeryTired`: 检查是否极度疲劳（精力 < 10）
- `HasFood`: 检查是否有食物
- `HasWorkplace`: 检查是否有工作场所
- `HasNearbyAgents`: 检查附近是否有其他智能体
- `LowHappiness`: 检查是否幸福感低（< 40）
- `ShouldWork`: 检查是否应该工作（精力 > 40 且饥饿度 < 60）

#### 动作节点
- `Eat`: 进食，减少饥饿度30点
- `EmergencyEat`: 紧急进食，将饥饿度设为20
- `Sleep`: 睡眠，恢复精力50点
- `EmergencySleep`: 紧急睡眠，将精力设为80
- `Work`: 工作，消耗精力20点，增加收入10点
- `Socialize`: 社交，增加幸福感15点
- `Rest`: 休息，恢复精力25点

### 行为模板

#### UrgentNeeds（紧急需求）
- 处理极度饥饿和极度疲劳的紧急情况
- 包含：`IsVeryHungry` -> `EmergencyEat`，`IsVeryTired` -> `EmergencySleep`

#### BasicNeeds（基本需求）
- 处理日常的基本生存需求
- 包含：`IsHungry` -> `Eat`，`IsTired` -> `Sleep`

#### SocialNeeds（社交需求）
- 处理社交和情感需求
- 包含：`HasNearbyAgents` + `LowHappiness` -> `Socialize`

#### Work（工作）
- 处理工作相关行为
- 包含：`ShouldWork` + `HasWorkplace` -> `Work`

#### Rest（休息）
- 处理休息恢复行为
- 包含：`IsTired` -> `Rest`

## 配置方法

### 1. 基于模板的简单配置

```java
// 创建居民行为树：紧急需求 -> 基本需求 -> 社交需求
List<String> templates = List.of("UrgentNeeds", "BasicNeeds", "SocialNeeds");
BehaviorNode residentTree = treeBuilder.buildTreeFromTemplates(templates, "selector");

// 创建工人行为树：工作优先，然后基本需求
List<String> workerTemplates = List.of("Work", "BasicNeeds");
BehaviorNode workerTree = treeBuilder.buildTreeFromTemplates(workerTemplates, "selector");
```

### 2. 通过API配置

```bash
# 获取所有公共行为节点
curl http://localhost:8080/api/behavior-tree/common-behaviors

# 获取所有行为模板
curl http://localhost:8080/api/behavior-tree/templates

# 获取模板详情
curl http://localhost:8080/api/behavior-tree/templates/UrgentNeeds

# 基于模板创建行为树
curl -X POST http://localhost:8080/api/behavior-tree/create-from-templates \
  -H "Content-Type: application/json" \
  -d '{
    "templateNames": ["UrgentNeeds", "BasicNeeds", "SocialNeeds"],
    "rootType": "selector"
  }'
```

### 3. 智能体类型默认配置

每种智能体类型都有基于模板的默认行为树：

```java
// 居民：紧急需求 -> 基本需求 -> 社交需求
RESIDENT: ["UrgentNeeds", "BasicNeeds", "SocialNeeds"]

// 工人：工作 -> 基本需求
WORKER: ["Work", "BasicNeeds"]

// 商人：社交需求 -> 基本需求
MERCHANT: ["SocialNeeds", "BasicNeeds"]

// 艺术家：社交需求 -> 基本需求
ARTIST: ["SocialNeeds", "BasicNeeds"]

// 科学家：工作 -> 基本需求
SCIENTIST: ["Work", "BasicNeeds"]

// 守卫：工作 -> 基本需求
GUARD: ["Work", "BasicNeeds"]

// 领导者：社交需求 -> 基本需求
LEADER: ["SocialNeeds", "BasicNeeds"]

// 儿童：社交需求 -> 基本需求
CHILD: ["SocialNeeds", "BasicNeeds"]

// 长者：社交需求 -> 基本需求
ELDER: ["SocialNeeds", "BasicNeeds"]

// 访客：社交需求 -> 基本需求
VISITOR: ["SocialNeeds", "BasicNeeds"]
```

## 自定义扩展

### 1. 添加新的公共行为节点

```java
// 注册新的条件节点
behaviorLibrary.registerConditionNode("IsWeekend", context -> {
    return isWeekend();
});

// 注册新的动作节点
behaviorLibrary.registerActionNode("BuyFood", context -> {
    Integer money = context.get("money", Integer.class);
    if (money != null && money >= 50) {
        context.put("money", money - 50);
        context.put("hasFood", true);
        return BehaviorNode.Status.SUCCESS;
    }
    return BehaviorNode.Status.FAILURE;
});
```

### 2. 创建新的行为模板

```java
// 创建购物模板
BehaviorLibrary.BehaviorTemplate shoppingTemplate = new BehaviorLibrary.BehaviorTemplate("Shopping", "购物行为");
shoppingTemplate.addCondition("IsWeekend");
shoppingTemplate.addCondition("HasMoney");
shoppingTemplate.addAction("BuyFood");

// 注册模板
behaviorLibrary.registerTemplate(shoppingTemplate);
```

### 3. 组合多个模板

```java
// 创建复杂的智能体行为树
List<String> complexTemplates = List.of(
    "UrgentNeeds",    // 最高优先级：紧急需求
    "Shopping",       // 购物需求
    "SocialNeeds",    // 社交需求
    "Work",           // 工作需求
    "BasicNeeds"      // 最低优先级：基本需求
);

BehaviorNode complexTree = treeBuilder.buildTreeFromTemplates(complexTemplates, "selector");
```

## 配置示例

### 示例1：创建专注工作的智能体

```bash
curl -X POST http://localhost:8080/api/behavior-tree/create-from-templates \
  -H "Content-Type: application/json" \
  -d '{
    "templateNames": ["Work", "BasicNeeds"],
    "rootType": "selector"
  }'
```

### 示例2：创建社交导向的智能体

```bash
curl -X POST http://localhost:8080/api/behavior-tree/create-from-templates \
  -H "Content-Type: application/json" \
  -d '{
    "templateNames": ["SocialNeeds", "BasicNeeds"],
    "rootType": "selector"
  }'
```

### 示例3：创建平衡发展的智能体

```bash
curl -X POST http://localhost:8080/api/behavior-tree/create-from-templates \
  -H "Content-Type: application/json" \
  -d '{
    "templateNames": ["UrgentNeeds", "Work", "SocialNeeds", "BasicNeeds"],
    "rootType": "selector"
  }'
```

## 优势

1. **代码重用**: 公共行为只需定义一次，所有智能体都可以使用
2. **易于维护**: 修改公共行为会自动影响所有使用它的智能体
3. **灵活组合**: 通过模板组合可以快速创建不同的行为模式
4. **配置简单**: 只需要指定模板名称和根节点类型
5. **扩展性强**: 可以轻松添加新的公共行为和模板

## 最佳实践

1. **合理使用模板**: 将相关的条件和动作组合成有意义的模板
2. **优先级设计**: 将紧急需求放在高层，日常需求放在低层
3. **避免重复**: 使用公共行为库避免重复定义相同的逻辑
4. **命名规范**: 使用清晰的名称来标识行为和模板
5. **文档维护**: 及时更新公共行为库的文档

通过这个公共行为配置系统，您可以轻松地创建出丰富多样的智能体行为，同时保持代码的整洁和可维护性。
