package org.extendedhl.cpp.config

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.intellij.psi.codeStyle.DisplayPriority
import com.intellij.psi.codeStyle.DisplayPrioritySortable
import org.extendedhl.cpp.config.Colors
import org.extendedhl.cpp.config.Colors.cppHighlighter
import org.extendedhl.cpp.config.HlConfigProvider
import javax.swing.Icon

class ExtendedColorSettingsPage : ColorSettingsPage, DisplayPrioritySortable {
  private val descriptors by lazy {
    HlConfigProvider.configs.map { config ->
      AttributesDescriptor(
        config.settingsPath,
        Colors.ALL_KEYS[config.name] ?: error("Missing TextAttributesKey for ${config.name}")
      )
    }.toTypedArray()
  }
  override fun getDisplayName() = "C/C++ Extended"
  override fun getIcon(): Icon? = null
  override fun getPriority() = DisplayPriority.KEY_LANGUAGE_SETTINGS
  override fun getWeight() = 100
  override fun getHighlighter(): SyntaxHighlighter = cppHighlighter

  override fun getAttributeDescriptors() = descriptors
  override fun getColorDescriptors() = emptyArray<ColorDescriptor>()
  override fun getDemoText() = """
      <INCLUDE_DIRECTIVE>#include</INCLUDE_DIRECTIVE> <iostream>
      
      <IFNDEF_DIRECTIVE>#ifndef</IFNDEF_DIRECTIVE> EXTENDED_HL_CPP
      <DEFINE_DIRECTIVE>#define</DEFINE_DIRECTIVE> EXTENDED_HL_CPP 1
      <IF_DIRECTIVE>#if</IF_DIRECTIVE> EXTENDED_HL_CPP>0
      <PRAGMA_DIRECTIVE>#pragma</PRAGMA_DIRECTIVE> clang diagnostic ignored "-Wunused-parameter"
      <ELIF_DIRECTIVE>#elif</ELIF_DIRECTIVE> EXTENDED_HL_CPP<0
      <UNDEF_DIRECTIVE>#undef</UNDEF_DIRECTIVE> EXTENDED_HL_CPP
      <ENDIF_DIRECTIVE>#endif</ENDIF_DIRECTIVE>
      <ENDIF_DIRECTIVE>#endif</ENDIF_DIRECTIVE>
      <IFDEF_DIRECTIVE>#ifdef</IFDEF_DIRECTIVE> EXTENDED_HL_CPP
      <LINE_DIRECTIVE>#line</LINE_DIRECTIVE> 1234 "extended_hl.cpp"
      <ELSE_DIRECTIVE>#else</ELSE_DIRECTIVE>
      <ERROR_DIRECTIVE>#error</ERROR_DIRECTIVE> "error"
      <ENDIF_DIRECTIVE>#endif</ENDIF_DIRECTIVE>
      
      <CLASS_KEYWORD>class</CLASS_KEYWORD> ExtendedHL {
      private:
        <STRUCT_KEYWORD>struct</STRUCT_KEYWORD> InnerStruct { <INT_KEYWORD>int</INT_KEYWORD> a{0}; };
        <ENUM_KEYWORD>enum</ENUM_KEYWORD> InnerEnum { A, B, C };
        InnerStruct innerStruct_;
        InnerEnum innerEnum_;
      public:
        explicit ExtendedHL(<INT_KEYWORD>int</INT_KEYWORD> a) { <THIS_KEYWORD>this-></THIS_KEYWORD>innerStruct_.a = a; }
      }
      
      <INT_KEYWORD>int</INT_KEYWORD> main(<INT_KEYWORD>int</INT_KEYWORD> argc, <CHAR_KEYWORD>char</CHAR_KEYWORD>** argv) {
        <CHAR_KEYWORD>char</CHAR_KEYWORD> c = 'a';
      }
  """.trimIndent()
  override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey> {
    return Colors.ALL_KEYS.mapKeys { (k, _) -> k.removeSuffix("EXTENDEDHL.")}
  }
}