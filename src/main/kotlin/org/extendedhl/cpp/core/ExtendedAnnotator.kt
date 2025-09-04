package org.extendedhl.cpp.core

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.platform.workspace.storage.impl.cache.cache
import com.intellij.psi.*
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiUtilCore
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.createSmartPointer
import com.jetbrains.rider.cpp.fileType.lexer.CppTokenTypes
import com.jetbrains.rider.cpp.fileType.psi.CppBlock
import com.jetbrains.rider.cpp.fileType.psi.CppDummyNode

import org.extendedhl.cpp.config.HlConfigProvider
import org.extendedhl.cpp.util.List.fromWrappedIndex
import org.extendedhl.cpp.util.psi.getElType


private data class AnnotEntry(
    val ptr: SmartPsiElementPointer<PsiElement>,
    val ranges: List<TextRange>,
    val key: TextAttributesKey
)

/**
 * Per-file cache that is:
 * - Invalidated on any PSI change (MODIFICATION_COUNT).
 * - Populated lazily per element via getOrComputeFor().
 */
private object AnnotationCache {
  private val KEY: Key<CachedValue<MutableMap<Int, AnnotEntry>>> =
    Key.create("your.plugin.annot.perFileElementCache")

  private fun getMap(file: PsiFile): MutableMap<Int, AnnotEntry> {
    val mgr = CachedValuesManager.getManager(file.project)
    var cached = file.getUserData(KEY)
    if (cached == null) {
      cached = mgr.createCachedValue({
        CachedValueProvider.Result.create(
           mutableMapOf<Int, AnnotEntry>(),
           PsiModificationTracker.MODIFICATION_COUNT
        )
      }, false)
      file.putUserData(KEY, cached)
    }
    return cached.value
  }

  /**
   * Returns an up-to-date entry for [element] if available; otherwise computes it via [compute],
   * stores it, and returns it. The stored entry is validated before reuse.
   */
  fun getOrComputeFor(
      element: PsiElement,
      compute: (PsiElement) -> AnnotEntry?
  ): AnnotEntry? {
    val file = element.containingFile ?: return null
    val map = getMap(file)
    val key = element.textRange.startOffset

    val existing = map[key]
    if (existing != null) {
      val resolved = existing.ptr.element
      if (resolved === element && resolved.isValid) {
        // still valid for the same start offset
        return existing
      }
      // If pointer resolves to something else or null at this offset, recompute below.
    }

    val created = compute(element) ?: return null
    map[key] = created
    return created
  }
}


class ExtendedAnnotator : Annotator, DumbAware {
  private val log = org.extendedhl.cpp.logging.logger<ExtendedAnnotator>()

  override fun annotate(el: PsiElement, holder: AnnotationHolder) {

    if (PsiUtilCore.findLanguageFromElement(el).id != "C++") return
    log.debug("Annotating element: $el")
    when (el) {
      is CppDummyNode -> {
        log.debug("el is CppDummyNode!!!: $el")
        if (el.firstChild.getElType() == CppTokenTypes.LBRACKET && el.firstChild.nextSibling.getElType() == CppTokenTypes.LBRACKET) {
          //val entry = AnnotationCache.getOrComputeFor(el) { el ->
          //
          //}
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
        val lookup = el.text.take(3).replace("*", "/")
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