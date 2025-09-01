package org.extendedhl.cpp.config

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.intellij.psi.codeStyle.DisplayPriority
import com.intellij.psi.codeStyle.DisplayPrioritySortable
import org.extendedhl.cpp.config.HlConfigProvider
import javax.swing.Icon

class ExtendedColorSettingsPage : ColorSettingsPage, DisplayPrioritySortable {
  private val descriptors by lazy {
    HlConfigProvider.configs.map { config ->
      AttributesDescriptor(
        config.settingsPath,
        HlConfigProvider.ALL_KEYS[config.name] ?: error("Missing TextAttributesKey for ${config.name}")
      )
    }.toTypedArray()
  }
  override fun getDisplayName() = "C/C++ Extended"
  override fun getIcon(): Icon? = null
  override fun getPriority() = DisplayPriority.LANGUAGE_SETTINGS
  override fun getWeight() = 100
  override fun getHighlighter(): SyntaxHighlighter = HlConfigProvider.cppHighlighter

  override fun getAttributeDescriptors() = descriptors
  override fun getColorDescriptors() = emptyArray<ColorDescriptor>()
  /*override fun getDemoText() = """
      <INCLUDE_DIRECTIVE>#include</INCLUDE_DIRECTIVE> <iostream>
      
      <NAMESPACE_KEYWORD>namespace</NAMESPACE_KEYWORD> extended_hl <BRACKET_LEVEL_1>{</BRACKET_LEVEL_1>
        <BOOL_KEYWORD>bool</BOOL_KEYWORD> isEven(<LONG_KEYWORD>long</LONG_KEYWORD> num) <BRACKET_LEVEL_2>{</BRACKET_LEVEL_2> return num % 2 == 0; <BRACKET_LEVEL_2>}</BRACKET_LEVEL_2>
      <BRACKET_LEVEL_1>}</BRACKET_LEVEL_1>
      
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
      
      <CLASS_KEYWORD>class</CLASS_KEYWORD> ExtendedHL <BRACKET_LEVEL_1>{</BRACKET_LEVEL_1>
      <ACCESS_SPECIFIER_KEYWORDS>private</ACCESS_SPECIFIER_KEYWORDS>:
        <STRUCT_KEYWORD>struct</STRUCT_KEYWORD> InnerStruct <BRACKET_LEVEL_2>{</BRACKET_LEVEL_2> <INT_KEYWORD>int</INT_KEYWORD> a{0}; <BRACKET_LEVEL_2>}</BRACKET_LEVEL_2>;
        <ENUM_KEYWORD>enum</ENUM_KEYWORD> InnerEnum <BRACKET_LEVEL_2>{</BRACKET_LEVEL_2> A, B, C <BRACKET_LEVEL_2>}</BRACKET_LEVEL_2>;
        InnerStruct innerStruct_;
        InnerEnum innerEnum_;
      <ACCESS_SPECIFIER_KEYWORDS>public</ACCESS_SPECIFIER_KEYWORDS>:
        explicit ExtendedHL(<INT_KEYWORD>int</INT_KEYWORD> a) <BRACKET_LEVEL_2>{</BRACKET_LEVEL_2> <THIS_KEYWORD>this-></THIS_KEYWORD>innerStruct_.a = a; <BRACKET_LEVEL_2>}</BRACKET_LEVEL_2>
      <BRACKET_LEVEL_1>}</BRACKET_LEVEL_1>
      
      <INT_KEYWORD>int</INT_KEYWORD> main(<INT_KEYWORD>int</INT_KEYWORD> argc, <CHAR_KEYWORD>char</CHAR_KEYWORD>** argv) <BRACKET_LEVEL_1>{</BRACKET_LEVEL_1>
        <CHAR_KEYWORD>char</CHAR_KEYWORD> c = 'a';
      <BRACKET_LEVEL_1>}</BRACKET_LEVEL_1>
  """.trimIndent()*/
  override fun getDemoText() = org.extendedhl.cpp.util.BuildDemoText().build("""
      #include <iostream>
      
      namespace extended_hl <LVL1>{</LVL1>
        bool isEvenOrDivisibleByThree(long num) <LVL2>{</LVL2>
          if (num % 2 == 0) <LVL3>{</LVL3>
            std::cout << "Even" << '\n';
            return true;
          <LVL3>}</LVL3> else <LVL3>{</LVL3>
            if (num % 3 == 0) <LVL4>{</LVL4>
              return true;
            <LVL4>}</LVL4>
            std::cout << "Odd and not divisible by three" << '\n';
            return false;
          <LVL3>}</LVL3>
        <LVL2>}</LVL2>
      <LVL1>}</LVL1>
      
      #ifndef EXTENDED_HL_CPP
      #define EXTENDED_HL_CPP 1
      #if EXTENDED_HL_CPP>0
      #pragma clang diagnostic ignored "-Wunused-parameter"
      #elif EXTENDED_HL_CPP<0
      #undef EXTENDED_HL_CPP
      #endif
      #endif
      #ifdef EXTENDED_HL_CPP
      #line 1234 "extended_hl.cpp"
      #else
      #error "error"
      #endif
      
      <CAUTION_COMMENT>/*!*/</CAUTION_COMMENT><QUESTION_COMMENT>/*?*/</QUESTION_COMMENT><AT_COMMENT>/*@*/</AT_COMMENT><HASH_COMMENT>/*#*/</HASH_COMMENT><DOLLAR_COMMENT>/*$*/</DOLLAR_COMMENT><TILDE_COMMENT>/*~*/</TILDE_COMMENT><PERCENT_COMMENT>/*%*/</PERCENT_COMMENT>
      <CAUTION_COMMENT>//! comment</CAUTION_COMMENT>
      <QUESTION_COMMENT>//? comment</QUESTION_COMMENT>
      <AT_COMMENT>//@ comment</AT_COMMENT>
      <HASH_COMMENT>//# comment</HASH_COMMENT>
      <DOLLAR_COMMENT>//$ comment</DOLLAR_COMMENT>
      <TILDE_COMMENT>//~ comment</TILDE_COMMENT>
      <PERCENT_COMMENT>//% comment</PERCENT_COMMENT>
      
      class <CLASSID>ExtendedHL</CLASSID> <LVL1>{</LVL1>
      private:
        struct <STRUCTID>InnerStruct</STRUCTID> <LVL2>{</LVL2> int a{0}; <LVL2>}</LVL2>;
        enum <ENUMID>InnerEnum</ENUMID> <LVL2>{</LVL2> A, B, C <LVL2>}</LVL2>;
        <STRUCTID>InnerStruct</STRUCTID> innerStruct_;
        <ENUMID>InnerEnum</ENUMID> innerEnum_;
      public:
        explicit ExtendedHL(int a) <LVL2>{</LVL2> this->innerStruct_.a = a; <LVL2>}</LVL2>
        int <MFUNCID>returnFive</MFUNCID>() <LVL2>{</LVL2> return 5; <LVL2>}</LVL2>
      <LVL1>}</LVL1>
      
      int main(int argc, char** argv) <LVL1>{</LVL1>
        char c = 'a';
      <LVL1>}</LVL1>
  """.trimIndent())
  override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey> {
    return HlConfigProvider.ALL_KEYS.mapKeys { (k, _) -> k.removeSuffix("EXTENDEDHL.")}
  }
}