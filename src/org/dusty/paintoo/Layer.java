package org.dusty.paintoo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import org.dusty.paintoo.PainToo.Tool;

class Layer extends JComponent {
  public boolean isDrawingArea = false, isPreviewArea = false;
  public Image image;
  public BufferedImage graphics;
  public int zoomScale = 1;
  public Dimension dimension = new Dimension(640,480);
  public int newX, newY, oldX, oldY;
  
  public Layer(PainToo paint) {
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
  
  public void drawLine(Color color, int x, int y, int x2, int y2) {
    Graphics2D g = graphics.createGraphics();

    Color oldColor = g.getColor();
    g.setColor(color);
    g.drawLine(x, y, x2, y2);
    repaint();
    g.dispose();
    
    g.setColor(oldColor);
  }
  
  public void drawLine(int x, int y, int x2, int y2) {
    Graphics2D g = graphics.createGraphics();

    g.setColor(Color.black);
    g.drawLine(x, y, x2, y2);
    this.repaint();
    g.dispose();
  }
  
  public void zoomCanvas(int scale) {
    this.zoomScale = scale;
    super.setPreferredSize(new Dimension(dimension.width*scale,dimension.height*scale));
    super.revalidate();
  }

  
  public void clearCanvas() {
    Graphics2D g = graphics.createGraphics();
    Color oldColor = g.getColor();
    g.setPaint(new Color(255, 0, 255, 0));
    g.fillRect(0, 0, graphics.getWidth(), graphics.getHeight());
    g.setPaint(oldColor);
    g.dispose();
    this.repaint();
  }
}
