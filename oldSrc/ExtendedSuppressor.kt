package org.extendedhl.cpp.core

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.jetbrains.rdclient.daemon.FrontendHighlighterSuppressionHandler
import com.jetbrains.rd.ide.model.HighlighterModel
import com.intellij.codeInsight.highlighting.HighlightManager

class ExtendedSuppressor : FrontendHighlighterSuppressionHandler {
  private val log = org.extendedhl.cpp.logging.logger<ExtendedSuppressor>()
  override fun shouldSuppress(
      highlighterModel: HighlighterModel,
      document: Document
  ): Boolean {
    var shouldSuppress = false
    val hlModelInfo: StringBuilder = StringBuilder("[")
    val keyModel = highlighterModel.textAttributesKey
    if (keyModel != null) {
      shouldSuppress = if (keyModel.externalName == "ReSharper.CPP_BUILTIN_TYPE_KEYWORD") {
        log.info("!!!keyModel.externalName == \"ReSharper.CPP_BUILTIN_TYPE_KEYWORD\"!!!")
        true
      } else { false }
      hlModelInfo.append("extName=${keyModel.externalName}, ")
    }
    shouldSuppress = if (highlighterModel.properties.attributeId == "ReSharper C++ Builtin Type Keyword") {
      log.info("!!!highlighterModel.properties.attributeId == \"ReSharper C++ Builtin Type Keyword\"!!!")
      true
    } else { shouldSuppress }
    shouldSuppress = if (highlighterModel.properties.demoTextTag == "CPP_BUILTIN_TYPE_KEYWORD") {
      log.info("!!!highlighterModel.properties.demoTextTag == \"CPP_BUILTIN_TYPE_KEYWORD\"!!!")
      true
    } else { shouldSuppress }
    hlModelInfo.append("hlModelId=${highlighterModel.id}, ")
    hlModelInfo.append("layer=${highlighterModel.layer}, ")
    hlModelInfo.append("properties.attributeId=${highlighterModel.properties.attributeId}, ")
    hlModelInfo.append("properties.demoTextTag=${highlighterModel.properties.demoTextTag}")
    hlModelInfo.append("]")
    log.info("HighlighterModel: $hlModelInfo")
    return true
  }
}