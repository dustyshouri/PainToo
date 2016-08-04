
package se.deas.paintoo.handlers;

import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import org.dusty.paintoo.PainToo;
import org.dusty.paintoo.PainToo.Tool;

public class FrameKeyEvent implements KeyListener {

  private PainToo paintToo;
  private boolean shiftHeld;

  public FrameKeyEvent( PainToo painToo ) {
    this.paintToo = painToo;
  }

  @Override
  public void keyTyped( KeyEvent e ) {
    // TODO Auto-generated method stub
  }

  @Override
  public void keyPressed( KeyEvent e ) {
    int mod = e.getModifiers();
    int key = e.getKeyCode();

    if ( key == KeyEvent.VK_SHIFT ) setShift( true );

    if ( key != KeyEvent.VK_CONTROL && key != KeyEvent.VK_SHIFT ) {
      System.out.println( "Key pressed: " + key + " - " + mod );
      if ( mod == 2 || mod == 4 ) {
        // shift = 1, ctrl = 2, cmd (macOS) = 4
        switch ( key ) {
        // Ctrl + '-'
          case KeyEvent.VK_MINUS:
          case 109: // TODO 109 = ?? 
            paintToo.getDrawingPane().decreaseBrushSize();
            break;
          // Ctrl + '='
          case KeyEvent.VK_EQUALS:
          case 107: // TODO 107 = ??
            paintToo.getDrawingPane().increaseBrushSize();
            break;
          // Ctrl + 'v'
          case KeyEvent.VK_V: // ctrl+v
            Image img = paintToo.getImageFromClipboard();
            if ( img != null ) paintToo.getDrawingPane().pasteClipboard( img );
            break;
        }
      }
      else {
        switch ( key ) {
          case KeyEvent.VK_B:
            paintToo.changeTool( Tool.BRUSH );
            break;
          case KeyEvent.VK_G:
            paintToo.changeTool( Tool.BUCKETFILL );
            break;
        }
      }
    }
  }

  @Override
  public void keyReleased( KeyEvent e ) {
    setShift( false );
  }

  public void setShift( boolean shift ) {
    shiftHeld = shift;
    paintToo.shiftHeld = shift;
  }

}
