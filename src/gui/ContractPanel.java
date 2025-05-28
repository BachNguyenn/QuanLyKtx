package gui;

import model.Contract;
import util.DataStorage;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ContractPanel extends JPanel {
    private final MainFrame mainFrame;
    private final DataStorage dataStorage;
    private JTable contractTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, refreshButton, viewButton;
    private JComboBox<String> statusFilterCombo, paymentMethodFilterCombo;

    // Form fields
    private JTextField contractCodeField, studentIdField, roomIdField,
            startDateField, endDateField, roomPriceField, depositAmountField;
    private JComboBox<String> paymentMethodCombo, contractStatusCombo;
    private JDialog formDialog;
    private Contract currentContract;

    // Date formatter
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ContractPanel(MainFrame mainFrame) {
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

        // Status filter
        JPanel statusFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        statusFilterPanel.add(new JLabel("Status:"));
        statusFilterCombo = new JComboBox<>(new String[]{"All", "ACTIVE", "EXPIRED", "TERMINATED", "PENDING"});
        statusFilterPanel.add(statusFilterCombo);
        searchFilterPanel.add(statusFilterPanel);

        // Payment method filter
        JPanel paymentFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        paymentFilterPanel.add(new JLabel("Payment:"));
        paymentMethodFilterCombo = new JComboBox<>(new String[]{"All", "MONTHLY", "SEMESTER", "ANNUAL", "CASH"});
        paymentFilterPanel.add(paymentMethodFilterCombo);
        searchFilterPanel.add(paymentFilterPanel);

        topPanel.add(searchFilterPanel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        addButton = new JButton("Add Contract");
        editButton = new JButton("Edit Contract");
        deleteButton = new JButton("Delete Contract");
        viewButton = new JButton("View Details");
        refreshButton = new JButton("Refresh");

        // Set preferred size for all buttons
        Dimension buttonSize = new Dimension(120, 30);
        addButton.setPreferredSize(buttonSize);
        editButton.setPreferredSize(buttonSize);
        deleteButton.setPreferredSize(buttonSize);
        viewButton.setPreferredSize(buttonSize);
        refreshButton.setPreferredSize(buttonSize);

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(refreshButton);

        topPanel.add(buttonPanel);

        // Initially disable buttons that require selection
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        viewButton.setEnabled(false);

        // Table setup with enhanced styling
        String[] columnNames = {"Contract ID", "Contract Code", "Student ID", "Room ID",
                "Start Date", "End Date", "Room Price", "Payment Method",
                "Contract Status", "Deposit Amount"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0 || column == 2 || column == 3) return Integer.class;
                if (column == 6 || column == 9) return BigDecimal.class;
                return String.class;
            }
        };

        contractTable = new JTable(tableModel);
        contractTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contractTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        contractTable.getTableHeader().setReorderingAllowed(false);
        contractTable.setRowHeight(25);
        contractTable.setShowGrid(true);
        contractTable.setGridColor(Color.LIGHT_GRAY);
        contractTable.getTableHeader().setBackground(new Color(240, 240, 240));
        contractTable.getTableHeader().setFont(contractTable.getTableHeader().getFont().deriveFont(Font.BOLD));

        // Set column widths
        int[] columnWidths = {50, 100, 80, 80, 100, 100, 100, 100, 100, 100};
        for (int i = 0; i < columnWidths.length; i++) {
            contractTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Custom renderer for status column
        contractTable.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String status = (String) value;
                    if ("ACTIVE".equals(status)) {
                        c.setForeground(new Color(0, 150, 0)); // Dark green
                    } else if ("TERMINATED".equals(status)) {
                        c.setForeground(new Color(200, 0, 0)); // Dark red
                    } else if ("PENDING".equals(status)) {
                        c.setForeground(new Color(200, 130, 0)); // Orange
                    } else {
                        c.setForeground(Color.GRAY); // Gray for expired
                    }
                }
                return c;
            }
        });

        // Custom renderer for currency columns
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

        contractTable.getColumnModel().getColumn(6).setCellRenderer(currencyRenderer); // Room Price
        contractTable.getColumnModel().getColumn(9).setCellRenderer(currencyRenderer); // Deposit Amount

        // Center-align certain columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        contractTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Contract ID
        contractTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Contract Code
        contractTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Student ID
        contractTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Room ID
        contractTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Start Date
        contractTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // End Date
        contractTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer); // Payment Method

        JScrollPane scrollPane = new JScrollPane(contractTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupLayout() {
        setBorder(BorderFactory.createTitledBorder("Contract Management"));
    }

    private void setupEventHandlers() {
        addButton.addActionListener(e -> showContractForm(null));
        editButton.addActionListener(e -> editSelectedContract());
        deleteButton.addActionListener(e -> deleteSelectedContract());
        viewButton.addActionListener(e -> viewSelectedContract());
        refreshButton.addActionListener(e -> refreshData());

        searchField.addActionListener(e -> performSearch());
        statusFilterCombo.addActionListener(e -> performSearch());
        paymentMethodFilterCombo.addActionListener(e -> performSearch());

        contractTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = contractTable.getSelectedRow() != -1;
                editButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);
                viewButton.setEnabled(hasSelection);
            }
        });
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        List<Contract> contracts = dataStorage.getAllContracts();

        if (contracts != null) {
            for (Contract contract : contracts) {
                Object[] rowData = {
                        contract.getContractId(),
                        contract.getContractCode() != null ? contract.getContractCode() : "",
                        contract.getStudentId(),
                        contract.getRoomId(),
                        contract.getStartDate() != null ? contract.getStartDate().format(DATE_FORMATTER) : "",
                        contract.getEndDate() != null ? contract.getEndDate().format(DATE_FORMATTER) : "",
                        contract.getRoomPrice() != null ? String.format("$%.2f", contract.getRoomPrice()) : "$0.00",
                        contract.getPaymentMethod() != null ? contract.getPaymentMethod() : "MONTHLY",
                        contract.getContractStatus() != null ? contract.getContractStatus() : "ACTIVE",
                        contract.getDepositAmount() != null ? String.format("$%.2f", contract.getDepositAmount()) : "$0.00"
                };
                tableModel.addRow(rowData);
            }
        }

        // Update button states
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        viewButton.setEnabled(false);
    }

    private void performSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        String statusFilter = (String) statusFilterCombo.getSelectedItem();
        String paymentFilter = (String) paymentMethodFilterCombo.getSelectedItem();

        tableModel.setRowCount(0);
        List<Contract> contracts = dataStorage.getAllContracts();

        if (contracts != null) {
            for (Contract contract : contracts) {
                boolean matchesSearch = searchText.isEmpty() ||
                        (contract.getContractCode() != null && contract.getContractCode().toLowerCase().contains(searchText)) ||
                        String.valueOf(contract.getStudentId()).contains(searchText) ||
                        String.valueOf(contract.getRoomId()).contains(searchText);

                boolean matchesStatus = "All".equals(statusFilter) ||
                        (contract.getContractStatus() != null && contract.getContractStatus().equals(statusFilter));

                boolean matchesPayment = "All".equals(paymentFilter) ||
                        (contract.getPaymentMethod() != null && contract.getPaymentMethod().equals(paymentFilter));

                if (matchesSearch && matchesStatus && matchesPayment) {
                    Object[] rowData = {
                            contract.getContractId(),
                            contract.getContractCode() != null ? contract.getContractCode() : "",
                            contract.getStudentId(),
                            contract.getRoomId(),
                            contract.getStartDate() != null ? contract.getStartDate().format(DATE_FORMATTER) : "",
                            contract.getEndDate() != null ? contract.getEndDate().format(DATE_FORMATTER) : "",
                            contract.getRoomPrice() != null ? String.format("$%.2f", contract.getRoomPrice()) : "$0.00",
                            contract.getPaymentMethod() != null ? contract.getPaymentMethod() : "MONTHLY",
                            contract.getContractStatus() != null ? contract.getContractStatus() : "ACTIVE",
                            contract.getDepositAmount() != null ? String.format("$%.2f", contract.getDepositAmount()) : "$0.00"
                    };
                    tableModel.addRow(rowData);
                }
            }
        }
    }

    private void editSelectedContract() {
        int selectedRow = contractTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a contract to edit.");
            return;
        }

        int contractId = (Integer) tableModel.getValueAt(selectedRow, 0);
        List<Contract> contracts = dataStorage.getAllContracts();

        if (contracts != null) {
            contracts.stream()
                    .filter(c -> c.getContractId() == contractId)
                    .findFirst().ifPresent(this::showContractForm);
        }
    }

    private void deleteSelectedContract() {
        int selectedRow = contractTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a contract to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this contract?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int contractId = (Integer) tableModel.getValueAt(selectedRow, 0);
            if (dataStorage.deleteContract(contractId)) {
                refreshData();
                mainFrame.updateStatusBar("Contract deleted successfully");
            }
        }
    }

    private void viewSelectedContract() {
        int selectedRow = contractTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a contract to view.");
            return;
        }

        int contractId = (Integer) tableModel.getValueAt(selectedRow, 0);
        List<Contract> contracts = dataStorage.getAllContracts();
        Contract contract = null;

        if (contracts != null) {
            contract = contracts.stream()
                    .filter(c -> c.getContractId() == contractId)
                    .findFirst()
                    .orElse(null);
        }

        if (contract != null) {
            showContractViewDialog(contract);
        } else {
            JOptionPane.showMessageDialog(this, "Contract not found.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showContractViewDialog(Contract contract) {
        JDialog detailsDialog = new JDialog(mainFrame, "Contract Details", true);
        detailsDialog.setSize(500, 600);
        detailsDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Title panel with status
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = new JLabel("Contract Information");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Status panel
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 0, 0, 0),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            )
        ));
        JLabel statusLabel = new JLabel("Status: " + contract.getContractStatus());
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));
        
        // Set status color
        switch (contract.getContractStatus()) {
            case "ACTIVE":
                statusPanel.setBackground(new Color(220, 255, 220)); // Light green
                break;
            case "PENDING":
                statusPanel.setBackground(new Color(255, 255, 220)); // Light yellow
                break;
            case "TERMINATED":
                statusPanel.setBackground(new Color(255, 220, 220)); // Light red
                break;
            case "EXPIRED":
                statusPanel.setBackground(new Color(240, 240, 240)); // Light gray
                break;
        }
        statusPanel.add(statusLabel);
        titlePanel.add(statusPanel, BorderLayout.SOUTH);

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Contract Details Section
        JPanel contractDetailsPanel = new JPanel(new GridBagLayout());
        contractDetailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Contract Details"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Contract details with consistent formatting
        addDetailRow(contractDetailsPanel, gbc, 0, "Contract ID:", String.valueOf(contract.getContractId()));
        addDetailRow(contractDetailsPanel, gbc, 1, "Contract Code:", contract.getContractCode());
        addDetailRow(contractDetailsPanel, gbc, 2, "Student ID:", String.valueOf(contract.getStudentId()));
        addDetailRow(contractDetailsPanel, gbc, 3, "Room ID:", String.valueOf(contract.getRoomId()));
        addDetailRow(contractDetailsPanel, gbc, 4, "Start Date:", 
            contract.getStartDate() != null ? contract.getStartDate().format(DATE_FORMATTER) : "Not set");
        addDetailRow(contractDetailsPanel, gbc, 5, "End Date:", 
            contract.getEndDate() != null ? contract.getEndDate().format(DATE_FORMATTER) : "Not set");
        addDetailRow(contractDetailsPanel, gbc, 6, "Room Price:", 
            contract.getRoomPrice() != null ? String.format("$%.2f", contract.getRoomPrice()) : "$0.00");
        addDetailRow(contractDetailsPanel, gbc, 7, "Payment Method:", 
            contract.getPaymentMethod() != null ? contract.getPaymentMethod() : "Not specified");
        addDetailRow(contractDetailsPanel, gbc, 8, "Deposit Amount:", 
            contract.getDepositAmount() != null ? String.format("$%.2f", contract.getDepositAmount()) : "$0.00");

        contentPanel.add(contractDetailsPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Duration and Financial Section
        if (contract.getStartDate() != null && contract.getEndDate() != null && contract.getRoomPrice() != null) {
            JPanel financialPanel = new JPanel(new GridBagLayout());
            financialPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Duration & Financial Summary"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
            GridBagConstraints fgbc = new GridBagConstraints();
            fgbc.insets = new Insets(5, 5, 5, 5);
            fgbc.anchor = GridBagConstraints.WEST;

            // Calculate duration
            long totalDays = contract.getStartDate().until(contract.getEndDate()).getDays();
            long remainingDays = LocalDate.now().until(contract.getEndDate()).getDays();
            String durationStatus = remainingDays > 0 ? 
                remainingDays + " days remaining" : 
                remainingDays == 0 ? "Expires today" : 
                "Expired " + Math.abs(remainingDays) + " days ago";

            // Calculate financial details
            BigDecimal totalAmount = contract.getRoomPrice();
            String paymentFrequency = "Monthly";
            
            if (contract.getPaymentMethod() != null) {
                switch (contract.getPaymentMethod()) {
                    case "SEMESTER":
                        paymentFrequency = "Per Semester";
                        break;
                    case "ANNUAL":
                        paymentFrequency = "Per Year";
                        totalAmount = contract.getRoomPrice().multiply(BigDecimal.valueOf(12));
                        break;
                    case "CASH":
                        paymentFrequency = "One-time Payment";
                        break;
                    default:
                        totalAmount = contract.getRoomPrice().multiply(
                            BigDecimal.valueOf(totalDays > 0 ? Math.max(1, totalDays / 30) : 1));
                        break;
                }
            }

            BigDecimal totalWithDeposit = totalAmount.add(
                contract.getDepositAmount() != null ? contract.getDepositAmount() : BigDecimal.ZERO);

            addDetailRow(financialPanel, fgbc, 0, "Contract Duration:", totalDays + " days");
            addDetailRow(financialPanel, fgbc, 1, "Time Status:", durationStatus);
            addDetailRow(financialPanel, fgbc, 2, "Payment Frequency:", paymentFrequency);
            addDetailRow(financialPanel, fgbc, 3, "Total Rent:", String.format("$%.2f", totalAmount));
            addDetailRow(financialPanel, fgbc, 4, "Total with Deposit:", String.format("$%.2f", totalWithDeposit));

            contentPanel.add(financialPanel);
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
            showContractForm(contract);
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

    private void showContractForm(Contract contract) {
        currentContract = contract;
        boolean isEdit = (contract != null);

        formDialog = new JDialog(mainFrame, isEdit ? "Edit Contract" : "Add New Contract", true);
        formDialog.setSize(450, 450);
        formDialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Form fields
        contractCodeField = new JTextField(20);
        studentIdField = new JTextField(20);
        roomIdField = new JTextField(20);
        startDateField = new JTextField(20);
        endDateField = new JTextField(20);
        roomPriceField = new JTextField(20);
        depositAmountField = new JTextField(20);
        paymentMethodCombo = new JComboBox<>(new String[]{"MONTHLY", "SEMESTER", "ANNUAL", "CASH"});
        contractStatusCombo = new JComboBox<>(new String[]{"ACTIVE", "EXPIRED", "TERMINATED", "PENDING"});

        // Add components to form
        addFormField(formPanel, gbc, 0, "Contract Code:", contractCodeField);
        addFormField(formPanel, gbc, 1, "Student ID:", studentIdField);
        addFormField(formPanel, gbc, 2, "Room ID:", roomIdField);
        addFormField(formPanel, gbc, 3, "Start Date (yyyy-mm-dd):", startDateField);
        addFormField(formPanel, gbc, 4, "End Date (yyyy-mm-dd):", endDateField);
        addFormField(formPanel, gbc, 5, "Room Price ($):", roomPriceField);
        addFormField(formPanel, gbc, 6, "Payment Method:", paymentMethodCombo);
        addFormField(formPanel, gbc, 7, "Contract Status:", contractStatusCombo);
        addFormField(formPanel, gbc, 8, "Deposit Amount ($):", depositAmountField);

        // Fill form if editing
        if (isEdit) {
            contractCodeField.setText(contract.getContractCode() != null ? contract.getContractCode() : "");
            studentIdField.setText(String.valueOf(contract.getStudentId()));
            roomIdField.setText(String.valueOf(contract.getRoomId()));
            startDateField.setText(contract.getStartDate() != null ?
                    contract.getStartDate().format(DATE_FORMATTER) : "");
            endDateField.setText(contract.getEndDate() != null ?
                    contract.getEndDate().format(DATE_FORMATTER) : "");
            roomPriceField.setText(contract.getRoomPrice() != null ?
                    contract.getRoomPrice().toString() : "0.00");
            paymentMethodCombo.setSelectedItem(contract.getPaymentMethod() != null ?
                    contract.getPaymentMethod() : "MONTHLY");
            contractStatusCombo.setSelectedItem(contract.getContractStatus() != null ?
                    contract.getContractStatus() : "ACTIVE");
            depositAmountField.setText(contract.getDepositAmount() != null ?
                    contract.getDepositAmount().toString() : "0.00");
        } else {
            // Set default values
            paymentMethodCombo.setSelectedItem("MONTHLY");
            contractStatusCombo.setSelectedItem("ACTIVE");
            startDateField.setText(LocalDate.now().format(DATE_FORMATTER));
            endDateField.setText(LocalDate.now().plusYears(1).format(DATE_FORMATTER));
            depositAmountField.setText("0.00");
            roomPriceField.setText("0.00");
        }

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton(isEdit ? "Update" : "Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> saveContract());
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

    private void saveContract() {
        try {
            // Validate required fields
            if (contractCodeField.getText().trim().isEmpty() ||
                    studentIdField.getText().trim().isEmpty() ||
                    roomIdField.getText().trim().isEmpty() ||
                    startDateField.getText().trim().isEmpty() ||
                    endDateField.getText().trim().isEmpty() ||
                    roomPriceField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(formDialog,
                        "Please fill in all required fields.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Parse numeric fields
            int studentId, roomId;
            BigDecimal roomPrice, depositAmount;

            try {
                studentId = Integer.parseInt(studentIdField.getText().trim());
                roomId = Integer.parseInt(roomIdField.getText().trim());
                roomPrice = new BigDecimal(roomPriceField.getText().trim());
                depositAmount = new BigDecimal(depositAmountField.getText().trim());

                if (studentId <= 0 || roomId <= 0 || roomPrice.compareTo(BigDecimal.ZERO) < 0 ||
                        depositAmount.compareTo(BigDecimal.ZERO) < 0) {
                    throw new NumberFormatException("Values must be positive");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(formDialog,
                        "Invalid numeric values. Please enter valid positive numbers.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Parse dates
            LocalDate startDate, endDate;

            try {
                startDate = LocalDate.parse(startDateField.getText().trim(), DATE_FORMATTER);
                endDate = LocalDate.parse(endDateField.getText().trim(), DATE_FORMATTER);

                if (endDate.isBefore(startDate)) {
                    JOptionPane.showMessageDialog(formDialog,
                            "End date must be after start date.",
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(formDialog,
                        "Invalid date format. Please use yyyy-mm-dd format.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check for duplicate contract code
            String contractCode = contractCodeField.getText().trim();
            List<Contract> existingContracts = dataStorage.getAllContracts();
            if (existingContracts != null) {
                boolean isDuplicate = existingContracts.stream()
                        .anyMatch(c -> c.getContractCode() != null &&
                                c.getContractCode().equals(contractCode) &&
                                (currentContract == null || c.getContractId() != currentContract.getContractId()));

                if (isDuplicate) {
                    JOptionPane.showMessageDialog(formDialog,
                            "Contract code already exists. Please use a unique contract code.",
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (currentContract == null) {
                // Create a new contract
                Contract contract = new Contract(contractCode, studentId, roomId, startDate, endDate, roomPrice);
                contract.setPaymentMethod((String) paymentMethodCombo.getSelectedItem());
                contract.setContractStatus((String) contractStatusCombo.getSelectedItem());
                contract.setDepositAmount(depositAmount);

                boolean contractAdded = dataStorage.addContract(contract);
                if (contractAdded) {
                    mainFrame.updateStatusBar("Contract added successfully");
                } else {
                    JOptionPane.showMessageDialog(formDialog,
                            "Failed to add contract. Contract code may already exist.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                // Update existing contract
                currentContract.setContractCode(contractCode);
                currentContract.setStudentId(studentId);
                currentContract.setRoomId(roomId);
                currentContract.setStartDate(startDate);
                currentContract.setEndDate(endDate);
                currentContract.setRoomPrice(roomPrice);
                currentContract.setPaymentMethod((String) paymentMethodCombo.getSelectedItem());
                currentContract.setContractStatus((String) contractStatusCombo.getSelectedItem());
                currentContract.setDepositAmount(depositAmount);

                if (dataStorage.updateContract(currentContract)) {
                    mainFrame.updateStatusBar("Contract updated successfully");
                } else {
                    JOptionPane.showMessageDialog(formDialog,
                            "Failed to update contract.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            formDialog.dispose();
            refreshData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(formDialog,
                    "Error saving contract: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // For debugging purposes
        }
    }
}