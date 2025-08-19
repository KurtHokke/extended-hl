package org.extendedhl.cpp.tools;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;

public class ShowPsiDebugHintAction extends AnAction {
  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    Project project = e.getProject();
    Editor editor = e.getData(CommonDataKeys.EDITOR);
    if (project == null || editor == null) return;

    PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
    if (psiFile == null) return;

    int offset = editor.getCaretModel().getOffset();
    PsiElement el = psiFile.findElementAt(Math.max(0, Math.min(offset, psiFile.getTextLength() - 1)));
    if (el == null && offset > 0) el = psiFile.findElementAt(offset - 1);
    if (el == null) {
      HintManager.getInstance().showInformationHint(editor, "No PSI element at caret");
      return;
    }

    // Build debug text
    String html = buildDebugHtml(el);

    // Show a small hint at caret
    HintManager.getInstance().showInformationHint(editor, html);
  }

  private static String buildDebugHtml(@NotNull PsiElement el) {
  StringBuilder sb = new StringBuilder("<html><body style='font-family: monospace;'>");

  appendLine(sb, "element.class", el.getClass().getName());
  appendLine(sb, "element.typename", el.getClass().getTypeName());
  appendLine(sb, "element.simplename", el.getClass().getSimpleName());
  appendLine(sb, "element.toString", String.valueOf(el));
  appendLine(sb, "element.elementType", getElementTypeString(el));
  appendLine(sb, "element.range", String.valueOf(el.getTextRange()));
  appendLine(sb, "element.text", escape(el.getText()));

  PsiElement original = el.getOriginalElement();
  appendLine(sb, "original.class", original != null ? original.getClass().getName() : "null");
  appendLine(sb, "original.toString", String.valueOf(original));
  appendLine(sb, "original.elementType", getElementTypeString(original));
  appendLine(sb, "original==element", String.valueOf(original == el));
  if (original != null) {
    appendLine(sb, "original.range", String.valueOf(original.getTextRange()));
    appendLine(sb, "original.text", escape(original.getText()));
  }

  PsiElement nav = el.getNavigationElement();
  appendLine(sb, "navigation.class", nav != null ? nav.getClass().getName() : "null");
  appendLine(sb, "navigation.toString", String.valueOf(nav));
  appendLine(sb, "navigation.elementType", getElementTypeString(nav));
  appendLine(sb, "navigation.range", nav != null ? String.valueOf(nav.getTextRange()) : "null");

  PsiReference ref = el.getReference();
  if (ref != null) {
    PsiElement resolved = ref.resolve();
    appendLine(sb, "reference", ref.getClass().getName());
    appendLine(sb, "resolvesTo", resolved != null ? resolved.getClass().getName() : "null");
    appendLine(sb, "resolvesTo.toString", String.valueOf(resolved));
    appendLine(sb, "resolvesTo.elementType", getElementTypeString(resolved));
  }

  sb.append("</body></html>");
  return sb.toString();
}

private static String getElementTypeString(PsiElement el) {
  if (el == null) return "null";
  var node = el.getNode();
  if (node == null) return "null";
  var type = node.getElementType();
  return type != null ? type.toString() : "null";
}

  private static void appendLine(StringBuilder sb, String k, String v) {
    sb.append("<b>").append(escape(k)).append(":</b> ").append(escape(v)).append("<br/>");
  }

  private static String escape(String s) {
    if (s == null) return "null";
    return s
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\n", "\\n")
        .replace("\r", "\\r");
  }
}