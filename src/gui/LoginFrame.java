package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import util.DataStorage;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }

    public LoginFrame() {
        DataStorage.getInstance();
        setTitle("Hệ thống Quản lý Ký túc xá");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("ĐĂNG NHẬP HỆ THỐNG");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("QUẢN LÝ KÝ TÚC XÁ PHENIKAA");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username field
        JLabel usernameLabel = new JLabel("Tài khoản:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField = new JTextField(20);
        styleTextField(usernameField);

        // Password field
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField = new JPasswordField(20);
        styleTextField(passwordField);

        // Add components to form panel
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(usernameField, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)), gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(passwordField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        loginButton = new JButton("Đăng nhập");
        cancelButton = new JButton("Hủy");
        
        styleButton(loginButton);
        styleButton(cancelButton);

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        // Add all panels to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);

        // Setup event handlers
        setupEventHandlers();

        // Set size and center on screen
        setSize(350, 400);
        setLocationRelativeTo(null);
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setPreferredSize(new Dimension(200, 30));
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(120, 35));
        button.setFocusPainted(false);
    }

    private void setupEventHandlers() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                
                if (validateLogin(username, password)) {
                    dispose();
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                        "Tài khoản hoặc mật khẩu không chính xác!",
                        "Lỗi đăng nhập",
                        JOptionPane.ERROR_MESSAGE);
                    passwordField.setText("");
                }
            }
        });

        cancelButton.addActionListener(e -> System.exit(0));
        getRootPane().setDefaultButton(loginButton);
    }

    private boolean validateLogin(String username, String password) {
        return username.equals("admin") && password.equals("admin");
    }

    
}