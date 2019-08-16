/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import frame.MTEFrame;
import java.awt.FileDialog;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.undo.UndoManager;

/**
 *
 * @author MSI
 */
public class MainController {

    String checkSaveContent;
    File currentFile;
    MTEFrame frame;
    JTextArea textArea;
    FileDialog fileDialog;
    JFileChooser fileChooser;
    UndoManager undoManager;

    FindController findControl;
    ReplaceController repControl;
    FontController fontControl;

    public MainController(MTEFrame frame) {
        this.frame = frame;
        textArea = frame.getTextArea();
        setFrame();
        setFileDialog();
        setFileChooser();
        setUndoManager();
        checkSaveContent = "";
        currentFile = null;
//        textArea.setText("");
        setTextArea();
        findControl = new FindController(frame);
        repControl = new ReplaceController(frame);
        fontControl = new FontController(frame);
    }

    void setTextArea() {
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateEnableItem();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateEnableItem();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
    }

    void setUndoManager() {
        undoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(undoManager);
    }

    void setFrame() {
        //do the same thing as item exit when user close window
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                itemExit();
            }
        });
    }

    void setFileChooser() {
        fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                return f.getName().toLowerCase().endsWith(".txt");
            }

            @Override
            public String getDescription() {
                return "Text Documents (*.txt)";
            }
        });
        fileChooser.setCurrentDirectory(new File("Desktop"));
    }

    void setFileDialog() {
        fileDialog = new FileDialog(frame);
        fileDialog.setDirectory("Desktop");
    }

    boolean showAskSaveDialog() {
        int reply = JOptionPane.showConfirmDialog(frame, "Do you want to save change to this text document?");
        //if user choose yes, do save
        if (reply == JOptionPane.YES_OPTION) {
            //continue before operation if user choose save, cancel before
            //operation if user choose cancel or close save dialog
            return itemSave();
            //if user choose no, do not save and continue before operation
        } else if (reply == JOptionPane.NO_OPTION) {
            return true;
        } //if user choose cancel or close dialog, cancel before operation
        else {
            return false;
        }
    }

    public void itemNew() {
        boolean doNewOperation = true;
        //if user has not save or have change content from last save, ask to save
        if (!checkSaveContent.equals(textArea.getText())) {
            doNewOperation = showAskSaveDialog();
        }
        //if user not choose cancel, continue do new operation
        if (doNewOperation) {
            checkSaveContent = "";
            currentFile = null;
            textArea.setText("");
        }
    }

    public void itemOpen() {
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            try {
                //get input stream of file
                FileInputStream fins = new FileInputStream(currentFile);
                DataInputStream din = new DataInputStream(fins);
                //create byte array to contain data of file
                byte data[] = new byte[fins.available()];
                StringBuilder allData = new StringBuilder();
                //read data in file to array
                din.read(data);
                //for each character in data array, append it to string builder
                for (byte character : data) {
                    allData.append((char) character);
                }
                //set text in text area to string builder
                textArea.setText(allData.toString());
                din.close();
                //                BufferedReader fin = new BufferedReader(new FileReader(currentFile));
                //                String line = null;
                //                String allData = null;
                //                while((line = fin.readLine())!=null) {
                //                    allData+=line+"\n";
                //                }
                //                textArea.setText(allData);
                //                fin.close();
                checkSaveContent = textArea.getText();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Cannot open file!");
                ex.printStackTrace();
            }
        }
    }

    public boolean itemSave() {
        //if there is no file to save, do save as
        if (currentFile == null) {
            return itemSaveAs();
        } else {
            try {
                FileWriter fout = new FileWriter(currentFile);
                //write text in text area to file
                fout.write(textArea.getText());
                checkSaveContent = textArea.getText();
                fout.close();
                return true;
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Cannot save file!");
                ex.printStackTrace();
            }
            return false;
        }
    }

    public boolean itemSaveAs() {
        int choice;
        File checkExistFile;
        //break when file name not existed or existed and user want to replace
        while (true) {
            choice = fileChooser.showSaveDialog(frame);
            //if user not save
            if (choice != JFileChooser.APPROVE_OPTION) {
                return false;
            }

            checkExistFile = fileChooser.getSelectedFile();
            if (checkExistFile.exists()) {
                int confirm = JOptionPane.showConfirmDialog(frame, "This file has existed, do you want to replace it?", "Replace",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    break;
                }
            } else {
                break;
            }
        }

        currentFile = fileChooser.getSelectedFile();
        
        //if user enter 
        if (!currentFile.getName().contains(".")) {
            String path = currentFile.getPath() + ".txt";
            currentFile = new File(path);
        }
        try {
            FileWriter fout = new FileWriter(currentFile);
            //write content in text area to file
            fout.write(textArea.getText());
            checkSaveContent = textArea.getText();
            fout.close();
            //save successful
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Cannot save file!");
            ex.printStackTrace();
        }

        return false;
    }

    public void itemExit() {
        boolean doExit = true;
        //if user has not save or have change content from last save, ask to save
        if (!checkSaveContent.equals(textArea.getText())) {
            doExit = showAskSaveDialog();
        }
        //if user choose yes and save or no, exit program
        if (doExit) {
            System.exit(0);
        }
    }

    public void itemSelectAll() {
        textArea.selectAll();
    }

    public void itemCut() {
        textArea.cut();
    }

    public void itemCopy() {
        textArea.copy();
    }

    public void itemPaste() {
        textArea.paste();
    }

    public void itemUndo() {
        undoManager.undo();
        frame.getItUndo().setEnabled(undoManager.canUndo());
    }

    public void itemRedo() {
        undoManager.redo();
        frame.getItRedo().setEnabled(undoManager.canRedo());
    }

    public void itemFind() {
        findControl.visibleFindDialog();
    }

    public void itemReplace() {
        repControl.visibleRepDialog();
    }

    public void itemFont() {
        fontControl.visibleFontDialog();
    }

    public void updateEnableItem() {
        boolean selected = textArea.getSelectionStart() != textArea.getSelectionEnd();
        frame.getItSelect().setEnabled(!textArea.getText().isEmpty());
        frame.getItCopy().setEnabled(selected);
        frame.getItCut().setEnabled(selected);
        frame.getItUndo().setEnabled(undoManager.canUndo());
        frame.getItRedo().setEnabled(undoManager.canRedo());
        frame.getItFind().setEnabled(!textArea.getText().isEmpty());
    }

    public JTextArea getTextArea() {
        return textArea;
    }

}
