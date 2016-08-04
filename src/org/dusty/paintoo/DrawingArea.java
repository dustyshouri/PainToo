package org.dusty.paintoo;

public class DrawingArea extends Layer {
  private static final long serialVersionUID = 1L;

  public DrawingArea(PainToo paint) {
    super(paint);
    this.isDrawingArea = true;
   
    super.setPreferredSize(dimension);
  }
 
}
