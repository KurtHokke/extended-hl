package org.extendedhl.cpp

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.psi.tree.IElementType
import com.intellij.testFramework.LightVirtualFile
import com.jetbrains.rider.cpp.fileType.lexer.CppTokenTypes
object Colors {

  val ALL_KEYS: Map<String, TextAttributesKey> by lazy {
    HlConfigProvider.configs.associate { config ->
      //config.name to CreateTAK(config.name, getDefHl(config.tokenType))
      val uniqueName = "EXTENDEDHL.${config.name}"
      config.name to TextAttributesKey.createTextAttributesKey(uniqueName)
    }
  }
  val ALL_KEYS_BY_TOKEN: Map<IElementType, TextAttributesKey> by lazy {
    HlConfigProvider.configs.associate { config ->
      //config.name to CreateTAK(config.name, getDefHl(config.tokenType))
      val uniqueName = "EXTENDEDHL.${config.name}"
      config.tokenType to TextAttributesKey.createTextAttributesKey(uniqueName)
    }
  }

  fun CreateTAK(name: String, fallback: TextAttributesKey? = null): TextAttributesKey =
    TextAttributesKey.createTextAttributesKey(name, fallback)

  private fun getDefHl(tokenType: IElementType): TextAttributesKey? {
    return fallbackCache.getOrPut(tokenType) {
      val keys = cppHighlighter.getTokenHighlights(tokenType)
      keys.firstOrNull()
    }
  }
  private val fallbackCache = mutableMapOf<IElementType, TextAttributesKey?>()
  val cppHighlighter: SyntaxHighlighter by lazy {
    val fileType = FileTypeManager.getInstance().getFileTypeByFileName("dummy.cpp")
    val vFile = LightVirtualFile("dummy.cpp", fileType, "")
    SyntaxHighlighterFactory.getSyntaxHighlighter(fileType, /* project = */ null, vFile)
      ?: error("No SyntaxHighlighter found for .cpp")
  }
}