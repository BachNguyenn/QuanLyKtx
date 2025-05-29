package gui;

import model.Report;
import model.Student;
import model.Room;
import model.Contract;
import model.Fee;
import model.FeeType;
import util.ReportExporter;
import util.DataStorage;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReportPanel extends JPanel {
    private final DataStorage dataStorage;
    private JComboBox<String> reportTypeCombo;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> formatCombo;
    private JButton generateButton;
    private JButton deleteButton;
    private JButton viewButton;
    private JButton refreshButton;
    private JTable reportsTable;
    private DefaultTableModel tableModel;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ReportPanel() {
        this.dataStorage = DataStorage.getInstance();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        refreshData();
    }

    private void initializeComponents() {
        // Form components
        String[] reportTypes = {
            "Overview Report",
            "Financial Report",
            "Student List Report",
            "Room Occupancy Report",
            "Contract Status Report",
            "Fee Collection Report"
        };
        reportTypeCombo = new JComboBox<>(reportTypes);
        
        titleField = new JTextField(30);
        
        descriptionArea = new JTextArea(3, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        String[] formats = {"PDF", "Excel"};
        formatCombo = new JComboBox<>(formats);
        
        // Buttons
        generateButton = new JButton("Generate Report");
        deleteButton = new JButton("Delete");
        viewButton = new JButton("View");
        refreshButton = new JButton("Refresh");
        
        // Set preferred button size
        Dimension buttonSize = new Dimension(150, 30);
        generateButton.setPreferredSize(buttonSize);
        deleteButton.setPreferredSize(buttonSize);
        viewButton.setPreferredSize(buttonSize);
        refreshButton.setPreferredSize(buttonSize);
        
        // Initially disable buttons
        deleteButton.setEnabled(false);
        viewButton.setEnabled(false);

        // Table setup
        setupReportsTable();
    }

    private void setupReportsTable() {
        String[] columns = {
            "ID", "Title", "Type", "Generated Date", "Format", "Status"
        };
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Integer.class;
                return String.class;
            }
        };
        
        reportsTable = new JTable(tableModel);
        reportsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reportsTable.setRowHeight(25);
        reportsTable.getTableHeader().setReorderingAllowed(false);
        reportsTable.getTableHeader().setBackground(new Color(240, 240, 240));
        reportsTable.getTableHeader().setFont(reportsTable.getTableHeader().getFont().deriveFont(Font.BOLD));
        
        // Column widths
        int[] columnWidths = {50, 200, 150, 150, 80, 100};
        for (int i = 0; i < columnWidths.length; i++) {
            reportsTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
        
        // Center align certain columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        reportsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        reportsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        reportsTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        
        // Status column renderer
        reportsTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String status = (String) value;
                    if ("COMPLETED".equals(status)) {
                        c.setForeground(new Color(0, 150, 0));
                    } else if ("GENERATING".equals(status)) {
                        c.setForeground(new Color(200, 130, 0));
                    } else {
                        c.setForeground(Color.RED);
                    }
                }
                return c;
            }
        });
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new BorderLayout(10, 0));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Generate New Report"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Input fields panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Add form fields
        addFormField(inputPanel, gbc, 0, "Report Type:", reportTypeCombo);
        addFormField(inputPanel, gbc, 1, "Title:", titleField);
        addFormField(inputPanel, gbc, 2, "Description:", new JScrollPane(descriptionArea));
        addFormField(inputPanel, gbc, 3, "Format:", formatCombo);
        
        // Generate button panel
        JPanel generatePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        generatePanel.add(generateButton);
        
        formPanel.add(inputPanel, BorderLayout.CENTER);
        formPanel.add(generatePanel, BorderLayout.SOUTH);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout(0, 5));
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Generated Reports"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(viewButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(refreshButton);
        
        tablePanel.add(buttonPanel, BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(reportsTable), BorderLayout.CENTER);

        // Add panels to main layout
        add(formPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row,
                            String label, JComponent component) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, gbc);
        gbc.fill = GridBagConstraints.NONE;
    }

    private void setupEventHandlers() {
        generateButton.addActionListener(e -> generateReport());
        deleteButton.addActionListener(e -> deleteSelectedReport());
        viewButton.addActionListener(e -> viewSelectedReport());
        refreshButton.addActionListener(e -> refreshData());
        
        reportsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = reportsTable.getSelectedRow() != -1;
                deleteButton.setEnabled(hasSelection);
                viewButton.setEnabled(hasSelection);
            }
        });
    }

    private void generateReport() {
        String type = (String) reportTypeCombo.getSelectedItem();
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String format = (String) formatCombo.getSelectedItem();
        
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a report title",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Report report = new Report();
        report.setId(dataStorage.getNextReportId());
        report.setTitle(title);
        report.setDescription(description);
        report.setType(type);
        report.setGeneratedDate(LocalDateTime.now());
        report.setStatus("GENERATING");
        
        // Add to storage first to show in table
        dataStorage.addReport(report);
        refreshData();
        
        // Generate report content
        String[][] data = generateReportData(type);
        String[] headers = getReportHeaders(type);
        
        // Export based on format
        String filePath;
        if ("PDF".equals(format)) {
            String content = formatReportContent(data, headers);
            filePath = ReportExporter.exportToPDF(report, content);
        } else {
            filePath = ReportExporter.exportToExcel(report, data, headers);
        }
        
        if (filePath != null) {
            report.setStatus("COMPLETED");
        } else {
            report.setStatus("FAILED");
        }
        
        refreshData();
        
        // Clear form
        titleField.setText("");
        descriptionArea.setText("");
    }

    private void deleteSelectedReport() {
        int selectedRow = reportsTable.getSelectedRow();
        if (selectedRow == -1) return;

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this report?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            int reportId = (Integer) tableModel.getValueAt(selectedRow, 0);
            if (dataStorage.deleteReport(reportId)) {
                refreshData();
                JOptionPane.showMessageDialog(this,
                    "Report deleted successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to delete report",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewSelectedReport() {
        int selectedRow = reportsTable.getSelectedRow();
        if (selectedRow == -1) return;

        String filePath = getReportFilePath(selectedRow);
        if (filePath != null && !filePath.isEmpty()) {
            try {
                java.awt.Desktop.getDesktop().open(new java.io.File(filePath));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error opening report: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String getReportFilePath(int row) {
        Report report = dataStorage.getReports().get(row);
        return report.getFilePath();
    }

    private String[][] generateReportData(String type) {
        List<String[]> data = new ArrayList<>();
        
        switch (type) {
            case "Overview Report":
                data.add(new String[]{"Total Students", String.valueOf(dataStorage.getAllStudents().size())});
                data.add(new String[]{"Total Rooms", String.valueOf(dataStorage.getAllRooms().size())});
                data.add(new String[]{"Active Contracts", String.valueOf(dataStorage.getAllContracts().stream()
                    .filter(c -> "Active".equals(c.getStatus())).count())});
                data.add(new String[]{"Total Fees", String.valueOf(dataStorage.getAllFees().size())});
                break;
                
            case "Financial Report":
                BigDecimal totalIncome = dataStorage.getAllFees().stream()
                    .filter(f -> "Paid".equals(f.getPaymentStatus()))
                    .map(Fee::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal pendingPayments = dataStorage.getAllFees().stream()
                    .filter(f -> "Unpaid".equals(f.getPaymentStatus()))
                    .map(Fee::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                data.add(new String[]{"Total Income", totalIncome.toString()});
                data.add(new String[]{"Pending Payments", pendingPayments.toString()});
                data.add(new String[]{"Total Expected", totalIncome.add(pendingPayments).toString()});
                break;
                
            case "Student List Report":
                for (Student student : dataStorage.getAllStudents()) {
                    data.add(new String[]{
                        student.getStudentCode(),
                        student.getFullName(),
                        String.valueOf(student.getRoomId()),
                        student.getStatus()
                    });
                }
                break;
                
            case "Room Occupancy Report":
                for (Room room : dataStorage.getAllRooms()) {
                    data.add(new String[]{
                        room.getRoomNumber(),
                        String.valueOf(room.getCurrentOccupancy()),
                        String.valueOf(room.getBedCount()),
                        String.format("%.2f%%", (room.getCurrentOccupancy() * 100.0 / room.getBedCount())),
                        room.getStatus()
                    });
                }
                break;
                
            case "Contract Status Report":
                for (Contract contract : dataStorage.getAllContracts()) {
                    Student student = dataStorage.getStudentById(contract.getStudentId());
                    data.add(new String[]{
                        contract.getContractCode(),
                        student != null ? student.getFullName() : "Unknown",
                        contract.getStartDate().toString(),
                        contract.getEndDate().toString(),
                        contract.getContractStatus()
                    });
                }
                break;
                
            case "Fee Collection Report":
                for (Fee fee : dataStorage.getAllFees()) {
                    Student student = dataStorage.getStudentById(fee.getStudentId());
                    data.add(new String[]{
                        fee.getFeeCode(),
                        student != null ? student.getFullName() : "Unknown",
                        fee.getFeeType().toString(),
                        fee.getAmount().toString(),
                        fee.getDueDate().toString(),
                        fee.getPaymentStatus()
                    });
                }
                break;
        }
        
        return data.toArray(new String[0][]);
    }

    private String[] getReportHeaders(String type) {
        switch (type) {
            case "Overview Report":
                return new String[]{"Metric", "Value"};
                
            case "Financial Report":
                return new String[]{"Category", "Amount"};
                
            case "Student List Report":
                return new String[]{"Student Code", "Full Name", "Room ID", "Status"};
                
            case "Room Occupancy Report":
                return new String[]{"Room Number", "Current Occupancy", "Total Beds", "Occupancy Rate", "Status"};
                
            case "Contract Status Report":
                return new String[]{"Contract Code", "Student Name", "Start Date", "End Date", "Status"};
                
            case "Fee Collection Report":
                return new String[]{"Fee Code", "Student Name", "Fee Type", "Amount", "Due Date", "Status"};
                
            default:
                return new String[]{"Column1", "Column2"};
        }
    }

    private String formatReportContent(String[][] data, String[] headers) {
        StringBuilder content = new StringBuilder();
        
        // Add title
        content.append("Report: ").append(reportTypeCombo.getSelectedItem()).append("\n");
        content.append("Generated: ").append(LocalDateTime.now().format(DATE_FORMATTER)).append("\n\n");
        
        // Add headers
        content.append(String.join("\t", headers)).append("\n");
        content.append("-".repeat(80)).append("\n");
        
        // Add data
        for (String[] row : data) {
            content.append(String.join("\t", row)).append("\n");
        }
        
        return content.toString();
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        List<Report> reports = dataStorage.getReports();
        
        if (reports != null) {
            for (Report report : reports) {
                Object[] rowData = {
                    report.getId(),
                    report.getTitle(),
                    report.getType(),
                    report.getGeneratedDate().format(DATE_FORMATTER),
                    report.getFormat(),
                    report.getStatus()
                };
                tableModel.addRow(rowData);
            }
        }
        
        // Reset button states
        deleteButton.setEnabled(false);
        viewButton.setEnabled(false);
    }
} 