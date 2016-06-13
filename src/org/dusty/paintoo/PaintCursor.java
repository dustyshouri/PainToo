package org.dusty.paintoo;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PaintCursor {
  private Cursor cursors[] = new Cursor[5];
  private String cursor_images[] = {
    "cursor_crosshair.png",
    "cursor_zoom.png",
    "cursor_eyedrop.png",
    "cursor_linecross.png",
    "cursor_pencil.png",
  };
  private Point cursor_points[] = {
      new Point(15, 15), // Crosshair
      new Point( 4,  4), // Zoom
      new Point( 0, 31), // Eyedrop
      new Point(15, 15), // Line Cross
      new Point( 1, 31), // Pencil
  };
  
  Toolkit toolkit = Toolkit.getDefaultToolkit();
  
  public static enum Cursors {
    CROSSHAIR, ZOOM, EYEDROP, LINECROSS, PENCIL
  }
  
  public PaintCursor() {
    BufferedImage icon = null;
    try {
      for (int i = 0; i < cursors.length; i ++) {
        String file = cursor_images[i];
        icon = ImageIO.read(this.getClass().getResource("/assets/cursors/" + file));
        cursors[i] = this.toolkit.createCustomCursor(icon, cursor_points[i], "crosshair");
      }
    } catch (IOException e1) {
        e1.printStackTrace();
    }
  }
  
  public Cursor getCursor(int i) {
    return cursors[i];
  }
  
  public Cursor getCursor(Cursors i) {
    return cursors[i.ordinal()];
  }
}