plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.7.1"
}

group = "org.dean.idea.plugin"
version = "1.0.2"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Configure IntelliJ Platform Gradle Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    intellijPlatform {
        local("/Users/aido/Applications/IntelliJ IDEA Community Edition.app")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)

        // Add necessary plugin dependencies for compilation here, example:
        // bundledPlugin("com.intellij.java")
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "251"
        }

        changeNotes = """
            <h3>1.0.2 - 功能优化</h3>
            <ul>
                <li>✨ 加入到主菜单Tools中</li>
                <li>📁 增加同时打开central.sonatype.com中央仓库</li>
            </ul>
            <h3>1.0.1 - 文案优化</h3>
            <ul>
                <li>✨ 优化插件名</li>
                <li>📁 优化右键菜单名</li>
            </ul>
            <h3>1.0.0 - 初始版本</h3>
            <ul>
                <li>✨ 支持智能解析 Maven 依赖的 groupId、artifactId、version</li>
                <li>📁 支持在本地 Maven 仓库中查找构件（macOS Finder）</li>
                <li>🌐 支持在 Nexus 仓库中搜索构件（可配置域名）</li>
                <li>📦 支持在 Maven Central Repository 中搜索构件</li>
                <li>⚙️ 提供配置界面，支持自定义 Nexus 域名</li>
                <li>🔄 支持动态插件，无需重启 IDE</li>
                <li>⚡ 右键菜单集成，快速访问所有功能，还可以手动添加到Floating Code Toolbar中</li>
            </ul>
        """.trimIndent()
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}
