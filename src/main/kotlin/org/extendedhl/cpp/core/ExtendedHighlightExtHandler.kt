package org.extendedhl.cpp.core

import com.intellij.openapi.editor.ex.RangeHighlighterEx
import com.jetbrains.rd.ide.model.HighlighterExtension
import com.jetbrains.rd.ide.model.HighlighterModel
import com.jetbrains.rdclient.daemon.FrontendHighlighterExtensionHandler
// Kotlin
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.ProjectLocator
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiUtilCore
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.tree.IElementType
import com.jetbrains.rdclient.daemon.util.text

import org.extendedhl.cpp.HlConfigProvider
import org.extendedhl.cpp.Colors

class ExtendedHighlighterExtensionHandler : FrontendHighlighterExtensionHandler {
  private val log = org.extendedhl.cpp.logger<ExtendedHighlighterExtensionHandler>()

  override fun processExtension(
    highlighter: RangeHighlighterEx,
    highlighterModel: HighlighterModel,
    extensions: List<HighlighterExtension>
  ) {
    val hlExtName = highlighter.textAttributesKey?.externalName ?: "NULL!!!"
    val hlModelExtName = highlighterModel.textAttributesKey?.externalName ?: "NULL!!!"

    log.debug("hlExtName::$hlExtName, hlModelExtName::$hlModelExtName")
    log.debug("$hlExtName -> ${highlighter.text}")
    log.debug("$hlModelExtName -> ${highlighterModel.textToHighlight}")

    if (extensions.isEmpty()) {
      log.debug("No extensions for $hlExtName | $hlModelExtName")
    } else {
      for (e in extensions) {
        log.debug("HL ext: ${e.javaClass.name} -> $e")
      }
    }

    val elementType = resolveElementTypeAtOffset(highlighter)
    if (elementType == null) {
      log.debug("No elementType for $hlModelExtName")
      return
    }
    log.debug("elementType for $hlModelExtName is $elementType")

    val config = HlConfigProvider.configs.find { it.tokenType == elementType }
    if (config == null) {
      log.debug("No config for $hlModelExtName")
      return
    }
    log.debug("Found config for $hlModelExtName: ${config.name}, ${config.tokenType}, ${config.settingsPath}")

    val colorKey = Colors.ALL_KEYS[config.name]
    if (colorKey == null) {
      log.debug("No colorKey for ${config.name}")
      return
    }
    log.debug("Found colorKey for ${config.name}: $colorKey")
    highlighter.setTextAttributesKey(colorKey)
  }
  private fun resolveElementTypeAtOffset(
    highlighter: RangeHighlighterEx,
    preferStart: Boolean = true
  ): IElementType? = ReadAction.compute<IElementType?, Throwable> {
    val document = highlighter.document // RangeHighlighterEx gives you the Document
    val vFile = FileDocumentManager.getInstance().getFile(document) ?: return@compute null
    val project = ProjectLocator.getInstance().guessProjectForFile(vFile) ?: return@compute null

    // Ensure PSI is in sync with the document
    PsiDocumentManager.getInstance(project).commitDocument(document)

    val psiFile = PsiManager.getInstance(project).findFile(vFile) ?: return@compute null

    val offset = if (preferStart) highlighter.startOffset else highlighter.endOffset.coerceAtLeast(highlighter.startOffset)
    val leaf = psiFile.findElementAt(offset) ?: return@compute null
    PsiUtilCore.getElementType(leaf)
  }
}