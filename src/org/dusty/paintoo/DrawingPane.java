package org.dusty.paintoo;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import org.dusty.paintoo.PainToo.Tool;
import org.dusty.paintoo.PaintCursor.Cursors;

public class DrawingPane extends JLayeredPane {
  private int zoomScale = 1, brushSize = 1, desiredZoom = 12;
  Color bgColor = new Color(255,255,255,255);
  Color fgColor = new Color(0,0,0,255);
  public Dimension dimension = PainToo.dimension;
  private Point mousePosition;
  public PaintCursor cursor = new PaintCursor();
  public PainToo paint;
  
  public BufferedImage graphics;
  public int newX, newY, oldX, oldY, startX, startY;
  Color paintColor = fgColor;
  
  public enum dir {UP, DOWN, LEFT, RIGHT}
  private dir stickDir;
  Robot robot;
  
  public DrawingPane(PainToo paint) {
    this.paint = paint;
    setLayout(new GridBagLayout());
    setPreferredSize(new Dimension(640,480));
    
    try {
      robot = new Robot();
    } catch (AWTException e) {}
    
    addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent e) {
        stickDir = null;
        newX = e.getX()/zoomScale;
        newY = e.getY()/zoomScale;
        paintColor = fgColor;
        if (paint.getTool() == Tool.BRUSH) previewPixel(paintColor,brushSize, newX, newY);
      }
      public void mousePressed(MouseEvent e) {
        paintColor = e.getButton() == 1 ? fgColor : bgColor;
        int mx = e.getX()/zoomScale;
        int my = e.getY()/zoomScale;
        
        if (paint.shiftHeld) {
          startX = mx;
          startY = my;
          stickDir = null;
          mousePosition = MouseInfo.getPointerInfo().getLocation();
        }

        switch (paint.getTool()) {
          case ZOOM:
            if (zoomScale > 1) zoomCanvas(1);
            else zoomCanvas(desiredZoom);
            paint.defaultTool();
          break;
          
          case BUCKETFILL:
            bucketFill(paintColor,mx,my);
          break;
          case PENCIL:
          case BRUSH:
            previewPixel(paintColor,brushSize,mx, my);
            drawBrush(paintColor,brushSize,mx,my);
          break;
          default:
          break;
        }
        oldX = mx;
        oldY = my;
      }
      
      public void mouseEntered(MouseEvent e) {
      }
      public void mouseExited(MouseEvent e) {
        clearPreview();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        int mx = e.getX()/zoomScale;
        int my = e.getY()/zoomScale;
        if (paint.getTool() == Tool.BRUSH || paint.getTool() == Tool.PENCIL) {
          
          if (paint.shiftHeld) {
            if (stickDir == null) {
              if (startX < mx) stickDir = dir.LEFT;
              else if (startY < my) stickDir = dir.UP;
              else if (startX > mx) stickDir = dir.RIGHT;
              else if (startY > my) stickDir = dir.DOWN;
            }
          } else {
            startX = mx;
            startY = my;
          }
          
          if (stickDir == dir.LEFT || stickDir == dir.RIGHT) {
            int mousex = MouseInfo.getPointerInfo().getLocation().x;
            robot.mouseMove(mousex,mousePosition.y);
            my = startY;
          }
          else if (stickDir == dir.UP || stickDir == dir.DOWN) {
            int mousey = MouseInfo.getPointerInfo().getLocation().y;
            robot.mouseMove(mousePosition.x,mousey);
            mx = startX;
          }
          
          drawBrush(paintColor, brushSize, oldX, oldY, mx, my);
          oldX = mx;
          oldY = my;
        }
      }
      
