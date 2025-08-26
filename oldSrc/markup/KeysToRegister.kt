package org.extendedhl.cpp.core.markup

object KeysToRegister {

  val list: List<String> = org.extendedhl.cpp.config.Colors.ALL_KEYS.values
      .map { it.externalName }
}