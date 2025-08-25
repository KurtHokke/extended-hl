package org.extendedhl.cpp

import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import com.jetbrains.rider.cpp.fileType.lexer.CppTokenTypes
import com.jetbrains.rider.model.ElementType



object Utils {
  val getChildIfParentOfTokenSet: TokenSet
    get() = TokenSet.create(CppTokenTypes.CLASS_KEYWORD, CppTokenTypes.NAMESPACE_CPP_KEYWORD)

  fun ASTgetChildByType(element: PsiElement, type: IElementType): PsiElement? {
    if (getChildIfParentOfTokenSet.contains(type)) {
      return element.node.findChildByType(type)?.psi
    }
    return null
  }
}