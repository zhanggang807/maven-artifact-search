package org.dean.idea.plugin.mavenartifactsearch

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

/**
 * Maven Artifact Search 主菜单组
 */
class MavenArtifactSearchActionGroup : ActionGroup() {
    
    private val localRepoAction = LocalRepoAction()
    private val nexusRepoAction = NexusRepoAction()
    private val centralRepoAction = CentralRepoAction()

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        return arrayOf(
            localRepoAction,
            nexusRepoAction,
            centralRepoAction
        )
    }
    
    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabled = editor != null
    }
}

