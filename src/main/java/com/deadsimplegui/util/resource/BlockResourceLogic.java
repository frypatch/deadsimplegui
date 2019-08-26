package com.deadsimplegui.util.resource;

import java.awt.Image;
import java.net.URI;
import java.util.Map;

/**
 * The `BlockResources` class provides logic to block the GUI from loading resources. In most
 * situations this implementation will be redundant.
 */
public class BlockResourceLogic implements ResourceLogic {

  @Override
  public byte[] get(URI uri) {
    return null;
  }

  @Override
  public byte[] post(URI uri, Map<String, String> params) {
    return null;
  }

  @Override
  public Image readImage(URI uri) {
    return null;
  }

}
