package org.dusty.paintoo;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.util.ArrayDeque;
import java.awt.image.DataBufferInt;

import javax.swing.JComponent;

import org.dusty.paintoo.PainToo.Tool;

class Layer extends JComponent {
  
  public boolean isDrawingArea = false, isPreviewArea = false;
  public Image image;
  public BufferedImage graphics;
  public int zoomScale = 1;
  //public Dimension dimension = new Dimension(640*2,480*2);
  public Dimension dimension = PainToo.dimension;
  //public Dimension dimension = new Dimension(32,32);
  public int newX, newY, oldX, oldY;
  private boolean alphaValue;
  private boolean currentlyFilling = false;
  PainToo paintoo;
  
  public Layer(PainToo paint) {
    paintoo = paint;
    graphics = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_4BYTE_ABGR);
    setPreferredSize(dimension);
    //graphics.createGraphics().setComposite(AlphaComposite.Src);
    setDoubleBuffered(false);
    setOpaque(false);
    //setBackground(new Color(255,0,0,255));
    clearCanvas();
  }
  
  protected void paintComponent(Graphics g) {
    g.drawImage(graphics, 0, 0, getSize().width, this.getSize().height, 0, 0, getSize().width/this.zoomScale, getSize().height/this.zoomScale, null);
    g.dispose();
  }
  
  public int getCanvasZoom() {
    return this.zoomScale;
  }
  
  public void drawBrush(Color color, int x, int y, int radius) {

    Graphics2D g = graphics.createGraphics();

    Color oldColor = g.getColor();
    g.setColor(color);
    if (radius%2 == 0) {
      x--;
      y--;
    }
    if (radius == 2) g.drawRect(x,y,1,1);
    else {
      if (paintoo.getTool() == PainToo.Tool.PENCIL) {
        g.fillRect(x -  radius/2, y -  radius/2, radius, radius);
      } else {
        radius--;
        g.fillOval(x -  radius/2, y -  radius/2, radius, radius);
        g.drawOval(x -  radius/2, y -  radius/2, radius, radius);
      }
    }
    repaint();
    g.dispose();
    
    g.setColor(oldColor);
  }
  
  public void drawBrushLine(Color color, int x, int y, int x2, int y2, int radius) {
    Graphics2D g = graphics.createGraphics();

    Stroke defaultStroke = g.getStroke();
    Color oldColor = g.getColor();
    g.setColor(color);
    
    float dx = x2 - x;
    float dy = y2 - y;
    float dist = (int) Math.sqrt(dx*dx+dy*dy);
    int strokeRadius = radius;

    if (radius < 6 || paintoo.getTool() == PainToo.Tool.PENCIL) {
      for (int i=0;i<(int)dist;i++) {
        int drawx = (int) (x + (dx/dist)*i);
        int drawy = (int) (y + (dy/dist)*i);
        drawBrush(color,drawx,drawy,radius);
      }
    } else {
      g.setStroke(new BasicStroke(strokeRadius,BasicStroke.CAP_ROUND,BasicStroke.JOIN_BEVEL));
      g.drawLine(x, y, x2, y2);
    }
    g.setStroke(defaultStroke);
    g.setColor(color);
    drawBrush(color,x , y , radius);
    drawBrush(color,x2, y2, radius);
    
    repaint();
    g.dispose();
    
    g.setColor(oldColor);
  }
  
  public void pasteClipboard(Image img, int x, int y) {
    Graphics2D g = graphics.createGraphics();
    g.drawImage(img, x, y, null);
    repaint();
    g.dispose();
  }
  
  
  public void drawLine(Color color, int x, int y, int x2, int y2) {
    Graphics2D g = graphics.createGraphics();

    Color oldColor = g.getColor();
    g.setColor(color);

    g.drawLine(x, y, x2, y2);
    repaint();
    g.setColor(oldColor);
    g.dispose();
  }
  
  public void drawLine(int x, int y, int x2, int y2) {
    Graphics2D g = graphics.createGraphics();

    g.setColor(Color.black);
    g.drawLine(x, y, x2, y2);
    this.repaint();
    g.dispose();
  }
  
  public void bucketFill(Color color, int mx, int my) {
    if (currentlyFilling) return;
    Graphics2D g = graphics.createGraphics();
  
    g.setColor(color);
    int w = graphics.getWidth();
    int h = graphics.getHeight();
    
    DataBuffer d = graphics.getRaster().getDataBuffer();
    byte[] pixels = ((DataBufferByte)d).getData();

    alphaValue = pixels.length/(w*h) > 3;
    
    int clickcolor = getPixelData(pixels, mx, my);
    
    System.out.println(clickcolor + " : " + color.getRGB());
    if (clickcolor == color.getRGB()) return;

    ArrayDeque<Point> stack = new ArrayDeque<Point>();
    currentlyFilling = true;
    
    stack.clear();
    stack.push(new Point(mx, my));

    //Color fill = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255),55+(int)(Math.random()*200));
    //fill = new Color(255, 255, 255, 255);
    
    long timeStart = System.currentTimeMillis();
    while (!stack.isEmpty()) {
      Point p = stack.pop();
      int x = p.x;
      int y = p.y;
      int y1 = y;

      while (y1 >= 0 && getPixelData(pixels, x, y1) == clickcolor) y1--;
      y1++;
      boolean spanRight = false;
      boolean spanLeft = false;
      
      while (y1 < h && getPixelData(pixels, x, y1) == clickcolor) {
        setDataPixel(pixels, color, x, y1);
        if (!spanLeft && x > 0 && getPixelData(pixels, x - 1, y1) == clickcolor) {
          stack.push(new Point(x - 1, y1));
          spanLeft = true;
        } else if (spanLeft && x > 0 && getPixelData(pixels, x - 1, y1) != clickcolor) spanLeft = false;
        if (!spanRight && x < w - 1 && getPixelData(pixels, x + 1, y1) == clickcolor) {
          stack.push(new Point(x + 1, y1));
          spanRight = true;
        } else if (spanRight && x < w - 1 && getPixelData(pixels, x + 1, y1) != clickcolor) spanRight = false;
        y1++;
      }
    }
    
    long timePassed = (System.currentTimeMillis() - timeStart);
    currentlyFilling = false;
    System.out.println(w + "*" + h + " pixels filled in " + timePassed + " milliseconds");
    this.repaint();
    g.dispose();
  }
  
  public int getRGB(int x, int y) {
    if (x < 0) x = 0;
    if (y < 0) y = 0;
    if (x > graphics.getWidth()-1) x = graphics.getWidth()-1;
    if (y > graphics.getHeight()-1) y = graphics.getHeight()-1;
    return graphics.getRGB(x, y);
  }

  public int getPixelData(byte[] pixels, int x, int y) {
    int w = graphics.getWidth();
    int h = graphics.getHeight();
    if (x < 0) x = 0;
    else if (x > w-1) x = w-1;
    if (y < 0) y = 0;
    else if (y > h-1) y = h-1;
    int pixelOffset = (alphaValue ? 4 : 3) * (x + y * w);
    
    int r = pixels[1 + pixelOffset] & 255;
    int g = pixels[2 + pixelOffset] & 255;
    int b = pixels[3 + pixelOffset] & 255;
    int a = 255;
    if (alphaValue) a = pixels[0 + pixelOffset] & 255;

    return new Color(r, g, b, a).getRGB();
  }
  
  public void setDataPixel(byte[] pixels, Color color, int x, int y) {
    int w = graphics.getWidth();

    pixels[0 + 4 * (x + y * w)] = (byte)color.getAlpha(); // alpha
    pixels[1 + 4 * (x + y * w)] = (byte)color.getGreen();   // green
    pixels[2 + 4 * (x + y * w)] = (byte)color.getBlue();   // blue
    pixels[3 + 4 * (x + y * w)] = (byte)color.getRed(); // red

    /*
    int pixelOffset = (alphaValue ? 4 : 3) * (x + y * w);
    pixelOffset = (x + y * w);
    int rgba = 0;
    rgba += -16777216; // 255 alpha
    rgba += ((int) pixels[pixelOffset] & 0xff); // blue
    rgba += (((int) pixels[pixelOffset + 1] & 0xff) << 8); // green
    rgba += (((int) pixels[pixelOffset + 2] & 0xff) << 16); // red
    */
  }
  
  public void previewZoom(BufferedImage drawingGraphics, int x, int y, int w, int h) {}
  
  public void zoomCanvas(int scale) {
    this.zoomScale = scale;
    super.setPreferredSize(new Dimension(dimension.width*scale,dimension.height*scale));
    super.revalidate();
  }
  
  public void fillCanvas(Color color) {
    Graphics2D g = graphics.createGraphics();
    Color oldColor = g.getColor();
    g.setPaint(color);
    g.fillRect(0, 0, graphics.getWidth(), graphics.getHeight());
    g.setPaint(oldColor);
    g.dispose();
    this.repaint();
  }

  public void clearCanvas() {
    fillCanvas(new Color(255,255,255,0));
  }
}
