# 发布到 JetBrains 插件市场指南

本文档说明如何将 Maven Artifact Search 插件发布到 JetBrains 插件市场。

## 发布前准备

### 1. 检查清单

- [ ] 确保 `plugin.xml` 中所有信息完整：
  - 插件 ID
  - 插件名称
  - 版本号
  - 作者信息（vendor）
  - 描述（description）
  - 更新日志（change-notes）
  - 分类（category）

- [ ] 确保代码已测试通过
- [ ] 确保插件图标已设置
- [ ] 确保版本号已更新（`plugin.xml` 和 `build.gradle.kts` 保持一致）

### 2. 构建插件

```bash
./gradlew clean buildPlugin
```

构建产物位于 `build/libs/maven-artifact-search-1.0.0.jar`

## 发布步骤

### 1. 登录 JetBrains 插件市场

访问 [JetBrains Plugin Repository](https://plugins.jetbrains.com/)

使用你的 JetBrains 账号登录（如果没有账号，需要先注册）

### 2. 创建新插件

1. 点击右上角的 "Add New Plugin" 或 "Upload Plugin"
2. 填写插件基本信息：
   - **Plugin ID**: `org.dean.idea.plugin.maven-artifact-search`（必须与 plugin.xml 中的 id 一致）
   - **Plugin Name**: `Maven-Artifact-Search`
   - **Version**: `1.0.0`

### 3. 上传插件文件

1. 上传构建好的 JAR 文件：`build/libs/maven-artifact-search-1.0.0.jar`
2. 系统会自动验证插件并提取元数据

### 4. 填写插件信息

在插件管理页面填写以下信息：

#### 基本信息
- **Name**: Maven-Artifact-Search
- **Version**: 1.0.0
- **Vendor**: Dean Zhg
- **Category**: Productivity

#### 描述
- 插件描述会自动从 `plugin.xml` 的 `<description>` 中提取
- 可以在插件市场页面进一步编辑和优化

#### 更新日志
- 更新日志会自动从 `plugin.xml` 的 `<change-notes>` 中提取
- 可以在插件市场页面进一步编辑

#### 截图和图标
- **插件图标**: 会自动从 `pluginIcon.svg` 中提取
- **截图**: 建议上传使用示例截图（可选）
  - 推荐尺寸：1200x600 像素
  - 可以展示插件的使用场景

#### 兼容性
- **Since Build**: 251（IntelliJ IDEA 2025.1）
- **Until Build**: 留空（表示支持最新版本）

### 5. 提交审核

1. 检查所有信息是否完整和正确
2. 点击 "Submit for Review" 提交审核
3. JetBrains 团队会在 1-3 个工作日内审核你的插件

### 6. 审核通过后

- 插件会在 JetBrains 插件市场上线
- 用户可以通过 IDE 的插件管理器搜索和安装你的插件
- 你可以在插件管理页面查看下载量、评分等统计数据

## 更新插件版本

当需要发布新版本时：

1. **更新版本号**
   - 在 `plugin.xml` 中更新 `<version>` 标签
   - 在 `build.gradle.kts` 中更新 `version` 变量
   - 确保两者一致

2. **更新更新日志**
   - 在 `plugin.xml` 的 `<change-notes>` 中添加新版本的更新内容
   - 在 `build.gradle.kts` 的 `changeNotes` 中同步更新

3. **构建新版本**
   ```bash
   ./gradlew clean buildPlugin
   ```

4. **上传新版本**
   - 登录 JetBrains 插件市场
   - 找到你的插件
   - 点击 "Upload New Version"
   - 上传新的 JAR 文件
   - 填写新版本的更新日志
   - 提交审核

## 注意事项

1. **版本号规范**
   - 使用语义化版本号（Semantic Versioning）
   - 格式：`主版本号.次版本号.修订号`（例如：1.0.0）
   - 不要使用 `-SNAPSHOT` 后缀（仅用于开发版本）

2. **插件 ID**
   - 插件 ID 一旦发布就不能更改
   - 确保使用正确的 FQN 格式

3. **动态插件**
   - 本插件已配置为动态插件（`require-restart="false"`）
   - 确保代码符合动态插件的要求

4. **测试**
   - 在发布前充分测试插件功能
   - 确保在不同版本的 IDE 上测试兼容性

5. **文档**
   - 确保 README.md 文档完整
   - 在插件描述中提供清晰的使用说明

## 参考链接

- [JetBrains 插件市场](https://plugins.jetbrains.com/)
- [插件开发文档](https://plugins.jetbrains.com/docs/intellij/)
- [插件发布指南](https://plugins.jetbrains.com/docs/marketplace/plugin-submission.html)
- [插件最佳实践](https://plugins.jetbrains.com/docs/marketplace/best-practices-for-listing.html)

