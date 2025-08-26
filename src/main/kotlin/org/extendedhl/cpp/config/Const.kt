package org.extendedhl.cpp.config

import com.intellij.openapi.util.Key

object Const {
  const val LOG_PATH = "C:/home/arcno/src/intellij/cpp"

  // UserData keys stored on RangeHighlighter so compare() can identify “same model”
  val MODEL_EXTERNAL_NAME_KEY = Key.create<String>("EXTENDEDHL.MODEL_EXTERNAL_NAME")
  val MODEL_TAK_KEY = Key.create<String>("EXTENDEDHL.MODEL_TAK_EXTERNAL_NAME")
  val MODEL_TOOLTIP_KEY = Key.create<String>("EXTENDEDHL.MODEL_TOOLTIP")
}