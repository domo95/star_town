# MySQL 连接问题解决指南

## 🚨 常见错误及解决方案

### 1. 字符编码错误：`Unsupported character encoding 'utf8mb4'`

**错误信息：**
```
Unsupported character encoding 'utf8mb4'
```

**原因：**
MySQL连接URL中使用了 `characterEncoding=utf8mb4`，但Java JDBC驱动不支持这种写法。

**解决方案：**
1. 将连接URL中的 `characterEncoding=utf8mb4` 改为 `characterEncoding=utf8`
2. 在数据库创建时使用 `CHARACTER SET utf8 COLLATE utf8_general_ci`

**修复后的配置：**
```properties
# application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/star_town?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8
```

```sql
-- 数据库创建
CREATE DATABASE IF NOT EXISTS star_town 
CHARACTER SET utf8 
COLLATE utf8_general_ci;
```

### 2. 连接被拒绝：`Connection refused`

**错误信息：**
```
Communications link failure
The last packet sent successfully to the server was 0 milliseconds ago.
```

**解决方案：**
1. 检查MySQL服务是否运行
   ```bash
   # Windows
   net start mysql
   sc query mysql
   
   # Linux
   sudo systemctl start mysql
   sudo systemctl status mysql
   ```

2. 检查端口是否被占用
   ```bash
   # Windows
   netstat -an | findstr :3306
   
   # Linux
   netstat -tlnp | grep :3306
   ```

3. 检查防火墙设置

### 3. 认证失败：`Access denied`

**错误信息：**
```
Access denied for user 'root'@'localhost'
```

**解决方案：**
1. 检查用户名和密码
2. 重置MySQL root密码：
   ```sql
   ALTER USER 'root'@'localhost' IDENTIFIED BY '新密码';
   FLUSH PRIVILEGES;
   ```

### 4. 数据库不存在

**错误信息：**
```
Unknown database 'star_town'
```

**解决方案：**
1. 先创建数据库：
   ```sql
   CREATE DATABASE star_town CHARACTER SET utf8 COLLATE utf8_general_ci;
   ```

2. 运行初始化脚本：
   ```bash
   # Windows
   database\setup-mysql.bat
   
   # Linux
   ./database/setup-mysql.sh
   ```

## 🔧 调试步骤

### 步骤1：测试MySQL连接
```bash
# 命令行测试
mysql -u root -p -h localhost -P 3306
```

### 步骤2：运行连接测试脚本
```sql
SOURCE database/test-connection.sql;
```

### 步骤3：检查Spring Boot配置
确保 `application.properties` 中的配置正确：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/star_town?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8
spring.datasource.username=root
spring.datasource.password=你的密码
```

### 步骤4：使用备用配置
如果主配置有问题，尝试使用备用配置：
```bash
mvn spring-boot:run -Dspring.profiles.active=mysql
```

## 🛠️ 替代方案

### 方案1：使用H2数据库（临时）
如果MySQL问题无法快速解决，可以临时切换回H2：
```properties
# 临时使用H2
spring.datasource.url=jdbc:h2:mem:star_town;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
```

### 方案2：使用Docker MySQL
```bash
# 启动MySQL容器
docker run --name mysql-star-town \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=star_town \
  -p 3306:3306 \
  -d mysql:9.0
```

### 方案3：使用云数据库
如果本地MySQL配置困难，可以考虑使用云数据库服务。

## 📋 检查清单

在报告问题前，请确认：

- [ ] MySQL服务正在运行
- [ ] 端口3306没有被其他程序占用
- [ ] 用户名和密码正确
- [ ] 数据库 `star_town` 已创建
- [ ] 表结构已正确创建
- [ ] 防火墙允许3306端口
- [ ] MySQL用户有足够权限

## 🆘 获取帮助

如果问题仍然存在，请提供以下信息：

1. 完整的错误堆栈跟踪
2. MySQL版本：`SELECT VERSION();`
3. 操作系统信息
4. Java版本：`java -version`
5. Spring Boot版本
6. MySQL配置文件内容（如果修改过）

## 🔄 回滚方案

如果所有方案都失败，可以回滚到H2数据库：

1. 修改 `application.properties`：
```properties
spring.datasource.url=jdbc:h2:mem:star_town;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
```

2. 重新启动应用

3. 访问H2控制台：http://localhost:8080/h2-console
