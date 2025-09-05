package org.extendedhl.cpp.core

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.ex.MarkupModelEx
import com.intellij.openapi.editor.impl.DocumentMarkupModel
import com.intellij.openapi.editor.markup.RangeHighlighter
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
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.elementType
import com.jetbrains.rider.cpp.fileType.lexer.CppTokenTypes
import com.jetbrains.rider.cpp.fileType.psi.CppBlock
import com.jetbrains.rider.cpp.fileType.psi.CppDummyNode


import org.extendedhl.cpp.config.HlConfigProvider
import org.extendedhl.cpp.logging.logger
import org.extendedhl.cpp.util.List.fromWrappedIndex
import org.extendedhl.cpp.util.psi.getElType

private data class CacheMapKey(
    private val startOffset: Int,
    private val endOffset: Int,
    private val elType: IElementType
) {
  companion object {
    fun from(el: PsiElement) = CacheMapKey(el.textRange.startOffset, el.textRange.endOffset, el.elementType!!)
  }
}
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
  private val log = logger<AnnotationCache>()

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
  fun <T : PsiElement> getOrComputeFor(
      element: T,
      compute: (T) -> AnnotEntry?
  ): AnnotEntry? {
    val file = element.containingFile ?: return null
    val map = getMap(file)
    val key = CacheMapKey.from(element).hashCode()
    val existing = map[key]
    if (existing != null) {
      val resolved = existing.ptr.element
      if (resolved === element && resolved.isValid) {
        // still valid for the same start offset
        //#if debug
        log.debug("Reusing existing AnnotEntry for $element")
        //#endif
        return existing
      }
      // If pointer resolves to something else or null at this offset, recompute below.
    }
    //#if debug
    log.debug("Computing AnnotEntry for $element")
    //#endif
    val created = compute(element) ?: return null
    map[key] = created
    return created
  }
}


class ExtendedAnnotator : Annotator, DumbAware {
  private val log = logger<ExtendedAnnotator>()

