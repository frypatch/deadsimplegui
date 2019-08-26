package com.deadsimplegui.util.resource;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * The `AnnotationScanner` class exists to provide a well-defined process for recursively scanning
 * the instance's configured package name for the instance's configured annotation. This is
 * necessary for the PageScanner class to get all the classes recursively from a package that are
 * annotated with the UrlBinding class and is generalized for the reader's benefit.
 */
public class AnnotationScanner {
  private static final Map<String, AnnotationScanner> INSTANCES = new HashMap<>();
  private static final Charset UTF_8 = StandardCharsets.UTF_8;
  private final Class annotation;
  private final String packageName;
  private ArrayList<Class<?>> classes;
  private RuntimeException exception;

  /**
   * The `AnnotationScanner` class constructor. This is private to encourage the use of the static
   * constructor.
   *
   * @param packageName to scan for classes annotated with the supplied annotation.
   * @param annotation to search for in the supplied package.
   */
  private AnnotationScanner (String packageName, Class annotation) {
    this.packageName = packageName;
    this.annotation = annotation;
    this.classes = null;
    this.exception = null;
  }

  /**
   * The `AnnotationScanner` static class constructor.
   *
   * @param packageName to scan for classes annotated with the supplied annotation.
   * @param annotation to search for in the supplied package.
   * @return a newly constructed AnnotationScanner instance.
   */
  public static void registerPackage(String packageName, Class annotation) {
    if (!INSTANCES.containsKey(packageName)) {
      INSTANCES.put(packageName, new AnnotationScanner(packageName, annotation));
    }
  }

  public static List<AnnotationScanner> getRegisteredPackages(Class annotation) {
    return INSTANCES
        .values()
        .stream()
        .filter(annotationScanner -> annotation.equals(annotationScanner.annotation))
        .collect(Collectors.toList());
  }

  public static List<Class<?>> find(Class annotation) {
    return INSTANCES
        .values()
        .stream()
        .filter(annotationScanner -> annotation.isInstance(annotationScanner.annotation))
        .flatMap(annotationScanner -> annotationScanner.getClasses().stream())
        .collect(Collectors.toList());
  }

  /**
   * The `isAnnotatedWith` function exists to deduplicate the code needed to check to see if the
   * supplied class is annotated with this AnnotationScanner instance's configured annotation class.
   *
   * @param aClass the class to check.
   * @return true if annotated with configured annotation class.
   */
  private boolean isAnnotatedWith (Class<?> aClass) {
    for (Annotation declaredAnnotation : aClass.getDeclaredAnnotations()) {
      if(annotation.isInstance(declaredAnnotation)) {
        return true;
      }
    }
    return false;
  }

  /**
   * This `scan` function will scan the provided directory for all classes that are annotated with
   * the AnnotationScanner instance's annotation class within the AnnotationScanner instance's
   * package name.
   *
   * @param directory The directory to start with.
   * @throws ClassNotFoundException when the process fails at any point.
   */
  private void scan (File directory) throws ClassNotFoundException {
    if(!directory.exists() || !directory.isDirectory()) {
      return;
    }
    final String[] files = directory.list();
    if(files == null) {
      return;
    }
    final String directoryPath = directory.getAbsolutePath().replace('/', '.');
    for (final String file : files) {
      if (file.endsWith(".class")) {
        try {
          Class aClass = Class.forName(directoryPath.substring(directoryPath.indexOf(packageName))
              + '.' + file.substring(0, file.length() - 6)
          );
          if (isAnnotatedWith(aClass)) {
            classes.add(aClass);
          }
        } catch (final NoClassDefFoundError e) {
          // do nothing. this class hasn't been found by the
          // loader, and we don't care.
        }
      } else {
        File potentialDirectory = new File(directory, file);
        if (potentialDirectory.isDirectory()) {
          scan(potentialDirectory);
        }
      }
    }
  }

  /**
   * This `scan` function will scan the provided JAR for all classes that are annotated with the
   * AnnotationScanner instance's annotation class within the AnnotationScanner instance's package
   * name.
   *
   * @param connection the connection to the JAR.
   * @throws ClassNotFoundException when unable to load an existing file from the JAR.
   * @throws IOException when unable to correctly read from the JAR file.
   */
  private void scan (JarURLConnection connection) throws ClassNotFoundException, IOException {
    final JarFile jarFile = connection.getJarFile();
    final Enumeration<JarEntry> entries = jarFile.entries();
    String name;

    for (JarEntry jarEntry; entries.hasMoreElements()
        && ((jarEntry = entries.nextElement()) != null);) {
      name = jarEntry.getName();

      if (name.contains(".class")) {
        name = name.substring(0, name.length() - 6).replace('/', '.');

        if (name.contains(packageName)) {
          try {
            Class aClass = Class.forName(name);
            if (isAnnotatedWith(aClass)) {
              classes.add(Class.forName(name));
            }
          } catch(final NoClassDefFoundError e) {
            // do nothing. this class hasn't been found by the
            // loader, and we don't care.
          }
        }
      }
    }
  }

  /**
   * Attempts to list all the classes that are annotated with the AnnotationScanner instance's
   * annotation class within the AnnotationScanner instance's package name as determined by the
   * context class loader.
   *
   * @return a list of classes.
   */
  ArrayList<Class<?>> getClasses() {
    if(exception != null) {
      throw exception;
    }
    if(classes != null) {
      return classes;
    }
    classes = new ArrayList<>();

    try {
      final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

      if (classLoader == null) {
        exception = new RuntimeException(new ClassNotFoundException("Can't get class loader."));
        throw exception;
      }

      final Enumeration<URL> resources = classLoader.getResources(packageName.replace('.', '/'));
      URLConnection connection;

      for (URL url; resources.hasMoreElements() && ((url = resources.nextElement()) != null);) {
        try {
          connection = url.openConnection();

          if (connection instanceof JarURLConnection) {
            try {
              scan((JarURLConnection) connection);
            } catch (final ClassNotFoundException ex) {
              exception = new RuntimeException(ex);
            }
          } else {
            try {
              scan(new File(URLDecoder.decode(url.getPath(), UTF_8.name())));
            } catch(final ClassNotFoundException ex) {
              exception = new RuntimeException(ex);
            } catch (final UnsupportedEncodingException ex) {
              exception = new RuntimeException(new ClassNotFoundException(
                  packageName + " does not appear to be a valid package (Unsupported encoding)",
                  ex
              ));
              throw exception;
            }
          }
        } catch (final IOException ex) {
          exception = new RuntimeException(new ClassNotFoundException(
              "IOException was thrown when trying to get all resources for " + packageName,
              ex
          ));
          throw exception;
        }
      }
    } catch (final NullPointerException ex) {
      exception = new RuntimeException(new ClassNotFoundException(
          packageName + " does not appear to be a valid package (Null pointer exception)",
          ex
      ));
      throw exception;
    } catch (final IOException ioex) {
      exception = new RuntimeException(new ClassNotFoundException(
          "IOException was thrown when trying to get all resources for " + packageName,
          ioex
      ));
      throw exception;
    }

    return classes;
  }

  public String getPackageName() {
    return packageName;
  }

}
