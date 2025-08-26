package org.extendedhl.cpp.core.markup

import com.intellij.openapi.editor.Document
import com.jetbrains.rd.ide.model.MarkupModelExtension
import com.jetbrains.rd.util.lifetime.Lifetime

class ExtendedMarkupExtension : com.jetbrains.rdclient.daemon.IFrontendProtocolMarkupExtension {
  private val log = org.extendedhl.cpp.logging.logger<ExtendedMarkupExtension>()
  override fun createExtensions(
    lifetime: Lifetime,
    document: Document
  ): List<MarkupModelExtension> {
    log.debug("MarkupExtension.createExtensions(document=$document)")
    return listOf(ExtendedMarkupRegisterKeys(lifetime, KeysToRegister.list))
  }
}