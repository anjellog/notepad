import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Notepad extends JFrame {
    private JTextArea textArea;
    private JComboBox<String> fontComboBox;
    private JSpinner fontSizeSpinner;
    private JButton colorButton;
    private JButton saveTxtButton;

    public Notepad() {
        setTitle("Notepad");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // text area
        textArea = new JTextArea();
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        textArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(textArea);

        // icon
        ImageIcon icon = new ImageIcon(getClass().getResource("notepad.png")); // Replace with your icon file path
        setIconImage(icon.getImage());

        // font style
        fontComboBox = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        fontComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        fontComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTextAreaFont();
            }
        });

        // font size
        fontSizeSpinner = new JSpinner(new SpinnerNumberModel(14, 8, 72, 1));
        fontSizeSpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        fontSizeSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateTextAreaFont();
            }
        });

        // color
        colorButton = new JButton("Color");
        colorButton.setFont(new Font("Arial", Font.PLAIN, 14));
        colorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(Notepad.this, "Choose Text Color", textArea.getForeground());
                if (color != null) {
                    textArea.setForeground(color);
                }
            }
        });

        // save button
        saveTxtButton = new JButton("Save as TXT");
        saveTxtButton.setFont(new Font("Arial", Font.PLAIN, 14));
        saveTxtButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAsTxt();
            }
        });

        // toolbar
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbarPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        toolbarPanel.add(new JLabel("Font:"));
        toolbarPanel.add(fontComboBox);
        toolbarPanel.add(new JLabel("Size:"));
        toolbarPanel.add(fontSizeSpinner);
        toolbarPanel.add(colorButton);
        toolbarPanel.add(saveTxtButton);

        // components to frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(toolbarPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void updateTextAreaFont() {
        String fontFamily = (String) fontComboBox.getSelectedItem();
        int fontSize = (int) fontSizeSpinner.getValue();
        Font font = new Font(fontFamily, Font.PLAIN, fontSize);
        textArea.setFont(font);
    }

    private void saveAsTxt() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.setFileFilter(txtFilter);
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }

            try {
                FileOutputStream fileOut = new FileOutputStream(file);
                fileOut.write(textArea.getText().getBytes());
                fileOut.close();
                JOptionPane.showMessageDialog(this, "File saved successfully.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Notepad();
            }
        });
    }
}