package com.deadsimplegui.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.FormSubmitEvent;
import com.deadsimplegui.util.resource.ResourceLogic;

/**
 * The `GuiLinkListener` class exists to provide a well-defined process for what happens when a
 * hyperlink is clicked on in the GUI.
 */
class GuiLinkListener {

  static void load(Gui gui, HyperlinkEvent event, ResourceLogic externalResourceLogic, ResourceLogic internalResourceLogic) {
    if (event.getEventType() != HyperlinkEvent.EventType.ACTIVATED) {
      return;
    }
    HtmlUri guiUri;
    try {
      guiUri = HtmlUri.getInstance(URLDecoder.decode(event.getURL().getQuery().substring("url=".length()), Charset.defaultCharset().toString()), gui.getCurrent());
    } catch (UnsupportedEncodingException | URISyntaxException e) {
      gui.executeRender(new HtmlPage(new GuiError(e.getLocalizedMessage(), e)));
      return;
    }
    if(!guiUri.getHost().equals("localhost")) {
      HtmlPage history = new HtmlPage(guiUri.getUri(), externalResourceLogic);
      gui.executeRender(history);
      return;
    }
    Map<String, String> params = new HashMap<>();
    try {
      Arrays.stream(((FormSubmitEvent) event).getData().split("&"))
          .filter(param -> param.contains("="))
          .map(String::trim)
          .map(param -> param.split("="))
          .forEach(param -> {
            try {
              params.put(
                  URLDecoder.decode(param[0], StandardCharsets.UTF_8.toString()),
                  URLDecoder.decode(param[1], StandardCharsets.UTF_8.toString())
              );
            } catch (UnsupportedEncodingException e) {
              //swallow
            }
          });
    } catch (Exception e){
      //swallow, no params
    }
    load(gui, internalResourceLogic, guiUri.getUri());
  }

  static void load(Gui gui, URI uri, ResourceLogic externalResourceLoader, ResourceLogic internalResourceLogic) {
    if (!uri.getHost().equals("localhost")) {
      gui.executeRender(new HtmlPage(uri, externalResourceLoader));
    } else{
      gui.executeRender(new HtmlPage(uri, internalResourceLogic));
    }
  }

  private static void load(Gui gui, ResourceLogic internalResourceLogic, URI uri) {
    try {
      gui.executeRender(new HtmlPage(uri, internalResourceLogic));
    } catch (Exception e) {
      gui.executeRender(new HtmlPage(new GuiError(e.getLocalizedMessage(), e)));
    }
  }

}
