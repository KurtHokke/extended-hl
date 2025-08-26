package org.extendedhl.cpp.util.javaAwtColor

fun java.awt.Color.toHexString() = String.format("#%02x%02x%02x", red, green, blue)
fun java.awt.Color.toRgbString() = String.format("[%d,%d,%d]", red, green, blue)