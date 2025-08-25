package org.extendedhl.cpp

import com.google.common.base.Strings
import com.intellij.codeInsight.hint.HintManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.util.PsiUtilCore
import org.jetbrains.annotations.NotNull

import com.jetbrains.rider.cpp.fileType.CppSyntaxHighlighter
import com.jetbrains.rider.cpp.fileType.lexer.CppTokenTypes
import com.jetbrains.rdclient.highlighting.FrontendHighlighterAttributeCustomizer

class Debugger : AnAction() {
  override fun actionPerformed(e: AnActionEvent) {
    val project: Project = e.project ?: return;
    val editor: Editor = e.getData(CommonDataKeys.EDITOR) ?: return
    val psiFile: PsiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document) ?: return
    val offset: Int = editor.caretModel.offset
    val element: PsiElement? = psiFile.findElementAt(0.coerceAtLeast(offset.coerceAtMost(psiFile.textLength - 1)))
    if (element == null) {
      HintManager.getInstance().showInformationHint(editor, "No PSI element at caret")
      return
    }
    val html: String = buildDebugHtml(element)
    HintManager.getInstance().showInformationHint(editor, html)
  }

  private fun buildDebugHtml(el: @NotNull PsiElement): String {
    val sb = StringBuilder("<html><body style='font-family: monospace;'>")
    val eltype = PsiUtilCore.getElementType(el)

    sb.append(printAstTypeNamesTree(el, StringBuilder(), 2))
    appendLine(sb, "", "\n")

    CppSyntaxHighlighter().getTokenHighlights(eltype)
      .forEach { attr -> appendLine(sb, "Syntax highlighting attribute", attr.externalName) }

    appendLine(sb, "PsiUtilCore.getElementType(el).toString()", eltype.toString())
    appendLine(sb, "PsiUtilCore.getElementType(el).debugName", eltype.debugName)
    appendLine(sb, "PsiUtilCore.getElementType(el).javaClass.toString()", eltype.javaClass.toString())
    appendLine(sb, "PsiUtilCore.getElementType(el).javaClass.name", eltype.javaClass.name)

    appendLine(sb, "toString()", el.toString())
    appendLine(sb, "javaClass.toString()", el.javaClass.toString())
    appendLine(sb, "javaClass.simpleName", el.javaClass.simpleName)
    appendLine(sb, "javaClass.name", el.javaClass.name)
    appendLine(sb, "javaClass.packageName", el.javaClass.packageName)
    appendLine(sb, "parent.toString()", el.parent.toString())
    appendLine(sb, "parent.javaClass.toString()", el.parent.javaClass.toString())
    appendLine(sb, "parent.javaClass.simpleName", el.parent.javaClass.simpleName)
    appendLine(sb, "parent.javaClass.name", el.parent.javaClass.name)
    appendLine(sb, "parent.javaClass.packageName", el.parent.javaClass.packageName)

    return sb.toString()
  }

  private fun printAstTypeNamesTree(
    node: PsiElement,
    builder: java.lang.StringBuilder,
    indent: Int
  ): java.lang.StringBuilder {
    builder.append(Strings.repeat(" ", indent))
    builder.append(PsiUtilCore.getElementType(node)).append("\n")
    var childNode = node.firstChild
    while (childNode != null) {
      printAstTypeNamesTree(childNode, builder, indent + 2)
      childNode = childNode.nextSibling
    }
    return builder
  }
  private fun appendLine(sb: java.lang.StringBuilder, k: String?, v: String?) {
    sb.append("<b>").append(escape(k)).append(":</b> ")
      .append(escape(v)).append("<br/>")
  }
  private fun escape(s: String?): String {
    if (s == null) return "null"
    return s
      .replace("&", "&amp;")
      .replace("<", "&lt;")
      .replace(">", "&gt;")
      .replace("\r\n", "\n")
      .replace("\r", "\n")
      .replace("\n", "<br/>")
  }


}

