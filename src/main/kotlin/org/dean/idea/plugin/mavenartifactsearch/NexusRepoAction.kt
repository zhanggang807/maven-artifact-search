package org.dean.idea.plugin.mavenartifactsearch

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import java.net.URLEncoder

/**
 * 在 Nexus 仓库中搜索
 */
class NexusRepoAction : AnAction("Nexus Repo") {
    
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        
        val artifact = MavenArtifactParser.parseFromEditor(editor, psiFile)
        
        if (artifact == null || (artifact.groupId.isNullOrBlank() && artifact.artifactId.isNullOrBlank())) {
            Messages.showErrorDialog(
                e.project,
                "无法解析 groupId 或 artifactId，请确保选中了包含 Maven 依赖的 XML 内容",
                "Maven Artifact Search"
            )
            return
        }
        
        val url = buildNexusUrl(artifact.groupId, artifact.artifactId, artifact.version, e.project)
        if (url != null) {
            BrowserUtil.browse(url)
        }
    }
    
    /**
     * 构建 Nexus 仓库搜索 URL
     * https://{你配置的域名}/#browse/search/maven=attributes.maven2.groupId%3Dorg.jacoco%20AND%20attributes.maven2.artifactId%3Djacoco-maven-plugin%20AND%20attributes.maven2.baseVersion%3D0.8.14
     */
    private fun buildNexusUrl(groupId: String?, artifactId: String?, version: String?, project: com.intellij.openapi.project.Project?): String? {
        // 从配置中读取 Nexus 域名
        val settings = MavenArtifactSearchSettings.getInstance()
        val nexusDomain = settings.nexusDomain.trim()
        
        // 如果配置为空，提示用户配置
        if (nexusDomain.isBlank()) {
            Messages.showErrorDialog(
                project,
                "请先在 Settings -> Other Settings -> Maven Artifact Search 中配置 Nexus 域名",
                "Maven Artifact Search"
            )
            return null
        }
        
        val baseUrl = "https://$nexusDomain/#browse/search/maven="
        
        val conditions = mutableListOf<String>()
        
        if (!groupId.isNullOrBlank()) {
            val encodedGroupId = URLEncoder.encode(groupId, "UTF-8")
            conditions.add("attributes.maven2.groupId%3D$encodedGroupId")
        }
        
        if (!artifactId.isNullOrBlank()) {
            val encodedArtifactId = URLEncoder.encode(artifactId, "UTF-8")
            conditions.add("attributes.maven2.artifactId%3D$encodedArtifactId")
        }
        
        // 如果版本号不为空，添加到搜索条件中
        if (!version.isNullOrBlank()) {
            val encodedVersion = URLEncoder.encode(version, "UTF-8")
            conditions.add("attributes.maven2.baseVersion%3D$encodedVersion")
        }
        
        val query = if (conditions.isNotEmpty()) {
            conditions.joinToString("%20AND%20")
        } else {
            ""
        }
        
        return baseUrl + query
    }
    
    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabled = editor != null
    }
}

