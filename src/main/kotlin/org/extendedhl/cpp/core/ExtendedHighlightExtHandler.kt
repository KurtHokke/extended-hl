package org.extendedhl.cpp.core

import com.intellij.openapi.editor.ex.RangeHighlighterEx
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.jetbrains.rd.ide.model.HighlighterExtension
import com.jetbrains.rd.ide.model.HighlighterModel
import com.jetbrains.rdclient.daemon.FrontendHighlighterExtensionHandler
// Kotlin
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.editor.Document
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
import com.jetbrains.ide.model.highlighterRegistration.TextAttributesKeyModel
import com.jetbrains.rdclient.daemon.util.text

import org.extendedhl.cpp.util.javaAwtColor.*
import org.extendedhl.cpp.config.HlConfigProvider
import org.extendedhl.cpp.config.KeysToAcceptProvider
import org.extendedhl.cpp.config.Colors
import org.extendedhl.cpp.logging.PluginLogger

private val DOC_LAST_APPLIED_CACHE: Key<MutableMap<SmartPsiElementPointer<PsiElement>, TextAttributesKey>> =
  Key.create("extendedhl.doc.lastAppliedCache")
//private val DOC_LAST_APPLIED_CACHE: Key<MutableMap<Pair<Int, Int>, TextAttributesKey>> =
//    Key.create("extendedhl.doc.lastAppliedCache")

class ExtendedHighlightExtHandler : FrontendHighlighterExtensionHandler {
  private val log = org.extendedhl.cpp.logging.logger<ExtendedHighlightExtHandler>()
  override fun processExtension(
      highlighter: RangeHighlighterEx,
      highlighterModel: HighlighterModel,
      extensions: List<HighlighterExtension>
  ) {
    if (KeysToAcceptProvider.list.contains(
        highlighterModel.textAttributesKey?.externalName ?: return)
    ) {
      log.info("Highlighting for ${highlighterModel.textAttributesKey?.externalName}")
      customProcess(highlighter, highlighterModel, extensions)
    } else {
      log.info("Skipping highlighting for ${highlighterModel.textAttributesKey?.externalName}")
    }
  }
  private fun customProcess(
      hl: RangeHighlighterEx,
      hlModel: HighlighterModel,
      extensions: List<HighlighterExtension>,
      hlKey: TextAttributesKey? = hl.textAttributesKey,
      hlModelKey: TextAttributesKeyModel? = hlModel.textAttributesKey,

  ) {
    val hlExtName = hlKey?.externalName ?: "NULL!!!"
    val hlModelExtName = hlModelKey?.externalName ?: "NULL!!!"
    val attr = EditorColorsManager.getInstance().globalScheme.getAttributes(hlKey)
    log.debug("hlModelExtName=$hlModelExtName, hlExtName=$hlExtName")
    //log.debug("$hlExtName -> ${hl.text}")
    if (attr != null) {
      val attrColor = attr.foregroundColor
      log.debug("color for $hlModelExtName / $hlExtName:")
      if (attrColor != null) {
        log.debug("rgb=${attrColor.toRgbString()}, hex=\"${attrColor.toHexString()}\"")
      } else {
        log.debug("rgb=null, hex=null")
      }
    }

// Per-document cache to survive highlighter recreation
    val document: Document = hl.document
    val cache = document.getUserData(DOC_LAST_APPLIED_CACHE)
      ?: mutableMapOf<SmartPsiElementPointer<PsiElement>, TextAttributesKey>().also {
        document.putUserData(DOC_LAST_APPLIED_CACHE, it)
      }

    val element = resolvePsiElementAtOffset(hl)
    if (element == null) {
      log.debug("No element for $hlModelExtName")
      return
    }
    log.debug("element for $hlModelExtName is $element")

    // Try reusing previously applied key for this element
    val rememberedEntry = cache.entries.firstOrNull { it.key.element === element }
    rememberedEntry?.let { remembered ->
      log.debug("remembered for element: ${remembered.value.externalName}")
      // Optional: apply immediately, or keep as a hint and still compute below
      log.info("Applying remembered key for $hlModelExtName: ${remembered.value.externalName}")
      hl.setTextAttributesKey(remembered.value)
      return
    } ?: log.debug("no remembered for element")


    log.debug("$hlModelExtName -> ${hlModel.textToHighlight}")


    val elementType = PsiUtilCore.getElementType(element)
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
    // Clean up invalid or matching entries before storing new one
    val keysToRemove = mutableListOf<SmartPsiElementPointer<PsiElement>>()
    for (entry in cache.entries) {
      if (entry.key.element == null || entry.key.element === element) {
        keysToRemove.add(entry.key)
      }
    }
    keysToRemove.forEach { cache.remove(it) }

    // Store the new pointer
    val pointer = SmartPointerManager.getInstance(element.project).createSmartPsiElementPointer(element)
    cache[pointer] = colorKey
  }

  private fun resolvePsiElementAtOffset(
      highlighter: RangeHighlighterEx,
      preferStart: Boolean = true
  ): PsiElement? = ReadAction.compute<PsiElement?, Throwable> {
    val document = highlighter.document // RangeHighlighterEx gives you the Document
    val vFile = FileDocumentManager.getInstance().getFile(document) ?: return@compute null
    val project = ProjectLocator.getInstance().guessProjectForFile(vFile) ?: return@compute null

    // Ensure PSI is in sync with the document
    PsiDocumentManager.getInstance(project).commitDocument(document)

    val psiFile = PsiManager.getInstance(project).findFile(vFile) ?: return@compute null

    val offset = if (preferStart) highlighter.startOffset else highlighter.endOffset.coerceAtLeast(highlighter.startOffset)
    psiFile.findElementAt(offset)
  }
}