package org.extendedhl.cpp.core

import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.jetbrains.rdclient.highlighting.FrontendHighlighterAttributeCustomizer
import org.extendedhl.cpp.config.HlConfigProvider

class ExtendedAttrHandler : FrontendHighlighterAttributeCustomizer {
  private val log = org.extendedhl.cpp.logging.logger<ExtendedAttrHandler>()
  override fun getTextAttributesKey(externalName: String): TextAttributesKey? {
    log.info("getTextAttributesKey: $externalName")
    val config = HlConfigProvider.configsByExternalName[externalName] ?: return null
    log.info("returning custom key: ${config.key.externalName}")
    return config.key
  }

  override fun getTextAttributesKey(
      scheme: EditorColorsScheme,
      externalName: String,
      attributeId: String
  ): TextAttributesKey? {
    log.info("getTextAttributesKey: $externalName, $attributeId")
    return getTextAttributesKey(externalName)
  }
}