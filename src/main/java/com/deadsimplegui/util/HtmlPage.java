package com.deadsimplegui.util;

import com.deadsimplegui.util.resource.InternalResourceLogic;
import com.deadsimplegui.util.resource.ResourceLogic;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public class HtmlPage {
  private final URI uri;
  private final ResourceLogic resourceLogic;
  private byte[] html;

  public HtmlPage(URI uri, ResourceLogic resourceLogic) {
    this.resourceLogic = resourceLogic;
    this.uri = uri;
  }

  public HtmlPage(GuiError error) {
    URI localHost = null;
    try{
      localHost = new URI("http://localhost/");
    } catch (URISyntaxException e) {
      //this will never happen.
    }
    this.uri = localHost;
    this.resourceLogic = new InternalResourceLogic();
    this.html = error.getHtml(new HashMap<>()).getBytes();
  }

  public URI getUri() {
    return uri;
  }

  public byte[] getHtml() {
    if (html == null) {
      html = resourceLogic.get(uri);
    }
    return html;
  }

}
