package org.extendedhl.cpp.core

import com.intellij.openapi.client.ClientAppSession
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.project.Project
import com.jetbrains.rd.ide.model.HighlighterModel
import com.jetbrains.rd.ide.model.RdMarkupModel
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rdclient.daemon.IProtocolHighlighterModelHandler

import org.extendedhl.cpp.config.Const
import org.extendedhl.cpp.config.KeysToAcceptProvider

class ExtendedHighlightHandler(
  private val lifetime: Lifetime,
  private val project: Project,
  private val session: ClientAppSession,
  private val rdMarkup: RdMarkupModel,
  private val document: Document
) : IProtocolHighlighterModelHandler {
  private val log = org.extendedhl.cpp.logging.logger<ExtendedHighlightHandler>()
  override fun accept(model: HighlighterModel): Boolean {
    val modelExternalName = model.textAttributesKey?.externalName ?: "NULL!!!"
    val inKeysToAccept = KeysToAcceptProvider.map[modelExternalName]?.externalName
    log.debug("accept: model.textAttributesKey=$modelExternalName, inKeysToAccept=$inKeysToAccept")
    return modelExternalName == inKeysToAccept
  }

  override fun initialize(
    model: HighlighterModel,
    highlighter: RangeHighlighter
  ) {
    highlighter.setTextAttributesKey(resolveTextAttributesKey(model))

    highlighter.isGreedyToLeft = false
    highlighter.isGreedyToRight = false

    // Keep essential identity on the highlighter for future comparisons
    highlighter.putUserData(Const.MODEL_EXTERNAL_NAME_KEY, model.textAttributesKey?.externalName)
    highlighter.putUserData(Const.MODEL_TAK_KEY, resolveTextAttributesKey(model).externalName)

  }

  override fun compare(
    model: HighlighterModel,
    highlighter: RangeHighlighter
  ): Boolean {
    // Range must match
    if (highlighter.startOffset != model.start || highlighter.endOffset != model.end) {
      log.debug("compare: highlighter.startOffset=${highlighter.startOffset} != model.start=${model.start} || highlighter.endOffset=${highlighter.endOffset} != model.end=${model.end}")
      return false
    }

    // Identity must match
    val existingExternalName = highlighter.getUserData(Const.MODEL_EXTERNAL_NAME_KEY)
    if (existingExternalName != model.textAttributesKey?.externalName) {
      log.debug("compare: existingExternalName=$existingExternalName != model.textAttributesKey?.externalName=${model.textAttributesKey?.externalName}")
      return false
    }

    // Attributes should match (avoid repaint if same)
    val existingTakName = highlighter.getUserData(Const.MODEL_TAK_KEY)
    val expectedTakName = resolveTextAttributesKey(model).externalName
    log.debug("compare: existingTakName=$existingTakName == expectedTakName=$expectedTakName")
    return existingTakName == expectedTakName
  }


  override fun move(
    startOffset: Int,
    endOffset: Int,
    model: HighlighterModel
  ): HighlighterModel? {
    return if (startOffset == model.start && endOffset == model.end) model
    else null
  }
  // Helper: resolve the TextAttributesKey to apply (from model.key or externalName)
  private fun resolveTextAttributesKey(model: HighlighterModel): TextAttributesKey {
    val externalName = model.textAttributesKey?.externalName
    return when {
      externalName == "ReSharper.CPP_BUILTIN_TYPE_KEYWORD" ->
          TextAttributesKey.createTextAttributesKey("EXTENDEDHL.CPP_BUILTIN_TYPE_KEYWORD")
      !externalName.isNullOrEmpty() ->
          TextAttributesKey.find(externalName)
      else ->
          TextAttributesKey.createTextAttributesKey("EXTENDEDHL.CPP_BUILTIN_TYPE_KEYWORD")
    }
  }

  // Convenience to apply TextAttributesKey to a RangeHighlighter across schemes
  //private fun RangeHighlighter.setTextAttributes(key: TextAttributesKey?) {
  //  if (key == null) return
  //  // Let the active scheme resolve attributes for this key
  //  val scheme = EditorColorsManager.getInstance().globalScheme
  //  val attrs = scheme.getAttributes(key)
  //  this.setTextAttributes()
  //  this.textAttributes = attrs
  //}

}



