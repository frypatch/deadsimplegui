package com.deadsimplegui.util;

import com.deadsimplegui.util.resource.Page;
import java.util.Map;

public class GuiError implements Page {
  private final String message;
  private final java.lang.Exception e;

  GuiError(String message, java.lang.Exception e) {
    this.message = message;
    this.e = e;
  }

  @Override
  public String getHtml(Map<String, String> params) {
      StringBuilder html = new StringBuilder();
      html.append("<h1>ERROR</h1>");
    if (message != null && !message.trim().isEmpty()) {
      html.append("<h2>").append(message).append("</h2>");
    }
    if (e != null) {
      for (StackTraceElement ste : e.getStackTrace()) {
        html.append("<div>").append(ste).append("</div>");
      }
    }
    return html.toString();
  }

}
