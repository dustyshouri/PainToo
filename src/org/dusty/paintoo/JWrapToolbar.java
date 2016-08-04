package org.dusty.paintoo;

import javax.swing.JToolBar;

public class JWrapToolbar extends JToolBar {
  private static final long serialVersionUID = 1L;

  public JWrapToolbar() {
    super.setLayout(new WrapLayout(0, 0, 0));
  }
}
