package com.deadsimplegui.util.resource;

import java.util.Optional;

public class PageScanner {

  public static Optional<Page> find(String path, Class annotation) {
    return AnnotationScanner
        .getRegisteredPackages(annotation)
        .stream()
        .flatMap(annotationScanner -> annotationScanner.getClasses().stream())
        .map(aClass -> {
          Object urlBinding;
          try {
            urlBinding = aClass.getDeclaredConstructor().newInstance();
          } catch(Exception e) {
            throw new RuntimeException("Unable to construct url binded class "
                + aClass.getCanonicalName(), e);
          }
          return urlBinding;
        })
        .filter(urlBinding -> urlBinding instanceof Page)
        .map(urlBinding -> (Page) urlBinding)
        .filter(page -> page.getClass().getAnnotation(UrlBinding.class).value().equals(path))
        .findFirst();
  }

}
