package org.extendedhl.cpp

import com.intellij.lang.Language
import com.intellij.lang.LanguageAnnotators
import com.intellij.lang.annotation.Annotator

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class RegisterCppAnnotator : ProjectActivity {
    private val log = Logger.getInstance(RegisterCppAnnotator::class.java)

    override suspend fun execute(project: Project) {
        val cppLang = Language.getRegisteredLanguages().firstOrNull { it.id.equals("C++", ignoreCase = false) }
        if (cppLang == null) {
            log.warn("C++ Language not found; skipping annotator registration")
            return
        }
        val annotator: Annotator = CppAnnotator()
        LanguageAnnotators.INSTANCE.addExplicitExtension(cppLang, annotator)
        log.info("Registered MyCppAnnotator for Language=${cppLang.id} (${cppLang.javaClass.name})")
    }
}