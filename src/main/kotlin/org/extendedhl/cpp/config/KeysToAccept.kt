package org.extendedhl.cpp.config

//data class KeysToAccept(
//    val externalName: String,
//)

object KeysToAcceptProvider{

  val list: List<String> by lazy {
    listOf(
        "ReSharper.CPP_BUILTIN_TYPE_KEYWORD"
    )
  }
}