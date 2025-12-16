package org.dean.idea.plugin.mavenartifactsearch

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag

/**
 * 解析 Maven artifact 的 groupId 和 artifactId
 */
object MavenArtifactParser {
    
    data class MavenArtifact(
        val groupId: String?,
        val artifactId: String?,
        val version: String? = null
    )
    
    /**
     * 从编辑器中解析选中的 XML 内容
     */
    fun parseFromEditor(editor: Editor, psiFile: PsiFile?): MavenArtifact? {
        // 首先尝试从选中的文本中解析
        val selectedText = editor.selectionModel.selectedText
        if (!selectedText.isNullOrBlank()) {
            val artifact = parseFromText(selectedText)
            if (artifact != null && (artifact.groupId != null || artifact.artifactId != null)) {
                return artifact
            }
        }
        
        // 如果直接解析失败或没有选中文本，尝试从 PSI 树中查找
        if (psiFile != null) {
            return parseFromPsiFile(psiFile, editor)
        }
        
        return null
    }
    
    /**
     * 从文本中解析 groupId、artifactId 和 version
     */
    fun parseFromText(text: String): MavenArtifact? {
        if (text.isBlank()) return null
        
        var groupId: String? = null
        var artifactId: String? = null
        var version: String? = null
        
        // 使用正则表达式提取
        val groupIdPattern = Regex("<groupId>(.*?)</groupId>", RegexOption.DOT_MATCHES_ALL)
        val artifactIdPattern = Regex("<artifactId>(.*?)</artifactId>", RegexOption.DOT_MATCHES_ALL)
        val versionPattern = Regex("<version>(.*?)</version>", RegexOption.DOT_MATCHES_ALL)
        
        groupIdPattern.find(text)?.let {
            groupId = it.groupValues[1].trim()
        }
        
        artifactIdPattern.find(text)?.let {
            artifactId = it.groupValues[1].trim()
        }
        
        versionPattern.find(text)?.let {
            version = it.groupValues[1].trim()
        }
        
        // 如果都为空，返回 null
        if (groupId.isNullOrBlank() && artifactId.isNullOrBlank()) {
            return null
        }
        
        return MavenArtifact(
            groupId = groupId?.takeIf { it.isNotBlank() },
            artifactId = artifactId?.takeIf { it.isNotBlank() },
            version = version?.takeIf { it.isNotBlank() }
        )
    }
    
    /**
     * 从 PSI 文件中解析
     */
    private fun parseFromPsiFile(psiFile: PsiFile, editor: Editor): MavenArtifact? {
        val offset = editor.caretModel.offset
        val element = psiFile.findElementAt(offset) ?: return null
        
        // 向上查找最近的 XmlTag
        var xmlTag = PsiTreeUtil.getParentOfType(element, XmlTag::class.java) ?: return null
        
        var groupId: String? = null
        var artifactId: String? = null
        var version: String? = null
        
        // 如果当前标签是 groupId、artifactId 或 version，直接获取值
        if (xmlTag.name == "groupId") {
            groupId = xmlTag.value?.textElements?.joinToString("") { it.text }?.trim()
            // 查找同级或父级的 artifactId 和 version
            xmlTag.parentTag?.let { parent ->
                parent.findFirstSubTag("artifactId")?.let {
                    artifactId = it.value?.textElements?.joinToString("") { e -> e.text }?.trim()
                }
                parent.findFirstSubTag("version")?.let {
                    version = it.value?.textElements?.joinToString("") { e -> e.text }?.trim()
                }
            }
        } else if (xmlTag.name == "artifactId") {
            artifactId = xmlTag.value?.textElements?.joinToString("") { it.text }?.trim()
            // 查找同级或父级的 groupId 和 version
            xmlTag.parentTag?.let { parent ->
                parent.findFirstSubTag("groupId")?.let {
                    groupId = it.value?.textElements?.joinToString("") { e -> e.text }?.trim()
                }
                parent.findFirstSubTag("version")?.let {
                    version = it.value?.textElements?.joinToString("") { e -> e.text }?.trim()
                }
            }
        } else if (xmlTag.name == "version") {
            version = xmlTag.value?.textElements?.joinToString("") { it.text }?.trim()
            // 查找同级或父级的 groupId 和 artifactId
            xmlTag.parentTag?.let { parent ->
                parent.findFirstSubTag("groupId")?.let {
                    groupId = it.value?.textElements?.joinToString("") { e -> e.text }?.trim()
                }
                parent.findFirstSubTag("artifactId")?.let {
                    artifactId = it.value?.textElements?.joinToString("") { e -> e.text }?.trim()
                }
            }
        } else {
            // 当前标签是父标签，查找子标签
            xmlTag.findFirstSubTag("groupId")?.let {
                groupId = it.value?.textElements?.joinToString("") { e -> e.text }?.trim()
            }
            xmlTag.findFirstSubTag("artifactId")?.let {
                artifactId = it.value?.textElements?.joinToString("") { e -> e.text }?.trim()
            }
            xmlTag.findFirstSubTag("version")?.let {
                version = it.value?.textElements?.joinToString("") { e -> e.text }?.trim()
            }
            
            // 如果当前标签的子标签中没有找到，向上查找父标签
            if ((groupId == null || artifactId == null || version == null) && xmlTag.parentTag != null) {
                var parent = xmlTag.parentTag
                while (parent != null && (groupId == null || artifactId == null || version == null)) {
                    if (groupId == null) {
                        parent.findFirstSubTag("groupId")?.let {
                            groupId = it.value?.textElements?.joinToString("") { e -> e.text }?.trim()
                        }
                    }
                    if (artifactId == null) {
                        parent.findFirstSubTag("artifactId")?.let {
                            artifactId = it.value?.textElements?.joinToString("") { e -> e.text }?.trim()
                        }
                    }
                    if (version == null) {
                        parent.findFirstSubTag("version")?.let {
                            version = it.value?.textElements?.joinToString("") { e -> e.text }?.trim()
                        }
                    }
                    parent = parent.parentTag
                }
            }
        }
        
        if (groupId.isNullOrBlank() && artifactId.isNullOrBlank()) {
            return null
        }
        
        return MavenArtifact(
            groupId = groupId?.takeIf { it.isNotBlank() },
            artifactId = artifactId?.takeIf { it.isNotBlank() },
            version = version?.takeIf { it.isNotBlank() }
        )
    }
}

