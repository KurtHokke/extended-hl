package org.extendedhl.cpp.config

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.psi.TokenType
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.testFramework.LightVirtualFile
import com.jetbrains.rider.cpp.fileType.psi.CppElementsTypes
import com.jetbrains.rider.cpp.fileType.lexer.CppTokenTypes
import com.jetbrains.rider.cpp.fileType.CppTextAttributeKeys
import com.jetbrains.cidr.radler.inspections.RadTextAttributesKeyProcessor
import com.jetbrains.rider.colors.RiderLanguageTextAttributeKeys
import com.intellij.codeHighlighting.RainbowHighlighter
import com.jetbrains.rhizomedb.attr

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
  val externalNames: List<String>? = null,
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
  private fun assembleKeywordConfigs(vararg types: Pair<String, IElementType>): List<HlConfig> =
      types.map { (name, type) ->
        HlConfig(name, GroupEnum.KEYWORDS, Conds(false), type)
      }
  private fun assembleBuiltinTypesConfigs(vararg types: Pair<String, IElementType>): List<HlConfig> =
      types.map { (name, type) ->
        HlConfig(name, GroupEnum.BUILTIN_TYPES, Conds(false), type)
      }
  // Static for now; later, load from resources or settings
  //HlConfig\(("[A-Z_]+").*(CppTokenTypes\.[A-Z_]+)\),
  val configs: List<HlConfig> by lazy {
    listOf(
        *assembleDirectiveConfigs(
          "INCLUDE_DIRECTIVE" to CppTokenTypes.INCLUDE_DIRECTIVE,
          "DEFINE_DIRECTIVE" to CppTokenTypes.DEFINE_DIRECTIVE,
          "UNDEF_DIRECTIVE" to CppTokenTypes.UNDEF_DIRECTIVE,
          "IFDEF_DIRECTIVE" to CppTokenTypes.IFDEF_DIRECTIVE,
          "IFNDEF_DIRECTIVE" to CppTokenTypes.IFNDEF_DIRECTIVE,
          "IF_DIRECTIVE" to CppTokenTypes.IF_DIRECTIVE,
          "ELSE_DIRECTIVE" to CppTokenTypes.ELSE_DIRECTIVE,
          "ELIF_DIRECTIVE" to CppTokenTypes.ELIF_DIRECTIVE,
          "ENDIF_DIRECTIVE" to CppTokenTypes.ENDIF_DIRECTIVE,
          "LINE_DIRECTIVE" to CppTokenTypes.LINE_DIRECTIVE,
          "ERROR_DIRECTIVE" to CppTokenTypes.ERROR_DIRECTIVE,
          "PRAGMA_DIRECTIVE" to CppTokenTypes.PRAGMA_DIRECTIVE,
        ).toTypedArray(),

        *assembleKeywordConfigs(
          "CLASS_KEYWORD" to CppTokenTypes.CLASS_KEYWORD,
          "STRUCT_KEYWORD" to CppTokenTypes.STRUCT_KEYWORD,
          "ENUM_KEYWORD" to CppTokenTypes.ENUM_KEYWORD,
          "NAMESPACE_KEYWORD" to CppTokenTypes.NAMESPACE_CPP_KEYWORD,
        ).toTypedArray(),
        HlConfig("ACCESS_SPECIFIER_KEYWORDS", GroupEnum.KEYWORDS, Conds(false, listOf("public","private","protected")), CppTokenTypes.KEYWORD),

        *assembleBuiltinTypesConfigs(
          "INT_KEYWORD" to CppTokenTypes.INT_KEYWORD,
          "SHORT_KEYWORD" to CppTokenTypes.SHORT_KEYWORD,
          "LONG_KEYWORD" to CppTokenTypes.LONG_KEYWORD,
          "CHAR_KEYWORD" to CppTokenTypes.CHAR_KEYWORD,
          "FLOAT_KEYWORD" to CppTokenTypes.FLOAT_KEYWORD,
          "DOUBLE_KEYWORD" to CppTokenTypes.DOUBLE_KEYWORD,
          "BOOL_KEYWORD" to CppTokenTypes.BOOL_KEYWORD,
          "VOID_KEYWORD" to CppTokenTypes.VOID_KEYWORD,
          "AUTO_KEYWORD" to CppTokenTypes.AUTO_KEYWORD,
        ).toTypedArray(),

        HlConfig("CLASSID", GroupEnum.TYPES, externalNames = listOf("ReSharper.CPP_CLASS_IDENTIFIER"), displayName = "Class identifier"),
        HlConfig("STRUCTID", GroupEnum.TYPES, externalNames = listOf("ReSharper.CPP_STRUCT_IDENTIFIER"), displayName = "Struct identifier"),
        HlConfig("ENUMID", GroupEnum.TYPES, externalNames = listOf("ReSharper.CPP_ENUM_IDENTIFIER"), displayName = "Enum identifier"),
        HlConfig("MFUNCID", GroupEnum.TYPES, externalNames = listOf("ReSharper.CPP_MEMBER_FUNCTION_IDENTIFIER"), displayName = "Member function identifier"),
        HlConfig("FUNCID", GroupEnum.TYPES, externalNames = listOf("ReSharper.CPP_GLOBAL_FUNCTION_IDENTIFIER", "CPP_GLOBAL_FUNCTION_USAGE_IDENTIFIER"), displayName = "Function identifier"),

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
    configs
      .asSequence()
      .flatMap { cfg -> (cfg.externalNames ?: emptyList()).asSequence().map { name -> name to cfg } }
      .toMap()
  }
  val keysByExternalName: Map<String, TextAttributesKey> by lazy {
    configs
      .asSequence()
      .flatMap { cfg -> (cfg.externalNames ?: emptyList()).asSequence().map { name -> name to cfg.key } }
      .toMap()
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
    configs
      .filter { it.optionalStringId != null }
      .associate { it.optionalStringId!! to it.key }
  }

  val cppHighlighter: SyntaxHighlighter by lazy {
    val fileType = FileTypeManager.getInstance().getFileTypeByFileName("dummy.cpp")
    val vFile = LightVirtualFile("dummy.cpp", fileType, "")
    SyntaxHighlighterFactory.getSyntaxHighlighter(fileType, /* project = */ null, vFile)
      ?: error("No SyntaxHighlighter found for .cpp")
  }

  private val supportedTokenSet: TokenSet by lazy {
    val singleTypes = configs.mapNotNull { it.tokenType }.toTypedArray()
    val singleSet = if (singleTypes.isNotEmpty()) TokenSet.create(*singleTypes) else TokenSet.EMPTY

    val sets = configs.mapNotNull { it.tokenSet }
    when {
      sets.isEmpty() -> singleSet
      singleSet == TokenSet.EMPTY -> TokenSet.orSet(*sets.toTypedArray())
      else -> TokenSet.orSet(singleSet, *sets.toTypedArray())
    }
  }
  private val extraSupportedTokenSet: TokenSet by lazy {
    TokenSet.create(
        CppElementsTypes.DUMMY_BLOCK,
        CppElementsTypes.DUMMY_NODE,
        CppTokenTypes.IDENTIFIER
    )
  }
  private val typesForAskingMarkupModel: TokenSet by lazy {
    TokenSet.create(
        CppTokenTypes.IDENTIFIER
    )
  }
  fun shouldHandleType(elType: IElementType): Boolean =
      supportedTokenSet.contains(elType) || extraSupportedTokenSet.contains(elType)
  fun shouldAskMarkupModel(elType: IElementType): Boolean =
      typesForAskingMarkupModel.contains(elType)

}
