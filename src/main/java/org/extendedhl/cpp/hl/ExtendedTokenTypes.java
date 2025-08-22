package org.extendedhl.cpp.hl;

//import com.jetbrains.cidr.lang.parser.OCLexerTokenTypes;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.jetbrains.cidr.lang.settings.formatter.OCLLDBCodeStyle;

import java.util.*;

public interface ExtendedTokenTypes {


  enum Ctx {
    BUILTIN_TYPES,
    CONTROL_FLOW
  }
  // A stable map of human-readable names to lexer token types.
  // LinkedHashMap preserves insertion order (useful if you ever iterate for UI).
  //Map<String, IElementType> BUILTIN_TYPES_NTT = buildNTT(Ctx.BUILTIN_TYPES);
  //Map<IElementType, String> BUILTIN_TYPES_TTN = buildTTN(Ctx.BUILTIN_TYPES);
  //Map<String, IElementType> CONTROL_FLOW_NTT = buildNTT(Ctx.CONTROL_FLOW);
  //Map<IElementType, String> CONTROL_FLOW_TTN = buildTTN(Ctx.CONTROL_FLOW);

  Map<IElementType, NamesAndAttrs> NAMES_AND_ATTRS_MAP = buildNamesAndAttrsMap();

  // TokenSet derived from the map values
  //TokenSet BUILTIN_TYPES = TokenSet.create(BUILTIN_TYPES_NTT.values().toArray(IElementType[]::new));
  //TokenSet BUILTIN_TYPES = buildTokenSet(new String[]{"void", "int", "short", "long", "bool", "float", "double", "char", "auto"});
  TokenSet BUILTIN_TYPES = TokenSet.create(new IElementType[]{
      OCltt.VOID_KEYWORD,
      OCltt.INT_KEYWORD,
      OCltt.SHORT_KEYWORD,
      OCltt.LONG_KEYWORD,
      OCltt.BOOL_CPP_KEYWORD,
      OCltt.FLOAT_KEYWORD,
      OCltt.DOUBLE_KEYWORD,
      OCltt.CHAR_KEYWORD,
      OCltt.AUTO_KEYWORD
  });
  TokenSet CONTROL_FLOW = TokenSet.create(new IElementType[]{
      OCltt.IF_KEYWORD,
      OCltt.ELSE_KEYWORD,
      OCltt.RETURN_KEYWORD,
      OCltt.DO_KEYWORD,
      OCltt.WHILE_KEYWORD,
      OCltt.CONTINUE_KEYWORD,
      OCltt.BREAK_KEYWORD,
      OCltt.FOR_KEYWORD
  });
  TokenSet CLASS_STRUCT = TokenSet.create(new IElementType[]{
      OCltt.STRUCT_KEYWORD
  });
  TokenSet MEMBER_FUNCTIONS = TokenSet.create(new IElementType[]{
      OCltt.IDENTIFIER
  });

  Map<String, TextAttributesKey> NAME_TO_ATTR = BuildNameToAttr();
  private static Map<String, TextAttributesKey> BuildNameToAttr() {
    LinkedHashMap<String, TextAttributesKey> r = new LinkedHashMap<>();
    for (var e : NAMES_AND_ATTRS_MAP.entrySet()) {
      r.put(e.getValue().name, e.getValue().attr);
    }
    return Collections.unmodifiableMap(r);
  }

  record NamesAndAttrs(String name, TextAttributesKey attr) {}

  private static Map<IElementType, NamesAndAttrs> buildNamesAndAttrsMap() {
    LinkedHashMap<IElementType, NamesAndAttrs> m = new LinkedHashMap<>();
    m.put(OCltt.VOID_KEYWORD,     new NamesAndAttrs("void",   HlTokens.EXT_VOID_KEYWORD));
    m.put(OCltt.INT_KEYWORD,      new NamesAndAttrs("int",    HlTokens.EXT_INT_KEYWORD));
    m.put(OCltt.SHORT_KEYWORD,    new NamesAndAttrs("short",  HlTokens.EXT_SHORT_KEYWORD));
    m.put(OCltt.LONG_KEYWORD,     new NamesAndAttrs("long",   HlTokens.EXT_LONG_KEYWORD));
    m.put(OCltt.BOOL_CPP_KEYWORD, new NamesAndAttrs("bool",   HlTokens.EXT_BOOL_KEYWORD));
    m.put(OCltt.FLOAT_KEYWORD,    new NamesAndAttrs("float",  HlTokens.EXT_FLOAT_KEYWORD));
    m.put(OCltt.DOUBLE_KEYWORD,   new NamesAndAttrs("double", HlTokens.EXT_DOUBLE_KEYWORD));
    m.put(OCltt.CHAR_KEYWORD,     new NamesAndAttrs("char",   HlTokens.EXT_CHAR_KEYWORD));
    m.put(OCltt.AUTO_KEYWORD,     new NamesAndAttrs("auto",   HlTokens.EXT_AUTO_KEYWORD));
    m.put(OCltt.IF_KEYWORD,       new NamesAndAttrs("if",       HlTokens.EXT_IF_KEYWORD));
    m.put(OCltt.ELSE_KEYWORD,     new NamesAndAttrs("else",     HlTokens.EXT_ELSE_KEYWORD));
    m.put(OCltt.RETURN_KEYWORD,   new NamesAndAttrs("return",   HlTokens.EXT_RETURN_KEYWORD));
    m.put(OCltt.DO_KEYWORD,       new NamesAndAttrs("do",       HlTokens.EXT_DO_KEYWORD));
    m.put(OCltt.WHILE_KEYWORD,    new NamesAndAttrs("while",    HlTokens.EXT_WHILE_KEYWORD));
    m.put(OCltt.CONTINUE_KEYWORD, new NamesAndAttrs("continue", HlTokens.EXT_CONTINUE_KEYWORD));
    m.put(OCltt.BREAK_KEYWORD,    new NamesAndAttrs("break",    HlTokens.EXT_BREAK_KEYWORD));
    m.put(OCltt.FOR_KEYWORD,      new NamesAndAttrs("for",      HlTokens.EXT_FOR_KEYWORD));
    m.put(OCltt.STRUCT_KEYWORD,   new NamesAndAttrs("struct", HlTokens.EXT_STRUCT_KEYWORD));
    m.put(OCltt.IDENTIFIER,       new NamesAndAttrs("member function", HlTokens.EXT_MEMBER_FUNCTION));

    return Collections.unmodifiableMap(m);
  }
}
