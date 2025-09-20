# Star Town - 斯坦福小镇风格游戏

一个基于Spring Boot的智能体模拟游戏，采用行为树、黑板系统和GOAP（Goal-Oriented Action Planning）框架，实现类似斯坦福小镇的AI智能体系统。

## 系统架构

### 核心组件

1. **行为树系统** (`ai.behavior`)
   - `BehaviorNode`: 行为节点基类
   - `SequenceNode`: 顺序节点
   - `SelectorNode`: 选择节点
   - `ActionNode`: 动作节点
   - `ConditionNode`: 条件节点

2. **黑板系统** (`ai.blackboard`)
   - `Blackboard`: 智能体间共享信息的中央存储
   - 支持全局数据和智能体特定数据

3. **GOAP框架** (`ai.goap`)
   - `Goal`: 目标定义
   - `Action`: 动作定义（前置条件和效果）
   - `GoapPlanner`: A*算法规划器
   - `PlanNode`: 计划节点

4. **智能体系统** (`agent`)
   - `Agent`: 智能体主类
   - `AgentManager`: 智能体管理器
   - `AgentConfig`: 配置化支持
   - `AgentType`: 智能体类型枚举

5. **游戏世界** (`world`)
   - `GameWorld`: 游戏世界管理
   - `Position`: 位置系统
   - 空间索引和碰撞检测

6. **数据持久化**
   - MySQL数据库存储
   - JPA实体和Repository
   - 智能体状态持久化

## 智能体类型

- **居民** (RESIDENT): 普通居民，负责日常活动
- **工人** (WORKER): 工人，负责生产和工作
- **商人** (MERCHANT): 商人，负责交易和商业活动
- **守卫** (GUARD): 守卫，负责安全和秩序
- **艺术家** (ARTIST): 艺术家，负责创作和文化活动
- **科学家** (SCIENTIST): 科学家，负责研究和发明
- **领导者** (LEADER): 领导者，负责管理和决策
- **儿童** (CHILD): 儿童，负责学习和游戏
- **长者** (ELDER): 长者，负责传授经验和智慧
- **访客** (VISITOR): 访客，临时访问者

## 基础动作

- **MoveAction**: 移动动作
- **WorkAction**: 工作动作
- **EatAction**: 进食动作
- **SleepAction**: 睡眠动作
- **SocializeAction**: 社交动作

## API接口

### 游戏控制
- `GET /api/game/status` - 获取游戏状态
- `POST /api/game/pause` - 暂停/恢复游戏
- `GET /api/game/agents` - 获取所有智能体

### 智能体管理
- `GET /api/agents/{agentId}` - 获取智能体信息
- `POST /api/agents` - 创建新智能体
- `DELETE /api/agents/{agentId}` - 删除智能体
- `POST /api/agents/{agentId}/move` - 移动智能体

### 智能体配置
- `POST /api/agents/{agentId}/goals` - 添加目标
- `GET /api/agents/{agentId}/goals` - 获取目标列表
- `POST /api/agents/{agentId}/state` - 设置状态
- `POST /api/agents/{agentId}/memory` - 设置记忆

### 事件系统
- `GET /api/events/recent` - 获取最近事件
- `GET /api/events/type/{type}` - 按类型获取事件
- `GET /api/events/agent/{agentId}` - 获取智能体事件

## 配置说明

### 数据库配置
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/star_town
spring.datasource.username=root
spring.datasource.password=your_password
```

### 游戏配置
```properties
star-town.game.tick-rate=1000
star-town.game.max-agents=100
star-town.game.world-size=1000
```

## 使用示例

### 创建智能体
```java
AgentConfig config = new AgentConfig("alice", "Alice", AgentType.RESIDENT);
config.setProperty("energy", 80);
config.setProperty("hunger", 30);

Agent agent = agentManager.createAgent("alice", "Alice", AgentType.RESIDENT, config);
agent.setPosition(new Position(100, 200));
```

### 添加目标和动作
```java
// 添加目标
Map<String, Object> desiredState = new HashMap<>();
desiredState.put("energy", 80);
Goal goal = new Goal("Rest", desiredState, 5);
agent.addGoal(goal);

// 添加动作
agent.addAction(new EatAction());
agent.addAction(new WorkAction());
```

### 创建自定义动作
```java
public class CustomAction extends Action {
    public CustomAction() {
        super("CustomAction");
        addPrecondition("hasResource", true);
        addEffect("taskCompleted", true);
    }
    
    @Override
    public boolean execute(ActionContext context) {
        // 实现动作逻辑
        return true;
    }
}
```

## 运行项目

1. 确保MySQL数据库运行
2. 配置数据库连接信息
3. 运行Spring Boot应用：
   ```bash
   mvn spring-boot:run
   ```
4. 访问 `http://localhost:8080/api/game/status` 查看游戏状态

## 测试

运行测试套件：
```bash
mvn test
```

测试包括：
- 行为树功能测试
- GOAP规划测试
- 智能体系统测试
- API接口测试

## 扩展指南

### 添加新的智能体类型
1. 在 `AgentType` 枚举中添加新类型
2. 实现对应的默认属性
3. 创建专门的初始化逻辑

### 添加新的动作
1. 继承 `Action` 类
2. 定义前置条件和效果
3. 实现 `execute` 方法

### 添加新的目标类型
1. 创建目标实例
2. 定义期望状态
3. 设置优先级和持久性

## 技术栈

- **Spring Boot 3.5.6**: 应用框架
- **Spring AI**: AI集成
- **Spring Data JPA**: 数据访问
- **MySQL**: 数据库
- **Jackson**: JSON处理
- **Lombok**: 代码简化
- **JUnit 5**: 测试框架

## 许可证

MIT License

## 贡献

欢迎提交Issue和Pull Request来改进这个项目。