  override fun annotate(el: PsiElement, holder: AnnotationHolder) {
    if (PsiUtilCore.findLanguageFromElement(el).id != "C++") return
    val elType = PsiUtilCore.getElementType(el)
    if (!HlConfigProvider.shouldHandleType(elType)) {
      //#if debug
      log.debug("elType not handled: $elType")
      //#endif
      return
    }
    //#if debug
    log.debug("Annotating element: $el")
    //#endif
    when (el) {
      is CppDummyNode -> {
        //#if debug
        log.debug("el is CppDummyNode!!!: $el")
        //#endif
        if (el.firstChild.getElType() == CppTokenTypes.LBRACKET && el.firstChild.nextSibling.getElType() == CppTokenTypes.LBRACKET) {
          val entry = AnnotationCache.getOrComputeFor(el) { el ->
            val ranges = resolveCPPAttributesBracketsRanges(el)
            if (ranges == null) {
              //#if debug
              log.debug("ranges is null")
              //#endif
              return@getOrComputeFor null
            }
            ranges.forEach { log.debug("ranges: $it") }
            val key = HlConfigProvider.ALL_KEYS["CPP_ATTRIBUTES_BRACKETS"]
            if (key == null) {
              log.error("HlConfigProvider.ALL_KEYS[\"CPP_ATTRIBUTES_BRACKETS\"] == null")
              return@getOrComputeFor null
            }
            AnnotEntry(
                ptr = el.createSmartPointer(),
                ranges = ranges,
                key = key
            )
          } ?: return
          holder.doAnnotate(entry.ranges, entry.key)
          return
        }
      }
      is CppBlock -> {
        //#if debug
        log.debug("el is CppBlock!!!: $el")
        //#endif
        val entry = AnnotationCache.getOrComputeFor(el) { el: CppBlock ->
          var depth = 0
          //#if debug
          log.debug("depth: 0")
          //#endif

          var parentBlock = PsiTreeUtil.getParentOfType(el, CppBlock::class.java)
          while (parentBlock != null) {
            depth++
            //#if debug
            log.debug("depth: $depth")
            //#endif
            parentBlock = PsiTreeUtil.getParentOfType(parentBlock, CppBlock::class.java)
          }
          val key = HlConfigProvider.bracketsKeysByLevel.fromWrappedIndex(depth)

          val lBraceRange = el.lBrace?.textRange
          val rBraceRange = el.rBrace?.textRange
          if (lBraceRange == null || rBraceRange == null) {
            //#if debug
            log.debug("lBraceRange or rBraceRange is null")
            //#endif
            return@getOrComputeFor null
          }
          AnnotEntry(
            ptr = el.createSmartPointer(),
            ranges = listOf(lBraceRange, rBraceRange),
            key = key
          )
        } ?: return
        holder.doAnnotate(entry.ranges, entry.key)
        return
      }
    }
    when (elType) {
      CppTokenTypes.BLOCK_COMMENT, CppTokenTypes.EOL_COMMENT -> {
        val entry = AnnotationCache.getOrComputeFor(el) { el ->
          val lookup = el.text.take(3).replace("*", "/")
          val key = HlConfigProvider.keysByOptionalStringId[lookup] ?: return@getOrComputeFor null
          AnnotEntry(
              ptr = el.createSmartPointer(),
              ranges = listOf(el.textRange),
              key = key
          )
        } ?: return
        holder.doAnnotate(entry.ranges, entry.key)
        return
      }
    }
    if (HlConfigProvider.shouldAskMarkupModel(elType)) {
      //#if debug
      log.debug("Asking markup model for key")
      //#endif
      val entry = AnnotationCache.getOrComputeFor(el) { el ->
        findKeyFromMarkupModel(el)?.let { backendKey ->
          //#if debug
          log.debug("Got answer: $elType: ${backendKey.externalName}")
          //#endif
          val key = HlConfigProvider.keysByExternalName[backendKey.externalName] ?: return@let null
          AnnotEntry(
              ptr = el.createSmartPointer(),
              ranges = listOf(el.textRange),
              key = key
          )
        } ?: return@getOrComputeFor null
      } ?: return
      holder.doAnnotate(entry.ranges, entry.key)
      return
    }
    val entry = AnnotationCache.getOrComputeFor(el) { el ->
      val config = HlConfigProvider.configsByType[elType]
      if (config == null) {
        //#if debug
        log.debug("Resolved token not in config: $elType\n")
        //#endif
        return@getOrComputeFor null
      }
      if (!config.checkConds(el)) {
        //#if debug
        log.debug("Condition check failed for token: $elType\n")
        //#endif
        return@getOrComputeFor null
      }
      AnnotEntry(
        ptr = el.createSmartPointer(),
        ranges = listOf(el.textRange),
        key = config.key
      )
    } ?: return
    //#if debug
    log.debug("Annotating element: [${el.text},${entry.ranges}] with key: ${entry.key}")
    //#endif
    holder.doAnnotate(entry.ranges, entry.key)
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
  /**
   * Finds the best TextAttributesKey from MarkupModel that applies to this element's range.
   * Prefers a highlighter that fully covers the element's range, then higher layer, then smaller span.
   */
  private fun findKeyFromMarkupModel(el: PsiElement): TextAttributesKey? {
    val file = el.containingFile ?: return null
    val doc = file.viewProvider.document ?: return null
    val markup = DocumentMarkupModel.forDocument(doc, el.project, /* create = */ false) ?: return null

    val elementRange = el.textRange ?: return null
    var bestKey: TextAttributesKey? = null
    var bestIsFullCover = false
    var bestLayer = Int.MIN_VALUE
    var bestSpan = Int.MAX_VALUE

    // Prefer MarkupModelEx for efficient range processing
    val processor: (RangeHighlighter) -> Boolean = processor@ { rh ->
      val key = rh.textAttributesKey ?: return@processor true // continue
      val hr = rh.startOffset..rh.endOffset
      val elr = elementRange.startOffset..elementRange.endOffset

      val fullyCovers = rh.startOffset <= elementRange.startOffset && rh.endOffset >= elementRange.endOffset
      val span = rh.endOffset - rh.startOffset
      val layer = rh.layer

      val better = when {
        // Prefer full cover over partial
        fullyCovers && !bestIsFullCover -> true
        fullyCovers == bestIsFullCover && layer > bestLayer -> true
        fullyCovers == bestIsFullCover && layer == bestLayer && span < bestSpan -> true
        else -> false
      }

      if (better) {
        val hlInfoType = HighlightInfo.fromRangeHighlighter(rh)?.type
        if (hlInfoType != null) {
          bestKey = hlInfoType.attributesKey
          bestIsFullCover = fullyCovers
          bestLayer = layer
          bestSpan = span
        }
      }
      true // continue processing
    }

    when (markup) {
      is MarkupModelEx -> {
        markup.processRangeHighlightersOverlappingWith(
            elementRange.startOffset,
            elementRange.endOffset,
            processor
        )
      }
      else -> {
        // Fallback: iterate all. Less efficient but safe.
        for (rh in markup.allHighlighters) {
          // quick reject
          if (rh.endOffset <= elementRange.startOffset || rh.startOffset >= elementRange.endOffset) continue
          processor(rh)
        }
      }
    }
    return bestKey
  }
}