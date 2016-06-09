package org.dusty.paintoo;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.event.MouseEvent;
@SuppressWarnings("unused")


/*
 * Feature plans:
 *   - many undos. So many
 *   - second pass on pencil tool to reduce adjacent pixels when drawing slow
 *     as seen here: http://pixelation.org/index.php?topic=16163.0
 * 
 */

public class PainToo {
  private JFrame frame;
  DrawingArea drawingArea;
  
  public enum Tool {
    FREEFORM, SELECT, ERASE, BUCKETFILL, EYEDROP, ZOOM, PENCIL, BRUSH, 
    SPRAY, TEXT, LINE, CURVELINE, SQUARE, POLYGON, ELLIPSE, ROUNDSQUARE
  }

  
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    } catch (Throwable e) {
      e.printStackTrace();
    }
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          PainToo window = new PainToo();
          window.frame.setIconImage(Toolkit.getDefaultToolkit().getImage("assets/favico.png"));
          window.frame.setTitle("untitled - Paint");
          window.frame.setVisible(true);
          window.frame.setMinimumSize(new Dimension(500,400));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  public PainToo() {
    initialize();
  }

  private void initialize() {
    frame = new JFrame();
    frame.setBounds(100, 100, 1280/2, 840/2);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    drawingArea = new DrawingArea();
    
    JMenuBar menuBar = new JMenuBar();
    frame.setJMenuBar(menuBar);
    
    String[][][] menuItems= {
        {{"File"}  ,{"New","Open","Save","Save As","","Exit"}},
        {{"Edit"}  ,{"Undo","Redo","","Cut","Copy","Paste","Clear","Select All"}},
        {{"View"}  ,{"Tool Box","Color Box","Status Bar"}},
        {{"Image"} ,{"Flip/Rotate","Stretch Skew","Invert","Attributes","Clear All"}},
        {{"Colors"},{"Edit Colors","","Import Palette","Save Palette"}},
        {{"Help"}  ,{"About"}},
    };
    
    for (String[][] i : menuItems) {
      String menuCategory = i[0][0];
      JMenu menu = new JMenu(menuCategory);
      menuBar.add(menu);
      for (String j : i[1]) {
        if (j.equals("")) menu.addSeparator();
        else {
          JMenuItem menuItem = new JMenuItem(j);
          menu.add(menuItem);
        }
      }
    }
    
    frame.getContentPane().setLayout(new BorderLayout(0, 0));
    
    JPanel bottomContainer = new JPanel();
    bottomContainer.setBackground(new Color(240,240,240,255));
    FlowLayout fl_bottomContainer = (FlowLayout) bottomContainer.getLayout();
    fl_bottomContainer.setVgap(32);
    bottomContainer.setBorder(new BevelBorder(BevelBorder.RAISED, new Color(255, 255, 255), new Color(240, 240, 240), null, UIManager.getColor("Label.background")));
    frame.getContentPane().add(bottomContainer, BorderLayout.SOUTH);
    
    JPanel toolbarContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
    toolbarContainer.setBackground(new Color(240,240,240,255));
    toolbarContainer.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.gray));
    frame.getContentPane().add(toolbarContainer, BorderLayout.WEST);
    
    JToolBar toolbar = new JWrapToolbar();
    toolbar.setBackground(new Color(240,240,240,255));
    //toolbar.setLayout(new WrapLayout(0, 0, 0));
    toolbar.setSize(56,1);
    toolbar.setRollover(true);
    toolbar.setOrientation(SwingConstants.VERTICAL);
    toolbar.setFloatable(false);
    toolbarContainer.add(toolbar);
    
    String[][] buttonArray = {
        {"Freeform Select","icon_freeform.png"}  ,{"Select"           ,"icon_select.png"},
        {"Eraser"         ,"icon_eraser.png"}    ,{"Bucket Fill"      ,"icon_bucketfill.png"},
        {"Eyedropper"     ,"icon_eyedropper.png"},{"Zoom"             ,"icon_zoom.png"},
        {"Pencil"         ,"icon_pencil.png"}    ,{"Brush"            ,"icon_brush.png"},
        {"Spraypaint"     ,"icon_spray.png"}     ,{"Text"             ,"icon_text.png"},
        {"Line"           ,"icon_line.png"}      ,{"Curve"            ,"icon_curveline.png"},
        {"Rectangle"      ,"icon_rectangle.png"} ,{"Polygon"          ,"icon_polygon.png"},
        {"Ellipse"        ,"icon_ellipse.png"}   ,{"Rounded Rectangle","icon_roundrectangle.png"},
    };
    
    JToggleButton buttons[] = new JToggleButton[buttonArray.length];
    
    for (int i = 0;i < buttonArray.length;i++) {
      String text = buttonArray[i][0];
      String icon = buttonArray[i][1];
      
      JToggleButton button = new JToggleButton("");
      button.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage("assets/icons/" + icon)));
      button.setToolTipText(text);
      button.setPreferredSize(new Dimension(25,25));
      button.setFocusPainted(false);
      button.setActionCommand(text);
      
      button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          //System.out.println(e.getActionCommand());
          for (JToggleButton btn : buttons) {
            btn.setSelected(false);
          }
          button.setSelected(true);

        }
      });

      toolbar.add(button);
      buttons[i] = button;
    }
    
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBorder(BorderFactory.createBevelBorder(1));
    //scrollPane.setBackground(new Color(171,171,171,255));
    scrollPane.getViewport().setOpaque(true);
    scrollPane.getViewport().setBackground(new Color(171,171,171,255));
    scrollPane.setOpaque(true);
    frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
    
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
    //panel.setPreferredSize(new Dimension(640,480));
    panel.setBackground(new Color(171,171,171,255));
 
    
    drawingArea = new DrawingArea();
    drawingArea.setPreferredSize(new Dimension(640,480));
    panel.add(drawingArea);
    
    scrollPane.setViewportView(panel);
    
    //JToolBar toolBar2 = new JToolBar();
  }
  
  /*
  ActionListener toolbarListener = new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      System.out.println("Test: " + e.paramString());
      for (JToggleButton btn : buttons) {
        btn.setSelected(false);
      }
      button.setSelected(true);
    }
  };
  */
}