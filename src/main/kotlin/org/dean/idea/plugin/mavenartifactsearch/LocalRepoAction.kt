package org.dean.idea.plugin.mavenartifactsearch

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import java.io.File
import java.io.IOException


/**
 * 打开本地 Repo 仓库文件夹
 */
class LocalRepoAction : AnAction("Local Repo") {
    
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
        
        val localRepoPath = getLocalRepositoryPath(artifact.groupId, artifact.artifactId, artifact.version)
        if (localRepoPath == null) {
            Messages.showErrorDialog(
                e.project,
                "无法构建 Maven 仓库路径，groupId 和 artifactId 不能同时为空",
                "Maven Artifact Search"
            )
            return
        }
        
        val LocalRepoDir = File(localRepoPath)
        if (!LocalRepoDir.exists()) {
            Messages.showWarningDialog(
                e.project,
                "路径不存在: $localRepoPath",
                "Maven Artifact Search"
            )
            return
        }
        
        if (!LocalRepoDir.isDirectory) {
            Messages.showWarningDialog(
                e.project,
                "路径不是目录: $localRepoPath",
                "Maven Artifact Search"
            )
            return
        }
        
        // 在 macOS Finder 中打开文件夹
        try {
            val processBuilder = ProcessBuilder(
                "open",
//                "-n",
                LocalRepoDir.absolutePath
            )
            // 启动进程
            val process = processBuilder.start()
            // 可选：等待进程执行完成并处理异常
            val exitCode = process.waitFor()
            if (exitCode != 0) {
                Messages.showErrorDialog(
                    e.project,
                    "打开: $localRepoPath 失败，exitCode: $exitCode",
                    "Maven Artifact Search"
                )
            }
        } catch (ex: IOException) {
            Messages.showErrorDialog(
                e.project,
                "打开: $localRepoPath 失败",
                "Maven Artifact Search"
            )
        }
    }
    
    /**
     * 根据 Maven 本地仓库路径规范构建路径
     * 格式: ~/.m2/repository/{groupId替换.为/}/{artifactId}/{version}
     * 如果没有 version，就打开 artifactId 目录
     */
    private fun getLocalRepositoryPath(groupId: String?, artifactId: String?, version: String?): String? {
        val homeDir = System.getProperty("user.home")
        val localRepoBase = "$homeDir/.m2/repository"
        
        if (groupId.isNullOrBlank() && artifactId.isNullOrBlank()) {
            return null
        }
        
        val pathBuilder = StringBuilder(localRepoBase)
        
        if (!groupId.isNullOrBlank()) {
            val groupPath = groupId.replace(".", "/")
            pathBuilder.append("/").append(groupPath)
        }
        
        if (!artifactId.isNullOrBlank()) {
            pathBuilder.append("/").append(artifactId)
        }
        
        // 如果版本号不为空，添加到路径中
        if (!version.isNullOrBlank()) {
            pathBuilder.append("/").append(version)
        }
        
        return pathBuilder.toString()
    }
    
    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabled = editor != null
    }
}

