package org.dusty.paintoo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import org.dusty.paintoo.PainToo.Tool;
import org.dusty.paintoo.PaintCursor.Cursors;

public class DrawingPane extends JLayeredPane {
  private int zoomScale = 1;
  private Dimension dimension = new Dimension(640,480);
  public PaintCursor cursor = new PaintCursor();
  
  public BufferedImage graphics;
  public int newX, newY, oldX, oldY;
  
  public DrawingPane(PainToo paint) {
    setLayout(new GridBagLayout());
    setPreferredSize(new Dimension(640,480));
    
    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        switch (paint.getTool()) {
          case ZOOM:
            if (zoomScale > 1) zoomCanvas(1);
            else zoomCanvas(12);
            paint.defaultTool();
          break;
          
          default:
            int mx = e.getX()/zoomScale;
            int my = e.getY()/zoomScale;
            drawLine(Color.black,mx,my,mx,my);
            oldX = mx;
            oldY = my;
          break;
        }
      }
      public void mouseExited(MouseEvent e) {
        System.out.println("Exited");
        int mx = e.getX()/zoomScale;
        int my = e.getY()/zoomScale;
        previewPixel(new Color(0,0,0,0), mx, my);
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        if (paint.getTool() != Tool.ZOOM) {
          newX = e.getX()/zoomScale;
          newY = e.getY()/zoomScale;

          drawLine(Color.black, oldX, oldY, newX, newY);
          oldX = newX;
          oldY = newY;
        }
      }
      
      public void mouseMoved(MouseEvent e) {
        int mx = e.getX()/zoomScale;
        int my = e.getY()/zoomScale;
        
        previewPixel(mx, my);
      }
    });
    
    super.setPreferredSize(dimension);
  }
  
  public void clearPreview() {
    for (Component c : super.getComponents()) {
      Layer panel = (Layer) c;
      if (panel.isPreviewArea) {
        panel.clearCanvas();
        break;
      }
    }
  }
  
  public void previewPixel(Color color, int mx, int my) {
    for (Component c : super.getComponents()) {
      Layer panel = (Layer) c;
      if (panel.isPreviewArea) {
        panel.drawLine(color, mx, my, mx, my);
        break;
      }
    }
  }
  
  public void previewPixel(int mx, int my) {
    for (Component c : super.getComponents()) {
      Layer panel = (Layer) c;
      if (panel.isPreviewArea) {
        panel.drawLine(Color.black, mx, my, mx, my);
        break;
      }
    }
  }
  
  public void drawLine(Color color, int x, int y, int x2, int y2) {
    for (Component c : super.getComponents()) {
      Layer panel = (Layer) c;
      if (panel.isDrawingArea) {
        panel.drawLine(Color.black, x, y, x2, y2);
        break;
      }
    }
  }
  
  public void zoomCanvas(int scale) {
    zoomScale = scale;
    System.out.println("Scale set to: " + scale);
    for (Component c : super.getComponents()) {
      Layer panel = (Layer) c;
      panel.zoomCanvas(scale);
    }
    super.setPreferredSize(new Dimension(dimension.width*scale,dimension.height*scale));
    super.revalidate();
  }

  public void addLayer(JComponent layer) {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;

    add(layer, gbc);
  }
  
  public void setCursor(int i) {
    Cursor c = cursor.getCursor(0);
    super.setCursor(c);
  }
  
  public void setCursor(Cursors i) {
    Cursor c = cursor.getCursor(i);
    super.setCursor(c);
  }
}
