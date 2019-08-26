package com.deadsimplegui.util.render;

import com.deadsimplegui.util.Gui;
import com.deadsimplegui.util.HtmlPage;
import com.deadsimplegui.util.HtmlUri;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * The `ImageRenderer` exists to provide a well defined process for loading all images into the
 * GUI that are referenced in the supplied HTML. This is necessary because images will only be
 * displayed in the GUI when they are loaded into the GUI's image cache. Without doing so the
 * images will be displayed as broken.
 * <p>
 * Note: If an image URL's host is `localhost` then the image will be loaded from the application's
 * resources. You would want to do this if you desired to bundle images into the application. If
 * the image URL's host is not local host then the image data will be retrieved through the supplied ExternalResourceLoader.
 * <p>
 * http://java-sl.com/tip_local_images.html
 */
class ImageRenderer {

  static String loadImage(String imageSrc, HtmlPage history, Gui gui) throws MalformedURLException {
    try{
      Image placeHolderImage = createImage();
      String proxiedImageSrc = "http://127.0.0.1/" + gui.getImageCacheSize() + ".img";
      gui.putImage(new URL(proxiedImageSrc), placeHolderImage);
      Image image;
      HtmlUri imageUri = HtmlUri.getInstance(imageSrc, history);
      if(!imageUri.getHost().equals("localhost")) {
        image = gui.getExternalResourceLogic().readImage(imageUri.getUri());
      } else {
        image = gui.getInternalResourceLogic().readImage(imageUri.getUri());
      }
      gui.putImage(new URL(proxiedImageSrc), image);
      return proxiedImageSrc;
    } catch (Exception e) {
      Image placeHolderImage = createImage();
      String proxiedImageSrc = "http://127.0.0.1/404_error_unknown_123_xyz.png";
      gui.putImage(new URL(proxiedImageSrc), placeHolderImage);
      return proxiedImageSrc;
    }
  }

  private static Image createImage() {
    BufferedImage img=new BufferedImage(100,50,BufferedImage.TYPE_INT_ARGB);
    Graphics g=img.getGraphics();
    ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(Color.BLUE);
    g.fillRect(0,0,100,50);

    g.setColor(Color.YELLOW);
    g.fillOval(5,5,90,40);
    img.flush();

    return img;
  }

}
