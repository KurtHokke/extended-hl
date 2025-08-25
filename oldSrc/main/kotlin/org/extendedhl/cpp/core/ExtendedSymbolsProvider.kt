package org.extendedhl.cpp.core

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.jetbrains.cidr.radler.protocol.RadExternalSymbolsProvider

class MyEntryPointProvider : RadExternalSymbolsProvider {

  override fun hasEntryPoint(file: PsiFile): Boolean? {
    // Fast checks first (file type/language); return null if not applicable
    val text = file.viewProvider.document?.charsSequence ?: return null
    // Extremely naive example; replace with proper PSI checks
    return if (Regex("""\b(int|void)\s+main\s*\(""").containsMatchIn(text)) true else null
  }

  override fun isEntryPointOffset(
    project: Project,
    file: VirtualFile,
    offset: Int
  ): Boolean? {
    // Map VF -> PSI and check whether offset is at the main() identifier node
    return null
  }

  override fun isInEntryPoint(
    project: Project,
    file: VirtualFile,
    offset: Int
  ): Boolean? {
    // Check whether offset lies within the entry point function’s body range
    return null
  }
}
