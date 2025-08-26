package org.extendedhl.cpp.config

data class KeysToAccept(
    val externalName: String,
)

object KeysToAcceptProvider{

  val list: List<KeysToAccept> by lazy {
    listOf(
        KeysToAccept("ReSharper.CPP_BUILTIN_TYPE_KEYWORD")
    )
  }
  val map: Map<String, KeysToAccept> by lazy {
    list.associateBy { it.externalName }
  }
}