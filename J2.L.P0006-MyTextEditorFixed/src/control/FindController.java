/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package control;

import frame.FindFrame;
import frame.MTEFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author MSI
 */

public class FindController {
    
    JTextArea textArea;
    JTextField txtFind;
    FindFrame findFrame;
    MTEFrame mainFrame;
    int currentIndex;
    
    public FindController(MTEFrame mainFrame) {
        this.mainFrame = mainFrame;
        findFrame = new FindFrame(this,mainFrame);
        textArea = mainFrame.getTextArea();
        txtFind = findFrame.getTxtFind();
        setFindFrame();
        setTxtFind();
    }
    
    void setFindFrame() {
        findFrame.setLocationRelativeTo(mainFrame);
        findFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                btCancel();
            }
        });
    }
    
    void setTxtFind() {
        txtFind.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                enableButton();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                enableButton();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                enableButton();
            }
        });
    }
    
    public void visibleFindDialog() {
        findFrame.setVisible(true);
    }
    
    public void btFind() {
        currentIndex = textArea.getSelectionEnd();
        
        String findWord = txtFind.getText();
        String findArea = textArea.getText();
        //if user want find ignore case, uppercase all
        if(!findFrame.getCheckMatch().isSelected()) {
            findWord = findWord.toUpperCase();
            findArea = findArea.toUpperCase();
        }
        //user want find down
        if(findFrame.getRadioDown().isSelected()) {
            findDown(findWord,findArea);
        //user want find up
        }else {
            findUp(findWord,findArea);
        }
    }
    
    public void findUp(String findWord, String findArea) {
        //caret is at beginning => reach end (up direction)
        if(currentIndex==0) {
            showMessage(findWord);
            return;
        }
        //if there is seletion text
        if(textArea.getSelectionStart()!=textArea.getSelectionEnd()) {
            //if find word is selected, move current index to left
            if(findWord.equalsIgnoreCase(textArea.getSelectedText())) {
                currentIndex = textArea.getSelectionStart();
            }
        }
        
        //get the start index of left nearest found word (up direction)
        int foundIndex = findArea.substring(0, currentIndex).lastIndexOf(findWord);
        //if not found
        if(foundIndex==-1) {
            showMessage(findWord);
        //found then select the found word
        }else {
            textArea.select(foundIndex, foundIndex+findWord.length());
        }
    }
    
    public void findDown(String findWord, String findArea) {
        //caret is at the end of find area => reach end, show message
        if(currentIndex>=findArea.length()) {
            showMessage(findWord);
            return;
        }
        //get start index of right nearest found word (down direction)(in substring)
        int foundIndex = findArea.substring(currentIndex).indexOf(findWord);
        //if not found, show message
        if (foundIndex == -1) {
            showMessage(txtFind.getText());
        //found then select the found word
        }else {
            //add current index to get the real found position
            foundIndex += currentIndex;
            textArea.select(foundIndex, foundIndex+findWord.length());
        }
    }
    
    public void enableButton() {
        findFrame.getBtFind().setEnabled(!txtFind.getText().isEmpty());
    }
    
    public void btCancel() {
        findFrame.setVisible(false);
    }
    
    void showMessage(String word) {
        JOptionPane.showMessageDialog(mainFrame, "Cannot find \""+word+"\"", 
                "MTE",JOptionPane.INFORMATION_MESSAGE);
    }
    
}
