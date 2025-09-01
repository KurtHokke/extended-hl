package org.extendedhl.cpp.core

import com.intellij.openapi.editor.ex.RangeHighlighterEx
import com.jetbrains.rd.ide.model.HighlighterExtension
import com.jetbrains.rd.ide.model.HighlighterModel
import com.jetbrains.rd.util.string.printToString
import com.jetbrains.rdclient.daemon.FrontendHighlighterExtensionHandler
import com.jetbrains.rdclient.daemon.util.attributeKey
import com.jetbrains.rdclient.daemon.util.attributeKeyName
import com.jetbrains.rdclient.daemon.util.backendAttributeId
import com.jetbrains.rdclient.daemon.util.isBackendHighlighter
import com.jetbrains.rdclient.daemon.util.text

import org.extendedhl.cpp.config.HlConfigProvider

class ExtendedHlExtensionHandler : FrontendHighlighterExtensionHandler {
  private val log = org.extendedhl.cpp.logging.logger<ExtendedHlExtensionHandler>()
  override fun processExtension(
      highlighter: RangeHighlighterEx,
      highlighterModel: HighlighterModel,
      extensions: List<HighlighterExtension>
  ) {
    val keyExternalName = highlighterModel.textAttributesKey?.externalName ?: return
    if (HlConfigProvider.configsByExternalName.containsKey(keyExternalName)) {
      val config = HlConfigProvider.configsByExternalName[keyExternalName]
      if (config == null) {
        log.warn("No config found for keyExternalName: $keyExternalName")
        return
      }
      log.info("setting text attributes for struct-like: ${highlighter.text} to ${config.key.externalName}")
      highlighter.setTextAttributesKey(config.key)
      return
    }
    log.info("externalName: $keyExternalName")
    log.info("text: ${highlighter.text}")
  }
}