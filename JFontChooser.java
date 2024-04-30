import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class JFontChooser extends JPanel {
    private JComboBox<String> fontNameCombo;
    private JComboBox<Integer> fontSizeCombo;
    private JCheckBox boldCheckBox;
    private JCheckBox italicCheckBox;
    private JLabel previewLabel;

    private Font selectedFont;

    public JFontChooser(Font initialFont) {
        setLayout(new BorderLayout());

        JPanel fontPanel = new JPanel(new GridLayout(0, 1));
        fontNameCombo = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        fontSizeCombo = new JComboBox<>(new Integer[]{8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72});
        boldCheckBox = new JCheckBox("Bold");
        italicCheckBox = new JCheckBox("Italic");

        fontNameCombo.setSelectedItem(initialFont.getName());
        fontSizeCombo.setSelectedItem(initialFont.getSize());
        boldCheckBox.setSelected(initialFont.isBold());
        italicCheckBox.setSelected(initialFont.isItalic());

        fontNameCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updatePreview();
            }
        });

        fontSizeCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updatePreview();
            }
        });

        boldCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updatePreview();
            }
        });

        italicCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updatePreview();
            }
        });

        fontPanel.add(fontNameCombo);
        fontPanel.add(fontSizeCombo);
        fontPanel.add(boldCheckBox);
        fontPanel.add(italicCheckBox);

        JPanel previewPanel = new JPanel(new BorderLayout());
        previewLabel = new JLabel("Preview Text", SwingConstants.CENTER);
        previewLabel.setFont(initialFont);
        previewPanel.add(previewLabel, BorderLayout.CENTER);

        add(fontPanel, BorderLayout.NORTH);
        add(previewPanel, BorderLayout.CENTER);
    }

    public Font getSelectedFont() {
        return selectedFont;
    }

    private void updatePreview() {
        String fontName = (String) fontNameCombo.getSelectedItem();
        int fontSize = (Integer) fontSizeCombo.getSelectedItem();
        int fontStyle = Font.PLAIN;
        if (boldCheckBox.isSelected()) {
            fontStyle |= Font.BOLD;
        }
        if (italicCheckBox.isSelected()) {
            fontStyle |= Font.ITALIC;
        }
        selectedFont = new Font(fontName, fontStyle, fontSize);
        previewLabel.setFont(selectedFont);
    }

    public static Font showDialog(Component parent, String title, Font initialFont) {
        JFontChooser chooser = new JFontChooser(initialFont);
        int result = JOptionPane.showConfirmDialog(parent, chooser, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            return chooser.getSelectedFont();
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        Font selectedFont = JFontChooser.showDialog(null, "Choose Font", new Font("Arial", Font.PLAIN, 12));
        if (selectedFont != null) {
            System.out.println("Selected Font: " + selectedFont);
        } else {
            System.out.println("Font selection cancelled");
        }
    }
}
