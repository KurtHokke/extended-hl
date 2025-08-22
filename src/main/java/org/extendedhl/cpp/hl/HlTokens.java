package org.extendedhl.cpp.hl;

import com.jetbrains.cidr.lang.editor.colors.OCHighlightingKeys;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class HlTokens {
  static TextAttributesKey CreateAttrKey(String name, TextAttributesKey defaultKey) {
    return TextAttributesKey.createTextAttributesKey(
        "EXTENDEDCPPHL." + name,
        defaultKey
    );
  }
  public static final TextAttributesKey EXT_VOID_KEYWORD =
      CreateAttrKey("VOID_TYPE", OCHighlightingKeys.BUILTIN_TYPE_KEYWORD);
  public static final TextAttributesKey EXT_INT_KEYWORD =
      CreateAttrKey("INT_TYPE", OCHighlightingKeys.BUILTIN_TYPE_KEYWORD);
  public static final TextAttributesKey EXT_SHORT_KEYWORD =
      CreateAttrKey("SHORT_TYPE", OCHighlightingKeys.BUILTIN_TYPE_KEYWORD);
  public static final TextAttributesKey EXT_LONG_KEYWORD =
      CreateAttrKey("LONG_TYPE", OCHighlightingKeys.BUILTIN_TYPE_KEYWORD);
  public static final TextAttributesKey EXT_BOOL_KEYWORD =
    CreateAttrKey("BOOL_TYPE", OCHighlightingKeys.BUILTIN_TYPE_KEYWORD);
  public static final TextAttributesKey EXT_FLOAT_KEYWORD =
    CreateAttrKey("FLOAT_TYPE", OCHighlightingKeys.BUILTIN_TYPE_KEYWORD);
  public static final TextAttributesKey EXT_DOUBLE_KEYWORD =
    CreateAttrKey("DOUBLE_TYPE", OCHighlightingKeys.BUILTIN_TYPE_KEYWORD);
  public static final TextAttributesKey EXT_CHAR_KEYWORD =
    CreateAttrKey("CHAR_TYPE", OCHighlightingKeys.BUILTIN_TYPE_KEYWORD);
  public static final TextAttributesKey EXT_AUTO_KEYWORD =
    CreateAttrKey("AUTO_TYPE", OCHighlightingKeys.BUILTIN_TYPE_KEYWORD);
  public static final TextAttributesKey EXT_IF_KEYWORD =
    CreateAttrKey("IF_KEYWORD", OCHighlightingKeys.CONTROL_FLOW_KEYWORD);
  public static final TextAttributesKey EXT_ELSE_KEYWORD =
    CreateAttrKey("ELSE_KEYWORD", OCHighlightingKeys.CONTROL_FLOW_KEYWORD);
  public static final TextAttributesKey EXT_RETURN_KEYWORD =
    CreateAttrKey("RETURN_KEYWORD", OCHighlightingKeys.CONTROL_FLOW_KEYWORD);
  public static final TextAttributesKey EXT_DO_KEYWORD =
    CreateAttrKey("DO_KEYWORD", OCHighlightingKeys.CONTROL_FLOW_KEYWORD);
  public static final TextAttributesKey EXT_WHILE_KEYWORD =
    CreateAttrKey("WHILE_KEYWORD", OCHighlightingKeys.CONTROL_FLOW_KEYWORD);
  public static final TextAttributesKey EXT_CONTINUE_KEYWORD =
    CreateAttrKey("CONTINUE_KEYWORD", OCHighlightingKeys.CONTROL_FLOW_KEYWORD);
  public static final TextAttributesKey EXT_BREAK_KEYWORD =
    CreateAttrKey("BREAK_KEYWORD", OCHighlightingKeys.CONTROL_FLOW_KEYWORD);
  public static final TextAttributesKey EXT_FOR_KEYWORD =
    CreateAttrKey("FOR_KEYWORD", OCHighlightingKeys.CONTROL_FLOW_KEYWORD);
  public static final TextAttributesKey EXT_STRUCT_KEYWORD =
    CreateAttrKey("STRUCT_KEYWORD", OCHighlightingKeys.STRUCT_FIELD);
  public static final TextAttributesKey EXT_MEMBER_FUNCTION =
    CreateAttrKey("MEMBER_FUNCTION", OCHighlightingKeys.FUNCTION_CALL);
  public static final TextAttributesKey INCLUDE_DIRECTIVE =
      TextAttributesKey.createTextAttributesKey(
          "EXTENDEDCPPHL.INCLUDE_DIRECTIVE"
          // , DefaultLanguageHighlighterColors.METADATA
      );



  //static Optional<TextAttributesKey> attrFor(String name) {
    //return Optional.ofNullable(NAME_TO_ATTR.get(name));
  //}



  private HlTokens() {}
}