      public void mouseMoved(MouseEvent e) {
        int mx = e.getX()/zoomScale;
        int my = e.getY()/zoomScale;
        newX = mx;
        newY = my;
        if (paint.getTool() == Tool.BRUSH || paint.getTool() == Tool.PENCIL) previewPixel(paintColor,brushSize,mx, my);
        else if (paint.getTool() == Tool.ZOOM && zoomScale == 1) previewZoom(e.getX(), e.getY());
      }
    });
    
    super.setPreferredSize(dimension);
  }
  
  public void clearPreview() {
    Layer panel = getPreviewLayer();
    panel.clearCanvas();
  }
  
  public void increaseBrushSize() {
    brushSize++;
    System.out.println(paint.getTool());
    if (paint.getTool() == Tool.BRUSH || paint.getTool() == Tool.PENCIL) previewPixel(paintColor,brushSize,newX,newY);
  }
  
  public void decreaseBrushSize() {
    brushSize = Math.max(1,brushSize - 1);
    if (paint.getTool() == Tool.BRUSH || paint.getTool() == Tool.PENCIL) previewPixel(paintColor,brushSize,newX,newY);
  }
  
  public void previewZoom(int x, int y) {
    Layer panel = getPreviewLayer();
    BufferedImage graphics = getCurrentLayer().graphics;
    int w = (PainToo.frame.getWidth()-89)/desiredZoom;
    int h = (PainToo.frame.getHeight()-150)/desiredZoom;
    panel.previewZoom(graphics, x,y,w,h);
  }
  
  public void previewPixel(Color color, int radius, int mx, int my) {
    Layer panel = getPreviewLayer();
    panel.drawBrush(color, mx, my, radius);
  }
  
  public void previewPixel(int mx, int my) {
    Layer panel = getPreviewLayer();
    panel.drawLine(paintColor, mx, my, mx, my);
  }
  
  public void drawBrush(Color color, int radius, int x, int y, int x2, int y2) {
    if (radius == 1) drawLine(color, x, y, x2, y2);
    else drawBrushLine(color, radius, x, y, x2, y2);
  }
  
  public void drawBrushLine(Color color, int radius, int x, int y, int x2, int y2) {
    Layer panel = getCurrentLayer();
    panel.drawBrushLine(color, x, y, x2, y2, radius);
  }
  
  public void drawBrush(Color color, int radius, int x, int y) {
    Layer panel = getCurrentLayer();
    if (radius == 1) drawLine(color, x, y, x, y);
    else panel.drawBrush(color, x, y, radius);
  }
  
  public void drawLine(Color color, int x, int y, int x2, int y2) {
    Layer panel = getCurrentLayer();
    panel.drawLine(color, x, y, x2, y2);
  }
  
  public void bucketFill(Color color, int x, int y) {
    Layer panel = getCurrentLayer();
    panel.bucketFill(color, x, y);
  }
  
  public void pasteClipboard(Image img) {
    Layer panel = getCurrentLayer();
    int x = Math.max(0,newX-img.getWidth(null)/2);
    int y = Math.max(0,newY-img.getHeight(null)/2);
    panel.pasteClipboard(img,x,y);
  }
  
  public void zoomCanvas(int scale) {
    clearPreview();
    
    zoomScale = scale;
    System.out.println("Scale set to: " + scale);
    for (Component c : super.getComponents()) {
      Layer panel = (Layer) c;
      panel.zoomCanvas(scale);
    }
    super.setPreferredSize(new Dimension(dimension.width*scale,dimension.height*scale));
    super.revalidate();
    
    /*
    if (scale > 1) {
      Rectangle bounds = new Rectangle(newX*zoomScale, newY*zoomScale,1,1);
      super.scrollRectToVisible(bounds);
    }
    */

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
 
  public Layer getPreviewLayer() {
    for (Component c : super.getComponents()) {
      Layer panel = (Layer) c;
      if (panel.isPreviewArea) return panel;
    }
    return (Layer) super.getComponents()[0];
  } 
  
  public Layer getCurrentLayer() {
    for (Component c : super.getComponents()) {
      Layer panel = (Layer) c;
      if (panel.isDrawingArea) return panel;
    }
    return (Layer) super.getComponents()[0];
  }
}