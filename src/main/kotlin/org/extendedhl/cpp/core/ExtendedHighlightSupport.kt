package org.extendedhl.cpp.core

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.project.Project
import com.jetbrains.rd.util.lifetime.Lifetime
import com.intellij.openapi.client.ClientAppSession
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.jetbrains.rd.ide.model.HighlighterModel
import com.jetbrains.rd.ide.model.RdMarkupModel
import com.jetbrains.rdclient.daemon.IProtocolHighlighterModelHandler
import com.jetbrains.rdclient.daemon.IProtocolHighlighterModelSupport

class ExtendedHighlightSupport : IProtocolHighlighterModelSupport {
  private val log = org.extendedhl.cpp.logging.logger<ExtendedHighlightSupport>()
  override fun createHandler(
    lifetime: Lifetime,
    project: Project?,
    session: ClientAppSession,
    markupModel: RdMarkupModel,
    document: Document
  ): IProtocolHighlighterModelHandler? {
    if (project == null) return null
    log.debug("createHandler: project=$project")
    return ExtendedHighlightHandler(lifetime, project, session, markupModel, document)
  }

  // Optional overload that has Editor
  override fun createHandler(
    lifetime: Lifetime,
    project: Project?,
    session: ClientAppSession,
    markupModel: RdMarkupModel,
    editor: Editor
  ): IProtocolHighlighterModelHandler? {
    // If you prefer to attach by editor (e.g., need caret-model info), do it here.
    return createHandler(lifetime, project, session, markupModel, editor.document)
  }
}