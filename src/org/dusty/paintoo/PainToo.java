
package org.dusty.paintoo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import se.deas.paintoo.handlers.FrameKeyEvent;

/*
 * Feature plans:
 *   - many undos. So many
 *   - second pass on pencil tool to reduce adjacent pixels when drawing slow
 *     as seen here: http://pixelation.org/index.php?topic=16163.0
 *   - Add option for default tool on launch(or launch last-used tool)
 *   - Option to change transparency grid size
 * 
 */

public class PainToo extends JFrame {
  private static final long serialVersionUID = 1L;
  private JScrollPane scrollPane;
  private ColorPicker colorPicker;
  private DrawingArea drawingArea;
  private PreviewDrawingArea previewDrawingArea;
  private Background background;
  private DrawingPane drawingPane;
  public Dimension dimension = new Dimension( 1000, 600 );
  public Tool selectedTool;
  public boolean shiftHeld = false;
  private JFrame frame;

  public enum Tool {
    FREEFORM,
    SELECT,
    ERASE,
    BUCKETFILL,
    EYEDROP,
    ZOOM,
    PENCIL,
    BRUSH,
    SPRAY,
    TEXT,
    LINE,
    CURVELINE,
    SQUARE,
    POLYGON,
    ELLIPSE,
    ROUNDSQUARE
  }

  private String[][][] menuItems = { { { "File" }, { "New", "Open", "Save", "Save As", "", "Exit" } },
      { { "Edit" }, { "Undo", "Redo", "", "Cut", "Copy", "Paste", "Clear", "Select All" } }, { { "View" }, { "Tool Box", "Color Box", "Status Bar" } },
      { { "Image" }, { "Flip/Rotate", "Stretch Skew", "Invert", "Attributes", "Clear All" } },
      { { "Colors" }, { "Edit Colors", "", "Import Palette", "Save Palette" } }, { { "Help" }, { "About" } }, };

  private String[][] buttonArray = { { "Freeform Select", "icon_freeform.png" }, { "Select", "icon_select.png" }, { "Eraser", "icon_eraser.png" },
      { "Bucket Fill", "icon_bucketfill.png" }, { "Eyedropper", "icon_eyedropper.png" }, { "Zoom", "icon_zoom.png" }, { "Pencil", "icon_pencil.png" },
      { "Brush", "icon_brush.png" }, { "Spraypaint", "icon_spray.png" }, { "Text", "icon_text.png" }, { "Line", "icon_line.png" },
      { "Curve", "icon_curveline.png" }, { "Rectangle", "icon_rectangle.png" }, { "Polygon", "icon_polygon.png" }, { "Ellipse", "icon_ellipse.png" },
      { "Rounded Rectangle", "icon_roundrectangle.png" }, };

  private JToggleButton buttons[] = new JToggleButton[ buttonArray.length ];

  public PainToo() {
    this.frame = this;
    initialize();
  }

