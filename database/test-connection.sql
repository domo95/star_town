-- MySQL 连接测试脚本

-- 1. 测试基本连接
SELECT 'MySQL连接测试开始' as test_step;

-- 2. 检查MySQL版本
SELECT VERSION() as mysql_version;

-- 3. 检查字符集支持
SHOW CHARACTER SET WHERE Charset LIKE '%utf%';

-- 4. 检查排序规则
SHOW COLLATION WHERE Charset = 'utf8';

-- 5. 测试数据库创建（如果不存在）
CREATE DATABASE IF NOT EXISTS star_town_test 
CHARACTER SET utf8 
COLLATE utf8_general_ci;

-- 6. 测试表创建
USE star_town_test;

CREATE TABLE IF NOT EXISTS test_table (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 7. 测试插入数据
INSERT INTO test_table (name) VALUES ('测试数据1'), ('测试数据2');

-- 8. 测试查询数据
SELECT * FROM test_table;

-- 9. 清理测试数据
DROP TABLE test_table;
DROP DATABASE star_town_test;

-- 10. 显示结果
SELECT 'MySQL连接测试完成 - 所有测试通过' as test_result;
