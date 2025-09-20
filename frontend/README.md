# Star Town 前端页面使用指南

## 📋 页面概览

### 1. 行为树管理器 (`behavior-tree-manager.html`)
**功能：** 基于模板的快速行为树创建
**特点：** 
- 选择预定义模板快速构建行为树
- 支持模板组合
- 简单的可视化展示
- 配置导入/导出

**适用场景：** 快速创建标准行为树，适合初学者

### 2. 行为树配置器 (`behavior-tree-configurator.html`)
**功能：** 完整的可视化行为树编辑器
**特点：**
- 拖拽式节点创建
- 实时可视化编辑
- 节点属性配置
- 缩放和平移
- 完整的保存/加载功能

**适用场景：** 复杂行为树设计，需要精确控制

### 3. 行为树编辑器 (`behavior-tree-editor.html`)
**功能：** 集成API的增强版编辑器
**特点：**
- 与后端API完全集成
- 动态加载节点类型和模板
- 实时验证和保存
- 智能体类型支持
- 模板应用功能

**适用场景：** 生产环境使用，需要与后端集成

### 4. 诊断工具 (`test-behavior-tree.html`)
**功能：** 系统诊断和调试
**特点：**
- 系统状态检查
- 行为树测试
- 节点和模板验证
- 实时监控

**适用场景：** 问题诊断和系统维护

## 🚀 快速开始

### 1. 启动后端服务
```bash
mvn spring-boot:run
```

### 2. 打开前端页面
在浏览器中打开任意一个HTML文件：
- `behavior-tree-manager.html` - 简单模板管理
- `behavior-tree-configurator.html` - 完整编辑器
- `behavior-tree-editor.html` - API集成版
- `test-behavior-tree.html` - 诊断工具

### 3. 开始创建行为树
1. 选择智能体类型
2. 输入行为树名称和描述
3. 使用拖拽或模板创建节点
4. 配置节点属性
5. 保存或导出配置

## 🎨 节点类型说明

### 基础节点类型
- **Sequence（序列）**：按顺序执行子节点，所有子节点成功才算成功
- **Selector（选择器）**：执行第一个成功的子节点
- **Condition（条件）**：检查条件是否满足
- **Action（动作）**：执行具体的行为动作

### 预定义条件节点
- `IsHungry`：检查是否饥饿（阈值可配置）
- `IsTired`：检查是否疲劳（阈值可配置）
- `HasFood`：检查是否有食物
- `HasWorkplace`：检查是否有工作场所
- `HasNearbyAgents`：检查附近是否有其他智能体
- `LowHappiness`：检查幸福感是否过低

### 预定义动作节点
- `Eat`：进食，减少饥饿度
- `Sleep`：睡眠，恢复精力
- `Work`：工作，消耗精力获得收入
- `Socialize`：社交，增加幸福感
- `Rest`：休息，恢复少量精力

## 📝 行为模板

### 内置模板
1. **UrgentNeeds（紧急需求）**
   - 处理极度饥饿和疲劳
   - 优先级最高

2. **BasicNeeds（基本需求）**
   - 处理一般饥饿和疲劳
   - 日常需求

3. **SocialNeeds（社交需求）**
   - 处理社交和幸福感需求
   - 提升智能体生活质量

4. **Work（工作行为）**
   - 执行工作相关动作
   - 获取收入

5. **Rest（休息行为）**
   - 执行休息动作
   - 恢复精力

## 🔧 操作指南

### 创建节点
1. **拖拽创建**：从调色板拖拽节点到画布
2. **模板应用**：点击模板列表中的模板
3. **示例加载**：使用"加载示例"按钮

### 编辑节点
1. **选择节点**：点击节点进行选择
2. **移动节点**：拖拽选中的节点
3. **删除节点**：右键点击或按Delete键
4. **配置属性**：在属性面板中修改

### 保存和加载
1. **保存到服务器**：使用"保存"按钮
2. **导出JSON**：使用"导出"按钮
3. **导入配置**：使用"导入"按钮

### 验证和测试
1. **验证配置**：使用"验证"按钮检查配置
2. **测试运行**：在诊断工具中测试行为树
3. **查看日志**：检查浏览器控制台和服务器日志

## 🌐 API接口

### 主要端点
- `GET /api/behavior-tree-visualization/tree/{agentType}` - 获取智能体行为树
- `POST /api/behavior-tree-visualization/save` - 保存行为树
- `GET /api/behavior-tree-visualization/node-types` - 获取节点类型
- `GET /api/behavior-tree-visualization/templates` - 获取行为模板
- `POST /api/behavior-tree-visualization/validate` - 验证行为树

### 请求示例
```javascript
// 保存行为树
const treeData = {
    name: "MyBehaviorTree",
    description: "A custom behavior tree",
    agentType: "RESIDENT",
    nodes: [...],
    edges: [...]
};

fetch('/api/behavior-tree-visualization/save', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(treeData)
});
```

## 🐛 故障排除

### 常见问题

1. **页面无法加载**
   - 检查后端服务是否启动
   - 确认端口8080是否可访问
   - 查看浏览器控制台错误

2. **拖拽功能不工作**
   - 确认浏览器支持HTML5拖拽
   - 检查JavaScript是否启用

3. **保存失败**
   - 检查网络连接
   - 确认API服务正常
   - 查看服务器日志

4. **节点显示异常**
   - 清除浏览器缓存
   - 检查CSS样式是否正确加载

### 调试技巧

1. **使用浏览器开发者工具**
   - F12打开开发者工具
   - 查看Console标签页的错误信息
   - 使用Network标签页检查API请求

2. **查看服务器日志**
   - 检查Spring Boot应用日志
   - 确认数据库连接正常

3. **使用诊断工具**
   - 打开`test-behavior-tree.html`
   - 运行系统诊断
   - 检查各个组件状态

## 📚 进阶使用

### 自定义节点类型
1. 在后端添加新的节点类型定义
2. 实现对应的条件或动作逻辑
3. 更新前端节点调色板

### 扩展模板系统
1. 在数据库中定义新的行为模板
2. 更新API返回模板数据
3. 前端自动加载新模板

### 集成到现有系统
1. 修改API端点路径
2. 调整前端API调用
3. 自定义样式和布局

## 🤝 贡献指南

1. Fork项目仓库
2. 创建功能分支
3. 提交更改
4. 创建Pull Request

## 📄 许可证

MIT License - 详见项目根目录LICENSE文件
