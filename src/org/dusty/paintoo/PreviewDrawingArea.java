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
  public void clearCanvas() {
    Graphics2D g = graphics.createGraphics();
    Color oldColor = g.getColor();
    
    g.setPaint(new Color(255, 0, 255, 0));
    g.setBackground(new Color(255, 0, 255, 0));
    g.clearRect(0, 0, graphics.getWidth(), graphics.getHeight());
    g.fillRect(0, 0, graphics.getWidth(), graphics.getHeight());
    
    g.setColor(oldColor);
    repaint();
    g.dispose();
  }
  
  @Override
  public void previewZoom(BufferedImage drawingGraphics, int x, int y, int w, int h) {
    Graphics2D g = graphics.createGraphics();
    clearCanvas();
    
    if (x - w/2 < 0) x = w/2;
    else if (x + w/2 > graphics.getWidth()) x = graphics.getWidth() - w/2 - 1;
    if (y - h/2 < 0) y = h/2;
    else if (y + h/2 > graphics.getHeight()) y = graphics.getHeight() - h/2 - 1;

    int dx  = x-w/2;
    int dx2 = x+w/2;
    int dy  = y-h/2;
    int dy2 = y+h/2;
    //graphics.setRGB(x, y, getInvertedColor(graphics.getRGB(x, y)));

    for (int i=0;i<w;i++) graphics.setRGB(dx+i, dy, getInvertedColor(drawingGraphics.getRGB(dx+i,dy)));
    for (int i=0;i<w;i++) graphics.setRGB(dx+i, dy2, getInvertedColor(drawingGraphics.getRGB(dx+i,dy2)));
    for (int i=0;i<h;i++) graphics.setRGB(dx, dy+i, getInvertedColor(drawingGraphics.getRGB(dx,dy+i)));
    for (int i=0;i<h;i++) graphics.setRGB(dx2, dy+i, getInvertedColor(drawingGraphics.getRGB(dx2,dy+i)));

    //g.drawLine(dx,dy,dx2,dy);
    //g.drawLine(dx,dy,dx,dy2);
    //g.drawLine(dx2,dy,dx2,dy2);
    //g.drawLine(dx,dy2,dx2,dy2);
    repaint();
    g.dispose();
  }
  
  private int getInvertedColor(int rgb) {
    int a = 0xFF & (rgb >> 24);
    if (a == 0) return 0xFFFF00FF;
    return (0xFFFFFF - rgb) | 0xFF000000;
  }
  
  @Override
  public void drawBrush(Color color, int x, int y, int radius) {
    clearCanvas();
    super.drawBrush(color, x, y, radius);
  }
  
  @Override
  public void drawLine(Color color, int x, int y, int x2, int y2) {
    Graphics2D g = graphics.createGraphics();

    Color oldColor = g.getColor();
    
    clearCanvas();
    
    g.setColor(color);
    g.drawLine(x, y, x2, y2);
    repaint();
    g.dispose();
    
    g.setColor(oldColor);
  }
}
