package org.extendedhl.cpp.settings;

import com.intellij.psi.tree.IElementType;
import com.jetbrains.cidr.lang.editor.colors.OCHighlightingKeys;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.PlainSyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import org.extendedhl.cpp.hl.ExtendedTokenTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.extendedhl.cpp.hl.ExtendedTokenTypes.*;
import org.extendedhl.cpp.hl.OCltt;

public class ColorSettings implements ColorSettingsPage {
  static AttributesDescriptor CreateAttrDescr(String cat, IElementType type) {
    return new AttributesDescriptor(
        cat + "//" + NAMES_AND_ATTRS_MAP.get(type).name(),
        NAMES_AND_ATTRS_MAP.get(type).attr()
    );
  }
  private final AttributesDescriptor[] descriptors = new AttributesDescriptor[]{

    CreateAttrDescr("Built-in Types", OCltt.VOID_KEYWORD),
    CreateAttrDescr("Built-in Types", OCltt.INT_KEYWORD),
    CreateAttrDescr("Built-in Types", OCltt.SHORT_KEYWORD),
    CreateAttrDescr("Built-in Types", OCltt.LONG_KEYWORD),
    CreateAttrDescr("Built-in Types", OCltt.BOOL_CPP_KEYWORD),
    CreateAttrDescr("Built-in Types", OCltt.FLOAT_KEYWORD),
    CreateAttrDescr("Built-in Types", OCltt.DOUBLE_KEYWORD),
    CreateAttrDescr("Built-in Types", OCltt.CHAR_KEYWORD),
    CreateAttrDescr("Built-in Types", OCltt.AUTO_KEYWORD),
    CreateAttrDescr("Control Flow", OCltt.IF_KEYWORD),
    CreateAttrDescr("Control Flow", OCltt.ELSE_KEYWORD),
    CreateAttrDescr("Control Flow", OCltt.RETURN_KEYWORD),
    CreateAttrDescr("Control Flow", OCltt.DO_KEYWORD),
    CreateAttrDescr("Control Flow", OCltt.WHILE_KEYWORD),
    CreateAttrDescr("Control Flow", OCltt.CONTINUE_KEYWORD),
    CreateAttrDescr("Control Flow", OCltt.BREAK_KEYWORD),
    CreateAttrDescr("Control Flow", OCltt.FOR_KEYWORD),
    CreateAttrDescr("Class & Struct", OCltt.STRUCT_KEYWORD),
    CreateAttrDescr("Functions", OCltt.IDENTIFIER)
  };

  @Override
  @NotNull
  public String getDisplayName() {
    return "C/C++ Extended";
  }

  @Override
  @Nullable
  public Icon getIcon() {
    return null;
  }

  @Override
  @NotNull
  public SyntaxHighlighter getHighlighter() {
    return new PlainSyntaxHighlighter();
  }

  @Override
  @NotNull
  public AttributesDescriptor[] getAttributeDescriptors() {
    return descriptors;
  }

  @Override
  @NotNull
  public ColorDescriptor[] getColorDescriptors() {
    return ColorDescriptor.EMPTY_ARRAY;
  }

  @Override
  @NotNull
  public String getDemoText() {
    return """
          <auto>auto</auto> <g_var>a</g_var> = <num>0</num>;
          <bool>bool</bool> <g_var>b</g_var> = true;
          <char>char</char> <g_var>c</g_var> = 'a';
          <double>double</double> <g_var>d</g_var> = <num>0</num><dot>.</dot><num>0</num>;
          <float>float</float> <g_var>f</g_var> = <num>0</num><dot>.</dot><num>0</num>f;
          <long>long</long> <long>long</long> <g_var>ll</g_var> = <num>0</num>;
          <short>short</short> <g_var>s</g_var> = <num>0</num>;
          <void>void</void> *<g_var>v</g_var> = nullptr_t;
          
          <int>int</int> main() {
            <if>if</if> (<g_var>a</g_var> < <num>0</num>) <g_var>b</g_var> = false;
          }
          
          """.trim();
  }

  @Override
  @Nullable
  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
    Map<String, TextAttributesKey> mergedMap = new LinkedHashMap<>(NAME_TO_ATTR);
    mergedMap.putAll(GetDefaults());
    return mergedMap;
  }
  private static Map<String, TextAttributesKey> GetDefaults() {
    LinkedHashMap<String, TextAttributesKey> d = new LinkedHashMap<>();
    d.put("num", OCHighlightingKeys.OC_NUMBER);
    d.put("comma", OCHighlightingKeys.OC_COMMA);
    d.put("g_var", OCHighlightingKeys.GLOBAL_VARIABLE);
    d.put("l_var", OCHighlightingKeys.LOCAL_VARIABLE);
    d.put("dot", OCHighlightingKeys.OC_DOT);
    return Collections.unmodifiableMap(d);
  }
}