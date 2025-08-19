package org.extendedhl.cpp.hl;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;

import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.cidr.lang.parser.OCLexerTokenTypes;
import com.jetbrains.cidr.lang.psi.OCIncludeDirective;
import com.jetbrains.cidr.lang.psi.OCTypeElement;
//import com.jetbrains.cidr.lang.psi.

import java.util.Set;
import java.util.HashSet;

public class ExtendedAnnotator implements Annotator, DumbAware {

  //private final Set<String> builtinTypes = new HashSet<>(Set.of(
  //    "void", "bool", "char", "wchar_t", "char8_t", "char16_t", "char32_t",
  //    "short", "int", "long", "signed", "unsigned",
  //    "float", "double", "auto", "decltype", "nullptr_t"
  //));

  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    var lang = PsiUtilCore.findLanguageFromElement(element);
    if (!"ObjectiveC".equals(lang.getID())) {
      return;
    }

    var scheme = EditorColorsManager.getInstance().getGlobalScheme();
    var builtinAttrs = scheme.getAttributes(HighlighterTokens.BUILTIN_TYPE);
    var includeAttrs = scheme.getAttributes(HighlighterTokens.INCLUDE_DIRECTIVE);

    if (matchTokens(element, OCLexerTokenTypes.SIMPLE_TYPE_SPECIFIERS)) {
      String token = element.getText();
      if (token != null) {
        holder.newAnnotation(HighlightSeverity.INFORMATION, "Built-in type")
            .range(element.getTextRange())
            .enforcedTextAttributes(builtinAttrs)
            .needsUpdateOnTyping(true)
            .create();
      }
    }

    //if (element.getFirstChild() == null) {
    //  String token = element.getText();
    //  if (token != null && builtinTypes.contains(token) && isInTypePosition(element)) {
    //    holder.newAnnotation(HighlightSeverity.INFORMATION, "Built-in type")
    //        .range(element)
    //        .enforcedTextAttributes(builtinAttrs)
    //        .needsUpdateOnTyping(true)
    //        .create();
    //  }
    //}

    // ... existing code ...

    // 2) Lightweight include-path highlight: target the path within #include
    if (element instanceof OCIncludeDirective include) {
      // Try to find the range of text within "" or <>
      String text = include.getText();
      assert text != null;
      int open = Math.max(text.indexOf('<'), text.indexOf('"'));
      if (open >= 0) {
        int close = (text.charAt(open) == '<') ? text.indexOf('>', open + 1)
                                               : text.indexOf('"', open + 1);
        if (close > open) {
          int startOffset = include.getTextRange().getStartOffset() + open + 1;
          int endOffset = include.getTextRange().getStartOffset() + close;
          TextRange pathRange = new TextRange(startOffset, endOffset);

          // Use a gentle severity and attributes (replace with your own TextAttributesKey if desired)
          holder.newAnnotation(HighlightSeverity.INFORMATION, "Include path")
              .range(pathRange)
              .enforcedTextAttributes(includeAttrs) // Replace with your INCLUDE_PATH key if you have one
              .needsUpdateOnTyping(true)
              .create();
        }
      }
    }
  }

  private static boolean matchTokens(PsiElement el, TokenSet ts) {
    var type = getIElementType(el);
    if (type == null) return false;
    return ts.contains(type);
  }

  private static IElementType getIElementType(PsiElement el) {
    if (el == null) return null;
    var node = el.getNode();
    if (node == null) return null;
    return node.getElementType();
  }

  //private boolean isInTypePosition(PsiElement element) {
  //  PsiElement current = element.getParent();
  //  int steps = 0;
  //  while (current != null && steps < 6) {
  //    String className = current.getClass().getSimpleName();
  //    if (className.toLowerCase().contains("type")
  //     || className.toLowerCase().contains("specifier")
  //     || className.toLowerCase().contains("declarator")
  //     || className.toLowerCase().contains("declarationspecifiers")) {
  //      return true;
  //    }
  //    current = current.getParent();
  //    steps++;
  //  }
  //  return false;
  //}
}