  private void initialize() {
    this.setBounds( 100, 100, 1280 / 2, 840 / 2 );
    this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

    //colorPicker.setIconImage(new ImageIcon(getClass().getResource("/assets/favico.png")).getImage());

    setDrawingPane( new DrawingPane( this ) );
    background = new Background( this );
    drawingArea = new DrawingArea( this );
    previewDrawingArea = new PreviewDrawingArea( this );

    JMenuBar menuBar = new JMenuBar();
    this.setJMenuBar( menuBar );

    for ( String[][] i : menuItems ) {
      String menuCategory = i[ 0 ][ 0 ];
      JMenu menu = new JMenu( menuCategory );
      menuBar.add( menu );
      for ( String j : i[ 1 ] ) {
        if ( j.equals( "" ) )
          menu.addSeparator();
        else {
          JMenuItem menuItem = new JMenuItem( j );
          if ( j.equals( "Edit Colors" ) ) {
            menuItem.addActionListener( new ActionListener() {
              public void actionPerformed( ActionEvent arg0 ) {
                colorPicker = new ColorPicker( frame );
              }
            } );
          }
          menu.add( menuItem );
        }
      }
    }

    this.addKeyListener( new FrameKeyEvent( this ) );
    this.setLayout( new BorderLayout( 0, 0 ) );

    JPanel bottomContainer = new JPanel();
    bottomContainer.setBackground( new Color( 240, 240, 240, 255 ) );
    FlowLayout fl_bottomContainer = ( FlowLayout ) bottomContainer.getLayout();
    fl_bottomContainer.setVgap( 32 );
    bottomContainer.setBorder( new BevelBorder( BevelBorder.RAISED, new Color( 255, 255, 255 ), new Color( 240, 240, 240 ), null, UIManager.getColor( "Label.background" ) ) );
    this.getContentPane().add( bottomContainer, BorderLayout.SOUTH );

    JPanel toolbarContainer = new JPanel( new FlowLayout( FlowLayout.CENTER, 2, 2 ) );
    toolbarContainer.setBackground( new Color( 240, 240, 240, 255 ) );
    toolbarContainer.setBorder( BorderFactory.createMatteBorder( 1, 0, 1, 0, Color.gray ) );
    this.getContentPane().add( toolbarContainer, BorderLayout.WEST );

    /*
    JPanel layersContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
    layersContainer.setBackground(new Color(240,240,240,255));
    layersContainer.setPreferredSize(new Dimension(120,layersContainer.getHeight()));
    layersContainer.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.gray));
    this.getContentPane().add(layersContainer, BorderLayout.EAST);
    
    JToolBar layersToolbar = new JWrapToolbar();
    layersToolbar.setBackground(new Color(240,240,240,255));
    //toolbar.setLayout(new WrapLayout(0, 0, 0));
    layersToolbar.setSize(120,1);
    layersToolbar.setRollover(true);
    layersToolbar.setOrientation(SwingConstants.VERTICAL);
    layersToolbar.setFloatable(true);
    layersToolbar.setFocusable(false);
    layersContainer.add(layersToolbar);
    */

    JToolBar toolbar = new JWrapToolbar();
    toolbar.setBackground( new Color( 240, 240, 240, 255 ) );
    //toolbar.setLayout(new WrapLayout(0, 0, 0));
    toolbar.setSize( 56, 1 );
    toolbar.setRollover( true );
    toolbar.setOrientation( SwingConstants.VERTICAL );
    toolbar.setFloatable( false );
    toolbar.setFocusable( false );
    toolbarContainer.add( toolbar );

    for ( int i = 0; i < buttonArray.length; i ++ ) {
      String text = buttonArray[ i ][ 0 ];
      String icon = buttonArray[ i ][ 1 ];

      JToggleButton button = new JToggleButton( "" );
      //button.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage("assets/icons/" + icon)));
      button.setIcon( new ImageIcon( getClass().getResource( "/assets/icons/" + icon ) ) );
      button.setToolTipText( text );
      button.setPreferredSize( new Dimension( 25, 25 ) );
      button.setFocusPainted( false );
      button.setActionCommand( text );

      button.addActionListener( new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
          for ( JToggleButton btn : buttons ) {
            btn.setSelected( false );
            btn.setFocusable( false );
          }
          button.setSelected( true );
          System.out.println( e.getActionCommand() );
          if ( e.getActionCommand().equals( "Zoom" ) )
            changeTool( Tool.ZOOM );
          else if ( e.getActionCommand().equals( "Bucket Fill" ) )
            changeTool( Tool.BUCKETFILL );
          else if ( e.getActionCommand().equals( "Brush" ) )
            changeTool( Tool.BRUSH );
          else if ( e.getActionCommand().equals( "Pencil" ) )
            changeTool( Tool.PENCIL );
          else changeTool( null );
        }
      } );

