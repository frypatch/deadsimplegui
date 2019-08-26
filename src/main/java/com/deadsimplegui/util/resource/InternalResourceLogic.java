package com.deadsimplegui.util.resource;

import java.awt.Image;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.imageio.ImageIO;

/**
 * The `SimpleInternalResources` class provides logic for accessing internal resources.
 */
public class InternalResourceLogic implements ResourceLogic {

  @Override
  public byte[] get(URI uri) {
    Map params = new HashMap<>();
    if(uri.getQuery() != null && !uri.getQuery().isEmpty()) {
      Stream.of(uri.getQuery().split("&"))
          .filter(param -> param.contains("="))
          .map(String::trim)
          .map(param -> param.split("="))
          .forEach(param -> {
            try {
              System.out.println(param[0]);
              params.put(
                  URLDecoder.decode(param[0], StandardCharsets.UTF_8.toString()),
                  URLDecoder.decode(param[1], StandardCharsets.UTF_8.toString())
              );
            } catch (UnsupportedEncodingException e) {
              //swallow
            }
          });
    }
    Optional<Page> page = PageScanner.find(uri.getPath(), UrlBinding.class);
    if(page.isPresent()){
      return page.get().getHtml(params).getBytes();
    }
    throw new RuntimeException("Could not locate any class existing in a registered package that "
        + "implements `" + Page.class.getCanonicalName() + "` and is bound to path `"
        + uri.getPath() + "`. Registered action packages are: "
        + String.join(", ", AnnotationScanner.getRegisteredPackages(UrlBinding.class)
        .stream()
        .map(registeredPackage -> "`" + registeredPackage.getPackageName() + "`")
        .collect(Collectors.toSet())));
  }

  @Override
  public byte[] post(URI uri, Map<String, String> params) {
    Optional<Page> page = PageScanner.find(uri.getPath(), UrlBinding.class);
    if(page.isPresent()){
      return page.get().getHtml(params).getBytes();
    }
    throw new RuntimeException("Could not locate any class existing in a registered package that "
        + "implements `" + Page.class.getCanonicalName() + "` and is bound to path `"
        + uri.getPath() + "`. Registered action packages are: "
        + String.join(", ", AnnotationScanner.getRegisteredPackages(UrlBinding.class)
        .stream()
        .map(registeredPackage -> "`" + registeredPackage.getPackageName() + "`")
        .collect(Collectors.toSet())));
  }

  @Override
  public Image readImage(URI uri) throws IOException {
    return ImageIO.read(InternalResourceLogic.class.getResourceAsStream(uri.getPath()));
  }

}
