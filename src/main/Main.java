package main;

import gui.LoginFrame;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set Windows Look and Feel for modern native look
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                // Modern UI customizations
                UIManager.put("control", new Color(240, 240, 240));
                UIManager.put("info", new Color(242, 242, 242));
                UIManager.put("nimbusBase", new Color(103, 139, 210));
                UIManager.put("nimbusBlueGrey", new Color(198, 212, 239));
                UIManager.put("nimbusSelectionBackground", new Color(103, 139, 210));
                UIManager.put("text", new Color(51, 51, 51));
                
                // Button customizations
                UIManager.put("Button.background", new Color(245, 245, 245));
                UIManager.put("Button.select", new Color(232, 238, 246));
                UIManager.put("Button.border", BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(198, 212, 239), 1),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
                
                // Table customizations
                UIManager.put("Table.background", new Color(255, 255, 255));
                UIManager.put("Table.alternateRowColor", new Color(245, 245, 245));
                UIManager.put("Table.selectionBackground", new Color(198, 212, 239));
                UIManager.put("Table.selectionForeground", new Color(51, 51, 51));
                UIManager.put("Table.gridColor", new Color(230, 230, 230));
                UIManager.put("TableHeader.background", new Color(240, 240, 240));
                
                // TextField customizations
                UIManager.put("TextField.background", new Color(252, 252, 252));
                UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(198, 212, 239), 1),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
                ));
                
                // TabbedPane customizations
                UIManager.put("TabbedPane.selected", new Color(198, 212, 239));
                UIManager.put("TabbedPane.borderHightlightColor", new Color(198, 212, 239));
                UIManager.put("TabbedPane.contentAreaColor", new Color(248, 248, 248));
                
                // Set modern fonts
                Font defaultFont = new Font("Segoe UI", Font.PLAIN, 13);
                Font boldFont = defaultFont.deriveFont(Font.BOLD);
                
                UIManager.put("Button.font", defaultFont);
                UIManager.put("Label.font", defaultFont);
                UIManager.put("Menu.font", defaultFont);
                UIManager.put("MenuItem.font", defaultFont);
                UIManager.put("RadioButton.font", defaultFont);
                UIManager.put("CheckBox.font", defaultFont);
                UIManager.put("ComboBox.font", defaultFont);
                UIManager.put("TextField.font", defaultFont);
                UIManager.put("TextArea.font", defaultFont);
                UIManager.put("Table.font", defaultFont);
                UIManager.put("TableHeader.font", boldFont);
                UIManager.put("TabbedPane.font", defaultFont);
                
                // Add subtle shadows to components
                System.setProperty("awt.useSystemAAFontSettings", "on");
                System.setProperty("swing.aatext", "true");
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Show login window instead of main frame
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}