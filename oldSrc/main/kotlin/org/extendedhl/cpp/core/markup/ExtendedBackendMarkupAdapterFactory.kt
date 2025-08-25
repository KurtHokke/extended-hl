package org.extendedhl.cpp.core.markup

class ExtendedBackendMarkupAdapterFactory : com.jetbrains.rdclient.daemon.FrontendMarkupAdapterFactory() {
  private val log = org.extendedhl.cpp.logger<ExtendedBackendMarkupAdapterFactory>()
  override fun create(
      rdMarkup: com.jetbrains.rd.ide.model.RdMarkupModel,
      ideaMarkup: com.intellij.openapi.editor.ex.MarkupModelEx,
      compoundHandler: com.jetbrains.rdclient.daemon.CompoundHighlighterModelsHandler
  ): com.jetbrains.rdclient.daemon.FrontendMarkupAdapter {
    log.info("new ExtendedBackendMarkupAdapter: model=${rdMarkup::class.qualifiedName}, doc=${ideaMarkup.document}, existingHighlighters=${ideaMarkup.allHighlighters.size}, handler=${compoundHandler::class.simpleName}")
    val ex = ideaMarkup.allHighlighters.filterIsInstance<com.intellij.openapi.editor.ex.RangeHighlighterEx>().toTypedArray()
    log.debug("Filtered RangeHighlighterEx count=${ex.size}")
    return ExtendedBackendMarkupAdapter(
        ideaMarkup.document,
        ideaMarkup.allHighlighters.filterIsInstance<com.intellij.openapi.editor.ex.RangeHighlighterEx>().toTypedArray(),
        rdMarkup.daemon
    )
  }
}