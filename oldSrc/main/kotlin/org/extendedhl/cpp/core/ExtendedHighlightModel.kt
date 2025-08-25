package org.extendedhl.cpp.core

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.HighlighterLayer
import java.awt.Color

data class ExtendedHighlightModel(
  val externalName: String,          // e.g., "ReSharper.CPP_BUILTIN_TYPE_KEYWORD"
  val key: TextAttributesKey?,       // optional: resolved key (may be null if you resolve later)
  val startOffset: Int,
  val endOffset: Int,
  val tooltip: String? = null,
  val errorStripeColor: Color? = null,
  val layer: Int = HighlighterLayer.ADDITIONAL_SYNTAX
)