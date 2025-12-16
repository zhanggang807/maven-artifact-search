package org.dean.idea.plugin.mavenartifactsearch

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import java.net.URLEncoder

/**
 * 在 Central Repository 中搜索
 */
class CentralRepoAction : AnAction("Central Repo") {
    
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
        
        var url = buildCentralRepositoryUrl(artifact.groupId, artifact.artifactId, artifact.version)
        BrowserUtil.browse(url)

        // "https://central.sonatype.com/artifact/org.apache.commons/commons-text/1.15.0"
        url = url.replace("mvnrepository.com", "central.sonatype.com")
        BrowserUtil.browse(url)
    }
    
    /**
     * 构建 Maven Repository 构件 URL
     * 格式: https://mvnrepository.com/artifact/{groupId}/{artifactId}/{version}
     */
    private fun buildCentralRepositoryUrl(groupId: String?, artifactId: String?, version: String?): String {
        // 如果只有其中一个，回退到搜索页面
        val baseUrl = "https://mvnrepository.com/artifact/"
        val queryParts = mutableListOf<String>()
        
        if (!groupId.isNullOrBlank()) {
            queryParts.add(groupId)
        }
        
        if (!artifactId.isNullOrBlank()) {
            queryParts.add(artifactId)
        }
        
        // 如果版本号不为空，也添加到搜索查询中
        if (!version.isNullOrBlank()) {
            queryParts.add(version)
        }
        
        val query = queryParts.joinToString("/")
//        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        // 不需要再编码了
        
        return baseUrl + query
    }
    
    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabled = editor != null
    }
}

