package org.extendedhl.cpp

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.*
import com.intellij.psi.util.PsiUtilCore
import com.jetbrains.rider.cpp.fileType.lexer.CppTokenTypes

class CppAnnotator : Annotator, DumbAware {
  private val log = org.extendedhl.cpp.logger<CppAnnotator>()
  override fun annotate(element: PsiElement, holder: AnnotationHolder) {
    if (!element.isValid || element.textLength == 0) return

    val A = holder.at(element)
    val elementType = PsiUtilCore.getElementType(element)

    if (elementType == CppTokenTypes.CHAR_KEYWORD) {
      A.info(Colors.C_CHAR_KEYWORD); return
    }
    if (elementType == CppTokenTypes.INT_KEYWORD) {
      A.info(Colors.C_INT_KEYWORD); return
    }

  }
  private fun AnnotationHolder.at(element: PsiElement) = ElementAnnotations(this, element)
  private class ElementAnnotations(
    private val holder: AnnotationHolder,
    private val element: PsiElement
  ) {
    fun info(key: TextAttributesKey) {
      holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
        .range(element)
        .textAttributes(key)
        .create()
    }
    fun warn(message: String = "", key: TextAttributesKey? = null) {
      val b = holder.newAnnotation(HighlightSeverity.WARNING, message).range(element)
      if (key != null) b.textAttributes(key)
      b.create()
    }
    fun error(message: String = "", key: TextAttributesKey? = null) {
      val b = holder.newAnnotation(HighlightSeverity.ERROR, message).range(element)
      if (key != null) b.textAttributes(key)
      b.create()
    }
    fun weakInfo(message: String = "", key: TextAttributesKey? = null) {
      val b = holder.newAnnotation(HighlightSeverity.WEAK_WARNING, message).range(element)
      if (key != null) b.textAttributes(key)
      b.create()
    }
  }
}