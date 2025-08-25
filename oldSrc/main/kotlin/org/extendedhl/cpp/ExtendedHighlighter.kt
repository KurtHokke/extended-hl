package org.extendedhl.cpp

import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.tree.IElementType
import com.jetbrains.rdclient.highlighting.FrontendHighlighterAttributeCustomizer

class ExtendedHighlighter : FrontendHighlighterAttributeCustomizer {

  private val log = logger<ExtendedHighlighter>()

  override fun getTextAttributesKey(externalName: String): TextAttributesKey? {
    log.debug("getTextAttributesKey1: externalName=$externalName")
    if (externalName == "ReSharper.CPP_BUILTIN_TYPE_KEYWORD") {
      log.debug("externalName == \"ReSharper.CPP_BUILTIN_TYPE_KEYWORD\", returning empty TextAttributesKey")
      return TextAttributesKey.createTextAttributesKey("")
    } else {
      return null
    }
  }

  override fun getTextAttributesKey(
    scheme: EditorColorsScheme,
    externalName: String,
    attributeId: String
  ): TextAttributesKey? {
    return getTextAttributesKey(externalName)
  }

}