package com.deadsimplegui.util.render;

import com.deadsimplegui.util.Gui;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import com.deadsimplegui.util.HtmlPage;

/**
 * The `DefaultHtmlRenderer` exists to provide a general catch-all implementation for rendering the
 * supplied HTML page to the supplied GUI.
 */
public class DefaultHtmlRenderer implements RendererLogic {

  @Override
  public void execute(HtmlPage htmlPage, Gui gui) throws IOException, BadLocationException {
    String html = new String(htmlPage.getHtml());
    html = removeJavaScript(html);
    html = removeMetaElements(html);

    EditableHtmlEditorKit htmlKit = new EditableHtmlEditorKit();
    EditableHtmlDocument htmlDoc = (EditableHtmlDocument) htmlKit.createDefaultDocument();
    htmlKit.read(new StringReader(html), htmlDoc, 0);

    proxyLinks(htmlDoc);
    cacheImages(htmlDoc, htmlPage, gui);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    htmlKit.write(baos, htmlDoc, 0, Integer.MAX_VALUE);
    gui.setText(baos.toString(Charset.defaultCharset().toString()));
  }

  /**
   * The `proxyLinks` function wraps all links in the supplied tokenized HTML document into a
   * "http://127.0.0.1/?url=" proxy. This should prevent the links from leaking the computers IP
   * address through automatic DNS look ups.
   * <p>
   * This is needed because the java URL class does an automatic DNS look up and the HyperlinkEvent
   * instances returned from the HyperlinkEventListener contain a URL object. This is a transparent
   * way to prevent that HyperlinkEvent from leaking the computers IP through an automatic DNS
   * lookup.
   *
   * @param htmlDoc The tokenized HTML document to proxy all of its links.
   * @throws UnsupportedEncodingException When unable to url-encode the proxy link parameter.
   */
  private static void proxyLinks(EditableHtmlDocument htmlDoc) throws UnsupportedEncodingException {
    for (HTMLDocument.Iterator iterator = htmlDoc.getIterator(HTML.Tag.A); iterator.isValid(); iterator.next()) {
      MutableAttributeSet attributes = (MutableAttributeSet) iterator.getAttributes();
      String originalHref = (String) attributes.getAttribute(HTML.Attribute.HREF);
      if(originalHref != null && !originalHref.startsWith("http://127.0.0.1/?url=")) {
        String proxiedHref = "http://127.0.0.1/?url=" + URLEncoder.encode(originalHref, Charset.defaultCharset().toString());
        htmlDoc.addAttribute(attributes, HTML.Attribute.HREF, proxiedHref);
      }
    }
  }

  /**
   * The `cacheImages` function will cache all images in the supplied tokenized HTML document so
   * that they can be render-able in the GUI and will change their image link to reference an
   * image at http://127.0.0.1/###.img to prevent leaking the computers IP through an automatic DNS
   * lookup.
   *
   * @param htmlDoc The tokenized HTML document to proxy all of its links.
   * @param htmlPage The HTML page to load into the supplied GUI.
   * @param gui The GUI to load the supplied HTML page into.
   * @throws IOException when unable to retrieve a resource.
   */
  private static void cacheImages(
      EditableHtmlDocument htmlDoc,
      HtmlPage htmlPage,
      Gui gui
  ) throws IOException {
    for (HTMLDocument.Iterator iterator = htmlDoc.getIterator(HTML.Tag.IMG); iterator.isValid(); iterator.next()) {
      MutableAttributeSet attributes = (MutableAttributeSet) iterator.getAttributes();
      String originalSrc = (String) attributes.getAttribute(HTML.Attribute.SRC);
      String proxiedSrc = ImageRenderer.loadImage(originalSrc, htmlPage, gui);
      htmlDoc.addAttribute(attributes, HTML.Attribute.SRC, proxiedSrc);
    }
  }

  /**
   * The `removeJavaScript` function exists to remove all javascript that exists within the SCRIPT
   * elements as it will not render anyways.
   *
   * @param rawHtml The HTML document to remove the javascript from.
   * @return The HTML document with all javascript removed.
   */
  private static String removeJavaScript(String rawHtml) {
    boolean inScript = false;
    StringBuilder html = new StringBuilder();
    for(int i=0; i<rawHtml.length(); i++) {
      if (inScript && i-9 > -1) {
        String buffer = rawHtml.substring(i-9, i);
        if (buffer.equalsIgnoreCase("</script>")) {
          inScript = false;
        }
      }
      if (i+8 < rawHtml.length()) {
        String buffer = rawHtml.substring(i, i+8);
        if (buffer.equalsIgnoreCase("<script>") || buffer.equalsIgnoreCase("<script ")) {
          inScript = true;
        }
      }
      if (!inScript) {
        html.append(rawHtml.charAt(i));
      }
    }
    return html.toString();
  }

  /**
   * The `removeMetaElements` is necessary because the GUI refuses to render HTML documents that
   * have un-closed META elements and it seems that most META elements have no closing tag.
   *
   * @param rawHtml The HTML document to remove the META tags from.
   * @return The HTML document with all META elements removed.
   */
  private static String removeMetaElements(String rawHtml) {
    boolean inMeta = false;
    boolean inHead = false;
    StringBuilder html = new StringBuilder();
    for(int i=0; i < rawHtml.length(); i++) {
      if (inHead && i - 7 > -1) {
        String buffer = rawHtml.substring(i-7, i);
        if(buffer.equalsIgnoreCase("</head>")) {
          inHead = false;
        }
      }
      if (i + 6 < rawHtml.length()) {
        String buffer = rawHtml.substring(i, i+6);
        if(buffer.equalsIgnoreCase("<head>") || buffer.equalsIgnoreCase("<head ")) {
          inHead = true;
        }
      }
      if (inHead && inMeta) {
        String buffer = rawHtml.substring(i-1, i);
        if(buffer.equals(">")) {
          inMeta = false;
        }
      }
      if (inHead && i + 6 < rawHtml.length()) {
        String buffer = rawHtml.substring(i, i+6);
        if(buffer.equalsIgnoreCase("<meta ")) {
          inMeta = true;
        }
      }
      if (!inMeta) {
        html.append(rawHtml.charAt(i));
      }
    }
    return html.toString();
  }

}
