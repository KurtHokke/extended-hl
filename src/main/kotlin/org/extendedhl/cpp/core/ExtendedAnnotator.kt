package org.extendedhl.cpp.core

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiUtilCore
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.rider.cpp.fileType.lexer.CppTokenTypes
import com.jetbrains.rider.cpp.fileType.psi.CppBlock
import com.jetbrains.rider.cpp.fileType.psi.CppDummyNode

import org.extendedhl.cpp.config.HlConfigProvider
import org.extendedhl.cpp.util.List.fromWrappedIndex
import org.extendedhl.cpp.util.psi.getElType

class ExtendedAnnotator : Annotator, DumbAware {
  private val log = org.extendedhl.cpp.logging.logger<ExtendedAnnotator>()
  override fun annotate(el: PsiElement, holder: AnnotationHolder) {
    if (PsiUtilCore.findLanguageFromElement(el).id != "C++") return
    log.debug("Annotating element: ${el.text}")
    when (el) {
      is CppDummyNode -> {
        log.debug("el is CppDummyNode!!!: $el")
        if (el.firstChild.getElType() == CppTokenTypes.LBRACKET && el.firstChild.nextSibling.getElType() == CppTokenTypes.LBRACKET) {
          val ranges = resolveCPPAttributesBracketsRanges(el)
          if (ranges == null) {
            log.debug("ranges is null")
            return
          }
          ranges.forEach {
            log.debug("ranges: $it")
          }
          holder.doAnnotate(ranges, HlConfigProvider.ALL_KEYS["CPP_ATTRIBUTES_BRACKETS"])
          return
        }
      }
      is CppBlock -> {
        var depth = 0
        log.debug("el is CppBlock!!!: $el")
        log.debug("depth: 0")

        var parentBlock = PsiTreeUtil.getParentOfType(el, CppBlock::class.java)
        while (parentBlock != null) {
          depth++
          log.debug("depth: $depth")
          parentBlock = PsiTreeUtil.getParentOfType(parentBlock, CppBlock::class.java)
        }
        log.debug("CppBlock (lBrace and rBrace).toString():")
        log.debug(el.lBrace.toString())
        log.debug(el.rBrace.toString())
        val key = HlConfigProvider.bracketsKeysByLevel.fromWrappedIndex(depth)
        log.debug("(lBrace and rBrace) key: ${key.externalName}")

        val lBraceRange = el.lBrace?.textRange
        val rBraceRange = el.rBrace?.textRange
        if (lBraceRange == null || rBraceRange == null) {
          log.debug("lBraceRange or rBraceRange is null")
          return
        }
        holder.doAnnotate(listOf(lBraceRange, rBraceRange), key)
      }
    }
    val resolvedToken = PsiUtilCore.getElementType(el)
    when (resolvedToken) {
      CppTokenTypes.BLOCK_COMMENT, CppTokenTypes.EOL_COMMENT -> {
        val lookup = el.text.substring(0,3).replace("*", "/")
        val key = HlConfigProvider.keysByOptionalStringId[lookup]
        if (key != null) {
          holder.doAnnotate(el.textRange, key)
          return
        }
      }
    }
    val config = HlConfigProvider.configsByType[resolvedToken]

    if (config == null) {
      log.debug("Resolved token: $resolvedToken\n")
      return
    }
    if (!config.checkConds(el)) {
      log.debug("Condition check failed for token: $resolvedToken\n")
      return
    }
    log.debug("Resolved token: $resolvedToken")
    log.debug("config?.name: ${config.name}")
    log.debug("config?.key?.externalName: ${config.key.externalName}\n")

    log.info("Annotating element: ${el.text} with config: ${config.name}")
    holder.doAnnotate(el.textRange, config.key)
  }

  private fun AnnotationHolder.doAnnotate(range: TextRange, key: TextAttributesKey?) {
    if (range.length != 0 && key != null) {
      this.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES).range(range).textAttributes(key).create()
    }
  }
  private fun AnnotationHolder.doAnnotate(rangeList: List<TextRange>, key: TextAttributesKey?) {
    for (range in rangeList) {
      if (range.length != 0 && key != null) {
        this.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES).range(range).textAttributes(key).create()
      }
    }
  }
  private fun resolveCPPAttributesBracketsRanges(el: PsiElement): List<TextRange>? {
    val nodes = el.node.getChildren(TokenSet.create(CppTokenTypes.LBRACKET, CppTokenTypes.RBRACKET))
    if (nodes.isEmpty()) return null
    val result = mutableListOf<TextRange>()
    nodeNameLoop@ for (node in nodes) {
      if (node.psi.getElType() == node.psi.nextSibling.getElType()) {
        when (node.psi.getElType()) {
          CppTokenTypes.LBRACKET, CppTokenTypes.RBRACKET -> {
            result.add(TextRange(node.psi.textRange.startOffset, node.psi.nextSibling.textRange.endOffset))
          }
        }
      }
    }
    return result
  }
}