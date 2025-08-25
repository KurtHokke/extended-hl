package org.extendedhl.cpp

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.tree.IElementType
import com.jetbrains.rider.cpp.fileType.lexer.CppTokenTypes



data class HlConfig(
  val tokenType: IElementType,
  private val groupEnum: HlConfigProvider.Group,
  val name: String,
  val displayName: String? = name.split("_").let { parts ->
      when (parts.size) {
        0 -> name
        1 -> parts[0].lowercase()
        else -> listOf(parts[0].lowercase(), parts[1].lowercase().replaceFirstChar { it.uppercase() })
            .joinToString(" ")
      }
  },
  val severity: HighlightSeverity = HighlightSeverity.INFORMATION,
) {

  val settingsPath: String get() = "${groupEnum.groupName}//${displayName}"
}

object HlConfigProvider {
  enum class Group(val groupName: String) {
    PREPROCESSORS("Preprocessors"),
    TYPES("Keywords//Types"),
    KEYWORDS("Keywords")
  }
  // Static for now; later, load from resources or settings
  val configs: List<HlConfig> by lazy {
    listOf(
      HlConfig(CppTokenTypes.INCLUDE_DIRECTIVE, Group.PREPROCESSORS, "INCLUDE_DIRECTIVE"),
      HlConfig(CppTokenTypes.DEFINE_DIRECTIVE, Group.PREPROCESSORS, "DEFINE_DIRECTIVE"),
      HlConfig(CppTokenTypes.UNDEF_DIRECTIVE, Group.PREPROCESSORS, "UNDEF_DIRECTIVE"),
      HlConfig(CppTokenTypes.IFDEF_DIRECTIVE, Group.PREPROCESSORS, "IFDEF_DIRECTIVE"),
      HlConfig(CppTokenTypes.IFNDEF_DIRECTIVE, Group.PREPROCESSORS, "IFNDEF_DIRECTIVE"),
      HlConfig(CppTokenTypes.IF_DIRECTIVE, Group.PREPROCESSORS, "IF_DIRECTIVE"),
      HlConfig(CppTokenTypes.ELSE_DIRECTIVE, Group.PREPROCESSORS, "ELSE_DIRECTIVE"),
      HlConfig(CppTokenTypes.ELIF_DIRECTIVE, Group.PREPROCESSORS, "ELIF_DIRECTIVE"),
      HlConfig(CppTokenTypes.ENDIF_DIRECTIVE, Group.PREPROCESSORS, "ENDIF_DIRECTIVE"),
      HlConfig(CppTokenTypes.LINE_DIRECTIVE, Group.PREPROCESSORS, "LINE_DIRECTIVE"),
      HlConfig(CppTokenTypes.ERROR_DIRECTIVE, Group.PREPROCESSORS, "ERROR_DIRECTIVE"),
      HlConfig(CppTokenTypes.PRAGMA_DIRECTIVE, Group.PREPROCESSORS, "PRAGMA_DIRECTIVE"),
      HlConfig(CppTokenTypes.CLASS_KEYWORD,  Group.KEYWORDS, "CLASS_KEYWORD"),
      HlConfig(CppTokenTypes.STRUCT_KEYWORD, Group.KEYWORDS, "STRUCT_KEYWORD"),
      HlConfig(CppTokenTypes.ENUM_KEYWORD,  Group.KEYWORDS, "ENUM_KEYWORD"),
      HlConfig(CppTokenTypes.NAMESPACE_CPP_KEYWORD, Group.KEYWORDS, "NAMESPACE_KEYWORD"),
      HlConfig(CppTokenTypes.THIS_CPP_KEYWORD, Group.KEYWORDS, "THIS_KEYWORD"),

      HlConfig(CppTokenTypes.INT_KEYWORD,  Group.TYPES, "INT_KEYWORD"),
      HlConfig(CppTokenTypes.SHORT_KEYWORD, Group.TYPES, "SHORT_KEYWORD"),
      HlConfig(CppTokenTypes.LONG_KEYWORD, Group.TYPES, "LONG_KEYWORD"),
      HlConfig(CppTokenTypes.CHAR_KEYWORD, Group.TYPES, "CHAR_KEYWORD"),
      HlConfig(CppTokenTypes.FLOAT_KEYWORD, Group.TYPES, "FLOAT_KEYWORD"),
      HlConfig(CppTokenTypes.DOUBLE_KEYWORD, Group.TYPES, "DOUBLE_KEYWORD"),
      HlConfig(CppTokenTypes.BOOL_KEYWORD, Group.TYPES, "BOOL_KEYWORD"),
      HlConfig(CppTokenTypes.VOID_KEYWORD, Group.TYPES, "VOID_KEYWORD"),
      HlConfig(CppTokenTypes.AUTO_KEYWORD, Group.TYPES, "AUTO_KEYWORD"),

    )
  }
}

