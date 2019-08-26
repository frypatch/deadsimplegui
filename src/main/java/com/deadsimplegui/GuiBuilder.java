package com.deadsimplegui;

import com.deadsimplegui.util.Gui;
import com.deadsimplegui.util.resource.AnnotationScanner;
import com.deadsimplegui.util.resource.ExternalResourceLogic;
import com.deadsimplegui.util.resource.InternalResourceLogic;
import com.deadsimplegui.util.resource.ResourceLogic;
import com.deadsimplegui.util.resource.UrlBinding;
import java.net.URI;
import java.util.Arrays;

public class GuiBuilder {
  private static final String DEFAULT_HOME_PAGE = "http://localhost/index.html";
  private String title;
  private URI homePage;
  private ResourceLogic externalResourceLogic = new ExternalResourceLogic();
  private ResourceLogic internalResourceLogic = new InternalResourceLogic();

  private GuiBuilder() {
    try {
      this.homePage = new URI(DEFAULT_HOME_PAGE);
    } catch(Exception e) {
      //will never happen.
    }
  }

  public static GuiBuilder getInstance() {
    return new GuiBuilder();
  }

  public GuiBuilder registerPackages(String... packageNames) {
    Arrays
        .stream(packageNames)
        .forEach(packageName -> AnnotationScanner.registerPackage(packageName, UrlBinding.class));
    return this;
  }

  public GuiBuilder setTitle(String title) {
    this.title = title;
    return this;
  }

  public GuiBuilder setHomePage(URI homePage) {
    this.homePage = homePage;
    return this;
  }

  public GuiBuilder setExternalResourceLogic(ResourceLogic resourceLogic) {
    this.externalResourceLogic = resourceLogic;
    return this;
  }

  public GuiBuilder setInternalResourceLogic(ResourceLogic resourceLogic) {
    this.internalResourceLogic = resourceLogic;
    return this;
  }

  public Gui build() {
    return Gui.getInstance(title, homePage, externalResourceLogic, internalResourceLogic);
  }

}
