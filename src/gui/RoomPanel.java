package gui;

import model.Room;
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
    private JTextField roomNumberField, roomPriceField;
    private JComboBox<String> statusCombo;
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
        filterCombo = new JComboBox<>(new String[]{"All", "4-Person", "8-Person"});
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
                "Room Price", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Integer.class;
                if (column == 5) return Double.class;
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
        int[] columnWidths = {50, 100, 80, 80, 80, 100, 100};
        for (int i = 0; i < columnWidths.length; i++) {
            roomTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Custom renderer for status column
        roomTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
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

        roomTable.getColumnModel().getColumn(5).setCellRenderer(currencyRenderer);

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

        // Status panel with color coding
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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
        
        // Set status color and background
        switch (status) {
            case "AVAILABLE":
                statusPanel.setBackground(new Color(220, 255, 220)); // Light green
                statusLabel.setForeground(new Color(0, 100, 0)); // Dark green
                break;
            case "OCCUPIED":
                statusPanel.setBackground(new Color(255, 255, 220)); // Light yellow
                statusLabel.setForeground(new Color(150, 100, 0)); // Dark orange
                break;
            case "FULL":
                statusPanel.setBackground(new Color(255, 220, 220)); // Light red
                statusLabel.setForeground(new Color(150, 0, 0)); // Dark red
                break;
            case "MAINTENANCE":
                statusPanel.setBackground(new Color(240, 240, 240)); // Light gray
                statusLabel.setForeground(Color.GRAY);
                break;
        }
        statusPanel.add(statusLabel);
        titlePanel.add(statusPanel, BorderLayout.EAST);

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

        addDetailRow(roomDetailsPanel, gbc, 0, "Room ID:", String.valueOf(room.getRoomId()));
        addDetailRow(roomDetailsPanel, gbc, 1, "Room Number:", room.getRoomNumber());
        addDetailRow(roomDetailsPanel, gbc, 2, "Room Type:", room.getRoomType());
        addDetailRow(roomDetailsPanel, gbc, 3, "Bed Count:", String.valueOf(room.getBedCount()));
        addDetailRow(roomDetailsPanel, gbc, 4, "Room Price:", String.format("$%.2f", room.getRoomPrice()));

        contentPanel.add(roomDetailsPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Occupancy Section with Progress Bar
        JPanel occupancyPanel = new JPanel(new GridBagLayout());
        occupancyPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Occupancy Information"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        GridBagConstraints ogbc = new GridBagConstraints();
        ogbc.insets = new Insets(5, 5, 5, 5);
        ogbc.anchor = GridBagConstraints.WEST;
        ogbc.fill = GridBagConstraints.HORIZONTAL;

        // Occupancy progress bar
        JProgressBar occupancyBar = new JProgressBar(0, room.getBedCount());
        occupancyBar.setValue(room.getCurrentOccupancy());
        occupancyBar.setStringPainted(true);
        occupancyBar.setString(String.format("%d / %d beds occupied", 
            room.getCurrentOccupancy(), room.getBedCount()));

        // Set progress bar colors based on occupancy
        if (room.getCurrentOccupancy() == 0) {
            occupancyBar.setForeground(new Color(0, 150, 0)); // Dark green for empty
        } else if (room.getCurrentOccupancy() == room.getBedCount()) {
            occupancyBar.setForeground(new Color(150, 0, 0)); // Dark red for full
        } else {
            occupancyBar.setForeground(new Color(150, 100, 0)); // Dark orange for partially occupied
        }

        ogbc.gridwidth = 2;
        occupancyPanel.add(occupancyBar, ogbc);
        ogbc.gridwidth = 1;
        ogbc.gridy = 1;

        addDetailRow(occupancyPanel, ogbc, 1, "Current Occupancy:", 
            String.format("%d / %d", room.getCurrentOccupancy(), room.getBedCount()));
        addDetailRow(occupancyPanel, ogbc, 2, "Available Beds:", 
            String.valueOf(room.getAvailableBeds()));
        addDetailRow(occupancyPanel, ogbc, 3, "Occupancy Status:", 
            room.getCurrentOccupancy() == 0 ? "Empty" : 
            room.getCurrentOccupancy() == room.getBedCount() ? "Full" : "Partially Occupied");

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
            Object[] rowData = {
                room.getRoomId(),
                room.getRoomNumber(),
                room.getRoomType(),
                room.getBedCount(),
                String.format("%d / %d", room.getCurrentOccupancy(), room.getBedCount()),
                String.format("$%.2f", room.getRoomPrice()),
                room.getStatus()
            };
            tableModel.addRow(rowData);
        }

        // Update button states
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
        JComboBox<String> bedCountCombo = new JComboBox<>(new String[]{"4-Person", "8-Person"});
        roomPriceField = new JTextField(20);
        statusCombo = new JComboBox<>(new String[]{"AVAILABLE", "OCCUPIED", "FULL", "MAINTENANCE"});

        // Add components to form
        addFormField(formPanel, gbc, 0, "Room Number:", roomNumberField);
        addFormField(formPanel, gbc, 1, "Room Type:", bedCountCombo);
        addFormField(formPanel, gbc, 2, "Room Price ($):", roomPriceField);
        addFormField(formPanel, gbc, 3, "Status:", statusCombo);

        // Room type change listener
        bedCountCombo.addActionListener(e -> {
            String selectedType = (String) bedCountCombo.getSelectedItem();
            // Auto-fill price based on room type
            if ("4-Person".equals(selectedType)) {
                roomPriceField.setText("120.00");
            } else {
                roomPriceField.setText("80.00");
            }
        });

        // Add notes for room types
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JPanel notesPanel = new JPanel();
        notesPanel.setLayout(new BoxLayout(notesPanel, BoxLayout.Y_AXIS));
        notesPanel.add(new JLabel("<html><i>Notes:</i></html>"));
        notesPanel.add(new JLabel("<html><i>- 4-Person Room: $120.00/month</i></html>"));
        notesPanel.add(new JLabel("<html><i>- 8-Person Room: $80.00/month</i></html>"));
        formPanel.add(notesPanel, gbc);
        gbc.gridwidth = 1;

        // Fill form if editing
        if (isEdit) {
            roomNumberField.setText(room.getRoomNumber());
            bedCountCombo.setSelectedItem(room.getRoomType());
            roomPriceField.setText(room.getRoomPrice().toString());
            statusCombo.setSelectedItem(room.getStatus());
            bedCountCombo.setEnabled(false); // Cannot change room type after creation
        } else {
            // Set default values for new room
            bedCountCombo.setSelectedIndex(0); // Default to 4-person room
            roomPriceField.setText("120.00"); // Default price for 4-person room
            statusCombo.setSelectedItem("AVAILABLE");
        }

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton(isEdit ? "Update" : "Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                // Validate fields
                if (roomNumberField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(formDialog,
                            "Please fill in all required fields.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Parse numeric fields
                BigDecimal roomPrice;
                String selectedType = (String) bedCountCombo.getSelectedItem();
                int bedCount = "4-Person".equals(selectedType) ? 4 : 8;

                try {
                    roomPrice = new BigDecimal(roomPriceField.getText().trim());
                    if (roomPrice.compareTo(BigDecimal.ZERO) < 0) {
                        throw new NumberFormatException("Room price must be positive");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(formDialog,
                            "Invalid room price.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (currentRoom == null) {
                    // Create a new room
                    Room newRoom = new Room(roomNumberField.getText().trim(), bedCount, roomPrice);
                    newRoom.setStatus((String) statusCombo.getSelectedItem());

                    if (!dataStorage.addRoom(newRoom)) {
                        JOptionPane.showMessageDialog(formDialog,
                                "Failed to add room.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    mainFrame.updateStatusBar("New room added successfully");
                } else {
                    // Update existing room
                    currentRoom.setRoomNumber(roomNumberField.getText().trim());
                    currentRoom.setRoomPrice(roomPrice);
                    currentRoom.setStatus((String) statusCombo.getSelectedItem());

                    if (!dataStorage.updateRoom(currentRoom)) {
                        JOptionPane.showMessageDialog(formDialog,
                                "Failed to update room.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    mainFrame.updateStatusBar("Room updated successfully");
                }

                formDialog.dispose();
                refreshData();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(formDialog,
                        "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

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
}