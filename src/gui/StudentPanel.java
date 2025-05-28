package gui;

import model.Student;
import model.Room;
import util.DataStorage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class StudentPanel extends JPanel {
    private final MainFrame mainFrame;
    private final DataStorage dataStorage;
    // Form fields
    private JTextField studentCodeField, fullNameField, phoneField, emailField, hometownField;
    private JTextField dateOfBirthField;
    private JComboBox<String> genderCombo, statusCombo, statusFilterCombo, roomFilterCombo;
    private Student currentStudent;
    private JButton assignRoomButton;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, viewButton, refreshButton;
    private JDialog formDialog;

    public StudentPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.dataStorage = mainFrame.getDataStorage();
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setupComponents();
        setupLayout();
        setupEventHandlers();
        refreshData();
    }

    private void setupComponents() {
        // Top panel with search and buttons
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Search panel
        JPanel searchPanel = createSearchPanel();
        topPanel.add(searchPanel);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        topPanel.add(buttonPanel);

        // Table
        setupTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        
        // Status filter
        JPanel statusFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        statusFilterPanel.add(new JLabel("Status:"));
        statusFilterCombo = new JComboBox<>(new String[]{"All", "ACTIVE", "INACTIVE", "GRADUATED"});
        statusFilterPanel.add(statusFilterCombo);
        searchPanel.add(statusFilterPanel);

        // Room filter
        JPanel roomFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        roomFilterPanel.add(new JLabel("Room:"));
        roomFilterCombo = new JComboBox<>(new String[]{"All", "Assigned", "Unassigned"});
        roomFilterPanel.add(roomFilterCombo);
        searchPanel.add(roomFilterPanel);
        
        return searchPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        // Create standard buttons
        Dimension buttonSize = new Dimension(120, 30);
        addButton = createStyledButton("Add Student", buttonSize);
        editButton = createStyledButton("Edit Student", buttonSize);
        deleteButton = createStyledButton("Delete Student", buttonSize);
        viewButton = createStyledButton("View Details", buttonSize);
        refreshButton = createStyledButton("Refresh", buttonSize);
        assignRoomButton = createStyledButton("Assign Room", buttonSize);

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(assignRoomButton);

        // Initially disable buttons that require selection
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        viewButton.setEnabled(false);
        assignRoomButton.setEnabled(false);

        return buttonPanel;
    }

    private JButton createStyledButton(String text, Dimension size) {
        JButton button = new JButton(text);
        button.setPreferredSize(size);
        return button;
    }

    private String getPanelTitle() {
        return "Student Management";
    }

    private void addCustomFilters(JPanel filterPanel) {
        // Status filter
        JPanel statusFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        statusFilterPanel.add(new JLabel("Status:"));
        statusFilterCombo = new JComboBox<>(new String[]{"All", "ACTIVE", "INACTIVE", "GRADUATED"});
        statusFilterPanel.add(statusFilterCombo);
        filterPanel.add(statusFilterPanel);

        // Room filter
        JPanel roomFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        roomFilterPanel.add(new JLabel("Room:"));
        roomFilterCombo = new JComboBox<>(new String[]{"All", "Assigned", "Unassigned"});
        roomFilterPanel.add(roomFilterCombo);
        filterPanel.add(roomFilterPanel);

        // Add action listeners
        statusFilterCombo.addActionListener(e -> performSearch());
        roomFilterCombo.addActionListener(e -> performSearch());
    }

    private void addCustomButtons(JPanel buttonPanel) {
        JButton assignRoomButton = createStyledButton("Assign Room", new Dimension(120, 30));
        assignRoomButton.setEnabled(false);
        buttonPanel.add(assignRoomButton);
        
        // Store for later use
        this.assignRoomButton = assignRoomButton;
        assignRoomButton.addActionListener(e -> assignRoomToStudent());
    }

    private void setupTable() {
        String[] columnNames = {"ID", "Student Code", "Full Name", "Date of Birth",
                "Gender", "Phone Number", "Gmail", "Hometown", "Room ID", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
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

        table = new JTable(tableModel);
        setupTableProperties();
        setupColumnRenderers();
    }

    private void setupLayout() {
        setBorder(BorderFactory.createTitledBorder(getPanelTitle()));
    }

    private void setupEventHandlers() {
        addButton.addActionListener(e -> showAddForm());
        editButton.addActionListener(e -> editSelected());
        deleteButton.addActionListener(e -> deleteSelected());
        viewButton.addActionListener(e -> viewSelected());
        refreshButton.addActionListener(e -> refreshData());
        assignRoomButton.addActionListener(e -> assignRoomToStudent());

        searchField.addActionListener(e -> performSearch());
        statusFilterCombo.addActionListener(e -> performSearch());
        roomFilterCombo.addActionListener(e -> performSearch());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onSelectionChanged(table.getSelectedRow() != -1);
            }
        });
    }

    private void onSelectionChanged(boolean hasSelection) {
        editButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
        viewButton.setEnabled(hasSelection);
        assignRoomButton.setEnabled(hasSelection);
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        List<Student> students = dataStorage.getAllStudents();

        for (Student student : students) {
            Object[] rowData = {
                    student.getStudentId(),
                    student.getStudentCode(),
                    student.getFullName(),
                    student.getDateOfBirth(),
                    student.getGender(),
                    student.getPhoneNumber(),
                    student.getGmail(),
                    student.getHometown(),
                    student.getRoomId() == 0 ? "Not Assigned" : student.getRoomId(),
                    student.getStatus()
            };
            tableModel.addRow(rowData);
        }
    }

    private void performSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        String statusFilter = (String) statusFilterCombo.getSelectedItem();
        String roomFilter = (String) roomFilterCombo.getSelectedItem();

        tableModel.setRowCount(0);
        List<Student> students = dataStorage.getAllStudents();

        for (Student student : students) {
            boolean matchesSearch = searchText.isEmpty() ||
                    student.getFullName().toLowerCase().contains(searchText) ||
                    student.getStudentCode().toLowerCase().contains(searchText) ||
                    student.getGmail().toLowerCase().contains(searchText);

            boolean matchesStatus = "All".equals(statusFilter) ||
                    student.getStatus().equals(statusFilter);

            boolean matchesRoom = "All".equals(roomFilter) ||
                    ("Assigned".equals(roomFilter) && student.getRoomId() != 0) ||
                    ("Unassigned".equals(roomFilter) && student.getRoomId() == 0);

            if (matchesSearch && matchesStatus && matchesRoom) {
                Object[] rowData = {
                        student.getStudentId(),
                        student.getStudentCode(),
                        student.getFullName(),
                        student.getDateOfBirth(),
                        student.getGender(),
                        student.getPhoneNumber(),
                        student.getGmail(),
                        student.getHometown(),
                        student.getRoomId() == 0 ? "Not Assigned" : student.getRoomId(),
                        student.getStatus()
                };
                tableModel.addRow(rowData);
            }
        }
    }

    private void showAddForm() {
        displayStudentForm(null);
    }

    private void editSelected() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showErrorDialog("Please select a student to edit.");
            return;
        }

        int studentId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Student student = dataStorage.getStudentById(studentId);
        if (student != null) {
            displayStudentForm(student);
        }
    }

    private void deleteSelected() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showErrorDialog("Please select a student to delete.");
            return;
        }

        showConfirmDialog("Are you sure you want to delete this student?", () -> {
            int studentId = (Integer) tableModel.getValueAt(selectedRow, 0);
            if (dataStorage.deleteStudent(studentId)) {
                mainFrame.refreshRelatedPanels("student");
                refreshData();
                mainFrame.updateStatusBar("Student deleted successfully");
            }
        });
    }

    private void viewSelected() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showErrorDialog("Please select a student to view.");
            return;
        }

        int studentId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Student student = dataStorage.getStudentById(studentId);
        if (student == null) {
            showErrorDialog("Student not found.");
            return;
        }

        showStudentDetailsDialog(student);
    }

    private void assignRoomToStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showErrorDialog("Please select a student to assign a room.");
            return;
        }

        int studentId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Student student = dataStorage.getStudentById(studentId);
        if (student == null) {
            showErrorDialog("Student not found.");
            return;
        }

        showRoomAssignmentDialog(student);
    }

    private void showRoomAssignmentDialog(Student student) {
        JDialog assignDialog = new JDialog(mainFrame, "Assign Room to Student", true);
        assignDialog.setSize(500, 400);
        assignDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Student info panel
        JPanel studentInfoPanel = new JPanel(new GridBagLayout());
        studentInfoPanel.setBorder(BorderFactory.createTitledBorder("Student Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        addInfoField(studentInfoPanel, gbc, 0, "Student Code:", student.getStudentCode());
        addInfoField(studentInfoPanel, gbc, 1, "Full Name:", student.getFullName());
        addInfoField(studentInfoPanel, gbc, 2, "Current Room:",
                student.getRoomId() == 0 ? "Not Assigned" : String.valueOf(student.getRoomId()));

        // Room selection panel
        JPanel roomPanel = new JPanel(new BorderLayout(5, 5));
        roomPanel.setBorder(BorderFactory.createTitledBorder("All Rooms - Room Status"));

        // Get all rooms instead of just available ones
        List<Room> allRooms = dataStorage.getAllRooms();

        if (allRooms.isEmpty()) {
            JLabel noRoomsLabel = new JLabel("No rooms found.");
            noRoomsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            roomPanel.add(noRoomsLabel, BorderLayout.CENTER);
        } else {
            // Create a room table with occupancy and availability information
            DefaultTableModel roomTableModel = getEnhancedRoomTableModel(allRooms, dataStorage);

            JTable roomTable = new JTable(roomTableModel) {
                @Override
                public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                    Component component = super.prepareRenderer(renderer, row, column);

                    // Get the availability status from the model
                    String availability = (String) getModel().getValueAt(row, 4);

                    // Color code rows based on availability
                    if ("FULL".equals(availability)) {
                        component.setBackground(new Color(255, 230, 230)); // Light red for full rooms
                        component.setForeground(Color.BLACK);
                    } else if ("AVAILABLE".equals(availability)) {
                        component.setBackground(new Color(230, 255, 230)); // Light green for available rooms
                        component.setForeground(Color.BLACK);
                    } else {
                        component.setBackground(Color.WHITE);
                        component.setForeground(Color.BLACK);
                    }

                    // Keep selection highlighting
                    if (isRowSelected(row)) {
                        component.setBackground(getSelectionBackground());
                        component.setForeground(getSelectionForeground());
                    }

                    return component;
                }
            };

            roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            // Set preferred column widths for better display
            roomTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // Room ID
            roomTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Room Number
            roomTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Room Type
            roomTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Occupancy
            roomTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Availability

            JScrollPane roomScrollPane = new JScrollPane(roomTable);
            roomScrollPane.setPreferredSize(new Dimension(450, 180));
            roomPanel.add(roomScrollPane, BorderLayout.CENTER);

            // Add legend for color coding
            JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            legendPanel.add(new JLabel("Condition: "));

            JLabel availableLabel = new JLabel(" Available ");
            availableLabel.setOpaque(true);
            availableLabel.setBackground(new Color(230, 255, 230));
            availableLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            legendPanel.add(availableLabel);

            JLabel fullLabel = new JLabel(" Full ");
            fullLabel.setOpaque(true);
            fullLabel.setBackground(new Color(255, 230, 230));
            fullLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            legendPanel.add(fullLabel);

            roomPanel.add(legendPanel, BorderLayout.SOUTH);

            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton assignButton = new JButton("Assign Room");
            JButton removeButton = new JButton("Remove from Room");
            JButton cancelButton = new JButton("Cancel");

            // Assign button action
            assignButton.addActionListener(e -> {
                int selectedRoomRow = roomTable.getSelectedRow();
                if (selectedRoomRow == -1) {
                    showErrorDialog("Please select a room to assign.");
                    return;
                }

                int roomId = (Integer) roomTableModel.getValueAt(selectedRoomRow, 0);
                String availability = (String) roomTableModel.getValueAt(selectedRoomRow, 4);

                // Check if room is full
                if ("FULL".equals(availability)) {
                    showErrorDialog("Cannot assign student to this room - it is already at full capacity.");
                    return;
                }

                // Confirm assignment
                int confirm = JOptionPane.showConfirmDialog(assignDialog,
                        "Are you sure you want to assign " + student.getFullName() + " to Room " + roomId + "?",
                        "Confirm Assignment", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    if (dataStorage.assignStudentToRoom(student.getStudentId(), roomId)) {
                        showInfoDialog("Student successfully assigned to room " + roomId + ".");
                        assignDialog.dispose();
                        refreshData();
                        mainFrame.updateStatusBar("Room assigned successfully");
                    } else {
                        showErrorDialog("Failed to assign room. The room may be full or unavailable.");
                    }
                }
            });

            // Remove button action (only enabled if student has a room)
            removeButton.setEnabled(student.getRoomId() != 0);
            removeButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(assignDialog,
                        "Are you sure you want to remove " + student.getFullName() + " from their current room?",
                        "Confirm Removal", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    if (dataStorage.removeStudentFromRoom(student.getStudentId())) {
                        showInfoDialog("Student successfully removed from room.");
                        assignDialog.dispose();
                        refreshData();
                        mainFrame.updateStatusBar("Student removed from room successfully");
                    } else {
                        showErrorDialog("Failed to remove student from room.");
                    }
                }
            });

            cancelButton.addActionListener(e -> assignDialog.dispose());

            buttonPanel.add(assignButton);
            buttonPanel.add(removeButton);
            buttonPanel.add(cancelButton);

            assignDialog.add(buttonPanel, BorderLayout.SOUTH);
        }

        mainPanel.add(studentInfoPanel, BorderLayout.NORTH);
        mainPanel.add(roomPanel, BorderLayout.CENTER);

        // If no rooms available, add cancel button
        if (allRooms.isEmpty()) {
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> assignDialog.dispose());
            buttonPanel.add(cancelButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        }

        assignDialog.add(mainPanel);
        assignDialog.setVisible(true);
    }

    // New method to create enhanced room table model with all rooms and their status
    private DefaultTableModel getEnhancedRoomTableModel(List<Room> allRooms, DataStorage dataStorage) {
        String[] roomColumns = {"Room ID", "Room Number", "Room Type", "Occupancy", "Availability"};
        DefaultTableModel roomTableModel = new DefaultTableModel(roomColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Room room : allRooms) {
            int currentOccupancy = dataStorage.getCurrentOccupancy(room.getRoomId());
            long capacity = room.getCapacity();
            String occupancyDisplay = currentOccupancy + "/" + capacity;

            // Determine availability status
            String availability;
            if (currentOccupancy >= capacity) {
                availability = "FULL";
            } else {
                availability = "AVAILABLE";
            }

            Object[] roomData = {
                    room.getRoomId(),
                    room.getRoomNumber(),
                    room.getRoomType(),
                    occupancyDisplay,
                    availability
            };
            roomTableModel.addRow(roomData);
        }
        return roomTableModel;
    }

    private void addInfoField(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(labelComponent.getFont().deriveFont(Font.BOLD));
        panel.add(labelComponent, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 10, 5, 5);
        JLabel valueLabel = new JLabel(value);
        panel.add(valueLabel, gbc);
        gbc.insets = new Insets(5, 5, 5, 5);
    }

    protected void displayStudentForm(Student student) {
        currentStudent = student;
        boolean isEdit = (student != null);

        formDialog = new JDialog(mainFrame, isEdit ? "Edit Student" : "Add New Student", true);
        formDialog.setSize(400, 500);
        formDialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Form fields
        studentCodeField = new JTextField(20);
        fullNameField = new JTextField(20);
        dateOfBirthField = new JTextField(20);
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        phoneField = new JTextField(20);
        emailField = new JTextField(20);
        hometownField = new JTextField(20);
        statusCombo = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE", "GRADUATED"});

        // Add components to form
        addFormField(formPanel, gbc, 0, "Student Code:", studentCodeField);
        addFormField(formPanel, gbc, 1, "Full Name:", fullNameField);
        addFormField(formPanel, gbc, 2, "Date of Birth (YYYY-MM-DD):", dateOfBirthField);
        addFormField(formPanel, gbc, 3, "Gender:", genderCombo);
        addFormField(formPanel, gbc, 4, "Phone Number:", phoneField);
        addFormField(formPanel, gbc, 5, "Email:", emailField);
        addFormField(formPanel, gbc, 6, "Hometown:", hometownField);
        addFormField(formPanel, gbc, 7, "Status:", statusCombo);

        // Fill form if editing
        if (isEdit) {
            studentCodeField.setText(student.getStudentCode());
            fullNameField.setText(student.getFullName());
            dateOfBirthField.setText(student.getDateOfBirth().toString());
            genderCombo.setSelectedItem(student.getGender());
            phoneField.setText(student.getPhoneNumber());
            emailField.setText(student.getGmail());
            hometownField.setText(student.getHometown());
            statusCombo.setSelectedItem(student.getStatus());
        }

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton(isEdit ? "Update" : "Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> saveStudent());
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

    private void saveStudent() {
        try {
            // Validate fields
            if (studentCodeField.getText().trim().isEmpty() ||
                    fullNameField.getText().trim().isEmpty() ||
                    dateOfBirthField.getText().trim().isEmpty()) {
                showErrorDialog("Please fill in all required fields.");
                return;
            }

            // Parse date
            LocalDate dateOfBirth;
            try {
                dateOfBirth = LocalDate.parse(dateOfBirthField.getText().trim());
            } catch (DateTimeParseException e) {
                showErrorDialog("Invalid date format. Please use YYYY-MM-DD.");
                return;
            }

            Student student;
            if (currentStudent == null) {
                // Create new student
                student = new Student(
                        studentCodeField.getText().trim(),
                        fullNameField.getText().trim(),
                        dateOfBirth,
                        (String) genderCombo.getSelectedItem(),
                        phoneField.getText().trim(),
                        emailField.getText().trim(),
                        hometownField.getText().trim()
                );
                student.setStatus((String) statusCombo.getSelectedItem());

                if (dataStorage.addStudent(student)) {
                    showInfoDialog("Student added successfully");
                    mainFrame.refreshRelatedPanels("student");
                } else {
                    showErrorDialog("Failed to add student.");
                    return;
                }
            } else {
                // Update existing student
                currentStudent.setStudentCode(studentCodeField.getText().trim());
                currentStudent.setFullName(fullNameField.getText().trim());
                currentStudent.setDateOfBirth(dateOfBirth);
                currentStudent.setGender((String) genderCombo.getSelectedItem());
                currentStudent.setPhoneNumber(phoneField.getText().trim());
                currentStudent.setGmail(emailField.getText().trim());
                currentStudent.setHometown(hometownField.getText().trim());
                currentStudent.setStatus((String) statusCombo.getSelectedItem());

                if (dataStorage.updateStudent(currentStudent)) {
                    showInfoDialog("Student updated successfully");
                    mainFrame.refreshRelatedPanels("student");
                } else {
                    showErrorDialog("Failed to update student.");
                    return;
                }
            }

            formDialog.dispose();
            refreshData();

        } catch (Exception e) {
            showErrorDialog("Error saving student: " + e.getMessage());
        }
    }

    private void showStudentDetailsDialog(Student student) {
        JDialog viewDialog = new JDialog(mainFrame, "Student Details", true);
        viewDialog.setSize(500, 600);
        viewDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Title panel with status
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = new JLabel("Student Information");
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
        String status = student.getStatus();
        JLabel statusLabel = new JLabel("Status: " + status);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));
        
        // Set status color
        switch (status) {
            case "ACTIVE":
                statusPanel.setBackground(new Color(220, 255, 220)); // Light green
                break;
            case "INACTIVE":
                statusPanel.setBackground(new Color(255, 220, 220)); // Light red
                break;
            case "GRADUATED":
                statusPanel.setBackground(new Color(240, 240, 240)); // Light gray
                break;
        }
        statusPanel.add(statusLabel);
        titlePanel.add(statusPanel, BorderLayout.SOUTH);

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Personal Information Section
        JPanel personalPanel = new JPanel(new GridBagLayout());
        personalPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Personal Information"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Personal details with consistent formatting
        addDetailRow(personalPanel, gbc, 0, "Student ID:", String.valueOf(student.getStudentId()));
        addDetailRow(personalPanel, gbc, 1, "Student Code:", student.getStudentCode());
        addDetailRow(personalPanel, gbc, 2, "Full Name:", student.getFullName());
        addDetailRow(personalPanel, gbc, 3, "Date of Birth:", student.getDateOfBirth().toString());
        addDetailRow(personalPanel, gbc, 4, "Gender:", student.getGender());

        contentPanel.add(personalPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Contact Information Section
        JPanel contactPanel = new JPanel(new GridBagLayout());
        contactPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Contact Information"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        GridBagConstraints cgbc = new GridBagConstraints();
        cgbc.insets = new Insets(5, 5, 5, 5);
        cgbc.anchor = GridBagConstraints.WEST;

        addDetailRow(contactPanel, cgbc, 0, "Phone Number:", student.getPhoneNumber());
        addDetailRow(contactPanel, cgbc, 1, "Email:", student.getGmail());
        addDetailRow(contactPanel, cgbc, 2, "Hometown:", student.getHometown());

        contentPanel.add(contactPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Room Information Section
        if (student.getRoomId() != 0) {
            Room room = dataStorage.getRoomById(student.getRoomId());
            if (room != null) {
                JPanel roomPanel = new JPanel(new GridBagLayout());
                roomPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Room Information"),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
                GridBagConstraints rgbc = new GridBagConstraints();
                rgbc.insets = new Insets(5, 5, 5, 5);
                rgbc.anchor = GridBagConstraints.WEST;

                addDetailRow(roomPanel, rgbc, 0, "Room Number:", room.getRoomNumber());
                addDetailRow(roomPanel, rgbc, 1, "Room Type:", room.getRoomType());
                addDetailRow(roomPanel, rgbc, 2, "Room Status:", room.getStatus());
                addDetailRow(roomPanel, rgbc, 3, "Room Price:", String.format("$%.2f", room.getRoomPrice()));

                contentPanel.add(roomPanel);
            }
        } else {
            JPanel roomPanel = new JPanel(new GridBagLayout());
            roomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Room Information"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
            GridBagConstraints rgbc = new GridBagConstraints();
            rgbc.insets = new Insets(5, 5, 5, 5);
            rgbc.anchor = GridBagConstraints.WEST;

            addDetailRow(roomPanel, rgbc, 0, "Room Assignment:", "Not assigned to any room");
            contentPanel.add(roomPanel);
        }

        // Add scrolling to content
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton editButton = new JButton("Edit");
        JButton assignRoomButton = new JButton("Assign Room");
        JButton closeButton = new JButton("Close");

        // Set preferred size for buttons
        Dimension buttonSize = new Dimension(100, 30);
        editButton.setPreferredSize(buttonSize);
        assignRoomButton.setPreferredSize(new Dimension(120, 30));
        closeButton.setPreferredSize(buttonSize);

        editButton.addActionListener(e -> {
            viewDialog.dispose();
            displayStudentForm(student);
        });
        assignRoomButton.addActionListener(e -> {
            viewDialog.dispose();
            showRoomAssignmentDialog(student);
        });
        closeButton.addActionListener(e -> viewDialog.dispose());

        buttonPanel.add(editButton);
        buttonPanel.add(assignRoomButton);
        buttonPanel.add(closeButton);

        // Add all components to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        viewDialog.add(mainPanel);
        viewDialog.setVisible(true);
    }

    private void setupTableProperties() {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(25);
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD));

        // Set column widths
        int[] columnWidths = {50, 100, 150, 100, 80, 100, 150, 150, 80, 100};
        for (int i = 0; i < columnWidths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
    }

    private void setupColumnRenderers() {
        // Custom renderer for status column
        table.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String status = (String) value;
                    if ("ACTIVE".equals(status)) {
                        c.setForeground(new Color(0, 150, 0)); // Dark green
                    } else if ("INACTIVE".equals(status)) {
                        c.setForeground(new Color(200, 0, 0)); // Dark red
                    } else {
                        c.setForeground(Color.GRAY); // Gray for graduated
                    }
                }
                return c;
            }
        });

        // Center-align certain columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        int[] centerColumns = {0, 1, 3, 4, 8}; // ID, Student Code, Date of Birth, Gender, Room ID
        for (int column : centerColumns) {
            table.getColumnModel().getColumn(column).setCellRenderer(centerRenderer);
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfoDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showConfirmDialog(String message, Runnable onConfirm) {
        int result = JOptionPane.showConfirmDialog(this, message, "Confirm", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            onConfirm.run();
        }
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(labelComponent.getFont().deriveFont(Font.BOLD));
        panel.add(labelComponent, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 10, 5, 5);
        JLabel valueLabel = new JLabel(value);
        panel.add(valueLabel, gbc);
        gbc.insets = new Insets(5, 5, 5, 5);
    }
}