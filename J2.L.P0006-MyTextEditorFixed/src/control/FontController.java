/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import frame.FontDialog;
import frame.MTEFrame;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Position;

/**
 *
 * @author MSI
 */
public class FontController implements ListSelectionListener {

    FontDialog fontDialog;
    MTEFrame mainFrame;
    JTextArea textArea;

    JList<String> listFamily;
    JList<String> listStyle;
    JList<String> listSize;

    JTextField txtFamily;
    JTextField txtStyle;
    JTextField txtSize;

    boolean changeByTxtSize;
    
    HashMap<String, Integer> styleMap;

    public FontController(MTEFrame mainFrame) {
        fontDialog = new FontDialog(mainFrame, this);
        textArea = mainFrame.getTextArea();
        setFontDialog();
        setTxtSize();
        changeByTxtSize = false;
    }

    

    void setTxtSize() {
        //add document listener for txtSize
        txtSize.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (txtSize.isFocusOwner()) {
                    editTxtSize();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                insertUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                insertUpdate(e);
            }
        });
    }

    void editTxtSize() {
        //get current font in sample
        Font sampleFont = fontDialog.getTxtSample().getFont();
        //get current text in txtSize
        String sizeStr = txtSize.getText();
        int size = -1;
        try {
            size = Integer.parseInt(sizeStr);
            //set size to 1 if size < 1
            if (size < 1) {
                size = 1;
            }
            fontDialog.getTxtSample().setFont(new Font(sampleFont.getFamily(),
                    sampleFont.getStyle(), size));
            //if text in txtSize exist in list, select it
            if (getIndexAllMatchIgnoreCase(listSize, sizeStr)!= -1) {
                changeByTxtSize = true;
                listSize.setSelectedValue(sizeStr, true);
            }else listSize.clearSelection();
        } catch (NumberFormatException e) {
            return;
        }
    }

    void setFontDialog() {
        fontDialog.setLocationRelativeTo(mainFrame);

        listFamily = fontDialog.getListFamily();
        listStyle = fontDialog.getListStyle();
        listSize = fontDialog.getListSize();

        listSize.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                changeByTxtSize = false;
            }
            
        });
        
        System.out.println("visible row of list size: " + listSize.getVisibleRowCount());

        txtFamily = fontDialog.getTxtFamily();
        txtStyle = fontDialog.getTxtStyle();
        txtSize = fontDialog.getTxtSize();

        //get all font family of system
        String[] fontFamilies = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();
        //set all font to list
        listFamily.setListData(fontFamilies);

        String styles[] = {"Regular", "Italic", "Bold", "Bold Italic"};
        listStyle.setListData(styles);

        Vector<String> sizes = new Vector<>();
        //add size from 8 to 12
        for (int i = 8; i <= 12; i++) {
            sizes.add(i + "");
        }
        //add size from 14 to 28
        for (int i = 14; i <= 28; i += 2) {
            sizes.add(i + "");
        }
        //add the remaining
        sizes.add("36");
        sizes.add("48");
        sizes.add("72");
        listSize.setListData(sizes);

        listFamily.addListSelectionListener(this);
        listStyle.addListSelectionListener(this);
        listSize.addListSelectionListener(this);

        //create hash map of style
        styleMap = new HashMap<>();
        styleMap.put("Regular", Font.PLAIN);
        styleMap.put("Italic", Font.ITALIC);
        styleMap.put("Bold", Font.BOLD);
        styleMap.put("Bold Italic", Font.BOLD + Font.ITALIC);

    }

    void setTxtAreaFontToList() {

        Font currentFont = textArea.getFont();

        //set font to sample label
        fontDialog.getTxtSample().setFont(currentFont);

        //set current size
        txtSize.setText(currentFont.getSize() + "");
        listSize.setSelectedValue(currentFont.getSize() + "", true);
        //set current font family
        txtFamily.setText(currentFont.getFamily());
        listFamily.setSelectedValue(currentFont.getFamily(), true);

        String currentStyle = getKeyFromValue(styleMap, currentFont.getStyle());

        //set current style
        txtStyle.setText(currentStyle);
        listStyle.setSelectedValue(currentStyle, false);

    }

    public void visibleFontDialog() {
        setTxtAreaFontToList();
        fontDialog.setVisible(true);
    }

    public void btOK() {

        Font currentFont = getCurrentFont();
        if (currentFont != null) {
            textArea.setFont(currentFont);
            fontDialog.setVisible(false);
        }

    }

    Font getCurrentFont() {
        String fontFamily;
        String styleStr;
        String sizeStr;

        int style = -1, size = -1;

        fontFamily = fontDialog.getTxtFamily().getText();
        styleStr = fontDialog.getTxtStyle().getText();
        sizeStr = fontDialog.getTxtSize().getText();

        //if font in txt not in list, show msg
        if (getIndexAllMatchIgnoreCase(listFamily, fontFamily) == -1) {
            showMessage("There is no font with that name.\n"
                    + "Choose a font from the list of fonts.");
            return null;
        }

        //if style in txt not in list, show msg
        if (getIndexAllMatchIgnoreCase(listStyle, styleStr) == -1) {
            showMessage("This font is not available in that style.\n"
                    + "Choose a style from the list of styles.");
            return null;
        }

        style = styleMap.get(styleStr);

        try {
            size = Integer.parseInt(sizeStr);
            //size > 1000
            if (size > 1000) {
                showMessage("Size must not exceed 1000.");
                return null;
            }
            //size < 0
            if (size < 0) {
                size = 1;
            }
            //wrong number format
        } catch (NumberFormatException e) {
            showMessage("Size must be a number.");
            return null;
        }

        return new Font(fontFamily, style, size);
    }

    public void btCancel() {
        fontDialog.setVisible(false);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(changeByTxtSize) return;
        Font sampleFont = fontDialog.getTxtSample().getFont();

        if (e.getSource().equals(listFamily)) {
            if (listFamily.isSelectionEmpty()) {
                return;
            }
            //selected font family
            String selectFamily = listFamily.getSelectedValue();
            //create new sample font with selected family
            sampleFont = new Font(selectFamily, sampleFont.getStyle(),
                    sampleFont.getSize());
            //set new font to sample
            fontDialog.getTxtSample().setFont(sampleFont);
            txtFamily.setText(selectFamily);
        } else if (e.getSource().equals(listStyle)) {
            if (listStyle.isSelectionEmpty()) {
                return;
            }
            //selected style
            String selectStyle = listStyle.getSelectedValue();
            //create new sample font with selected style
            sampleFont = sampleFont.deriveFont(styleMap.get(selectStyle));
            //set new font to sample
            fontDialog.getTxtSample().setFont(sampleFont);
            txtStyle.setText(selectStyle);
        } else {
            if (listSize.isSelectionEmpty()) {
                return;
            }
            //selected size
            String selectSize = listSize.getSelectedValue();
            //create new font with selected size
            sampleFont = new Font(sampleFont.getFamily(), sampleFont.getStyle(),
                    Integer.valueOf(selectSize));
            //set new font to sample
            fontDialog.getTxtSample().setFont(sampleFont);
            txtSize.setText(selectSize);
        }

        System.out.println("");
    }


    void showMessage(String message) {
        JOptionPane.showMessageDialog(fontDialog, message, "Font",
                JOptionPane.INFORMATION_MESSAGE);
    }

    String getKeyFromValue(HashMap<String, Integer> map, int value) {
        for (String key : map.keySet()) {
            if (map.get(key) == value) {
                return key;
            }
        }
        return null;
    }

    int getIndexAllMatchIgnoreCase(JList<String> list, String val) {
        ListModel<String> lm = list.getModel();
        //for each element in list
        for (int i = 0; i < lm.getSize(); i++) {
            if (val.equalsIgnoreCase(lm.getElementAt(i))) {
                return i;
            }
        }
        return -1;
    }

}
