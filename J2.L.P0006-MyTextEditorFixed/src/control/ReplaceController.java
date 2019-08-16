/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import frame.FindFrame;
import frame.MTEFrame;
import frame.ReplaceFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author MSI
 */
public class ReplaceController {

    ReplaceFrame repFrame;
    JTextArea textArea;
    JTextField txtFind;
    JTextField txtReplace;
    MTEFrame mainFrame;
    int currentIndex;
    boolean isFirstFind;

    public ReplaceController(MTEFrame frame) {
        this.mainFrame = frame;
        repFrame = new ReplaceFrame(this, frame);
        textArea = frame.getTextArea();
        txtFind = repFrame.getTxtFind();
        txtReplace = repFrame.getTxtReplace();
        setRepFrame();
        setTxtFind();
    }

    void setRepFrame() {
        repFrame.setLocationRelativeTo(mainFrame);
        repFrame.addWindowListener(new WindowAdapter() {
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
                updateEnableButton();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateEnableButton();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
    }

    public void visibleRepDialog() {
        isFirstFind = true;
        repFrame.setVisible(true);
    }

    public boolean btFind() {
        boolean match = repFrame.getCheckMatch().isSelected();
        String findWord = txtFind.getText();
        String findArea = textArea.getText();

        if (isFirstFind) {
            currentIndex = 0;
            isFirstFind = false;
        } else {
            currentIndex = textArea.getSelectionEnd();
        }

        //if user want find ignore case, uppercase all
        if (!match) {
            findWord = findWord.toUpperCase();
            findArea = findArea.toUpperCase();
        }

        System.out.println(currentIndex + "vs " + findArea.length());

        //caret is at the end of find area => reach end, show message
        if (currentIndex >= findArea.length()) {
            showMessage(txtFind.getText());
            return false;
        }
        //get start index of right nearest found word (down direction)(in substring)
        int foundIndex = findArea.substring(currentIndex).indexOf(findWord);
        //if not found, show message
        if (foundIndex == -1) {
            showMessage(txtFind.getText());
            return false;
            //found then select the found word
        } else {
            //add current index to get the real found position
            foundIndex += currentIndex;
            textArea.select(foundIndex, foundIndex + findWord.length());
            return true;
        }
    }

    public void btReplace() {
        boolean match = repFrame.getCheckMatch().isSelected();
        String findWord = txtFind.getText();
        String replaceWord = txtReplace.getText();
        int start = textArea.getSelectionStart();
        int end = textArea.getSelectionEnd();
        //if no text is select, find it
        if (start == end) {
            if (btFind()) btReplace();
            //if there is selected text but it not equal find word (no ignore case), find it
        } else if (match && !textArea.getSelectedText().equals(findWord)) {
            if (btFind()) btReplace();
            //if there is selected text but it not equal find word (ignore case), find it
        } else if (!match && !textArea.getSelectedText().equalsIgnoreCase(findWord)) {
            if (btFind()) btReplace();
            //replace selected word
        } else {
            textArea.replaceRange(replaceWord, start, end);
            btFind();
        }
    }

    public void btReplaceAll() {
        String area = textArea.getText();
        String replaceWord = txtReplace.getText();
        String findWord = txtFind.getText();
        //replace all ignore case
        if (!repFrame.getCheckMatch().isSelected()) {
            area = area.replaceAll("(?i)" + findWord, replaceWord);
            //replace all no ignore case
        } else {
            area = area.replaceAll(findWord, replaceWord);
        }
        textArea.setText(area);
    }

    public void btCancel() {
        repFrame.setVisible(false);
    }

    void updateEnableButton() {
        repFrame.getBtFind().setEnabled(!txtFind.getText().isEmpty());
        repFrame.getBtReplace().setEnabled(!txtFind.getText().isEmpty());
        repFrame.getBtReplaceAll().setEnabled(!txtFind.getText().isEmpty());
    }

    void showMessage(String word) {
        JOptionPane.showMessageDialog(mainFrame, "Cannot find \"" + word + "\"",
                "MTE", JOptionPane.INFORMATION_MESSAGE);
    }

}
