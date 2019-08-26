package com.deadsimplegui.util.resource;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The `UrlBinding` annotation exists to bind a `Page` to a specific path within the application.
 * The Gui will then examine the submitted URL and if its host is `localhost` will direct it to the
 * first found `Page` that is annotated with a `UrlBinding` value matching the submitted URL's path.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface UrlBinding {

  String value() default "default";

}
