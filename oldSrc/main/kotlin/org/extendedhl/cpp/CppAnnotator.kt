package org.extendedhl.cpp

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.editor.ex.RangeHighlighterEx
import com.intellij.psi.*
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiUtilCore
import com.jetbrains.rider.cpp.fileType.lexer.CppTokenTypes
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager


class CppAnnotator : Annotator, DumbAware {
  private val log = org.extendedhl.cpp.logger<CppAnnotator>()
  override fun annotate(element: PsiElement, holder: AnnotationHolder) {
    if (!element.isValid || element.textLength == 0) return

    val elementType = PsiUtilCore.getElementType(element)
    val config = HlConfigProvider.configs.find { it.tokenType == elementType } ?: return
    val colorKey = Colors.ALL_KEYS[config.name] ?: return
    val A = holder.at(element)

    val childElement = Utils.ASTgetChildByType(element, config.tokenType)
    if (childElement != null) {
      A.info(colorKey, childElement); return
    }

    //if (config.tokenType == CppTokenTypes.CLASS_KEYWORD) {
    //  val keywordPsi = element.node.findChildByType(CppTokenTypes.CLASS_KEYWORD)?.psi
    //  if (keywordPsi != null) A.info(colorKey, keywordPsi)
    //  return
    //}

    when (config.severity) {
      HighlightSeverity.INFORMATION -> A.info(colorKey)
      HighlightSeverity.WEAK_WARNING -> A.weakInfo(config.displayName ?: "", colorKey)
      HighlightSeverity.WARNING -> A.warn(config.displayName ?: "", colorKey)
      HighlightSeverity.ERROR -> A.error(config.displayName ?: "", colorKey)
      else -> A.info(colorKey)
    }

  }
  private fun AnnotationHolder.at(element: PsiElement) = ElementAnnotations(this, element)
  private class ElementAnnotations(
    private val holder: AnnotationHolder,
    private val element: PsiElement
  ) {
    private val scheme = EditorColorsManager.getInstance().globalScheme
    private val log = org.extendedhl.cpp.logger<CppAnnotator>()
    fun info(key: TextAttributesKey, element: PsiElement = this.element) {
      holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
          .range(element.textRange)
          .enforcedTextAttributes(scheme.getAttributes(key))
          .needsUpdateOnTyping()
          .create()
      log.debug("ANNOTATION created: severity=INFO key=${key.externalName} range=[${element.textRange.startOffset}, ${element.textRange.endOffset}] text='${element.text}'")
    }
    fun weakInfo(message: String = "", key: TextAttributesKey? = null) {
      val b = holder.newAnnotation(HighlightSeverity.WEAK_WARNING, message)
          .range(element.textRange)
          .needsUpdateOnTyping()
      if (key != null) b.textAttributes(key)
      b.create()
      log.debug("ANNOTATION created: severity=WEAK_WARNING key=${key?.externalName} range=[${element.textRange.startOffset}, ${element.textRange.endOffset}] text='${element.text}'")
    }
    fun warn(message: String = "", key: TextAttributesKey? = null) {
      val b = holder.newAnnotation(HighlightSeverity.WARNING, message)
          .range(element.textRange)
          .needsUpdateOnTyping()
      if (key != null) b.textAttributes(key)
      b.create()
      log.debug("ANNOTATION created: severity=WARNING key=${key?.externalName} range=[${element.textRange.startOffset}, ${element.textRange.endOffset}] text='${element.text}'")
    }
    fun error(message: String = "", key: TextAttributesKey? = null) {
      val b = holder.newAnnotation(HighlightSeverity.ERROR, message)
          .range(element.textRange)
          .needsUpdateOnTyping()
      if (key != null) b.textAttributes(key)
      b.create()
      log.debug("ANNOTATION created: severity=ERROR key=${key?.externalName} range=[${element.textRange.startOffset}, ${element.textRange.endOffset}] text='${element.text}'")
    }
  }
  private fun getColorKeyForType(element: PsiElement, elementType: IElementType): TextAttributesKey? {
    return CachedValuesManager.getCachedValue(element.containingFile) {
      val configMap = HlConfigProvider.configs.associate { it.tokenType to Colors.ALL_KEYS[it.name]!! }
      CachedValueProvider.Result.create(configMap[elementType], element.containingFile)
    }
  }
}