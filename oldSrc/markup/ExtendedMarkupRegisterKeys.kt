package org.extendedhl.cpp.core.markup

import com.jetbrains.rd.ide.model.MarkupModelExtension
import com.jetbrains.rd.util.lifetime.Lifetime

class ExtendedMarkupRegisterKeys(
  private val lifetime: Lifetime,
  val keys: List<String>
) : MarkupModelExtension(KEY) {
  companion object {
    const val KEY: String = "EXTENDEDHL.MARKUP_REGISTER_KEYS"
  }
}