package org.extendedhl.cpp.core.markup

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.jetbrains.rd.ide.model.MarkupModelExtension
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rdclient.daemon.IFrontendProtocolMarkupExtension

class ExtendedIFrontendProtocolMarkupExtension : IFrontendProtocolMarkupExtension {
  private val log = org.extendedhl.cpp.logger<ExtendedIFrontendProtocolMarkupExtension>()
  override fun createExtensions(
      lifetime: Lifetime,
      document: Document
  ): List<MarkupModelExtension> {
    log.debug("MarkupExtension.createExtensions(document=$document)")
    return emptyList()
  }

  override fun createExtensions(
      lifetime: Lifetime,
      editor: Editor
  ): List<MarkupModelExtension> {
    log.debug("MarkupExtension.createExtensions(editor=$editor)")
    return emptyList()
  }
}