package org.extendedhl.cpp

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey

object Colors {

  fun CreateTAK(name: String, fallback: TextAttributesKey): TextAttributesKey =
      TextAttributesKey.createTextAttributesKey(name, fallback)
  val SPECIAL_MACRO: TextAttributesKey = CreateTAK(
      "EXT_SPECIAL_MACRO", DefaultLanguageHighlighterColors.METADATA)
  //val CHAR_KEYWORD: TextAttributesKey = CreateTAK(
  //    "EXT_CHAR_KEYWORD", )


}