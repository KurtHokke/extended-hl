package org.extendedhl.cpp.core.markup

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.ex.RangeHighlighterEx
import com.intellij.openapi.util.Key
import com.jetbrains.rd.ide.model.HighlighterElement
import com.jetbrains.rd.ide.model.MarkupModelExtension
import com.jetbrains.rd.ide.model.RdDaemonModel
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rdclient.daemon.FrontendMarkupAdapterListener

class ExtendedBackendMarkupAdapter(
  override val document: Document,
  override val allHighlighters: Array<out RangeHighlighterEx>,
  override val daemonModel: RdDaemonModel
) : com.jetbrains.rdclient.daemon.FrontendMarkupAdapter {
  private val log = org.extendedhl.cpp.logger<ExtendedBackendMarkupAdapter>()
  init {
    log.debug("new ExtendedBackendMarkupAdapter(document=$document, allHighlighters=${allHighlighters.size}, daemonModel=$daemonModel)")
  }

  override fun addHighlighter(element: HighlighterElement): RangeHighlighterEx {
    log.debug("addHighlighter: element=$element")
    TODO("Not yet implemented")
  }

  override fun getHighlighters(offset: Int): List<RangeHighlighterEx> {
    log.debug("getHighlighters: offset=$offset")
    TODO("Not yet implemented")
  }

  override fun updateHighlighter(
    highlighter: RangeHighlighterEx,
    newElement: HighlighterElement
  ) {
    log.debug("updateHighlighter: highlighter=$highlighter, newElement=$newElement")
    TODO("Not yet implemented")
  }

  override fun removeHighlighter(highlighter: RangeHighlighterEx) {
    log.debug("removeHighlighter: highlighter=$highlighter")
    TODO("Not yet implemented")
  }

  override fun bulkRemoveHighlighters(highlighters: List<RangeHighlighterEx>) {
    log.debug("bulkRemoveHighlighters: highlighters=${highlighters.size}")
    TODO("Not yet implemented")
  }

  override fun bulkAddHighlighters(elements: List<HighlighterElement>): List<RangeHighlighterEx> {
    log.debug("bulkAddHighlighters: elements=${elements.size}")
    TODO("Not yet implemented")
  }

  override fun addListener(
    lifetime: Lifetime,
    listener: FrontendMarkupAdapterListener
  ) {
    log.debug("addListener: lifetime=$lifetime, listener=$listener")
    TODO("Not yet implemented")
  }

  override fun <T : MarkupModelExtension> getExtension(key: Key<T>): T? {
    log.debug("getExtension: key=$key")
    TODO("Not yet implemented")
  }
}