      toolbar.add( button );
      buttons[ i ] = button;
    }

    scrollPane = new JScrollPane();
    scrollPane.setBorder( BorderFactory.createBevelBorder( 1 ) );
    //scrollPane.setBackground(new Color(171,171,171,255));
    scrollPane.getViewport().setOpaque( true );
    scrollPane.getViewport().setBackground( new Color( 171, 171, 171, 255 ) );
    scrollPane.setOpaque( true );
    scrollPane.getVerticalScrollBar().setUnitIncrement( 200 );
    scrollPane.getHorizontalScrollBar().setUnitIncrement( 200 );
    this.getContentPane().add( scrollPane, BorderLayout.CENTER );

    JPanel panel = new JPanel( new FlowLayout( FlowLayout.LEFT, 2, 2 ) );
    //panel.setPreferredSize(new Dimension(640,480));
    panel.setBackground( new Color( 171, 171, 171, 255 ) );

    getDrawingPane().addLayer( previewDrawingArea );
    getDrawingPane().addLayer( drawingArea );
    getDrawingPane().addLayer( background );

    panel.add( getDrawingPane() );

    //drawingArea.setPreferredSize(new Dimension(640,480));
    //panel.add(drawingArea);

    scrollPane.setViewportView( panel );
    changeTool( Tool.BRUSH );

    //JToolBar toolBar2 = new JToolBar();
  }

  public void changeTool( Tool toolName ) {
    boolean defaultTool = false;
    if ( toolName == null ) {
      defaultTool();
      return;
    }
    System.out.println( "Tool change to: " + toolName.toString() );
    switch ( toolName ) {
      case ZOOM:
        getDrawingPane().setCursor( PaintCursor.Cursors.ZOOM );
        selectedTool = Tool.ZOOM;
        break;
      case BUCKETFILL:
        getDrawingPane().setCursor( PaintCursor.Cursors.BUCKETFILL );
        selectedTool = Tool.BUCKETFILL;
        break;
      case PENCIL:
        selectedTool = Tool.PENCIL;
        break;
      default:
        defaultTool = true;
        defaultTool();
        break;
    }

    if ( defaultTool ) return;

    for ( JToggleButton btn : buttons ) {
      btn.setSelected( false );
      btn.setFocusable( false );
    }
    buttons[ selectedTool.ordinal() ].setSelected( true );
    getDrawingPane().clearPreview();
    getDrawingPane().previewPixel();
  }

  public Image getImageFromClipboard() {
    System.out.println( "Getting Clipboard..." );
    Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents( null );
    if ( transferable != null && transferable.isDataFlavorSupported( DataFlavor.imageFlavor ) ) {
      try {
        return ( Image ) transferable.getTransferData( DataFlavor.imageFlavor );
      }
      catch ( UnsupportedFlavorException e ) {
        // handle this as desired
        e.printStackTrace();
      }
      catch ( IOException e ) {
        // handle this as desired
        e.printStackTrace();
      }
    }
    else {
      System.err.println( "getImageFromClipboard: That wasn't an image!" );
    }
    return null;
  }

  public Tool getTool() {
    return selectedTool;
  }

  public DrawingPane getDrawingPane() {
    return drawingPane;
  }

  public void setDrawingPane( DrawingPane drawingPane ) {
    this.drawingPane = drawingPane;
  }

  public void defaultTool() {
    selectedTool = Tool.BRUSH;
    getDrawingPane().setCursor( PaintCursor.Cursors.CROSSHAIR );

    for ( JToggleButton btn : buttons ) {
      btn.setSelected( false );
      btn.setFocusable( false );
    }
    buttons[ selectedTool.ordinal() ].setSelected( true );
  }

  public static void main( String[] args ) {
    try {
      UIManager.setLookAndFeel( "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" );
    }
    catch ( Exception e ) {
      System.err.println( e.getMessage() );
    }

    EventQueue.invokeLater( new Runnable() {
      public void run() {
        PainToo window = new PainToo();
        //window.this.setIconImage(Toolkit.getDefaultToolkit().getImage("assets/favico.png"));
        window.setIconImage( new ImageIcon( getClass().getResource( "/assets/favico.png" ) ).getImage() );
        window.setTitle( "untitled - Paint" );
        window.setMinimumSize( new Dimension( 500, 400 ) );
        //window.this.setExtendedState(PainToo.this.getExtendedState() | Jthis.MAXIMIZED_BOTH);
        window.setVisible( true );
      }
    } );
  }

}
