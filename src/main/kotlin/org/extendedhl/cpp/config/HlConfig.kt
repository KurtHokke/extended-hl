package org.extendedhl.cpp.config

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.jetbrains.rider.cpp.fileType.lexer.CppTokenTypes
import com.jetbrains.rider.colors.RiderLanguageTextAttributeKeys

enum class GroupEnum(val groupName: String) {
  PREPROCESSORS("Preprocessors"),
  TYPES("Keywords//Types"),
  KEYWORDS("Keywords")
}

data class HlConfig(

  val name: String,
  val groupEnum: GroupEnum,
  val tokenType: IElementType? = null,
  val tokenSet: TokenSet? = null,
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
  private val getFallback: () -> TextAttributesKey? = {
    when (this.groupEnum) {
      GroupEnum.TYPES -> RiderLanguageTextAttributeKeys.BUILTIN_TYPE_KEYWORD
      else -> null
    }
  }
  val key: TextAttributesKey get() = TextAttributesKey.createTextAttributesKey("EXTENDEDHL.${name}", getFallback())
  val settingsPath: String get() = "${groupEnum.groupName}//${displayName}"
}

object HlConfigProvider {

  // Static for now; later, load from resources or settings
  val configs: List<HlConfig> by lazy {
    listOf(
      HlConfig("INCLUDE_DIRECTIVE", GroupEnum.PREPROCESSORS, CppTokenTypes.INCLUDE_DIRECTIVE),
      HlConfig("DEFINE_DIRECTIVE", GroupEnum.PREPROCESSORS, CppTokenTypes.DEFINE_DIRECTIVE),
      HlConfig("UNDEF_DIRECTIVE", GroupEnum.PREPROCESSORS, CppTokenTypes.UNDEF_DIRECTIVE),
      HlConfig("IFDEF_DIRECTIVE", GroupEnum.PREPROCESSORS, CppTokenTypes.IFDEF_DIRECTIVE),
      HlConfig("IFNDEF_DIRECTIVE", GroupEnum.PREPROCESSORS, CppTokenTypes.IFNDEF_DIRECTIVE),
      HlConfig("IF_DIRECTIVE", GroupEnum.PREPROCESSORS, CppTokenTypes.IF_DIRECTIVE),
      HlConfig("ELSE_DIRECTIVE", GroupEnum.PREPROCESSORS, CppTokenTypes.ELSE_DIRECTIVE),
      HlConfig("ELIF_DIRECTIVE", GroupEnum.PREPROCESSORS, CppTokenTypes.ELIF_DIRECTIVE),
      HlConfig("ENDIF_DIRECTIVE", GroupEnum.PREPROCESSORS, CppTokenTypes.ENDIF_DIRECTIVE),
      HlConfig("LINE_DIRECTIVE", GroupEnum.PREPROCESSORS, CppTokenTypes.LINE_DIRECTIVE),
      HlConfig("ERROR_DIRECTIVE", GroupEnum.PREPROCESSORS, CppTokenTypes.ERROR_DIRECTIVE),
      HlConfig("PRAGMA_DIRECTIVE", GroupEnum.PREPROCESSORS, CppTokenTypes.PRAGMA_DIRECTIVE),
      HlConfig("CLASS_KEYWORD", GroupEnum.KEYWORDS, CppTokenTypes.CLASS_KEYWORD),
      HlConfig("STRUCT_KEYWORD", GroupEnum.KEYWORDS, CppTokenTypes.STRUCT_KEYWORD),
      HlConfig("ENUM_KEYWORD", GroupEnum.KEYWORDS, CppTokenTypes.ENUM_KEYWORD),
      HlConfig("NAMESPACE_KEYWORD", GroupEnum.KEYWORDS, CppTokenTypes.NAMESPACE_CPP_KEYWORD),
      HlConfig("THIS_KEYWORD", GroupEnum.KEYWORDS, CppTokenTypes.THIS_CPP_KEYWORD),

      HlConfig("BUILTIN_TYPE_KEYWORDS", GroupEnum.KEYWORDS, tokenSet = CppTokenTypes.BUILTIN_TYPE_KEYWORDS),
      HlConfig("INT_KEYWORD", GroupEnum.TYPES, CppTokenTypes.INT_KEYWORD),
      HlConfig("SHORT_KEYWORD", GroupEnum.TYPES, CppTokenTypes.SHORT_KEYWORD),
      HlConfig("LONG_KEYWORD", GroupEnum.TYPES, CppTokenTypes.LONG_KEYWORD),
      HlConfig("CHAR_KEYWORD", GroupEnum.TYPES, CppTokenTypes.CHAR_KEYWORD),
      HlConfig("FLOAT_KEYWORD", GroupEnum.TYPES, CppTokenTypes.FLOAT_KEYWORD),
      HlConfig("DOUBLE_KEYWORD", GroupEnum.TYPES, CppTokenTypes.DOUBLE_KEYWORD),
      HlConfig("BOOL_KEYWORD", GroupEnum.TYPES, CppTokenTypes.BOOL_KEYWORD),
      HlConfig("VOID_KEYWORD", GroupEnum.TYPES, CppTokenTypes.VOID_KEYWORD),
      HlConfig("AUTO_KEYWORD", GroupEnum.TYPES, CppTokenTypes.AUTO_KEYWORD),

    )
  }
}

