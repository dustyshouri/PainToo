
package org.dusty.paintoo;

import java.awt.Color;
import java.awt.Graphics2D;

public class Background extends Layer {
  private static final long serialVersionUID = 1L;

  public Background( PainToo paint ) {
    super( paint );
  }

  @Override
  public void clearCanvas() {
    Graphics2D g = graphics.createGraphics();
    for ( int h = 0; h < graphics.getHeight() / 16 + 1; h ++ ) {
      for ( int w = 0; w < graphics.getWidth() / 16 + 1; w ++ ) {
        Color c = ( w + h ) % 2 == 0 ? new Color( 255, 255, 255, 255 ) : new Color( 215, 215, 215, 255 );
        g.setPaint( c );
        g.fillRect( w * 16, h * 16, 16, 16 );
      }
    }

    g.dispose();
    this.repaint();
  }
}
