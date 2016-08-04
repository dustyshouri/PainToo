
package se.deas.paintoo.handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JFrame;

public class FrameDisposable implements ActionListener {
  private Object frame;

  public FrameDisposable( JDialog frame ) {
    this.frame = frame;
  }

  public FrameDisposable( JFrame frame ) {
    this.frame = frame;
  }

  @Override
  public void actionPerformed( ActionEvent e ) {
    if ( frame instanceof JFrame ) {
      ( ( JFrame ) frame ).dispose();
    }
    else if ( frame instanceof JDialog ) {
      ( ( JDialog ) frame ).dispose();
    }
  }

}
