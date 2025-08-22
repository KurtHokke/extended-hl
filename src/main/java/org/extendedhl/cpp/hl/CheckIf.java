package org.extendedhl.cpp.hl;

import com.intellij.psi.PsiElement;
import com.jetbrains.cidr.lang.psi.*;

public class CheckIf {
  enum InstanceKind {
    TYPE,
    CONTROL_FLOW,
    STRUCT,
    FUNCTION
  }
  static boolean IsInstanceOf(PsiElement el, InstanceKind kind) {
    switch (kind) {
      case TYPE:
        if (el.getParent() instanceof OCTypeElement) return true;
        break;
      case CONTROL_FLOW:
        if (el.getParent() instanceof OCIfStatement
         || el.getParent() instanceof OCReturnStatement
         || el.getParent() instanceof OCForStatement
         || el.getParent() instanceof OCBreakStatement
         || el.getParent() instanceof OCContinueStatement
         || el.getParent() instanceof OCDoWhileStatement) return true;
        break;
      case STRUCT:
        if (el.getParent() instanceof OCStruct) return true;
        break;
      default:
        return false;
    }
    return false;
  }
}
