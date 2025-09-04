package org.extendedhl.cpp.util.psi

import com.intellij.psi.util.PsiUtilCore
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType

fun PsiElement.getElType(): IElementType? = PsiUtilCore.getElementType(this)