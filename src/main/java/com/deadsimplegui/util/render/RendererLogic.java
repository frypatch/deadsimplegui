package com.deadsimplegui.util.render;

import com.deadsimplegui.util.Gui;
import com.deadsimplegui.util.HtmlPage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.swing.text.BadLocationException;

/**
 * The `RendererLogic` interface exists to provide an overridable structure for rendering the
 * supplied HTML page in the supplied GUI.
 */
public interface RendererLogic {

  /**
   * The `execute` function will render the supplied HTML page to the supplied GUI and will
   * retrieve any necessary external resources using the supplied external resource logic.
   *
   * @param htmlPage The HTML page to load into the supplied GUI.
   * @param gui The GUI to load the supplied HTML page into.
   * @throws IOException when unable to retrieve a resource.
   * @throws UnsupportedEncodingException when unable to proxy a link through
   *                                      `http://127.0.0.1/?url=` to prevent immediate DNS look
   *                                      ups.
   * @throws BadLocationException when unable to tokenize the supplied HTML.
   */
  void execute(HtmlPage htmlPage, Gui gui)
      throws IOException, UnsupportedEncodingException, BadLocationException;

}
