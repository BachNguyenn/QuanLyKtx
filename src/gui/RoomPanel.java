package gui;

import model.Room;
import model.ARoom;
import model.BRoom;
import util.DataStorage;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class RoomPanel extends JPanel {
    private final MainFrame mainFrame;
    private final DataStorage dataStorage;
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, viewButton, refreshButton;
    private JComboBox<String> filterCombo;

    // Form fields
    private JTextField roomNumberField, bedCountField, roomPriceField, additionalFeeField;
    private JComboBox<String> roomTypeCombo, statusCombo;
    private JDialog formDialog;
    private Room currentRoom;

    public RoomPanel(MainFrame mainFrame) {
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

        // Room type filter
        JPanel typeFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        typeFilterPanel.add(new JLabel("Room Type:"));
        filterCombo = new JComboBox<>(new String[]{"All", "A", "B"});
        typeFilterPanel.add(filterCombo);
        searchFilterPanel.add(typeFilterPanel);

        // Status filter
        JPanel statusFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        statusFilterPanel.add(new JLabel("Status:"));
        JComboBox<String> statusFilterCombo = new JComboBox<>(new String[]{"All", "AVAILABLE", "OCCUPIED", "FULL", "MAINTENANCE"});
        statusFilterPanel.add(statusFilterCombo);
        searchFilterPanel.add(statusFilterPanel);

        topPanel.add(searchFilterPanel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        addButton = new JButton("Add Room");
        editButton = new JButton("Edit Room");
        deleteButton = new JButton("Delete Room");
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
        String[] columnNames = {"ID", "Room Number", "Type", "Bed Count", "Occupancy",
                "Room Price", "Additional Fee", "Total Price", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Integer.class;
                if (column >= 5 && column <= 7) return Double.class;
                return String.class;
            }
        };

        roomTable = new JTable(tableModel);
        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        roomTable.getTableHeader().setReorderingAllowed(false);
        roomTable.setRowHeight(25);
        roomTable.setShowGrid(true);
        roomTable.setGridColor(Color.LIGHT_GRAY);
        roomTable.getTableHeader().setBackground(new Color(240, 240, 240));
        roomTable.getTableHeader().setFont(roomTable.getTableHeader().getFont().deriveFont(Font.BOLD));

        // Set column widths
        int[] columnWidths = {50, 100, 60, 80, 80, 100, 100, 100, 100};
        for (int i = 0; i < columnWidths.length; i++) {
            roomTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Custom renderer for status column
        roomTable.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String status = (String) value;
                    if ("AVAILABLE".equals(status)) {
                        c.setForeground(new Color(0, 150, 0)); // Dark green
                    } else if ("FULL".equals(status)) {
                        c.setForeground(new Color(200, 0, 0)); // Dark red
                    } else if ("OCCUPIED".equals(status)) {
                        c.setForeground(new Color(200, 130, 0)); // Orange
                    } else {
                        c.setForeground(Color.GRAY); // Gray for maintenance
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
                if (value instanceof Double) {
                    value = String.format("$%.2f", value);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };

        for (int i = 5; i <= 7; i++) {
            roomTable.getColumnModel().getColumn(i).setCellRenderer(currencyRenderer);
        }

        // Center-align certain columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        roomTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        roomTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Type
        roomTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Bed Count
        roomTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Occupancy

        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupLayout() {
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createTitledBorder("Room Management")
        ));
    }

    private void setupEventHandlers() {
        addButton.addActionListener(e -> showRoomForm(null));
        editButton.addActionListener(e -> editSelectedRoom());
        deleteButton.addActionListener(e -> deleteSelectedRoom());
        viewButton.addActionListener(e -> viewSelectedRoom());
        refreshButton.addActionListener(e -> refreshData());

        searchField.addActionListener(e -> performSearch());
        filterCombo.addActionListener(e -> performSearch());

        roomTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = roomTable.getSelectedRow() != -1;
                editButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);
                viewButton.setEnabled(hasSelection);
            }
        });
    }

    private void viewSelectedRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to view.");
            return;
        }

        int roomId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Room room = dataStorage.getRoomById(roomId);
        if (room == null) {
            JOptionPane.showMessageDialog(this, "Room not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get current occupancy details
        int currentOccupancy = dataStorage.getCurrentOccupancy(room.getRoomId());
        long capacity = room.getCapacity();
        boolean isFull = dataStorage.isRoomFull(roomId);

        JDialog viewDialog = new JDialog(mainFrame, "Room Details", true);
        viewDialog.setSize(500, 600);
        viewDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Title panel with status
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = new JLabel("Room Information");
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
        String status = room.getStatus();
        JLabel statusLabel = new JLabel("Status: " + status);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));
        
        // Set status color
        switch (status) {
            case "AVAILABLE":
                statusPanel.setBackground(new Color(220, 255, 220)); // Light green
                break;
            case "OCCUPIED":
                statusPanel.setBackground(new Color(255, 255, 220)); // Light yellow
                break;
            case "FULL":
                statusPanel.setBackground(new Color(255, 220, 220)); // Light red
                break;
            case "MAINTENANCE":
                statusPanel.setBackground(new Color(240, 240, 240)); // Light gray
                break;
        }
        statusPanel.add(statusLabel);
        titlePanel.add(statusPanel, BorderLayout.SOUTH);

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Room Details Section
        JPanel roomDetailsPanel = new JPanel(new GridBagLayout());
        roomDetailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Room Details"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Room details with consistent formatting
        addDetailRow(roomDetailsPanel, gbc, 0, "Room ID:", String.valueOf(room.getRoomId()));
        addDetailRow(roomDetailsPanel, gbc, 1, "Room Number:", room.getRoomNumber());
        addDetailRow(roomDetailsPanel, gbc, 2, "Room Type:", room.getRoomType());
        addDetailRow(roomDetailsPanel, gbc, 3, "Bed Count:", String.valueOf(room.getCapacity()));
        addDetailRow(roomDetailsPanel, gbc, 4, "Room Price:", String.format("$%.2f", room.getRoomPrice()));
        addDetailRow(roomDetailsPanel, gbc, 5, "Additional Fee:", String.format("$%.2f", room.getAdditionalFee()));
        addDetailRow(roomDetailsPanel, gbc, 6, "Total Price:", String.format("$%.2f", room.getTotalPrice()));

        contentPanel.add(roomDetailsPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Occupancy Section
        JPanel occupancyPanel = new JPanel(new GridBagLayout());
        occupancyPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Occupancy Information"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        GridBagConstraints ogbc = new GridBagConstraints();
        ogbc.insets = new Insets(5, 5, 5, 5);
        ogbc.anchor = GridBagConstraints.WEST;

        addDetailRow(occupancyPanel, ogbc, 0, "Current Occupancy:", currentOccupancy + " / " + capacity);
        addDetailRow(occupancyPanel, ogbc, 1, "Available Beds:", String.valueOf(capacity - currentOccupancy));
        addDetailRow(occupancyPanel, ogbc, 2, "Occupancy Status:", isFull ? "FULL" : currentOccupancy > 0 ? "PARTIALLY OCCUPIED" : "EMPTY");

        contentPanel.add(occupancyPanel);

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
            viewDialog.dispose();
            showRoomForm(room);
        });
        closeButton.addActionListener(e -> viewDialog.dispose());

        buttonPanel.add(editButton);
        buttonPanel.add(closeButton);

        // Add all components to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        viewDialog.add(mainPanel);
        viewDialog.setVisible(true);
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

    public void refreshData() {
        tableModel.setRowCount(0);
        List<Room> rooms = dataStorage.getAllRooms();

        for (Room room : rooms) {
            int currentOccupancy = dataStorage.getCurrentOccupancy(room.getRoomId());
            long capacity = room.getCapacity();
            String occupancyDisplay = String.format("%d / %d", currentOccupancy, capacity);

            Object[] rowData = {
                    room.getRoomId(),
                    room.getRoomNumber(),
                    room.getRoomType(),
                    room.getBedCount(),
                    occupancyDisplay,
                    String.format("$%.2f", room.getRoomPrice()),
                    String.format("$%.2f", room.getAdditionalFee()),
                    String.format("$%.2f", room.getTotalPrice()),
                    room.getStatus()
            };
            tableModel.addRow(rowData);
        }

        // Update button states - disable selection-dependent buttons
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        viewButton.setEnabled(false);
    }

    private void performSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        String typeFilter = (String) filterCombo.getSelectedItem();

        tableModel.setRowCount(0);
        List<Room> rooms = dataStorage.getAllRooms();

        for (Room room : rooms) {
            boolean matchesSearch = searchText.isEmpty() ||
                    room.getRoomNumber().toLowerCase().contains(searchText);

            boolean matchesType = "All".equals(typeFilter) ||
                    room.getRoomType().equals(typeFilter);

            if (matchesSearch && matchesType) {
                int currentOccupancy = dataStorage.getCurrentOccupancy(room.getRoomId());
                long capacity = room.getCapacity();
                String occupancyDisplay = String.format("%d / %d", currentOccupancy, capacity);

                Object[] rowData = {
                        room.getRoomId(),
                        room.getRoomNumber(),
                        room.getRoomType(),
                        room.getBedCount(),
                        occupancyDisplay,
                        String.format("$%.2f", room.getRoomPrice()),
                        String.format("$%.2f", room.getAdditionalFee()),
                        String.format("$%.2f", room.getTotalPrice()),
                        room.getStatus()
                };
                tableModel.addRow(rowData);
            }
        }
        
        // After search/filter, disable selection-dependent buttons
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        viewButton.setEnabled(false);
    }

    private void editSelectedRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to edit.");
            return;
        }

        int roomId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Room room = dataStorage.getRoomById(roomId);
        if (room != null) {
            showRoomForm(room);
        }
    }

    private void deleteSelectedRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to delete.");
            return;
        }

        String roomNumber = (String) tableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete room: " + roomNumber + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int roomId = (Integer) tableModel.getValueAt(selectedRow, 0);
            if (dataStorage.deleteRoom(roomId)) {
                refreshData();
                mainFrame.updateStatusBar("Room deleted successfully");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete room.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showRoomForm(Room room) {
        currentRoom = room;
        boolean isEdit = (room != null);

        formDialog = new JDialog(mainFrame, isEdit ? "Edit Room" : "Add New Room", true);
        formDialog.setSize(400, 400);
        formDialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Form fields
        roomNumberField = new JTextField(20);
        roomTypeCombo = new JComboBox<>(new String[]{"A", "B"});
        bedCountField = new JTextField(20);
        roomPriceField = new JTextField(20);
        additionalFeeField = new JTextField(20);
        statusCombo = new JComboBox<>(new String[]{"AVAILABLE", "OCCUPIED", "FULL", "MAINTENANCE"});

        // Add components to form
        addFormField(formPanel, gbc, 0, "Room Number:", roomNumberField);
        addFormField(formPanel, gbc, 1, "Room Type:", roomTypeCombo);
        addFormField(formPanel, gbc, 2, "Bed Count:", bedCountField);
        addFormField(formPanel, gbc, 3, "Room Price:", roomPriceField);
        addFormField(formPanel, gbc, 4, "Additional Fee:", additionalFeeField);
        addFormField(formPanel, gbc, 5, "Status:", statusCombo);

        // Add note for A rooms
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        JLabel noteLabel = new JLabel("<html><i>Note: A rooms have 10% premium automatically applied</i></html>");
        noteLabel.setFont(noteLabel.getFont().deriveFont(Font.ITALIC, 11f));
        formPanel.add(noteLabel, gbc);
        gbc.gridwidth = 1;

        // Fill form if editing
        if (isEdit) {
            roomNumberField.setText(room.getRoomNumber());
            roomTypeCombo.setSelectedItem(room.getRoomType());
            bedCountField.setText(String.valueOf(room.getBedCount()));
            roomPriceField.setText(room.getRoomPrice().toString());
            additionalFeeField.setText(room.getAdditionalFee().toString());
            statusCombo.setSelectedItem(room.getStatus());
        } else {
            // Set default values
            additionalFeeField.setText("0.00");
        }

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton(isEdit ? "Update" : "Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> saveRoom());
        cancelButton.addActionListener(e -> formDialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        formDialog.setLayout(new BorderLayout());
        formDialog.add(formPanel, BorderLayout.CENTER);
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

    private void saveRoom() {
        try {
            // Validate fields
            if (roomNumberField.getText().trim().isEmpty() ||
                    bedCountField.getText().trim().isEmpty() ||
                    roomPriceField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(formDialog,
                        "Please fill in all required fields.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Parse numeric fields
            int bedCount;
            BigDecimal roomPrice, additionalFee;

            try {
                bedCount = Integer.parseInt(bedCountField.getText().trim());
                roomPrice = new BigDecimal(roomPriceField.getText().trim());
                additionalFee = new BigDecimal(additionalFeeField.getText().trim());

                if (bedCount <= 0 || roomPrice.compareTo(BigDecimal.ZERO) < 0 ||
                        additionalFee.compareTo(BigDecimal.ZERO) < 0) {
                    throw new NumberFormatException("Values must be positive");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(formDialog,
                        "Invalid numeric values. Please enter valid positive numbers.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String roomType = (String) roomTypeCombo.getSelectedItem();
            Room room;

            if (currentRoom == null) {
                // Create a new room based on type
                if ("A".equals(roomType)) {
                    room = new ARoom(roomNumberField.getText().trim(), bedCount, roomPrice, additionalFee);
                } else {
                    room = new BRoom(roomNumberField.getText().trim(), bedCount, roomPrice);
                }
                room.setStatus((String) statusCombo.getSelectedItem());

                boolean roomAdded = dataStorage.addRoom(room);
                if (roomAdded) {
                    mainFrame.updateStatusBar("Room added successfully");
                } else {
                    JOptionPane.showMessageDialog(formDialog,
                            "Failed to add room.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                // Update the existing room
                currentRoom.setRoomNumber(roomNumberField.getText().trim());
                currentRoom.setRoomType(roomType);
                currentRoom.setBedCount(bedCount);
                currentRoom.setRoomPrice(roomPrice);
                currentRoom.setAdditionalFee(additionalFee);
                currentRoom.setStatus((String) statusCombo.getSelectedItem());

                if (!dataStorage.updateRoom(currentRoom)) {
                    JOptionPane.showMessageDialog(formDialog,
                            "Failed to update room.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                } else {
                    mainFrame.updateStatusBar("Room updated successfully");
                }
            }

            formDialog.dispose();
            refreshData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(formDialog,
                    "Error saving room: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}