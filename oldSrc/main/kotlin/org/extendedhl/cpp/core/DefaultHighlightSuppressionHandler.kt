package org.extendedhl.cpp.core

import com.intellij.openapi.editor.Document
import com.jetbrains.rd.ide.model.HighlighterModel
import com.jetbrains.rdclient.daemon.FrontendHighlighterSuppressionHandler


class DefaultHighlightSuppressionHandler : FrontendHighlighterSuppressionHandler {
  private val log = org.extendedhl.cpp.logger<DefaultHighlightSuppressionHandler>()
  override fun shouldSuppress(
    highlighterModel: HighlighterModel,
    document: Document
  ): Boolean {
    val externalName = highlighterModel.textAttributesKey?.externalName ?: return false
    // Suppress the default handler for builtin type keywords so our handler can take over
    val suppress = externalName == "ReSharper.CPP_BUILTIN_TYPE_KEYWORD"
    if (suppress) log.debug("Suppressing default handler for externalName=$externalName range=(${highlighterModel.start}, ${highlighterModel.end})")
    return suppress

  }
}