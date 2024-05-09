import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;


import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;


public class Notepad {

    private JFrame frame;
    private JTextArea textArea;
    private File currentFile;
    private boolean isTextChanged = false;
    private JPanel statusBar;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Notepad window = new Notepad();
                    window.startWebSocketServer();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Notepad() {
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        frame = new JFrame();
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });

        ImageIcon icon = new ImageIcon(Notepad.class.getResource("icons/notepad.png"));
        frame.setIconImage(icon.getImage()); 

        frame.setTitle("Version II");

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.NORTH);

        JButton btnSave = new JButton("");
        btnSave.setIcon(new ImageIcon(Notepad.class.getResource("icons/save.png")));
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });
        

        JButton btnOpen = new JButton("");
        btnOpen.setIcon(new ImageIcon(Notepad.class.getResource("icons/open.png")));
        btnOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });

        textArea = new JTextArea();
        textArea.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        createMenuBar();
        createStatusBar();
    }

    void appendTextFromHTML(String text) {
        textArea.append(text + " ");
    }

    private void startWebSocketServer() {
        NotepadWebSocketServer server = new NotepadWebSocketServer(this, 8080);
        server.start();
        System.out.println("WebSocket server started on port 8080");
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
    
        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);
    
        JMenuItem mntmNew = new JMenuItem("New");
        mntmNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        mntmNew.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newFile();
            }
        });
        mnFile.add(mntmNew);
    
        JMenuItem mntmNewWindow = new JMenuItem("New Window");
        mntmNewWindow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newWindow();
            }
        });
        mnFile.add(mntmNewWindow);
    
        JMenuItem mntmOpen = new JMenuItem("Open");
        mntmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        mntmOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });
        mnFile.add(mntmOpen);
    
        JMenuItem mntmPrint = new JMenuItem("Print");
        mntmPrint.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                print();
            }
        });
        mnFile.add(mntmPrint);
    
        JMenuItem mntmSave = new JMenuItem("Save");
        mntmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        mntmSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });
        mnFile.add(mntmSave);
    
        JMenuItem mntmSaveAs = new JMenuItem("Save As...");
        mntmSaveAs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveAsFile();
            }
        });
        mnFile.add(mntmSaveAs);
    
        JMenuItem mntmExit = new JMenuItem("Exit");
        mntmExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });
        mnFile.add(mntmExit);
    
        JMenu mnEdit = new JMenu("Edit");
        menuBar.add(mnEdit);
    
        JMenuItem mntmCut = new JMenuItem("Cut");
        Action cutAction = new AbstractAction("Cut") {
            public void actionPerformed(ActionEvent e) {
                textArea.cut();
            }
        };
        mntmCut.setAction(cutAction);
        mntmCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
        mnEdit.add(mntmCut);
    
        JMenuItem mntmCopy = new JMenuItem("Copy");
        Action copyAction = new AbstractAction("Copy") {
            public void actionPerformed(ActionEvent e) {
                textArea.copy();
            }
        };
        mntmCopy.setAction(copyAction);
        mntmCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
        mnEdit.add(mntmCopy);
    
        JMenuItem mntmPaste = new JMenuItem("Paste");
        Action pasteAction = new AbstractAction("Paste") {
            public void actionPerformed(ActionEvent e) {
                textArea.paste();
            }
        };
        mntmPaste.setAction(pasteAction);
        mntmPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
        mnEdit.add(mntmPaste);
    
        JMenuItem mntmSelectAll = new JMenuItem("Select All");
        mntmSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
        mntmSelectAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.selectAll();
            }
        });
        mnEdit.add(mntmSelectAll);
    
        JMenuItem mntmFind = new JMenuItem("Find");
        mntmFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
        mntmFind.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                find();
            }
        });
        mnEdit.add(mntmFind);
    
        JMenuItem mntmReplace = new JMenuItem("Replace");
        mntmReplace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK));
        mntmReplace.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                replace();
            }
        });
        mnEdit.add(mntmReplace);
    
        JMenu mnFormat = new JMenu("Format");
        menuBar.add(mnFormat);
    
        JMenuItem mntmSetFont = new JMenuItem("Set Font...");
        mntmSetFont.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setFont();
            }
        });
        mnFormat.add(mntmSetFont);
    
        JMenuItem mntmSetFontColor = new JMenuItem("Set Font Color...");
        mntmSetFontColor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setFontColor();
            }
        });
        mnFormat.add(mntmSetFontColor);
    
        JMenuItem mntmSetFontSize = new JMenuItem("Set Font Size...");
        mntmSetFontSize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setFontSize();
            }
        });
        mnFormat.add(mntmSetFontSize);
    
        JMenu mnView = new JMenu("View");
        menuBar.add(mnView);
    
        JCheckBoxMenuItem chckbxmntmStatusBar = new JCheckBoxMenuItem("Show Status Bar");
        chckbxmntmStatusBar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusBar.setVisible(chckbxmntmStatusBar.isSelected());
            }
        });
        mnView.add(chckbxmntmStatusBar);
    
        mnFile.addSeparator();
        JMenuItem mntmSaveButton = new JMenuItem(new AbstractAction("Save", new ImageIcon(Notepad.class.getResource("icons/save.png"))) {
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });
        mntmSaveButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        mnFile.add(mntmSaveButton);
    
        JMenuItem mntmOpenButton = new JMenuItem(new AbstractAction("Open", new ImageIcon(Notepad.class.getResource("icons/open.png"))) {
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });
        mntmOpenButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        mnFile.add(mntmOpenButton);
    }
    
    private void find() {
        String searchText = JOptionPane.showInputDialog(frame, "Enter text to find:");
        if (searchText != null) {
            String text = textArea.getText();
            int index = text.indexOf(searchText);
            if (index != -1) {
                textArea.setCaretPosition(index);
                textArea.moveCaretPosition(index + searchText.length());
            } else {
                JOptionPane.showMessageDialog(frame, "Text not found", "Find", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void replace() {
        String findText = JOptionPane.showInputDialog(frame, "Enter text to find:");
        if (findText != null) {
            String replaceText = JOptionPane.showInputDialog(frame, "Enter text to replace with:");
            if (replaceText != null) {
                String text = textArea.getText();
                text = text.replace(findText, replaceText);
                textArea.setText(text);
            }
        }
    }
    
   private void createStatusBar() {
    statusBar = new JPanel();
    statusBar.setLayout(new BorderLayout());
    statusBar.setBackground(Color.lightGray);
    frame.getContentPane().add(statusBar, BorderLayout.SOUTH);

    JLabel lineLabel = new JLabel("Line: 1 ");
    statusBar.add(lineLabel,BorderLayout.WEST);

    JLabel columnLabel = new JLabel(" Column: 1");
    statusBar.add(columnLabel, BorderLayout.CENTER);

    textArea.getDocument().addDocumentListener(new DocumentListener() {
        public void insertUpdate(DocumentEvent e) {
            updateStatusBar();
        }

        public void removeUpdate(DocumentEvent e) {
            updateStatusBar();
        }

        public void changedUpdate(DocumentEvent e) {
            updateStatusBar();
        }

        private void updateStatusBar() {
            try {
                int caretPosition = textArea.getCaretPosition();
                int lineNumber = textArea.getLineOfOffset(caretPosition) + 1;
                int columnNumber = caretPosition - textArea.getLineStartOffset(lineNumber - 1) + 1;
                lineLabel.setText("Line: " + lineNumber+" ");
                columnLabel.setText(" Column: " + columnNumber);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
    });
}

    private void newFile() {
        if (isTextChanged) {
            int option = JOptionPane.showConfirmDialog(frame, "Do you want to save changes to " + 
                                                         (currentFile != null ? currentFile.getName() : "Untitled") + "?",
                                                         "Save Changes", JOptionPane.YES_NO_CANCEL_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                saveFile();
            } else if (option == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        textArea.setText("");
        currentFile = null;
        frame.setTitle("Untitled - Notepad");
        isTextChanged = false;
    }

    private void newWindow() {
        Notepad newNotepad = new Notepad();
        newNotepad.frame.setVisible(true);
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            StringBuilder text = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    text.append(line).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            textArea.setText(text.toString());
            currentFile = selectedFile;
            frame.setTitle(currentFile.getName() + " - Notepad");
            isTextChanged = false;
        }
    }

    private void saveFile() {
        if (currentFile == null) {
            saveAsFile();
        } else {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
                writer.write(textArea.getText());
                frame.setTitle(currentFile.getName() + " - Notepad");
                isTextChanged = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void saveAsFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt", "html", "java", "py", "cpp", "js", "c", "cs", "php", "scala", "jsp", "asp", "xml", "css", "ts", "scss", "less"));
        int result = fileChooser.showSaveDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String extension = getFileExtension(selectedFile);
            if (extension == null) {
                String fileType = detectFileType(textArea.getText());
                if (fileType != null) {
                    selectedFile = new File(selectedFile.getAbsolutePath() + "." + fileType);
                }
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
                writer.write(textArea.getText());
                currentFile = selectedFile;
                frame.setTitle(currentFile.getName() + " - Notepad");
                isTextChanged = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOfDot = name.lastIndexOf(".");
        if (lastIndexOfDot != -1 && lastIndexOfDot != 0) {
            return name.substring(lastIndexOfDot + 1);
        }
        return null;
    }
    
    private String detectFileType(String text) {
        if (text.contains("<html") || text.contains("<!DOCTYPE html")) {
            return "html";
        } else if (text.contains("public class") || text.contains("class ")) {
            return "java";
        } else if (text.contains("def ") || text.contains("import ") || text.contains("print ")) {
            return "python";
        } else if (text.contains("#include ")) {
            return "cpp";
        } else if (text.contains("function ") || text.contains("var ") || text.contains("console.log")) {
            return "javascript";
        } else if (text.contains("package ") || text.contains("import ")) {
            return "java";
        } else if (text.contains("printf") || text.contains("scanf")) {
            return "c";
        } else if (text.contains("class") && text.contains("{") && text.contains("}")) {
            return "c#";
        } else if (text.contains("<?php") || text.contains("<?")) {
            return "php";
        } else if (text.contains("println") || text.contains("class ") && text.contains("{")) {
            return "scala";
        } else if (text.contains("<!DOCTYPE html>")) {
            return "html";
        } else if (text.contains("<%") || text.contains("<%=")) {
            return "jsp";
        } else if (text.contains("<script") && text.contains("javascript")) {
            return "javascript";
        } else if (text.contains("<style") && text.contains("css")) {
            return "css";
        } else if (text.contains("<?php")) {
            return "php";
        } else if (text.contains("<%=")) {
            return "asp";
        } else if (text.contains("<%--")) {
            return "xml";
        } else if (text.contains("<%@")) {
            return "asp";
        } else if (text.contains("import ")) {
            return "typescript";
        } else if (text.contains("@import ") && text.contains("scss")) {
            return "scss";
        } else if (text.contains("@import ") && text.contains("less")) {
            return "less";
        }
        return "txt";
    }
    
    

    private String getExtension(File file) {
        String fileName = file.getName();
        int index = fileName.lastIndexOf('.');
        if (index > 0 && index < fileName.length() - 1) {
            return fileName.substring(index + 1).toLowerCase();
        }
        return null;
    }

    private void print() {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        if (printerJob.printDialog()) {
            try {
                printerJob.setPrintable(new Printable() {
                    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                        if (pageIndex > 0) {
                            return NO_SUCH_PAGE;
                        }
                        Graphics2D g2d = (Graphics2D) graphics;
                        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                        textArea.printAll(graphics);
                        return PAGE_EXISTS;
                    }
                });
                printerJob.print();
            } catch (PrinterException e) {
                e.printStackTrace();
            }
        }
    }
    

    private void setFont() {
        Font selectedFont = JFontChooser.showDialog(frame, "Choose Font", textArea.getFont());
        if (selectedFont != null) {
            textArea.setFont(selectedFont);
        }
    }

    private void setFontColor() {
        Color color = JColorChooser.showDialog(frame, "Choose Font Color", textArea.getForeground());
        if (color != null) {
            textArea.setForeground(color);
        }
    }

    private void setFontSize() {
        String size = JOptionPane.showInputDialog(frame, "Enter Font Size:", textArea.getFont().getSize());
        try {
            int fontSize = Integer.parseInt(size);
            if (fontSize > 0) {
                textArea.setFont(textArea.getFont().deriveFont((float)fontSize));
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid font size!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exit() {
        if (isTextChanged) {
            int option = JOptionPane.showConfirmDialog(frame, "Do you want to save changes to " + 
                                                         (currentFile != null ? currentFile.getName() : "Untitled") + "?",
                                                         "Save Changes", JOptionPane.YES_NO_CANCEL_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                saveFile();
            } else if (option == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        System.exit(0);
    }
}