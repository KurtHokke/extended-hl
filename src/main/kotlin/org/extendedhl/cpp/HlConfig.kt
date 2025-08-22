package org.extendedhl.cpp

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.tree.IElementType
import com.jetbrains.rider.cpp.fileType.lexer.CppTokenTypes

// In a new file: org.extendedhl.cpp.HighlightConfigs.kt
data class HlConfig(
  val tokenType: IElementType,
  val name: String, // e.g., "C_INT_KEYWORD"
  val displayName: String? = null, // Optional for settings page; auto-generate if null
  val severity: HighlightSeverity = HighlightSeverity.INFORMATION // Allow warnings/errors for extended cases
)

object HlConfigProvider {
  // Static for now; later, load from resources or settings
  val configs: List<HlConfig> by lazy {
    listOf(
      HlConfig(CppTokenTypes.INT_KEYWORD, "INT_KEYWORD"),
      HlConfig(CppTokenTypes.CHAR_KEYWORD, "CHAR_KEYWORD")
    )
  }
}