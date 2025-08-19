package org.extendedhl.cpp.hl;

import com.intellij.openapi.editor.colors.TextAttributesKey;

public final class HighlighterTokens {
  public static final TextAttributesKey BUILTIN_TYPE =
      TextAttributesKey.createTextAttributesKey(
          "EXTENDEDCPPHL.BUILTIN_TYPE"
          // , DefaultLanguageHighlighterColors.METADATA
      );
  public static final TextAttributesKey INCLUDE_DIRECTIVE =
      TextAttributesKey.createTextAttributesKey(
          "EXTENDEDCPPHL.INCLUDE_DIRECTIVE"
          // , DefaultLanguageHighlighterColors.METADATA
      );
  private HighlighterTokens() {}
}