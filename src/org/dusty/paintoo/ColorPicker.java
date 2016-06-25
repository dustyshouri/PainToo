package org.dusty.paintoo;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

public class ColorPicker extends JDialog {
  private ColorPicker frame;
  
  private Color[] defaultColors = {
    new Color(255,128,128),new Color(255,255,128),new Color(128,255,128),new Color(0,255,128),
    new Color(128,255,255),new Color(0,128,255),new Color(255,128,192),new Color(255,128,255),
    
    new Color(255,0,0),new Color(255,255,0),new Color(128,255,0),new Color(0,255,64),
    new Color(0,255,255),new Color(0,128,192),new Color(128,128,192),new Color(255,0,255),
    
    new Color(128,64,64),new Color(255,128,64),new Color(0,255,0),new Color(0,128,128),
    new Color(0,64,128),new Color(128,128,255),new Color(128,0,64),new Color(255,0,128),
    
    // TEMP    
    new Color(255,128,128),new Color(255,255,128),new Color(128,255,128),new Color(0,255,128),
    new Color(128,255,255),new Color(0,128,255),new Color(255,128,192),new Color(255,128,255),
    new Color(255,0,0),new Color(255,255,0),new Color(128,255,0),new Color(0,255,64),
    new Color(0,255,255),new Color(0,128,192),new Color(128,128,192),new Color(255,0,255),
    new Color(128,64,64),new Color(255,128,64),new Color(0,255,0),new Color(0,128,128),
    new Color(0,64,128),new Color(128,128,255),new Color(128,0,64),new Color(255,0,128),
    new Color(255,128,128),new Color(255,255,128),new Color(128,255,128),new Color(0,255,128),
    new Color(128,255,255),new Color(0,128,255),new Color(255,128,192),new Color(255,128,255),
    new Color(255,0,0),new Color(255,255,0),new Color(128,255,0),new Color(0,255,64),
    new Color(0,255,255),new Color(0,128,192),new Color(128,128,192),new Color(255,0,255),
    new Color(128,64,64),new Color(255,128,64),new Color(0,255,0),new Color(0,128,128),
    new Color(0,64,128),new Color(128,128,255),new Color(128,0,64),new Color(255,0,128),
    new Color(0,64,128),new Color(128,128,255),new Color(128,0,64),new Color(255,0,128),
    new Color(0,64,128),new Color(128,128,255),new Color(128,0,64),new Color(255,0,128),
    
  };
  
  private JPanel[] colorButtons = new JPanel[defaultColors.length];
  
