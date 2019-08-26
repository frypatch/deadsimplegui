package com.deadsimplegui.util.resource;

import java.awt.Image;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * The `ResourceLogic` interface exists to provide an overridable structure for obtaining
 * resources from sources.
 */
public interface ResourceLogic {

  /**
   * The `get` function's implementation should send a GET request to the supplied URI and return
   * the raw response body.
   *
   * @param uri The URI to send the post request to.
   * @return The raw response body.
   */
  byte[] get(URI uri);

  /**
   * The `post` function's implementation should send a POST request to the supplied URI with the
   * defined parameters and return the raw response body.
   *
   * @param uri The URI to send the post request to.
   * @param params The parameters to add to the post request body.
   * @return The raw response body.
   */
  byte[] post(URI uri, Map<String, String> params);

  /**
   * The `readImage` function's implementation should define the process for obtaining the raw image
   * data from the supplied URI and then converting that raw image data into a java Image object
   * for consumption.
   *
   * @param uri to obtain the image data from.
   * @return an image obtained from the supplied image source
   * @throws IOException when unable to return the image.
   */
  Image readImage(URI uri) throws IOException;

}
