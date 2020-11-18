package org.dusty.paintoo;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
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

public class PreviewDrawingArea extends Layer {
  
  public PreviewDrawingArea(PainToo paint) {
    super(paint);
    isPreviewArea = true;

    /*
    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {
        int mx = e.getX()/zoomScale;
        int my = e.getY()/zoomScale;
        
        clearCanvas();
        if (paint.getTool() == Tool.BRUSH) {
          if (graphics != null) {
            drawLine(Color.black, mx, my, mx, my);
          }
        }
      }
    });
    */
  }
  
  @Override
  public void drawLine(Color color, int x, int y, int x2, int y2) {
    Graphics2D g = graphics.createGraphics();

    Color oldColor = g.getColor();
    
    g.setPaint(new Color(255, 0, 255, 0));
    g.setBackground(new Color(255, 0, 255, 0));
    g.clearRect(0, 0, graphics.getWidth(), graphics.getHeight());
    g.fillRect(0, 0, graphics.getWidth(), graphics.getHeight());
    
    g.setColor(color);
    g.drawLine(x, y, x2, y2);
    repaint();
    g.dispose();
    
    g.setColor(oldColor);
  }
}