  public ColorPicker(JFrame parent) {
    frame = this;
    setModal(true);
    setIconImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE));
    setResizable(false);
    setTitle("Edit Colors");
    setLayout(null);
    setBounds(parent.getX() + (parent.getWidth() - 463)/2, parent.getY() + (parent.getHeight() - 337)/2, 463, 302);
    setAutoRequestFocus(true);
    //setModalityType(Dialog.ModalityType.MODELESS);
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    
    Insets insets = getInsets();
    
    JLabel selectText = new JLabel("Basic Colors:");
    selectText.setBounds(insets.left + 7, insets.top + 6, 100, 16);
    add(selectText);
    
    for (int i = 0; i < defaultColors.length;i++) {
      JPanel defColorBox = new JPanel();
      defColorBox.setBackground(defaultColors[i]);
      defColorBox.setForeground(defaultColors[i]);
      defColorBox.setPreferredSize(new Dimension(19,17));
      defColorBox.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(255, 255, 255), new Color(240, 240, 240), new Color(105,105,105), new Color(160,160,160)));
      defColorBox.setBounds(insets.left + 10 + 26*(i%8), insets.top + 26 + 22*(int)(i/8), 19, 17);
      add(defColorBox);
      colorButtons[i] = defColorBox;
    }
    
    // 'OK' button
    JButton okayButton = new JButton("OK");
    okayButton.setBounds(insets.left + 6, insets.top + 245, 66, 23);
    add(okayButton);
    
    okayButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        frame.dispose();
      }
    });
    
    // 'Cancel' button
    JButton cancelButton = new JButton("Cancel");
    cancelButton.setBounds(insets.left + 78, insets.top + 245, 66, 23);
    add(cancelButton);

    
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        frame.dispose();
      }
    });
    
    JPanel colorPickerBox = new JPanel();
    colorPickerBox.setBackground(Color.green);
    colorPickerBox.setBounds(insets.left + 228, insets.top + 7, 177, 189);
    colorPickerBox.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(255, 255, 255), new Color(0,0,0,0), new Color(0,0,0,0), null));
    add(colorPickerBox);
    
    JPanel colorLookBox = new JPanel();
    colorLookBox.setBackground(Color.green);
    colorLookBox.setBounds(insets.left + 228, insets.top + 202, 60, 42);
    colorLookBox.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(255, 255, 255), new Color(0,0,0,0), new Color(0,0,0,0), null));
    add(colorLookBox);
    
    NumberFormatter nf = new NumberFormatter();
    nf.setMinimum(new Integer(0));
    nf.setMaximum(new Integer(255));
    
    // Hue
    JLabel textHue = new JLabel("Hue:");
    textHue.setBounds(insets.left + 324 - 25, insets.top + 204, 40, 16);
    add(textHue);
    JFormattedTextField editHue = new JFormattedTextField(nf);
    editHue.setText("255");
    editHue.setBounds(insets.left + 324, insets.top + 202, 27, 20);
    add(editHue);
    
    // Saturation
    JLabel textSat = new JLabel("Sat:");
    textSat.setBounds(insets.left + 324 - 22, insets.top + 204 + 23, 40, 16);
    add(textSat);
    JFormattedTextField editSat = new JFormattedTextField(createFormatter("###"));
    editSat.setText("255");
    editSat.setBounds(insets.left + 324, insets.top + 201 + 23, 27, 20);
    add(editSat);
    
    // Luminosity
    JLabel textLum = new JLabel("Lum:");
    textLum.setBounds(insets.left + 324 - 25, insets.top + 203 + 23*2, 40, 16);
    add(textLum);
    JFormattedTextField editLum = new JFormattedTextField(createFormatter("###"));
    editLum.setText("255");
    editLum.setBounds(insets.left + 324, insets.top + 201 + 23*2, 27, 20);
    add(editLum);
    
    // Red
    JLabel textRed = new JLabel("Red:");
    textRed.setBounds(insets.left + 324 + 80 - 25, insets.top + 204, 40, 16);
    add(textRed);
    JFormattedTextField editRed = new JFormattedTextField(createFormatter("###"));
    editRed.setText("255");
    editRed.setBounds(insets.left + 324 + 80, insets.top + 202, 27, 20);
    add(editRed);
    
    // Green
    JLabel textGreen = new JLabel("Green:");
    textGreen.setBounds(insets.left + 324 + 80 - 35, insets.top + 205 + 23, 40, 16);
    add(textGreen);
    JFormattedTextField editGreen = new JFormattedTextField(createFormatter("###"));
    editGreen.setText("255");
    editGreen.setBounds(insets.left + 324 + 80, insets.top + 201 + 23, 27, 20);
    add(editGreen);
    
    // Blue
    JLabel textBlue = new JLabel("Blue:");
    textBlue.setBounds(insets.left + 324 + 80 - 26, insets.top + 204 + 23*2, 40, 16);
    add(textBlue);
    JFormattedTextField editBlue = new JFormattedTextField(createFormatter("###"));
    editBlue.setText("255");
    editBlue.setBounds(insets.left + 324 + 80, insets.top + 201 + 23*2, 27, 20);
    add(editBlue);
    
    setVisible(true);
  }
  
  private MaskFormatter createFormatter(String s) {
    MaskFormatter formatter = null;
    try {
        formatter = new MaskFormatter(s);
    } catch (java.text.ParseException exc) {
        System.err.println("formatter is bad: " + exc.getMessage());
        System.exit(-1);
    }
    return formatter;
}
 
}