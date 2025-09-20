package org.example.star_town.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 前端页面控制器
 * 提供静态 HTML 页面的访问
 */
@Controller
public class FrontendController {

    /**
     * 行为树编辑器页面
     */
    @GetMapping("/frontend/behavior-tree-editor.html")
    public String behaviorTreeEditor() {
        return "forward:/static/behavior-tree-editor.html";
    }

    /**
     * 行为树配置器页面
     */
    @GetMapping("/frontend/behavior-tree-configurator.html")
    public String behaviorTreeConfigurator() {
        return "forward:/static/behavior-tree-configurator.html";
    }

    /**
     * 行为树管理器页面
     */
    @GetMapping("/frontend/behavior-tree-manager.html")
    public String behaviorTreeManager() {
        return "forward:/static/behavior-tree-manager.html";
    }

    /**
     * 测试页面
     */
    @GetMapping("/frontend/test-behavior-tree.html")
    public String testBehaviorTree() {
        return "forward:/static/test-behavior-tree.html";
    }
}
