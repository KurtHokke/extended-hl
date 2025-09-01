package org.extendedhl.cpp.config

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.testFramework.LightVirtualFile
import com.jetbrains.rhizomedb.attr
import com.jetbrains.rider.cpp.fileType.lexer.CppTokenTypes
import com.jetbrains.rider.colors.RiderLanguageTextAttributeKeys
import com.jetbrains.rider.cpp.fileType.CppTextAttributeKeys
import com.jetbrains.cidr.radler.inspections.RadTextAttributesKeyProcessor
import com.intellij.codeHighlighting.RainbowHighlighter

private val log = org.extendedhl.cpp.logging.logger<HlConfig>()

enum class GroupEnum(val leafGroup: String, val parentGroup: GroupEnum? = null) {
  KEYWORDS("Keywords"),
  TYPES("Types"),
  BUILTIN_TYPES("Builtin Types", TYPES),
  PREPROCESSORS("Preprocessors"),
  BRACKETS("Brackets"),
  COMMENTS("Comments");

  val groupName: String get() = parentGroup?.groupName?.let { "$it//$leafGroup" } ?: leafGroup
}
data class Conds(
  val hasChildren: Boolean? = null,
  val textEqualsList: List<String>? = null,
  val textEquals: String? = null,
  val textEqualsUntil: String? = null
)

private fun createDisplayName(s: String): String =
  s.split('_')
    .filter { it.isNotEmpty() }
    .mapIndexed { index, part ->
      val lower = part.lowercase()
      if (index == 0) lower.replaceFirstChar { it.titlecase() } else lower
    }
    .joinToString(" ")


data class HlConfig(
  val name: String,
  val groupEnum: GroupEnum,
  val conds: Conds = Conds(),
  val tokenType: IElementType? = null,
  val tokenSet: TokenSet? = null,
  val displayName: String? = createDisplayName(name),
  val severity: HighlightSeverity = HighlightSeverity.INFORMATION,
  val externalName: String? = null,
  val optionalId: Int? = null,
  val optionalStringId: String? = null
) {
  private val fallbackKey: TextAttributesKey? by lazy {
    when (this.groupEnum) {
      GroupEnum.KEYWORDS -> resolveRadKey(CppTextAttributeKeys.KEYWORD)
      GroupEnum.BUILTIN_TYPES -> resolveRadKey(CppTextAttributeKeys.BUILTIN_TYPE_KEYWORD)
      GroupEnum.PREPROCESSORS -> resolveRadKey(CppTextAttributeKeys.DIRECTIVE)
      else -> null
    }
  }
  val key: TextAttributesKey by lazy {
    TextAttributesKey.createTextAttributesKey("EXTENDEDHL.${name}", fallbackKey)
  }
  val settingsPath: String get() = "${groupEnum.groupName}//${displayName}"
  fun checkConds(el: PsiElement): Boolean {
    var ok = true
    conds.hasChildren?.let {
      ok = when (it) {
        false -> el.firstChild == null
        true -> el.firstChild != null
      }
      log.debug("checked hasChildren for $name: $ok")
    }
    if (!ok) return false
    conds.textEquals?.let {
      ok = el.text == it
      log.debug("checked textEquals for $name: $ok")
    }
    if (!ok) return false
    conds.textEqualsList?.let {
      ok = it.contains(el.text)
      log.debug("checked textEqualsList for $name: $ok")
    }
    conds.textEqualsUntil?.let {
      ok = el.text.regionMatches(0, it, 0, it.length)
      log.debug("checked textEqualsUntil for $name: $ok")
    }
    return ok
  }
  fun checkConds(el: IElementType, text: String): Boolean {
    var ok = true
    conds.textEqualsList?.let {
      ok = it.contains(text)
    }
    return ok
  }
}
private fun resolveRadKey(key: TextAttributesKey): TextAttributesKey? =
    RadTextAttributesKeyProcessor.externalName2Key[key.externalName]

