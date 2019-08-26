package com.deadsimplegui.util.resource;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * The `ExternalResourceLogic` class provides extremely rudimentary logic for accessing
 * external resources. It is provided purely as a demonstration implementation, is not expected to
 * work in most cases, and leaks the computer's IP address during DNS look ups.
 */
public class ExternalResourceLogic implements ResourceLogic {

  @Override
  public byte[] get(URI uri) {
    StringBuilder html = new StringBuilder();
    try (Reader reader = new InputStreamReader(uri.toURL().openStream())) {
      for(int data; (data = reader.read()) != -1;) {
        html.append((char)data);
      }
    } catch (IOException e) {
      throw new RuntimeException("Unable to retrieve data from " + uri, e);
    }
    return html.toString().getBytes();
  }

  @Override
  public byte[] post(URI uri, Map<String, String> params) {
    StringBuilder html = new StringBuilder();
    try (Reader reader = new InputStreamReader(uri.toURL().openStream())) {
      for(int data; (data = reader.read()) != -1;) {
        html.append((char)data);
      }
    } catch (IOException e) {
      throw new RuntimeException("Unable to retrieve data from " + uri, e);
    }
    return html.toString().getBytes();
  }

  @Override
  public Image readImage(URI uri) throws IOException {
    return ImageIO.read(uri.toURL());
  }

}
