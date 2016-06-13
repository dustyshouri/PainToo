package org.dusty.paintoo;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import org.dusty.paintoo.PainToo.Tool;
import org.dusty.paintoo.PaintCursor.Cursors;

public class DrawingArea extends Layer {
  
  public DrawingArea(PainToo paint) {
    super(paint);
    this.isDrawingArea = true;

    /*
    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        if (paint.getTool() == Tool.ZOOM) {
          paint.defaultTool();
        } else {
          int mx = e.getX()/zoomScale;
          int my = e.getY()/zoomScale;
          if (graphics != null) {
            drawLine(mx,my,mx,my);
          }
          oldX = mx;
          oldY = my;
        }
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        if (paint.getTool() != Tool.ZOOM) {
          newX = e.getX()/zoomScale;
          newY = e.getY()/zoomScale;

          if (graphics != null) {
            drawLine(oldX, oldY, newX, newY);
            oldX = newX;
            oldY = newY;
          }
        }
      }
    });
    */
    
    super.setPreferredSize(dimension);
  }
 
}
