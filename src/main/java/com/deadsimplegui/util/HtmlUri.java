package com.deadsimplegui.util;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * The `HtmlUri` class exists to provide the logic to determine the URI from a supplied link within
 * the supplied html page. Note that supplying the html page is necessary so that the URI can be
 * reconstructed from a partial or relative link.
 */
public class HtmlUri {
  private final URI uri;

  private HtmlUri(URI uri) {
    this.uri = uri;
  }

  public static HtmlUri getInstance(String link, HtmlPage html) throws URISyntaxException {
    if(link.startsWith("//")) {
      link = html.getUri().getScheme() + ":" + link;
    }
    if(link.startsWith("/")) {
      String baseUri = html.getUri().getScheme() + "://" + html.getUri().getHost();
      if(html.getUri().getPort() != -1) {
        baseUri += ":" + html.getUri().getPort();
      }
      link = baseUri + link;
    }
    if(link.startsWith("#")) {
      String baseUri = html.getUri().getScheme() + "://" + html.getUri().getHost();
      if(html.getUri().getPort() != -1) {
        baseUri += ":" + html.getUri().getPort();
      }
      link = baseUri + html.getUri().getPath() + link;
    }
    if(!link.contains("://")) {
      String currentUri = html.getUri().getScheme() + "://" + html.getUri().getHost();
      if(html.getUri().getPort() != -1) {
        currentUri += ":" + html.getUri().getPort();
      }
      if(html.getUri().getPath().contains("/") && html.getUri().getPath().substring(html.getUri().getPath().lastIndexOf("/")).contains(".")) {
        currentUri += html.getUri().getPath().substring(0, html.getUri().getPath().lastIndexOf("/"));
      } else {
        currentUri += html.getUri().getPath();
      }
      if(!currentUri.endsWith("/")) {
        currentUri += "/";
      }
      link = currentUri + link;
    }
    return new HtmlUri(new URI(link));
  }

  public String getHost() {
    return uri.getHost();
  }

  public URI getUri() {
    return uri;
  }

}
