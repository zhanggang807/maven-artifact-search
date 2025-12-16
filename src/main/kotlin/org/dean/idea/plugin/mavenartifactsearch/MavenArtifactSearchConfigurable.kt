package org.dean.idea.plugin.mavenartifactsearch

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.*
import javax.swing.JComponent

/**
 * 插件设置界面 Configurable
 * 在 Settings -> Other Settings -> Maven Artifact Search 中显示
 */
class MavenArtifactSearchConfigurable : SearchableConfigurable {
    
    private val settings = MavenArtifactSearchSettings.getInstance()
    private var nexusDomainField: javax.swing.JTextField? = null
    private var panel: DialogPanel? = null
    
    override fun getId(): String {
        return "org.dean.idea.plugin.maven-artifact-search.settings"
    }
    
    override fun getDisplayName(): String {
        return "Maven Artifact Search"
    }
    
    override fun createComponent(): JComponent {
        panel = panel {
            row("Nexus Domain:") {
                nexusDomainField = textField()
                    .bindText(settings::nexusDomain)
                    .comment("配置私有仓库 Nexus 服务的域名，例如xxx.yyy.zzz")
                    .component
            }
        }
        return panel!!
    }
    
    override fun isModified(): Boolean {
        return nexusDomainField?.text?.trim() != settings.nexusDomain
    }
    
    override fun apply() {
        nexusDomainField?.text?.let {
            settings.nexusDomain = it.trim()
        }
    }
    
    override fun reset() {
        nexusDomainField?.text = settings.nexusDomain
    }
}
