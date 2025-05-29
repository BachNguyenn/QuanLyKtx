package gui;
import util.DataStorage;
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
    private ReportPanel reportPanel;
    private StatisticsPanel statisticsPanel;

    public MainFrame() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        this.dataStorage = DataStorage.getInstance();
        setTitle("Student Housing Management System");
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
        reportPanel = new ReportPanel();
        statisticsPanel = new StatisticsPanel();

        tabbedPane.addTab("Students", studentPanel);
        tabbedPane.addTab("Rooms", roomPanel);
        tabbedPane.addTab("Contracts", contractPanel);
        tabbedPane.addTab("Fees", feePanel);
        tabbedPane.addTab("Reports", reportPanel);
        tabbedPane.addTab("Statistics", statisticsPanel);
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
        // Help Menu
        JMenu helpMenu = createHelpMenu();

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        JMenuItem refreshItem = new JMenuItem("Refresh All");
        JMenuItem saveItem = new JMenuItem("Save All");
        JMenuItem exitItem = new JMenuItem("Exit");

        refreshItem.addActionListener(e -> refreshAllPanels());
        saveItem.addActionListener(e -> dataStorage.saveAllData());
        exitItem.addActionListener(e -> handleExit());

        fileMenu.add(refreshItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        return fileMenu;
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
                case 4: // Reports tab
                    reportPanel.refreshData();
                    break;
                case 5: // Statistics tab
                    statisticsPanel.refreshData();
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
            reportPanel.refreshData();
            statisticsPanel.refreshData();
            updateStatusBar("All panels refreshed");
        });
    }

    public void refreshRelatedPanels(String source) {
        SwingUtilities.invokeLater(() -> {
            switch (source) {
                case "student":
                    contractPanel.refreshData();
                    feePanel.refreshData();
                    statisticsPanel.refreshData();
                    break;
                case "room":
                    studentPanel.refreshData();
                    contractPanel.refreshData();
                    statisticsPanel.refreshData();
                    break;
                case "contract":
                    studentPanel.refreshData();
                    roomPanel.refreshData();
                    feePanel.refreshData();
                    statisticsPanel.refreshData();
                    break;
                case "fee":
                    studentPanel.refreshData();
                    statisticsPanel.refreshData();
                    break;
                case "report":
                    // No need to refresh other panels
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

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "Student Dormitory Management System\nVersion 1.0\n\n" +
            "Developed by Nguyen Tung Bach\n" +
            "PHENIKAA University of Technology",
            "About",
            JOptionPane.INFORMATION_MESSAGE);
    }

    public void switchToTab(int tabIndex) {
        if (tabIndex >= 0 && tabIndex < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(tabIndex);
        }
    }
}
