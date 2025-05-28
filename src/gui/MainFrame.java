package gui;
import util.DataStorage;
import util.ReportGenerator;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main application window for the Dormitory Management System
 */
public class MainFrame extends JFrame {

    private DataStorage dataStorage;
    private JTabbedPane tabbedPane;
    private StudentPanel studentPanel;
    private RoomPanel roomPanel;
    private ContractPanel contractPanel;
    private FeePanel feePanel;

    public MainFrame() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        this.dataStorage = DataStorage.getInstance();
        setTitle("Dormitory Management System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        
        initializeComponents();
        setupLayout();
        setupMenuBar();
        setupEventHandlers();
        setupWindowClosing();
    }

    public DataStorage getDataStorage() {
        return dataStorage;
    }

    public StudentPanel getStudentPanel() {
        return studentPanel;
    }

    public RoomPanel getRoomPanel() {
        return roomPanel;
    }

    public ContractPanel getContractPanel() {
        return contractPanel;
    }

    public FeePanel getFeePanel() {
        return feePanel;
    }

    private void initializeComponents() {
        tabbedPane = new JTabbedPane();

        studentPanel = new StudentPanel(this);
        roomPanel = new RoomPanel(this);
        contractPanel = new ContractPanel(this);
        feePanel = new FeePanel(this);

        tabbedPane.addTab("Students", studentPanel);
        tabbedPane.addTab("Rooms", roomPanel);
        tabbedPane.addTab("Contracts", contractPanel);
        tabbedPane.addTab("Fees", feePanel);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
        JLabel statusLabel = new JLabel("Ready");
        statusBar.add(statusLabel);
        add(statusBar, BorderLayout.SOUTH);
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = createFileMenu();
        // Reports Menu
        JMenu reportsMenu = createReportsMenu();
        // Help Menu
        JMenu helpMenu = createHelpMenu();

        menuBar.add(fileMenu);
        menuBar.add(reportsMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        JMenuItem refreshItem = new JMenuItem("Refresh All");
        JMenuItem saveItem = new JMenuItem("Save All");
        JMenuItem exitItem = new JMenuItem("Exit");

        refreshItem.addActionListener(e -> refreshAllPanels());
        exitItem.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to exit?", "Confirm Exit",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        exitItem.addActionListener(e -> handleExit());

        fileMenu.add(refreshItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        return fileMenu;
    }

    private JMenu createReportsMenu() {
        JMenu reportsMenu = new JMenu("Reports");
        JMenuItem studentReportItem = new JMenuItem("Student Statistics");
        JMenuItem occupancyReportItem = new JMenuItem("Occupancy Report");
        JMenuItem financialReportItem = new JMenuItem("Financial Report");
        JMenuItem contractReportItem = new JMenuItem("Contract Report");
        JMenuItem summaryReportItem = new JMenuItem("Summary Report");

        studentReportItem.addActionListener(e -> showReport("Student Statistics",
                ReportGenerator.generateStudentReport()));
        occupancyReportItem.addActionListener(e -> showReport("Occupancy Report",
                ReportGenerator.generateOccupancyReport()));
        financialReportItem.addActionListener(e -> showReport("Financial Report",
                ReportGenerator.generateFinancialReport()));
        contractReportItem.addActionListener(e -> showReport("Contract Report",
                ReportGenerator.generateContractReport()));
        summaryReportItem.addActionListener(e -> showReport("Summary Report",
                ReportGenerator.generateSummaryReport()));

        reportsMenu.add(studentReportItem);
        reportsMenu.add(occupancyReportItem);
        reportsMenu.add(financialReportItem);
        reportsMenu.add(contractReportItem);
        reportsMenu.addSeparator();
        reportsMenu.add(summaryReportItem);
        return reportsMenu;
    }

    private JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        return helpMenu;
    }

    private void setupEventHandlers() {
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            switch (selectedIndex) {
                case 0: // Students tab
                    studentPanel.refreshData();
                    break;
                case 1: // Rooms tab
                    roomPanel.refreshData();
                    break;
                case 2: // Contracts tab
                    contractPanel.refreshData();
                    break;
                case 3: // Fees tab
                    feePanel.refreshData();
                    break;
            }
        });
    }

    private void setupWindowClosing() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });
    }

    private void handleExit() {
        if (dataStorage.showExitConfirmation()) {
            dispose();
            System.exit(0);
        }
    }

    public void refreshAllPanels() {
        SwingUtilities.invokeLater(() -> {
            studentPanel.refreshData();
            roomPanel.refreshData();
            contractPanel.refreshData();
            feePanel.refreshData();
            updateStatusBar("All panels refreshed");
        });
    }

    public void refreshRelatedPanels(String source) {
        SwingUtilities.invokeLater(() -> {
            switch (source) {
                case "student":
                    contractPanel.refreshData();
                    feePanel.refreshData();
                    break;
                case "room":
                    studentPanel.refreshData();
                    contractPanel.refreshData();
                    break;
                case "contract":
                    studentPanel.refreshData();
                    roomPanel.refreshData();
                    feePanel.refreshData();
                    break;
                case "fee":
                    studentPanel.refreshData();
                    break;
            }
        });
    }

    public void updateStatusBar(String message) {
        SwingUtilities.invokeLater(() -> {
            Component statusBar = ((BorderLayout) getContentPane().getLayout()).getLayoutComponent(BorderLayout.SOUTH);
            if (statusBar instanceof JPanel) {
                JLabel statusLabel = (JLabel) ((JPanel) statusBar).getComponent(0);
                statusLabel.setText(message);
                
                // Clear the status message after 3 seconds
                javax.swing.Timer timer = new javax.swing.Timer(3000, e -> statusLabel.setText("Ready"));
                timer.setRepeats(false);
                timer.start();
            }
        });
    }

    private void showReport(String title, String reportContent) {
        JDialog reportDialog = new JDialog(this, title, true);
        reportDialog.setSize(800, 600);
        reportDialog.setLocationRelativeTo(this);

        JTextArea textArea = new JTextArea(reportContent);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel buttonPanel = createReportButtonPanel(reportDialog, textArea);

        reportDialog.setLayout(new BorderLayout());
        reportDialog.add(scrollPane, BorderLayout.CENTER);
        reportDialog.add(buttonPanel, BorderLayout.SOUTH);

        reportDialog.setVisible(true);
    }

    private JPanel createReportButtonPanel(JDialog reportDialog, JTextArea textArea) {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeButton = new JButton("Close");
        JButton printButton = new JButton("Print");
        JButton saveButton = new JButton("Save");

        closeButton.addActionListener(e -> reportDialog.dispose());
        printButton.addActionListener(e -> {
            try {
                textArea.print();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(reportDialog,
                        "Error printing report: " + ex.getMessage(),
                        "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        saveButton.addActionListener(e -> saveReport(textArea.getText()));

        buttonPanel.add(saveButton);
        buttonPanel.add(printButton);
        buttonPanel.add(closeButton);
        return buttonPanel;
    }

    private void saveReport(String content) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".txt")) {
                    filePath += ".txt";
                }
                java.nio.file.Files.write(java.nio.file.Paths.get(filePath), 
                    content.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                updateStatusBar("Report saved successfully");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error saving report: " + e.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "Dormitory Management System\nVersion 1.0\n\n" +
            "Developed by Group 9\n" +
            "© 2024 All rights reserved",
            "About",
            JOptionPane.INFORMATION_MESSAGE);
    }

    public void switchToTab(int tabIndex) {
        if (tabIndex >= 0 && tabIndex < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(tabIndex);
        }
    }
}
