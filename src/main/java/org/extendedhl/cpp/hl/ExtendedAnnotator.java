package org.extendedhl.cpp.hl;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import com.jetbrains.cidr.lang.psi.*;
import org.jetbrains.annotations.NotNull;

import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.tree.IElementType;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExtendedAnnotator implements Annotator, DumbAware {
  private static final Logger LOG = Logger.getInstance(ExtendedAnnotator.class);

  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    var lang = PsiUtilCore.findLanguageFromElement(element);
    if (!"ObjectiveC".equals(lang.getID())) {
      return;
    }

    var scheme = EditorColorsManager.getInstance().getGlobalScheme();
    //var builtinAttrs = scheme.getAttributes(HlTokens.BUILTIN_TYPE);
    var includeAttrs = scheme.getAttributes(HlTokens.INCLUDE_DIRECTIVE);

    if (element.getFirstChild() == null) {
      if (CheckIf.IsInstanceOf(element, CheckIf.InstanceKind.TYPE)) {
        var map = matchTokens(element, ExtendedTokenTypes.BUILTIN_TYPES);
        if (map != null) {
          String token = element.getText();
          if (token != null) {
            holder.newAnnotation(HighlightSeverity.INFORMATION, map.getValue())
                .range(element.getTextRange())
                .textAttributes(map.getKey())
                .needsUpdateOnTyping(true)
                .create();
          }
        }
      }
      else if (CheckIf.IsInstanceOf(element, CheckIf.InstanceKind.CONTROL_FLOW)) {
        var map = matchTokens(element, ExtendedTokenTypes.CONTROL_FLOW);
        if (map != null) {
          String token = element.getText();
          if (token != null) {
            holder.newAnnotation(HighlightSeverity.INFORMATION, map.getValue())
                .range(element.getTextRange())
                .textAttributes(map.getKey())
                .needsUpdateOnTyping(true)
                .create();
          }
        }
      }
      /*else if (CheckIf.IsInstanceOf(element, CheckIf.InstanceKind.STRUCT)) {
        var map = matchTokens(element, ExtendedTokenTypes.CLASS_STRUCT);
        if (map != null) {
          String token = element.getText();
          if (token != null) {
            holder.newAnnotation(HighlightSeverity.INFORMATION, map.getValue())
                .range(element.getTextRange())
                .textAttributes(map.getKey())
                .needsUpdateOnTyping(true)
                .create();
          }
        }
        if (element.getParent() instanceof OCStruct struct) {
          List<OCDeclaration> members = struct.getMembers();
          for (OCDeclaration member : members) {
            if (member instanceof OCFunctionDeclaration func) {
              TextRange range = Objects.requireNonNull(func.getNameIdentifier()).getTextRange();
              var memMap = matchTokens(member.getParent(), ExtendedTokenTypes.MEMBER_FUNCTIONS);
              if (memMap != null) {
                holder.newAnnotation(HighlightSeverity.INFORMATION, memMap.getValue())
                    .range(range)
                    .textAttributes(memMap.getKey())
                    .needsUpdateOnTyping(true)
                    .create();
              }
            }
          }
        }
      }*/
    }
    // Handle struct/class members independently of firstChild() gate
    if (CheckIf.IsInstanceOf(element, CheckIf.InstanceKind.STRUCT)) {
      var map = matchTokens(element, ExtendedTokenTypes.CLASS_STRUCT);
      if (map != null) {
        String token = element.getText();
        if (token != null) {
          holder.newAnnotation(HighlightSeverity.INFORMATION, map.getValue())
              .range(element.getTextRange())
              .textAttributes(map.getKey())
              .needsUpdateOnTyping(true)
              .create();
        }
      }
    }
    OCFunctionDeclaration fdecl = PsiTreeUtil.getParentOfType(element, OCFunctionDeclaration.class, false);
    if (fdecl != null) {
      LOG.info("Found function declaration");
      OCParameterList params = fdecl.getParameterList();
      if (params == null) {
        // ... existing code ...
        return;
      }
      boolean notParam = params.getParameters().stream().noneMatch(p ->
          com.intellij.psi.util.PsiTreeUtil.isAncestor(p, element, false)
      );
      if (!notParam) {
        // ... existing code ...
        return;
      }
      OCStruct enclosingStruct = PsiTreeUtil.getParentOfType(fdecl, OCStruct.class, false);
      if (enclosingStruct == null) {
        // ... existing code ...
        return;
      }
      var nameId = fdecl.getNameIdentifier();
      if (nameId != null) {
        // Compute token mapping on the actual identifier node
        var memMap = matchTokens(nameId, ExtendedTokenTypes.MEMBER_FUNCTIONS);

        // Prefer element equivalence over raw TextRange identity
        boolean isNameElement =
            com.intellij.psi.PsiManager.getInstance(element.getProject()).areElementsEquivalent(nameId, element)
            || nameId.getTextRange() != null && nameId.getTextRange().equals(element.getTextRange());

        if (memMap != null && isNameElement) {
          holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
              .range(nameId.getTextRange())
              .enforcedTextAttributes(scheme.getAttributes(memMap.getKey()))
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

  private static Map.Entry<TextAttributesKey, String> matchTokens(PsiElement el, TokenSet ts) {
    var type = getIElementType(el);
    if (type == null) return null;
    if (ts.contains(type)) return Map.entry(ExtendedTokenTypes.NAMES_AND_ATTRS_MAP.get(type).attr(), ExtendedTokenTypes.NAMES_AND_ATTRS_MAP.get(type).name());
    return null;
  }

  private static IElementType getIElementType(PsiElement el) {
    if (el == null) return null;
    var node = el.getNode();
    if (node == null) return null;
    return node.getElementType();
  }
}