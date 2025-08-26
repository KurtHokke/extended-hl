package org.extendedhl.cpp.core

import com.intellij.openapi.editor.ex.RangeHighlighterEx
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.jetbrains.rd.ide.model.HighlighterExtension
import com.jetbrains.rd.ide.model.HighlighterModel
import com.jetbrains.rdclient.daemon.FrontendHighlighterExtensionHandler
// Kotlin
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.ProjectLocator
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiUtilCore
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.psi.tree.IElementType
import com.intellij.openapi.util.TextRange
import com.jetbrains.rdclient.daemon.util.text

import org.extendedhl.cpp.util.javaAwtColor.*
import org.extendedhl.cpp.config.HlConfigProvider
import org.extendedhl.cpp.config.Colors

private val LAST_APPLIED_KEY: Key<TextAttributesKey> =
    Key.create("extendedhl.lastappliedkey")

class ExtendedHighlightExtHandler : FrontendHighlighterExtensionHandler {
  private val log = org.extendedhl.cpp.logging.logger<ExtendedHighlightExtHandler>()
  override fun processExtension(
      highlighter: RangeHighlighterEx,
      highlighterModel: HighlighterModel,
      extensions: List<HighlighterExtension>
  ) { customProcess(highlighter, highlighterModel, extensions) }
  private fun customProcess(
      hl: RangeHighlighterEx,
      hlModel: HighlighterModel,
      extensions: List<HighlighterExtension>
  ) {
    val hlKey = hl.textAttributesKey
    val hlModelKey = hlModel.textAttributesKey
    val hlExtName = hlKey?.externalName ?: "NULL!!!"
    val hlModelExtName = hlModelKey?.externalName ?: "NULL!!!"
    val attr = EditorColorsManager.getInstance().globalScheme.getAttributes(hlKey)
    log.debug("hlExtName=$hlExtName, hlModelExtName=$hlModelExtName")
    log.debug("$hlExtName -> ${hl.text}")
    if (attr != null) {
      val attrColor = attr.foregroundColor
      log.debug("color for $hlExtName / $hlModelExtName:")
      if (attrColor != null) {
        log.debug("rgb=${attrColor.toRgbString()}, hex=\"${attrColor.toHexString()}\"")
      } else {
        log.debug("rgb=null, hex=null")
      }
    }
    val lastApplied = hl.getUserData(LAST_APPLIED_KEY)
    if (lastApplied != null) {
      log.debug("last applied: $lastApplied, ${lastApplied.externalName}")
    } else {
      log.debug("no last applied")
    }

    log.debug("$hlModelExtName -> ${hlModel.textToHighlight}")

    if (extensions.isEmpty()) {
      //log.debug("No extensions for $hlExtName | $hlModelExtName")
    } else {
      for (e in extensions) {
        log.debug("HL ext: ${e.javaClass.name} -> $e")
      }
    }

    val elementType = resolveElementTypeAtOffset(hl)
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
    hl.setTextAttributesKey(colorKey)
    hl.putUserData(LAST_APPLIED_KEY, colorKey)
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