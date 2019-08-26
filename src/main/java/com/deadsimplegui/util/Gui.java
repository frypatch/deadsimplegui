package com.deadsimplegui.util;

import java.awt.Image;
import java.net.URI;
import java.net.URL;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Stack;
import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import com.deadsimplegui.util.render.DefaultHtmlRenderer;
import com.deadsimplegui.util.resource.ResourceLogic;

/**
 * The `Gui` class exists to wrap the Java Swing classes into a well-defined HTML-driven GUI
 * platform. The GUI will render the provided HTML via the HTML 3.1 specification.
 *
 * Hyperlinks with a hostname of localhost will render the string returned from the `getHtml`
 * function as HTML from the first found Page class in a registered package that is annotated with
 * a UrlBinding value that matches the hyperlink's path. If the hyperlink has a hostname of
 * localhost but no UrlBinging exists a page not found will be returned. When a hyperlink has a
 * hostname other than localhost it will be considered an external resource and will be retrieved
 * from the internet before rendering.
 *
 * Images with a hostname of localhost will be considered internal resources that are packaged with
 * the application and their data will be retrieved from the application's bundled resources using
 * the image's path. If the image had a hostname of localhost but no application resource exists at
 * the image's path then a broken image icon will be rendered instead. When an image has a hostname
 * other than localhost it will be considered an external resource and will be retrieved from the
 * internet before rendering.
 */
public class Gui {
  private final String title;
  private final URI homePage;
  private final ResourceLogic externalResourceLogic;
  private final ResourceLogic internalResourceLogic;
  private JEditorPane editor;
  private Stack<HtmlPage> history = new Stack<>();
  private static final Dictionary<URL, Image> IMAGE_CACHE = new Hashtable<>();

  private Gui(
      String title,
      URI homePage,
      ResourceLogic externalResourceLogic,
      ResourceLogic internalResourceLogic
  ) {
    this.title = title;
    this.homePage = homePage;
    this.externalResourceLogic = externalResourceLogic;
    this.internalResourceLogic = internalResourceLogic;
  }

  public static Gui getInstance(
      String title,
      URI homePage,
      ResourceLogic externalResourceLogic,
      ResourceLogic internalResourceLogic) {
    return new Gui(title, homePage, externalResourceLogic, internalResourceLogic);
  }

  /**
   * The `launch` function exists to provide a well-defined process for constructing and launching
   * a GUI window from the configured instance's settings.
   */
  public void launch() {
    editor = new JEditorPane();
    editor.setContentType("text/html");
    editor.setEditable(false);
    ((HTMLEditorKit) editor.getEditorKit()).setAutoFormSubmission(false);
    editor.addHyperlinkListener(event -> GuiLinkListener.load(this, event, externalResourceLogic, internalResourceLogic));

    //add ability to scroll
    JScrollPane scrollbar = new JScrollPane(editor);
    JFrame jFrame = new JFrame();
    jFrame.add(scrollbar);

    //make sure the program exits when the frame closes
    jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //This will center the JFrame in the middle of the screen
    jFrame.setLocationRelativeTo(null);
    jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    jFrame.setVisible(true);
    if (title != null) {
      jFrame.setTitle(title);
    }
    GuiLinkListener.load(this, homePage, externalResourceLogic, internalResourceLogic);
  }

  /**
   * The `executeRender` function will render the supplied HTML in this GUI instance.
   *
   * @param html to render.
   */
  void executeRender(HtmlPage html) {
    try {
      history.push(html);
      new DefaultHtmlRenderer().execute(html, this);
    } catch(Exception ex1) {
      try {
        HtmlPage errorHtml = new HtmlPage(new GuiError(ex1.getLocalizedMessage(), ex1));
        new DefaultHtmlRenderer().execute(errorHtml, this);
      } catch(Exception ex2) {
        //This should never happen.
        editor.setText(new GuiError(ex2.getLocalizedMessage(), ex2).getHtml(new HashMap<>()));
      }
    }
  }

  public void setText(String html) {
    editor.setText(html);
  }

  public ResourceLogic getExternalResourceLogic() {
    return this.externalResourceLogic;
  }

  public ResourceLogic getInternalResourceLogic() {
    return this.internalResourceLogic;
  }

  public void putImage(URL url, Image image) {
    if (editor.getDocument().getProperty("imageCache") == null) {
      editor.getDocument().putProperty("imageCache", IMAGE_CACHE);
    }
    IMAGE_CACHE.put(url, image);
  }

  public long getImageCacheSize() {
    return IMAGE_CACHE.size();
  }

  HtmlPage getCurrent() {
    return history.peek();
  }

}
