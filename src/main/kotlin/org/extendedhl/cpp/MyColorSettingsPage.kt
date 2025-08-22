package org.extendedhl.cpp

import com.intellij.openapi.fileTypes.PlainSyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

class MyColorSettingsPage : ColorSettingsPage {
    private val descriptors = arrayOf(
        AttributesDescriptor("int Keyword", Colors.C_INT_KEYWORD),
        AttributesDescriptor("char Keyword", Colors.C_CHAR_KEYWORD)
    )

    override fun getDisplayName() = "C/C++ Extended"
    override fun getIcon(): Icon? = null
    override fun getHighlighter(): SyntaxHighlighter = PlainSyntaxHighlighter()

    override fun getAttributeDescriptors() = descriptors
    override fun getColorDescriptors() = emptyArray<com.intellij.openapi.options.colors.ColorDescriptor>()
    override fun getDemoText() = """
        #if SPECIAL_MACRO
        int foo();
        #endif
    """.trimIndent()
    override fun getAdditionalHighlightingTagToDescriptorMap() = null
}