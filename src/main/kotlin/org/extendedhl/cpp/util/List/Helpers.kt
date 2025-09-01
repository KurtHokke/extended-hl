package org.extendedhl.cpp.util.List

fun <T> List<T>.fromWrappedIndex(index: Int): T {
  return this[((index % size) + size) % size]
}