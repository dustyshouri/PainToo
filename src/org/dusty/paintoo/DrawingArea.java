package org.dusty.paintoo;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JComponent;

public class DrawingArea extends JComponent {
  private Image image;
  private Graphics2D graphics;
  
  private int newX, newY, oldX, oldY;
  
  Toolkit toolkit = Toolkit.getDefaultToolkit();
  Cursor cursor_zoom = toolkit.createCustomCursor(toolkit.getImage("assets/cursors/cursor_crosszoom.png"), new Point(0, 0),"zoom");
  Cursor cursor_eyedrop = toolkit.createCustomCursor(toolkit.getImage("assets/cursors/cursor_eyedrop.png"), new Point(0, 31),"eyedrop");
  Cursor cursor_pencil = toolkit.createCustomCursor(toolkit.getImage("assets/cursors/cursor_pencil.png"), new Point(1, 31),"pencil");
  Cursor cursor_linecross = toolkit.createCustomCursor(toolkit.getImage("assets/cursors/cursor_linecross.png"), new Point(15, 15),"linecross");
  Cursor cursor_crosshair = toolkit.createCustomCursor(toolkit.getImage("assets/cursors/cursor_crosshair.png"), new Point(15, 15),"crosshair");
  
  Cursor cursor;
  
  public DrawingArea() {
    
    cursor = cursor_crosshair;
    
    setDoubleBuffered(false);
    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        if (graphics != null) {
          graphics.drawLine(e.getX(),e.getY(),e.getX(),e.getY());
          repaint();
        }
        oldX = e.getX();
        oldY = e.getY();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        newX = e.getX();
        newY = e.getY();

        if (graphics != null) {
          graphics.drawLine(oldX, oldY, newX, newY);
          repaint();
          oldX = newX;
          oldY = newY;
        }
      }
    });
    
    super.setCursor(cursor);
  }
  
  protected void paintComponent(Graphics g) {
    if (image == null) {
      image = createImage(getSize().width, getSize().height);
      graphics = (Graphics2D) image.getGraphics();
      graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
      clearCanvas();
    }

    g.drawImage(image, 0, 0, null);
  }
  
  public void clearCanvas() {
    graphics.setPaint(Color.white);
    graphics.fillRect(0, 0, getSize().width, getSize().height);
    graphics.setPaint(Color.black);
    repaint();
  }
}
