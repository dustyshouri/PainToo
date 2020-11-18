package org.dusty.paintoo;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
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
import javax.swing.JComponent;
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
 *   - Add option for default tool on launch(or launch last-used tool)
 *   - Option to change transparency grid size
 * 
 */

public class PainToo {
  private JFrame frame;
  DrawingArea drawingArea;
  PreviewDrawingArea previewDrawingArea;
  Background background;
  DrawingPane drawingPane;
  Tool selectedTool;
  
  public enum Tool {
    FREEFORM, SELECT, ERASE, BUCKETFILL, EYEDROP, ZOOM, PENCIL, BRUSH, 
    SPRAY, TEXT, LINE, CURVELINE, SQUARE, POLYGON, ELLIPSE, ROUNDSQUARE
  }
  
  String[][][] menuItems= {
      {{"File"}  ,{"New","Open","Save","Save As","","Exit"}},
      {{"Edit"}  ,{"Undo","Redo","","Cut","Copy","Paste","Clear","Select All"}},
      {{"View"}  ,{"Tool Box","Color Box","Status Bar"}},
      {{"Image"} ,{"Flip/Rotate","Stretch Skew","Invert","Attributes","Clear All"}},
      {{"Colors"},{"Edit Colors","","Import Palette","Save Palette"}},
      {{"Help"}  ,{"About"}},
  };
  
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
          //window.frame.setIconImage(Toolkit.getDefaultToolkit().getImage("assets/favico.png"));
          window.frame.setIconImage(new ImageIcon(getClass().getResource("/assets/favico.png")).getImage());
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
    
    drawingPane = new DrawingPane(this);
    background = new Background(this);
    drawingArea = new DrawingArea(this);
    previewDrawingArea = new PreviewDrawingArea(this);
    
    JMenuBar menuBar = new JMenuBar();
    frame.setJMenuBar(menuBar);
    
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
    toolbar.setFocusable(false);
    
    for (int i = 0;i < buttonArray.length;i++) {
      String text = buttonArray[i][0];
      String icon = buttonArray[i][1];
      
      JToggleButton button = new JToggleButton("");
      //button.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage("assets/icons/" + icon)));
      button.setIcon(new ImageIcon(getClass().getResource("/assets/icons/" + icon)));
      button.setToolTipText(text);
      button.setPreferredSize(new Dimension(25,25));
      button.setFocusPainted(false);
      button.setActionCommand(text);
      
      button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          for (JToggleButton btn : buttons) {
            btn.setSelected(false);
            btn.setFocusable(false);
          }
          button.setSelected(true);
          System.out.println(e.getActionCommand());
          if (e.getActionCommand().equals("Zoom")) changeTool(Tool.ZOOM);
          else if (e.getActionCommand().equals("Brush")) changeTool(Tool.BRUSH);
          else changeTool(null);
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
    scrollPane.getVerticalScrollBar().setUnitIncrement(200);
    scrollPane.getHorizontalScrollBar().setUnitIncrement(200);
    frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
    
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
    //panel.setPreferredSize(new Dimension(640,480));
    panel.setBackground(new Color(171,171,171,255));
    
    drawingPane.addLayer(previewDrawingArea);
    drawingPane.addLayer(drawingArea);
    drawingPane.addLayer(background);

    panel.add(drawingPane);
    
    //drawingArea.setPreferredSize(new Dimension(640,480));
    //panel.add(drawingArea);
    
    scrollPane.setViewportView(panel);
    changeTool(Tool.BRUSH);
    
    //JToolBar toolBar2 = new JToolBar();
  }
  
  public void changeTool(Tool toolName) {
    boolean defaultTool = false;
    if (toolName == null) {
      defaultTool();
      return;
    }
    System.out.println("Tool change to: " + toolName.toString());
    switch (toolName) {
      case ZOOM: 
        drawingPane.setCursor(PaintCursor.Cursors.ZOOM);
        selectedTool = Tool.ZOOM;
      break;
      default: 
        defaultTool = true; 
        defaultTool();
      break;
    }
    
    if (defaultTool) return;
    
    for (JToggleButton btn : buttons) {
      btn.setSelected(false);
      btn.setFocusable(false);
    }
    buttons[selectedTool.ordinal()].setSelected(true);
  }
  
  public Tool getTool() {
    return selectedTool;
  }
  
  public void defaultTool() {
    selectedTool = Tool.BRUSH;
    drawingPane.setCursor(PaintCursor.Cursors.CROSSHAIR);
    
    for (JToggleButton btn : buttons) {
      btn.setSelected(false);
      btn.setFocusable(false);
    }
    buttons[selectedTool.ordinal()].setSelected(true);
  }

}