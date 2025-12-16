package org.dean.idea.plugin.mavenartifactsearch

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*

/**
 * 插件设置状态类，用于持久化保存配置
 */
@State(
    name = "MavenArtifactSearchSettings",
    storages = [Storage("maven-artifact-search.xml")]
)
@Service
class MavenArtifactSearchSettings : PersistentStateComponent<MavenArtifactSearchSettings.State> {
    
    data class State(
        var nexusDomain: String = "",
        //todo  这个可以设置，二选其一或者自己配置 2025-12-09 因为新发现一个中央名仓库的查询地方哈哈哈哈
        // https://repo1.maven.org/maven2/org/apache/commons/commons-beanutils2/2.0.0-M1/
        // 还有这个最官方的哈哈哈哈
        var centralDomain: String = "https://central.sonatype.com/artifact/org.apache.commons/commons-text/1.15.0"
    )
    
    private var state = State()
    
    override fun getState(): State {
        return state
    }
    
    override fun loadState(state: State) {
        this.state = state
    }
    
    var nexusDomain: String
        get() = state.nexusDomain
        set(value) {
            state.nexusDomain = value
        }
    
    companion object {
        fun getInstance(): MavenArtifactSearchSettings {
            return ApplicationManager.getApplication().getService(MavenArtifactSearchSettings::class.java)
        }
    }
}

