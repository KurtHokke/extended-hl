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
import com.jetbrains.cidr.lang.psi.impl.OCTypeElementImpl;
//import com.jetbrains.cidr.lang.psi.

import java.util.Set;
import java.util.HashSet;

public class ExtendedAnnotator implements Annotator, DumbAware {


  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    var lang = PsiUtilCore.findLanguageFromElement(element);
    if (!"ObjectiveC".equals(lang.getID())) {
      return;
    }

    var scheme = EditorColorsManager.getInstance().getGlobalScheme();
    var builtinAttrs = scheme.getAttributes(HighlighterTokens.BUILTIN_TYPE);
    var includeAttrs = scheme.getAttributes(HighlighterTokens.INCLUDE_DIRECTIVE);

    if (element.getFirstChild() == null && element.getParent().getClass() == OCTypeElementImpl.class) {
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
    }

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
}