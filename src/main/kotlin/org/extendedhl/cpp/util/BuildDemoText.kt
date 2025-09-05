package org.extendedhl.cpp.util

import com.jetbrains.rider.cpp.fileType.lexer.CppLexer
import com.intellij.lexer.Lexer
import com.intellij.psi.tree.IElementType
import com.jetbrains.rider.cpp.fileType.lexer.CppTokenTypes

import org.extendedhl.cpp.config.HlConfigProvider

class BuildDemoText {
  private val log = org.extendedhl.cpp.logging.logger<BuildDemoText>()
  fun build(cppCode: String): String {

    val lexer: Lexer = CppLexer()
    lexer.start(cppCode)

    val htmlOutput = StringBuilder()
    var lastEnd = 0

    // Process each token
    while (lexer.tokenType != null) {
      val tokenType: IElementType? = lexer.tokenType
      val tokenStart = lexer.tokenStart
      val tokenEnd = lexer.tokenEnd

      val tokenText = cppCode.substring(tokenStart, tokenEnd)
      //#if debug
      log.debug("Token: $tokenType, $tokenText, $tokenStart-$tokenEnd")
      //#endif
      // Preserve whitespace or text before the token
      if (tokenStart > lastEnd) {
        val beforeText = cppCode.substring(lastEnd, tokenStart)
        htmlOutput.append(beforeText)
      }

      when (val htmlTag = getHtmlTagForToken(tokenType, tokenText)) {
        null -> htmlOutput.append(tokenText)
        else -> htmlOutput.append("<$htmlTag>$tokenText</$htmlTag>")
      }
      lastEnd = tokenEnd
      lexer.advance()
    }
    // Append any remaining text after the last token
    if (lastEnd < cppCode.length) {
      val remainingText = cppCode.substring(lastEnd)
      htmlOutput.append(remainingText)
    }
    //#if debug
    log.debug("Demo text:\n$htmlOutput")
    //#endif
    return htmlOutput.toString()
  }
  private fun getHtmlTagForToken(tokenType: IElementType?, tokenText: String): String? {
    if (tokenType == null) return null
    val config = HlConfigProvider.configsByType[tokenType] ?: return null
    return if (config.checkConds(tokenType, tokenText)) {
      config.name
    } else {
      null
    }
  }
}

