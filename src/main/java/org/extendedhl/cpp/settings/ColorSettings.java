package org.extendedhl.cpp.settings;

import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.PlainSyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import org.extendedhl.cpp.hl.HighlighterTokens;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.Map;

public class ColorSettings implements ColorSettingsPage {

  private final AttributesDescriptor[] descriptors = new AttributesDescriptor[]{
      //new AttributesDescriptor("Unsafe Identifier", ClionChlHighlighterKeys.UNSAFE_IDENTIFIER),
      new AttributesDescriptor("Built-in Type", HighlighterTokens.BUILTIN_TYPE),
      new AttributesDescriptor("Include path", HighlighterTokens.INCLUDE_DIRECTIVE)
  };

  @Override
  @NotNull
  public String getDisplayName() {
    return "C/C++ Extended";
  }

  @Override
  @Nullable
  public Icon getIcon() {
    return null;
  }

  @Override
  @NotNull
  public SyntaxHighlighter getHighlighter() {
    return new PlainSyntaxHighlighter();
  }

  @Override
  @NotNull
  public AttributesDescriptor[] getAttributeDescriptors() {
    return descriptors;
  }

  @Override
  @NotNull
  public ColorDescriptor[] getColorDescriptors() {
    return ColorDescriptor.EMPTY_ARRAY;
  }

  @Override
  @NotNull
  public String getDemoText() {
    return """
                // Demo: built-in types
                <bt>char</bt> c = 'a';
                <bt>double</bt> d = 0.0;
                <bt>long</bt> <bt>long</bt> ll = 0;
                const <bt>int</bt>* p = <bt>nullptr</bt>;
                """.trim();
  }

  @Override
  @Nullable
  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
    return Map.of(
        "bt", HighlighterTokens.BUILTIN_TYPE
    );
  }
}