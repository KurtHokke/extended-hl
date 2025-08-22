package org.extendedhl.cpp

import com.intellij.openapi.diagnostic.Logger
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.*
import com.intellij.psi.util.PsiUtilCore

import com.jetbrains.rider.cpp.fileType.lexer.CppTokenTypes
import com.jetbrains.rider.cpp.fileType.CppSyntaxHighlighter

class CppAnnotator : Annotator, DumbAware {
  companion object {
    private val log = Logger.getInstance(CppAnnotator::class.java)
  }
  override fun annotate(element: PsiElement, holder: AnnotationHolder) {
    // Example: detect a macro-like element or attribute by name/structure.
    // Start with safe guards so you don’t touch huge trees unnecessarily.
    if (!element.isValid || element.textLength == 0) return

    // Replace these with actual Nova PSI checks once you learn the element classes:
    if (looksLikeSpecialThing(element)) {
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(element)
            .textAttributes(Colors.SPECIAL_MACRO)
            .create()
        return
    }
    val highlighter = CppSyntaxHighlighter()
    val elementType = PsiUtilCore.getElementType(element)

    //if (highlighter.getTokenHighlights(elementType)[0].externalName == "") {}
  }

  private fun looksLikeSpecialThing(e: PsiElement): Boolean {
    // Start with a heuristic (text, parent chain, token type name), then refine
    val text = e.text
    if (text == "SPECIAL_MACRO") return true

    // You can branch on class name without linking to internal API:
    val cls = e.javaClass.name
    // e.g., when you discover actual classes, do:
    // if (cls.endsWith(".CppPreprocessorIdentifier") && text == "SPECIAL_MACRO") return true
    return false
  }
}