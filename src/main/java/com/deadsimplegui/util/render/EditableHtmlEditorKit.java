package com.deadsimplegui.util.render;

import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 * The `EditableHtmlEditorKit` class exists so that we are able to access an EditableHtmlDocument
 * which will allow us to be able to edit attributes within the document.
 */
public class EditableHtmlEditorKit extends HTMLEditorKit {

  @Override
  public Document createDefaultDocument() {
    StyleSheet styles = getStyleSheet();
    StyleSheet ss = new StyleSheet();

    ss.addStyleSheet(styles);

    HTMLDocument doc = new EditableHtmlDocument(ss);
    doc.setParser(getParser());
    doc.setAsynchronousLoadPriority(4);
    doc.setTokenThreshold(100);
    return doc;
  }

}
