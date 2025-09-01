package org.extendedhl.cpp.util.psi

import com.intellij.psi.util.PsiUtilCore
import com.intellij.psi.PsiElement

fun PsiElement.getElType() = PsiUtilCore.getElementType(this)