object HlConfigProvider {
  private fun assembleDirectiveConfigs(vararg types: Pair<String, IElementType>): List<HlConfig> =
      types.map { (name, type) ->
        HlConfig(name, GroupEnum.PREPROCESSORS, Conds(false), type)
      }
  // Static for now; later, load from resources or settings
  val configs: List<HlConfig> by lazy {
    listOf(
        HlConfig("INCLUDE_DIRECTIVE", GroupEnum.PREPROCESSORS, Conds(false), CppTokenTypes.INCLUDE_DIRECTIVE),
        HlConfig("DEFINE_DIRECTIVE", GroupEnum.PREPROCESSORS, Conds(false), CppTokenTypes.DEFINE_DIRECTIVE),
        HlConfig("UNDEF_DIRECTIVE", GroupEnum.PREPROCESSORS, Conds(false), CppTokenTypes.UNDEF_DIRECTIVE),
        HlConfig("IFDEF_DIRECTIVE", GroupEnum.PREPROCESSORS, Conds(false), CppTokenTypes.IFDEF_DIRECTIVE),
        HlConfig("IFNDEF_DIRECTIVE", GroupEnum.PREPROCESSORS, Conds(false), CppTokenTypes.IFNDEF_DIRECTIVE),
        HlConfig("IF_DIRECTIVE", GroupEnum.PREPROCESSORS, Conds(false), CppTokenTypes.IF_DIRECTIVE),
        HlConfig("ELSE_DIRECTIVE", GroupEnum.PREPROCESSORS, Conds(false), CppTokenTypes.ELSE_DIRECTIVE),
        HlConfig("ELIF_DIRECTIVE", GroupEnum.PREPROCESSORS, Conds(false), CppTokenTypes.ELIF_DIRECTIVE),
        HlConfig("ENDIF_DIRECTIVE", GroupEnum.PREPROCESSORS, Conds(false), CppTokenTypes.ENDIF_DIRECTIVE),
        HlConfig("LINE_DIRECTIVE", GroupEnum.PREPROCESSORS, Conds(false), CppTokenTypes.LINE_DIRECTIVE),
        HlConfig("ERROR_DIRECTIVE", GroupEnum.PREPROCESSORS, Conds(false), CppTokenTypes.ERROR_DIRECTIVE),
        HlConfig("PRAGMA_DIRECTIVE", GroupEnum.PREPROCESSORS, Conds(false), CppTokenTypes.PRAGMA_DIRECTIVE),

        HlConfig("CLASS_KEYWORD", GroupEnum.KEYWORDS, Conds(false), CppTokenTypes.CLASS_KEYWORD),
        HlConfig("ACCESS_SPECIFIER_KEYWORDS", GroupEnum.KEYWORDS, Conds(false, listOf("public","private","protected")), CppTokenTypes.KEYWORD),
        HlConfig("STRUCT_KEYWORD", GroupEnum.KEYWORDS, Conds(false), CppTokenTypes.STRUCT_KEYWORD),
        HlConfig("ENUM_KEYWORD", GroupEnum.KEYWORDS, Conds(false), CppTokenTypes.ENUM_KEYWORD),
        HlConfig("NAMESPACE_KEYWORD", GroupEnum.KEYWORDS, Conds(false), CppTokenTypes.NAMESPACE_CPP_KEYWORD),

        HlConfig("INT_KEYWORD", GroupEnum.BUILTIN_TYPES, Conds(false), CppTokenTypes.INT_KEYWORD),
        HlConfig("SHORT_KEYWORD", GroupEnum.BUILTIN_TYPES, Conds(false), CppTokenTypes.SHORT_KEYWORD),
        HlConfig("LONG_KEYWORD", GroupEnum.BUILTIN_TYPES, Conds(false), CppTokenTypes.LONG_KEYWORD),
        HlConfig("CHAR_KEYWORD", GroupEnum.BUILTIN_TYPES, Conds(false), CppTokenTypes.CHAR_KEYWORD),
        HlConfig("FLOAT_KEYWORD", GroupEnum.BUILTIN_TYPES, Conds(false), CppTokenTypes.FLOAT_KEYWORD),
        HlConfig("DOUBLE_KEYWORD", GroupEnum.BUILTIN_TYPES, Conds(false), CppTokenTypes.DOUBLE_KEYWORD),
        HlConfig("BOOL_KEYWORD", GroupEnum.BUILTIN_TYPES, Conds(false), CppTokenTypes.BOOL_KEYWORD),
        HlConfig("VOID_KEYWORD", GroupEnum.BUILTIN_TYPES, Conds(false), CppTokenTypes.VOID_KEYWORD),
        HlConfig("AUTO_KEYWORD", GroupEnum.BUILTIN_TYPES, Conds(false), CppTokenTypes.AUTO_KEYWORD),

        HlConfig("CLASSID", GroupEnum.TYPES, externalName = "ReSharper.CPP_CLASS_IDENTIFIER", displayName = "class Identifier"),
        HlConfig("STRUCTID", GroupEnum.TYPES, externalName = "ReSharper.CPP_STRUCT_IDENTIFIER", displayName = "struct Identifier"),
        HlConfig("ENUMID", GroupEnum.TYPES, externalName = "ReSharper.CPP_ENUM_IDENTIFIER", displayName = "enum Identifier"),
        HlConfig("MFUNCID", GroupEnum.TYPES, externalName = "ReSharper.CPP_MEMBER_FUNCTION_IDENTIFIER", displayName = "member Function Identifier"),

        HlConfig("LVL1", GroupEnum.BRACKETS, optionalId = 0, displayName = "curly braces lvl 1"),
        HlConfig("LVL2", GroupEnum.BRACKETS, optionalId = 1, displayName = "curly braces lvl 2"),
        HlConfig("LVL3", GroupEnum.BRACKETS, optionalId = 2, displayName = "curly braces lvl 3"),
        HlConfig("LVL4", GroupEnum.BRACKETS, optionalId = 3, displayName = "curly braces lvl 4"),
        HlConfig("CPP_ATTRIBUTES_BRACKETS", GroupEnum.BRACKETS, displayName = "C++ Attributes Brackets"),

        HlConfig("CAUTION_COMMENT", GroupEnum.COMMENTS, optionalStringId = "//!", displayName = "/\u2060/! | /*! comment"),
        HlConfig("QUESTION_COMMENT", GroupEnum.COMMENTS, optionalStringId = "//?", displayName = "/\u2060/? | /*? comment"),
        HlConfig("AT_COMMENT", GroupEnum.COMMENTS, optionalStringId = "//@", displayName = "/\u2060/@ | /*@ comment"),
        HlConfig("HASH_COMMENT", GroupEnum.COMMENTS, optionalStringId = "//#", displayName = "/\u2060/# | /*# comment"),
        HlConfig("DOLLAR_COMMENT", GroupEnum.COMMENTS, optionalStringId = "//$", displayName = "/\u2060/$ | /*$ comment"),
        HlConfig("TILDE_COMMENT", GroupEnum.COMMENTS, optionalStringId = "//~", displayName = "/\u2060/~ | /*~ comment"),
        HlConfig("PERCENT_COMMENT", GroupEnum.COMMENTS, optionalStringId = "//%", displayName = "/\u2060/% | /*% comment"),
    )
  }
  val configsByName: Map<String, HlConfig> by lazy { configs.associateBy { it.name } }
  val configsByType: Map<IElementType, HlConfig> by lazy {
    configs.mapNotNull { cfg -> cfg.tokenType?.let { t -> t to cfg } }.toMap()
  }
  val configsByExternalName: Map<String, HlConfig> by lazy {
    configs.mapNotNull { cfg -> cfg.externalName?.let { t -> t to cfg } }.toMap()
  }
  val ALL_KEYS: Map<String, TextAttributesKey> by lazy {
    configs.associate { config ->
      config.name to config.key
    }
  }
  val bracketsKeysByLevel: List<TextAttributesKey> by lazy {
    configs
      .filter { it.groupEnum == GroupEnum.BRACKETS && it.optionalId != null }
      .sortedBy { it.optionalId }
      .map { it.key }
  }
  val keysByOptionalStringId: Map<String, TextAttributesKey> by lazy {
    configs.filter { it.optionalStringId != null }
    .associate { it.optionalStringId!! to it.key }
  }

  val cppHighlighter: SyntaxHighlighter by lazy {
    val fileType = FileTypeManager.getInstance().getFileTypeByFileName("dummy.cpp")
    val vFile = LightVirtualFile("dummy.cpp", fileType, "")
    SyntaxHighlighterFactory.getSyntaxHighlighter(fileType, /* project = */ null, vFile)
      ?: error("No SyntaxHighlighter found for .cpp")
  }
}
