package gui;

import model.Fee;
import model.FeeType;
import model.Student;
import util.DataStorage;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class FeePanel extends JPanel {
    private final MainFrame mainFrame;
    private final DataStorage dataStorage;
    private JTable feeTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, refreshButton, markPaidButton, viewButton;
    private JComboBox<String> filterCombo, statusFilterCombo;

    // Form fields
    private JTextField feeCodeField, studentIdField, amountField, dueDateField,
            paymentDateField, descriptionField;
    private JComboBox<FeeType> feeTypeCombo;
    private JComboBox<String> paymentMethodCombo, paymentStatusCombo;
    private JDialog formDialog;
    private Fee currentFee;

    // Date formatter
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public FeePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.dataStorage = mainFrame.getDataStorage();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        refreshData();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        // Top panel with search and filter
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Search and filter row
        JPanel searchFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        
        // Search field
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(15);
        searchPanel.add(searchField);
        searchFilterPanel.add(searchPanel);

        // Fee type filter
        JPanel typeFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        typeFilterPanel.add(new JLabel("Fee Type:"));
        String[] feeTypeNames = new String[FeeType.values().length + 1];
        feeTypeNames[0] = "All";
        for (int i = 0; i < FeeType.values().length; i++) {
            feeTypeNames[i + 1] = FeeType.values()[i].getDisplayName();
        }
        filterCombo = new JComboBox<>(feeTypeNames);
        typeFilterPanel.add(filterCombo);
        searchFilterPanel.add(typeFilterPanel);

        // Status filter
        JPanel statusFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        statusFilterPanel.add(new JLabel("Status:"));
        statusFilterCombo = new JComboBox<>(new String[]{"All", "PENDING", "PAID", "OVERDUE", "CANCELLED"});
        statusFilterPanel.add(statusFilterCombo);
        searchFilterPanel.add(statusFilterPanel);

        topPanel.add(searchFilterPanel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        addButton = new JButton("Add Fee");
        editButton = new JButton("Edit Fee");
        deleteButton = new JButton("Delete Fee");
        viewButton = new JButton("View Details");
        markPaidButton = new JButton("Mark as Paid");
        refreshButton = new JButton("Refresh");

        // Set preferred size for all buttons
        Dimension buttonSize = new Dimension(120, 30);
        addButton.setPreferredSize(buttonSize);
        editButton.setPreferredSize(buttonSize);
        deleteButton.setPreferredSize(buttonSize);
        viewButton.setPreferredSize(buttonSize);
        markPaidButton.setPreferredSize(buttonSize);
        refreshButton.setPreferredSize(buttonSize);

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(markPaidButton);
        buttonPanel.add(refreshButton);

        topPanel.add(buttonPanel);

        // Initially disable buttons that require selection
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        viewButton.setEnabled(false);
        markPaidButton.setEnabled(false);

        // Table setup with enhanced styling
        String[] columnNames = {"Fee ID", "Fee Code", "Student ID", "Fee Type",
                "Amount", "Payment Method", "Payment Status", "Due Date", "Payment Date",
                "Description"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0 || column == 2) return Integer.class;
                if (column == 4) return BigDecimal.class;
                return String.class;
            }
        };

        feeTable = new JTable(tableModel);
        feeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        feeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        feeTable.getTableHeader().setReorderingAllowed(false);
        feeTable.setRowHeight(25);
        feeTable.setShowGrid(true);
        feeTable.setGridColor(Color.LIGHT_GRAY);
        feeTable.getTableHeader().setBackground(new Color(240, 240, 240));
        feeTable.getTableHeader().setFont(feeTable.getTableHeader().getFont().deriveFont(Font.BOLD));

        // Set column widths
        int[] columnWidths = {50, 100, 80, 100, 100, 100, 100, 100, 100, 150};
        for (int i = 0; i < columnWidths.length; i++) {
            feeTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Custom renderer for status column
        feeTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String status = (String) value;
                    if ("PAID".equals(status)) {
                        c.setForeground(new Color(0, 150, 0)); // Dark green
                    } else if ("OVERDUE".equals(status)) {
                        c.setForeground(new Color(200, 0, 0)); // Dark red
                    } else if ("PENDING".equals(status)) {
                        c.setForeground(new Color(200, 130, 0)); // Orange
                    } else {
                        c.setForeground(Color.GRAY); // Gray for cancelled
                    }
                }
                return c;
            }
        });

        // Custom renderer for currency column
        DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(SwingConstants.RIGHT);
            }
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof BigDecimal) {
                    value = String.format("$%.2f", value);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
        feeTable.getColumnModel().getColumn(4).setCellRenderer(currencyRenderer);

        // Center-align certain columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        feeTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        feeTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Fee Code
        feeTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Student ID
        feeTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer); // Due Date
        feeTable.getColumnModel().getColumn(8).setCellRenderer(centerRenderer); // Payment Date

        JScrollPane scrollPane = new JScrollPane(feeTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupLayout() {
        setBorder(BorderFactory.createTitledBorder("Fee Management"));
    }

    private void setupEventHandlers() {
        addButton.addActionListener(e -> showFeeForm(null));
        editButton.addActionListener(e -> editSelectedFee());
        deleteButton.addActionListener(e -> deleteSelectedFee());
        viewButton.addActionListener(e -> viewSelectedFee());
        markPaidButton.addActionListener(e -> markSelectedFeeAsPaid());
        refreshButton.addActionListener(e -> refreshData());

        searchField.addActionListener(e -> performSearch());
        filterCombo.addActionListener(e -> performSearch());
        statusFilterCombo.addActionListener(e -> performSearch());

        feeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = feeTable.getSelectedRow() != -1;
                editButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);
                viewButton.setEnabled(hasSelection);

                // Enable mark as paid only for pending fees
                boolean canMarkPaid = false;
                if (hasSelection) {
                    int selectedRow = feeTable.getSelectedRow();
                    String status = (String) tableModel.getValueAt(selectedRow, 6); // Payment Status column
                    canMarkPaid = "PENDING".equals(status) || "OVERDUE".equals(status);
                }
                markPaidButton.setEnabled(canMarkPaid);
            }
        });
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        List<Fee> fees = dataStorage.getAllFees();

        for (Fee fee : fees) {
            Object[] rowData = {
                    fee.getFeeId(),
                    fee.getFeeCode(),
                    fee.getStudentId(),
                    fee.getFeeType(),
                    fee.getAmount(),
                    fee.getPaymentMethod(),
                    fee.getPaymentStatus(),
                    fee.getDueDate() != null ? fee.getDueDate().format(DATE_FORMATTER) : "",
                    fee.getPaymentDate() != null ? fee.getPaymentDate().format(DATE_FORMATTER) : "",
                    fee.getDescription()
            };
            tableModel.addRow(rowData);
        }

        // Update button states
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        viewButton.setEnabled(false);
        markPaidButton.setEnabled(false);
    }

    private void performSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        String typeFilter = (String) filterCombo.getSelectedItem();
        String statusFilter = (String) statusFilterCombo.getSelectedItem();

        tableModel.setRowCount(0);
        List<Fee> fees = dataStorage.getAllFees();

        for (Fee fee : fees) {
            boolean matchesSearch = searchText.isEmpty() ||
                    fee.getFeeCode().toLowerCase().contains(searchText) ||
                    fee.getDescription() != null && fee.getDescription().toLowerCase().contains(searchText) ||
                    String.valueOf(fee.getStudentId()).contains(searchText);

            boolean matchesType = "All".equals(typeFilter) ||
                    fee.getFeeType().getDisplayName().equals(typeFilter);

            boolean matchesStatus = "All".equals(statusFilter) ||
                    fee.getPaymentStatus().equals(statusFilter);

            if (matchesSearch && matchesType && matchesStatus) {
                Object[] rowData = {
                        fee.getFeeId(),
                        fee.getFeeCode(),
                        fee.getStudentId(),
                        fee.getFeeType(),
                        fee.getAmount(),
                        fee.getPaymentMethod(),
                        fee.getPaymentStatus(),
                        fee.getDueDate() != null ? fee.getDueDate().format(DATE_FORMATTER) : "",
                        fee.getPaymentDate() != null ? fee.getPaymentDate().format(DATE_FORMATTER) : "",
                        fee.getDescription()
                };
                tableModel.addRow(rowData);
            }
        }
    }

    private void viewSelectedFee() {
        int selectedRow = feeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a fee to view.");
            return;
        }

        int feeId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Fee fee = dataStorage.getAllFees().stream()
                .filter(f -> f.getFeeId() == feeId)
                .findFirst()
                .orElse(null);

        if (fee != null) {
            showFeeDetailsDialog(fee);
        }
    }

    private void showFeeDetailsDialog(Fee fee) {
        JDialog detailsDialog = new JDialog(mainFrame, "Fee Details", true);
        detailsDialog.setSize(500, 600);
        detailsDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Title panel with status
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = new JLabel("Fee Information");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Status panel - now aligned to the right
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 0, 0, 0),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            )
        ));
        JLabel statusLabel = new JLabel("Status: " + fee.getPaymentStatus());
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));
        
        // Set status color
        switch (fee.getPaymentStatus()) {
            case "PAID":
                statusPanel.setBackground(new Color(220, 255, 220)); // Light green
                break;
            case "PENDING":
                statusPanel.setBackground(new Color(255, 255, 220)); // Light yellow
                break;
            case "OVERDUE":
                statusPanel.setBackground(new Color(255, 220, 220)); // Light red
                break;
            case "CANCELLED":
                statusPanel.setBackground(new Color(240, 240, 240)); // Light gray
                break;
        }
        statusPanel.add(statusLabel);
        titlePanel.add(statusPanel, BorderLayout.EAST);

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Fee Details Section
        JPanel feeDetailsPanel = new JPanel(new GridBagLayout());
        feeDetailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Fee Details"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Get student information
        Student student = dataStorage.getStudentById(fee.getStudentId());

        // Fee details with consistent formatting
        addDetailRow(feeDetailsPanel, gbc, 0, "Fee ID:", String.valueOf(fee.getFeeId()));
        addDetailRow(feeDetailsPanel, gbc, 1, "Fee Code:", fee.getFeeCode());
        addDetailRow(feeDetailsPanel, gbc, 2, "Fee Type:", fee.getFeeType().getDisplayName());
        addDetailRow(feeDetailsPanel, gbc, 3, "Amount:", String.format("$%.2f", fee.getAmount()));
        addDetailRow(feeDetailsPanel, gbc, 4, "Payment Method:", fee.getPaymentMethod());
        addDetailRow(feeDetailsPanel, gbc, 5, "Due Date:", 
            fee.getDueDate() != null ? fee.getDueDate().format(DATE_FORMATTER) : "Not set");
        addDetailRow(feeDetailsPanel, gbc, 6, "Payment Date:", 
            fee.getPaymentDate() != null ? fee.getPaymentDate().format(DATE_FORMATTER) : "Not paid");
        
        contentPanel.add(feeDetailsPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Student Information Section
        if (student != null) {
            JPanel studentPanel = new JPanel(new GridBagLayout());
            studentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Student Information"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
            GridBagConstraints sgbc = new GridBagConstraints();
            sgbc.insets = new Insets(5, 5, 5, 5);
            sgbc.anchor = GridBagConstraints.WEST;

            addDetailRow(studentPanel, sgbc, 0, "Student Name:", student.getFullName());
            addDetailRow(studentPanel, sgbc, 1, "Student Code:", student.getStudentCode());
            addDetailRow(studentPanel, sgbc, 2, "Email:", student.getEmail());
            addDetailRow(studentPanel, sgbc, 3, "Phone:", student.getPhoneNumber());
            addDetailRow(studentPanel, sgbc, 4, "Room:", 
                student.getRoomId() != 0 ? "Room " + student.getRoomId() : "Not assigned");

            contentPanel.add(studentPanel);
            contentPanel.add(Box.createVerticalStrut(10));
        }

        // Description Section
        if (fee.getDescription() != null && !fee.getDescription().trim().isEmpty()) {
            JPanel descriptionPanel = new JPanel(new BorderLayout());
            descriptionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Description"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
            
            JTextArea descriptionArea = new JTextArea(fee.getDescription());
            descriptionArea.setEditable(false);
            descriptionArea.setLineWrap(true);
            descriptionArea.setWrapStyleWord(true);
            descriptionArea.setBackground(descriptionPanel.getBackground());
            descriptionArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            
            descriptionPanel.add(descriptionArea, BorderLayout.CENTER);
            contentPanel.add(descriptionPanel);
        }

        // Add scrolling to content
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton editButton = new JButton("Edit");
        JButton closeButton = new JButton("Close");

        // Set preferred size for buttons
        Dimension buttonSize = new Dimension(100, 30);
        editButton.setPreferredSize(buttonSize);
        closeButton.setPreferredSize(buttonSize);

        editButton.addActionListener(e -> {
            detailsDialog.dispose();
            showFeeForm(fee);
        });
        closeButton.addActionListener(e -> detailsDialog.dispose());

        buttonPanel.add(editButton);
        buttonPanel.add(closeButton);

        // Add all components to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        detailsDialog.add(mainPanel);
        detailsDialog.setVisible(true);
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(labelComp.getFont().deriveFont(Font.BOLD));
        panel.add(labelComp, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JLabel(value), gbc);
        gbc.fill = GridBagConstraints.NONE;
    }

    private void editSelectedFee() {
        int selectedRow = feeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a fee to edit.");
            return;
        }

        int feeId = (Integer) tableModel.getValueAt(selectedRow, 0);
        dataStorage.getAllFees().stream()
                .filter(f -> f.getFeeId() == feeId)
                .findFirst().ifPresent(this::showFeeForm);
    }

    private void deleteSelectedFee() {
        int selectedRow = feeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a fee to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this fee?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int feeId = (Integer) tableModel.getValueAt(selectedRow, 0);
            if (dataStorage.deleteFee(feeId)) {
                refreshData();
                mainFrame.updateStatusBar("Fee deleted successfully");
            }
        }
    }

    private void markSelectedFeeAsPaid() {
        int selectedRow = feeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a fee to mark as paid.");
            return;
        }

        int feeId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Fee fee = dataStorage.getAllFees().stream()
                .filter(f -> f.getFeeId() == feeId)
                .findFirst()
                .orElse(null);

        if (fee != null && ("PENDING".equals(fee.getPaymentStatus()) || "OVERDUE".equals(fee.getPaymentStatus()))) {
            fee.setPaymentStatus("PAID");
            fee.setPaymentDate(LocalDate.now());

            if (dataStorage.updateFee(fee)) {
                refreshData();
                mainFrame.updateStatusBar("Fee marked as paid successfully");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update fee status.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selected fee cannot be marked as paid.",
                    "Invalid Operation", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showFeeForm(Fee fee) {
        currentFee = fee;
        boolean isEdit = (fee != null);

        formDialog = new JDialog(mainFrame, isEdit ? "Edit Fee" : "Add New Fee", true);
        formDialog.setSize(450, 500);
        formDialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Form fields
        feeCodeField = new JTextField(20);
        studentIdField = new JTextField(20);
        feeTypeCombo = new JComboBox<>(FeeType.values());
        amountField = new JTextField(20);
        dueDateField = new JTextField(20);
        paymentDateField = new JTextField(20);
        paymentMethodCombo = new JComboBox<>(new String[]{"CASH", "CREDIT_CARD", "BANK_TRANSFER", "CHEQUE"});
        paymentStatusCombo = new JComboBox<>(new String[]{"PENDING", "PAID", "OVERDUE", "CANCELLED"});
        descriptionField = new JTextField(20);

        // Add components to form
        addFormField(formPanel, gbc, 0, "Fee Code:", feeCodeField);
        addFormField(formPanel, gbc, 1, "Student ID:", studentIdField);
        addFormField(formPanel, gbc, 2, "Fee Type:", feeTypeCombo);
        addFormField(formPanel, gbc, 3, "Amount ($):", amountField);
        addFormField(formPanel, gbc, 4, "Due Date (yyyy-mm-dd):", dueDateField);
        addFormField(formPanel, gbc, 5, "Payment Method:", paymentMethodCombo);
        addFormField(formPanel, gbc, 6, "Payment Status:", paymentStatusCombo);
        addFormField(formPanel, gbc, 7, "Payment Date (yyyy-mm-dd):", paymentDateField);
        addFormField(formPanel, gbc, 8, "Description:", descriptionField);

        // Add note
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        JLabel noteLabel = new JLabel("<html><i>Note: Payment date is automatically set when marking as paid</i></html>");
        noteLabel.setFont(noteLabel.getFont().deriveFont(Font.ITALIC, 11f));
        formPanel.add(noteLabel, gbc);
        gbc.gridwidth = 1;

        // Fill form if editing
        if (isEdit) {
            feeCodeField.setText(fee.getFeeCode());
            studentIdField.setText(String.valueOf(fee.getStudentId()));
            feeTypeCombo.setSelectedItem(fee.getFeeType());
            amountField.setText(fee.getAmount().toString());
            dueDateField.setText(fee.getDueDate() != null ? fee.getDueDate().format(DATE_FORMATTER) : "");
            paymentMethodCombo.setSelectedItem(fee.getPaymentMethod());
            paymentStatusCombo.setSelectedItem(fee.getPaymentStatus());
            paymentDateField.setText(fee.getPaymentDate() != null ? fee.getPaymentDate().format(DATE_FORMATTER) : "");
            descriptionField.setText(fee.getDescription() != null ? fee.getDescription() : "");
        } else {
            // Set default values
            paymentMethodCombo.setSelectedItem("CASH");
            paymentStatusCombo.setSelectedItem("PENDING");
            dueDateField.setText(LocalDate.now().plusDays(30).format(DATE_FORMATTER));
        }

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton(isEdit ? "Update" : "Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> saveFee());
        cancelButton.addActionListener(e -> formDialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        formDialog.setLayout(new BorderLayout());
        formDialog.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        formDialog.add(buttonPanel, BorderLayout.SOUTH);

        formDialog.setVisible(true);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row,
                              String label, JComponent component) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, gbc);
        gbc.fill = GridBagConstraints.NONE;
    }

    private void saveFee() {
        try {
            // Validate required fields
            if (feeCodeField.getText().trim().isEmpty() ||
                    studentIdField.getText().trim().isEmpty() ||
                    amountField.getText().trim().isEmpty() ||
                    dueDateField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(formDialog,
                        "Please fill in all required fields.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Parse numeric fields
            int studentId;
            BigDecimal amount;

            try {
                studentId = Integer.parseInt(studentIdField.getText().trim());
                amount = new BigDecimal(amountField.getText().trim());

                if (studentId <= 0 || amount.compareTo(BigDecimal.ZERO) < 0) {
                    throw new NumberFormatException("Values must be positive");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(formDialog,
                        "Invalid numeric values. Please enter valid positive numbers.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate student exists
            Student student = dataStorage.getStudentById(studentId);
            if (student == null) {
                JOptionPane.showMessageDialog(formDialog,
                        "Student with ID " + studentId + " does not exist.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Parse dates
            LocalDate dueDate = null;
            LocalDate paymentDate = null;

            try {
                if (!dueDateField.getText().trim().isEmpty()) {
                    dueDate = LocalDate.parse(dueDateField.getText().trim(), DATE_FORMATTER);
                }
                if (!paymentDateField.getText().trim().isEmpty()) {
                    paymentDate = LocalDate.parse(paymentDateField.getText().trim(), DATE_FORMATTER);
                }
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(formDialog,
                        "Invalid date format. Please use yyyy-mm-dd format.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (currentFee == null) {
                // Create a new fee
                Fee fee = new Fee(feeCodeField.getText().trim(), studentId,
                        (FeeType) feeTypeCombo.getSelectedItem(), amount, dueDate);
                fee.setPaymentMethod((String) paymentMethodCombo.getSelectedItem());
                fee.setPaymentStatus((String) paymentStatusCombo.getSelectedItem());
                fee.setPaymentDate(paymentDate);
                fee.setDescription(descriptionField.getText().trim());

                boolean feeAdded = dataStorage.addFee(fee);
                if (feeAdded) {
                    mainFrame.updateStatusBar("Fee added successfully for " + student.getFullName());
                } else {
                    JOptionPane.showMessageDialog(formDialog,
                            "Failed to add fee.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                // Update existing fee
                currentFee.setFeeCode(feeCodeField.getText().trim());
                currentFee.setStudentId(studentId);
                currentFee.setFeeType((FeeType) feeTypeCombo.getSelectedItem());
                currentFee.setAmount(amount);
                currentFee.setDueDate(dueDate);
                currentFee.setPaymentMethod((String) paymentMethodCombo.getSelectedItem());
                currentFee.setPaymentStatus((String) paymentStatusCombo.getSelectedItem());
                currentFee.setPaymentDate(paymentDate);
                currentFee.setDescription(descriptionField.getText().trim());

                if (dataStorage.updateFee(currentFee)) {
                    mainFrame.updateStatusBar("Fee updated successfully for " + student.getFullName());
                } else {
                    JOptionPane.showMessageDialog(formDialog,
                            "Failed to update fee.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            formDialog.dispose();
            refreshData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(formDialog,
                    "Error saving fee: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}