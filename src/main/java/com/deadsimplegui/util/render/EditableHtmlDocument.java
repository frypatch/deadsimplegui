package com.deadsimplegui.util.render;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

/**
 * The `EditableHtmlDocument` exists to provide a way for manipulating HTML document attributes and
 * elements in the HtmlRenderer.
 */
public class EditableHtmlDocument extends HTMLDocument {

  /**
   * The `EditableHtmlDocument` class constructor.
   *
   * @param ss The style sheet.
   */
  public EditableHtmlDocument(StyleSheet ss) {
    super(ss);
  }

  /**
   * The `addAttribute` function provides a process for editing the supplied attribute within the
   * supplied attribute set to the supplied value. This is needed so that we can change the links
   * and image sources to reference `http://127.0.0.1/`.
   *
   * @param attributeSet The attribute set to modify.
   * @param attribute The attribute in the attribute set to modify.
   * @param value The value to modify the attribute to.
   */
  public void addAttribute(AttributeSet attributeSet, HTML.Attribute attribute, String value) {
    try {
      writeLock();
      ((MutableAttributeSet)attributeSet).removeAttribute(attribute);
      ((MutableAttributeSet)attributeSet).addAttribute(attribute, value);
    } finally {
      writeUnlock();
    }
  }

}
