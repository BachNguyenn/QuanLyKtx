package gui;

import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDate.parse;

public class DormitoryManagementGUI extends JFrame {
    // Data storage (in real application, this would be connected to a database)
    private List<Student> students = new ArrayList<>();
    private List<Room> rooms = new ArrayList<>();
    private List<Contract> contracts = new ArrayList<>();
    private List<Fee> fees = new ArrayList<>();

    // GUI Components
    private JTabbedPane tabbedPane;
    private JTable studentTable, roomTable, contractTable, feeTable;
    private DefaultTableModel studentTableModel, roomTableModel, contractTableModel, feeTableModel;

    public DormitoryManagementGUI() {
        initializeData();
        initializeGUI();
        setupEventHandlers();
        updateStudentTable();
        updateRoomTable();
        updateContractTable();
        refreshFeeTable();
    }

    private void updateStudentTable() {
        studentTableModel.setRowCount(0);
        for (Student student : students) {
            studentTableModel.addRow(new Object[]{
                    student.getStudentId(),
                    student.getStudentCode(),
                    student.getFullName(),
                    student.getDateOfBirth(),
                    student.getGender(),
                    student.getPhoneNumber(),
                    student.getEmail(),
                    student.getHometown(),
                    student.getStatus()
            });
        }
    }

    private void updateRoomTable() {
        roomTableModel.setRowCount(0);
        for (Room room : rooms) {
            roomTableModel.addRow(new Object[]{
                    room.getRoomId(),
                    room.getRoomNumber(),
                    room.getRoomType(),
                    room.getBedCount(),
                    room.getRoomPrice(),
                    room.getAdditionalFee(),
                    room.getTotalPrice(),
                    room.getStatus()
            });
        }
    }

    private void updateContractTable() {
        contractTableModel.setRowCount(0);
    }

    private void updateFeeTable() {
        feeTableModel.setRowCount(0);
    }

    private void initializeData() {
        // Sample data initialization
        rooms.add(new ARoom("A101", 2, new BigDecimal("500"), new BigDecimal("50")));
        rooms.add(new ARoom("A102", 4, new BigDecimal("800"), new BigDecimal("80")));
        rooms.add(new BRoom("B101", 2, new BigDecimal("400")));
        rooms.add(new BRoom("B102", 3, new BigDecimal("600")));

        students.add(new Student("ST001", "Nguyen Van An", LocalDate.of(2000, 5, 15),
                "Male", "0123456789", "an@gmail.com", "Ho Chi Minh"));
        students.add(new Student("ST002", "Vu Thu Hang", LocalDate.of(2004, 9, 20),
                "Female", "0987654321", "hang@gmail.com", "Ha Noi"));

        // Assign IDs (in real app, this would be handled by database)
        for (int i = 0; i < rooms.size(); i++) {
            rooms.get(i).setRoomId(i + 1);
        }
        for (int i = 0; i < students.size(); i++) {
            students.get(i).setStudentId(i + 1);
        }
    }

    private void initializeGUI() {
        setTitle("Student Dormitory Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        // Create tabs
        tabbedPane.addTab("Students", createStudentPanel());
        tabbedPane.addTab("Rooms", createRoomPanel());
        tabbedPane.addTab("Contracts", createContractPanel());
        tabbedPane.addTab("Fees", createFeePanel());

        add(tabbedPane);

        // Menu bar
        createMenuBar();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table
        String[] columnNames = {"ID", "Student Code", "Full Name", "Date of Birth", "Gender", "Phone", "Gmail", "Hometown", "Status"};
        studentTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(studentTableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(studentTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add Student");
        JButton editBtn = new JButton("Edit Student");
        JButton deleteBtn = new JButton("Delete Student");
        JButton assignRoomBtn = new JButton("Assign Room");

        addBtn.addActionListener(e -> showAddStudentDialog());
        editBtn.addActionListener(e -> showEditStudentDialog());
        deleteBtn.addActionListener(e -> deleteSelectedStudent());
        assignRoomBtn.addActionListener(e -> showAssignRoomDialog());

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(assignRoomBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRoomPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table
        String[] columnNames = {"ID", "Room Number", "Type", "Bed Count", "Base Price", "Additional Fee", "Total Price", "Status"};
        roomTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        roomTable = new JTable(roomTableModel);
        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(roomTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addABtn = new JButton("Add A Room");
        JButton addBBtn = new JButton("Add B Room");
        JButton editBtn = new JButton("Edit Room");
        JButton deleteBtn = new JButton("Delete Room");

        addABtn.addActionListener(e -> showAddRoomDialog("A"));
        addBBtn.addActionListener(e -> showAddRoomDialog("B"));
        editBtn.addActionListener(e -> showEditRoomDialog());
        deleteBtn.addActionListener(e -> deleteSelectedRoom());

        buttonPanel.add(addABtn);
        buttonPanel.add(addBBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createContractPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table
        String[] columnNames = {"ID", "Contract Code", "Student", "Room", "Start Date", "End Date", "Price", "Status"};
        contractTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        contractTable = new JTable(contractTableModel);
        contractTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(contractTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add Contract");
        JButton editBtn = new JButton("Edit Contract");
        JButton deleteBtn = new JButton("Delete Contract");

        addBtn.addActionListener(e -> showAddContractDialog());
        editBtn.addActionListener(e -> showEditContractDialog());
        deleteBtn.addActionListener(e -> deleteSelectedContract());

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFeePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table
        String[] columnNames = {"ID", "Fee Code", "Student", "Type", "Amount", "Due Date", "Status", "Payment Date"};
        feeTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        feeTable = new JTable(feeTableModel);
        feeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(feeTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add Fee");
        JButton payBtn = new JButton("Mark as Paid");
        JButton deleteBtn = new JButton("Delete Fee");

        addBtn.addActionListener(e -> showAddFeeDialog());
        payBtn.addActionListener(e -> markFeeAsPaid());
        deleteBtn.addActionListener(e -> deleteSelectedFee());

        buttonPanel.add(addBtn);
        buttonPanel.add(payBtn);
        buttonPanel.add(deleteBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void setupEventHandlers() {
        // Double-click to view details
        studentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showStudentDetails();
                }
            }
        });
    }

    // Dialog methods
    private void showAddStudentDialog() {
        JDialog dialog = new JDialog(this, "Add Student", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField codeField = new JTextField(15);
        JTextField nameField = new JTextField(15);
        JTextField dobField = new JTextField(15);
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female"});
        JTextField phoneField = new JTextField(15);
        JTextField emailField = new JTextField(15);
        JTextField hometownField = new JTextField(15);

        // Add components
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Student Code:"), gbc);
        gbc.gridx = 1;
        panel.add(codeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Date of Birth (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        panel.add(dobField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        panel.add(genderBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Gmail:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Hometown:"), gbc);
        gbc.gridx = 1;
        panel.add(hometownField, gbc);

        JPanel buttonPanel = new JPanel();
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            try {
                Student student = new Student(
                        codeField.getText(),
                        nameField.getText(),
                        parse(dobField.getText()),
                        (String) genderBox.getSelectedItem(),
                        phoneField.getText(),
                        emailField.getText(),
                        hometownField.getText()
                );
                student.setStudentId(students.size() + 1);
                students.add(student);
                refreshStudentTable();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAddRoomDialog(String roomType) {
        JDialog dialog = new JDialog(this, "Add " + roomType + " Room", true);
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField numberField = new JTextField(15);
        JTextField bedCountField = new JTextField(15);
        JTextField priceField = new JTextField(15);
        JTextField additionalFeeField = new JTextField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Room Number:"), gbc);
        gbc.gridx = 1;
        panel.add(numberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Bed Count:"), gbc);
        gbc.gridx = 1;
        panel.add(bedCountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Base Price:"), gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);

        if ("A".equals(roomType)) {
            gbc.gridx = 0;
            gbc.gridy = 3;
            panel.add(new JLabel("Additional Fee:"), gbc);
            gbc.gridx = 1;
            panel.add(additionalFeeField, gbc);
        }

        JPanel buttonPanel = new JPanel();
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            try {
                Room room;
                if ("A".equals(roomType)) {
                    room = new ARoom(
                            numberField.getText(),
                            Integer.parseInt(bedCountField.getText()),
                            new BigDecimal(priceField.getText()),
                            new BigDecimal(additionalFeeField.getText())
                    );
                } else {
                    room = new BRoom(
                            numberField.getText(),
                            Integer.parseInt(bedCountField.getText()),
                            new BigDecimal(priceField.getText())
                    );
                }
                room.setRoomId(rooms.size() + 1);
                rooms.add(room);
                refreshRoomTable();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAddContractDialog() {
        if (students.isEmpty() || rooms.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add students and rooms first.");
            return;
        }

        JDialog dialog = new JDialog(this, "Add Contract", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField codeField = new JTextField(15);
        JComboBox<Student> studentBox = new JComboBox<>(students.toArray(new Student[0]));
        JComboBox<Room> roomBox = new JComboBox<>(rooms.toArray(new Room[0]));
        JTextField startDateField = new JTextField(15);
        JTextField endDateField = new JTextField(15);
        JTextField priceField = new JTextField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Contract Code:"), gbc);
        gbc.gridx = 1;
        panel.add(codeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Student:"), gbc);
        gbc.gridx = 1;
        panel.add(studentBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Room:"), gbc);
        gbc.gridx = 1;
        panel.add(roomBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Start Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        panel.add(startDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("End Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        panel.add(endDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);

        JPanel buttonPanel = new JPanel();
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            try {
                Student selectedStudent = (Student) studentBox.getSelectedItem();
                Room selectedRoom = (Room) roomBox.getSelectedItem();

                assert selectedStudent != null;
                Contract contract;
                contract = new Contract(
                        codeField.getText(),
                        selectedStudent.getStudentId(),
                        selectedRoom.getRoomId(),
                        parse(startDateField.getText()),
                        parse(endDateField.getText()),
                        new BigDecimal(priceField.getText())
                );
                contract.setContractId(contracts.size() + 1);
                contracts.add(contract);

                // Update student room assignment
                selectedStudent.setRoomId(selectedRoom.getRoomId());
                selectedRoom.setStatus("OCCUPIED");

                refreshAllTables();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAddFeeDialog() {
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add students first.");
            return;
        }

        JDialog dialog = new JDialog(this, "Add Fee", true);
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField codeField = new JTextField(15);
        JComboBox<Student> studentBox = new JComboBox<>(students.toArray(new Student[0]));
        JComboBox<FeeType> feeTypeBox = new JComboBox<>(FeeType.values());
        JTextField amountField = new JTextField(15);
        JTextField dueDateField = new JTextField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Fee Code:"), gbc);
        gbc.gridx = 1;
        panel.add(codeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Student:"), gbc);
        gbc.gridx = 1;
        panel.add(studentBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Fee Type:"), gbc);
        gbc.gridx = 1;
        panel.add(feeTypeBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        panel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Due Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        panel.add(dueDateField, gbc);

        JPanel buttonPanel = new JPanel();
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            try {
                Student selectedStudent = (Student) studentBox.getSelectedItem();

                Fee fee = new Fee(
                        codeField.getText(),
                        selectedStudent.getStudentId(),
                        (FeeType) feeTypeBox.getSelectedItem(),
                        new BigDecimal(amountField.getText()),
                        parse(dueDateField.getText())
                );
                fee.setFeeId(fees.size() + 1);
                fees.add(fee);
                refreshFeeTable();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // Additional methods for edit, delete, and other operations
    private void showEditStudentDialog() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to edit.");
            return;
        }

        Student student = students.get(selectedRow);

        // Implementation similar to showAddStudentDialog but with pre-filled fields
        JTextField codeField = new JTextField(student.getStudentCode());
        JTextField nameField = new JTextField(student.getFullName());
        JTextField dobField = new JTextField(student.getDateOfBirth().format(DateTimeFormatter.ISO_DATE));
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female"});
        genderBox.setSelectedItem(student.getGender());
        JTextField phoneField = new JTextField(student.getPhoneNumber());
        JTextField emailField = new JTextField(student.getEmail());
        JTextField homeField = new JTextField(student.getHometown());

        JPanel panel = new JPanel(new GridLayout(0,2,5,5));
        panel.add(new JLabel("Student Code:")); panel.add(codeField);
        panel.add(new JLabel("Full Name:"));    panel.add(nameField);
        panel.add(new JLabel("Date of Birth (YYYY-MM-DD):")); panel.add(dobField);
        panel.add(new JLabel("Gender:"));       panel.add(genderBox);
        panel.add(new JLabel("Phone:"));        panel.add(phoneField);
        panel.add(new JLabel("Gmail:"));        panel.add(emailField);
        panel.add(new JLabel("Hometown:"));     panel.add(homeField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Edit Student", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                // Cập nhật object
                student.setStudentCode(codeField.getText().trim());
                student.setFullName(nameField.getText().trim());
                student.setDateOfBirth(parse(dobField.getText().trim(), DateTimeFormatter.ISO_DATE));
                student.setGender((String)genderBox.getSelectedItem());
                student.setPhoneNumber(phoneField.getText().trim());
                student.setEmail(emailField.getText().trim());
                student.setHometown(homeField.getText().trim());
                // Refresh table
                updateStudentTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid data: " + ex.getMessage());
            }
        }
    }

    private void deleteSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this student?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            students.remove(selectedRow);
            refreshStudentTable();
        }
    }

    private void showAssignRoomDialog() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to assign a room.");
            return;
        }

        Student student = students.get(selectedRow);

        // Get available rooms
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {
            if ("AVAILABLE".equals(room.getStatus())) {
                availableRooms.add(room);
            }
        }

        if (availableRooms.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No available rooms.");
            return;
        }

        Room selectedRoom = (Room) JOptionPane.showInputDialog(
                this,
                "Select a room for " + student.getFullName() + ":",
                "Assign Room",
                JOptionPane.QUESTION_MESSAGE,
                null,
                availableRooms.toArray(),
                null
        );

        if (selectedRoom != null) {
            student.setRoomId(selectedRoom.getRoomId());
            selectedRoom.setStatus("OCCUPIED");
            refreshAllTables();
            JOptionPane.showMessageDialog(this, "Room assigned successfully!");
        }
    }

    private void showEditRoomDialog() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to edit.");
            return;
        }
        Room room = rooms.get(selectedRow);
        // Implementation similar to add room dialog
        JTextField numberField   = new JTextField(room.getRoomNumber());
        JTextField bedCountField = new JTextField(String.valueOf(room.getBedCount()));
        JTextField priceField    = new JTextField(room.getRoomPrice().toString());
        JTextField addFeeField   = new JTextField(
                room instanceof ARoom
                        ? ((ARoom)room).getAdditionalFee().toString()
                        : "0"
        );
        addFeeField.setEnabled(room instanceof ARoom);

        // Build the dialog layout
        JPanel panel = new JPanel(new GridLayout(0,2,5,5));
        panel.add(new JLabel("Room Number:")); panel.add(numberField);
        panel.add(new JLabel("Bed Count:"));    panel.add(bedCountField);
        panel.add(new JLabel("Base Price:"));   panel.add(priceField);
        if (room instanceof ARoom) {
            panel.add(new JLabel("Additional Fee:")); panel.add(addFeeField);
        }

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Edit Room", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                // Update common Room properties
                room.setRoomNumber(numberField.getText().trim());
                room.setBedCount(Integer.parseInt(bedCountField.getText().trim()));
                room.setRoomPrice(new BigDecimal(priceField.getText().trim()));
                // If it's an ARoom, update its additional fee
                if (room instanceof ARoom) {
                    ((ARoom)room).setAdditionalFee(new BigDecimal(addFeeField.getText().trim()));
                }
                // Refresh the table view to reflect changes
                updateRoomTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid data: " + ex.getMessage());
            }
        }

    }

    private void deleteSelectedRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this room?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            rooms.remove(selectedRow);
            refreshRoomTable();
        }
    }

    private void showEditContractDialog() {
        int selectedRow = contractTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a contract to edit.");
            return;
        }
        // Implementation similar to add contract dialog
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
            Contract contract = contracts.get(selectedRow);

            // Update room status
            for (Room room : rooms) {
                if (room.getRoomId() == contract.getRoomId()) {
                    room.setStatus("AVAILABLE");
                    break;
                }
            }

            // Update student room assignment
            for (Student student : students) {
                if (student.getStudentId() == contract.getStudentId()) {
                    student.setRoomId(0);
                    break;
                }
            }

            contracts.remove(selectedRow);
            refreshAllTables();
        }
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
            fees.remove(selectedRow);
            refreshFeeTable();
        }
    }

    private void markFeeAsPaid() {
        int selectedRow = feeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a fee to mark as paid.");
            return;
        }

        Fee fee = fees.get(selectedRow);
        fee.setPaymentStatus("PAID");
        fee.setPaymentDate(LocalDate.now());
        refreshFeeTable();
        JOptionPane.showMessageDialog(this, "Fee marked as paid successfully!");
    }


    private void showStudentDetails() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) return;

        Student student = students.get(selectedRow);

        StringBuilder details = new StringBuilder();
        details.append("Student Details:\n\n");
        details.append("ID: ").append(student.getStudentId()).append("\n");
        details.append("Code: ").append(student.getStudentCode()).append("\n");
        details.append("Name: ").append(student.getFullName()).append("\n");
        details.append("Date of Birth: ").append(student.getDateOfBirth()).append("\n");
        details.append("Gender: ").append(student.getGender()).append("\n");
        details.append("Phone: ").append(student.getPhoneNumber()).append("\n");
        details.append("Email: ").append(student.getEmail()).append("\n");
        details.append("Hometown: ").append(student.getHometown()).append("\n");

        // Find assigned room
        if (student.getRoomId() > 0) {
            for (Room room : rooms) {
                if (room.getRoomId() == student.getRoomId()) {
                    details.append("Assigned Room: ").append(room.getRoomNumber()).append("\n");
                    break;
                }
            }
        } else {
            details.append("Assigned Room: Not assigned\n");
        }

        // Find active contracts
        details.append("\nActive Contracts:\n");
        boolean hasContract = false;
        for (Contract contract : contracts) {
            if (contract.getStudentId() == student.getStudentId() &&
                    "ACTIVE".equals(contract.getContractStatus())) {
                details.append("- Contract ").append(contract.getContractCode())
                        .append(" (").append(contract.getStartDate())
                        .append(" to ").append(contract.getEndDate()).append(")\n");
                hasContract = true;
            }
        }
        if (!hasContract) {
            details.append("No active contracts\n");
        }

        // Find pending fees
        details.append("\nPending Fees:\n");
        boolean hasPendingFees = false;
        for (Fee fee : fees) {
            if (fee.getStudentId() == student.getStudentId() &&
                    "PENDING".equals(fee.getPaymentStatus())) {
                details.append("- ").append(fee.getFeeType())
                        .append(": $").append(fee.getAmount())
                        .append(" (Due: ").append(fee.getDueDate()).append(")\n");
                hasPendingFees = true;
            }
        }
        if (!hasPendingFees) {
            details.append("No pending fees\n");
        }

        JOptionPane.showMessageDialog(this, details.toString(), "Student Details",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAboutDialog() {
        String aboutText = "Student Dormitory Management System\n\n" +
                "Beta version 0.1\n" +
                "A comprehensive system for managing student accommodations,\n" +
                "contracts, and fee payments.\n\n" +
                "Features:\n" +
                "- Student registration and management\n" +
                "- Room allocation and tracking\n" +
                "- Contract management\n" +
                "- Fee collection and payment tracking\n" +
                "Developed using Java Swing\n" +
                "Authors:\n" +
                "Nguyen Tung Bach\n" +
                "Nguyen Thanh Duong\n" +
                "Le Duy Thai Duong\n";

        JOptionPane.showMessageDialog(this, aboutText, "About",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Table refresh methods
    private void refreshAllTables() {
        refreshStudentTable();
        refreshRoomTable();
        refreshContractTable();
        refreshFeeTable();
    }

    private void refreshStudentTable() {
        studentTableModel.setRowCount(0);
        for (Student student : students) {
            String roomNumber = "Not assigned";
            if (student.getRoomId() > 0) {
                for (Room room : rooms) {
                    if (room.getRoomId() == student.getRoomId()) {
                        roomNumber = room.getRoomNumber();
                        break;
                    }
                }
            }

            String status = student.getRoomId() > 0 ? "Assigned" : "Not Assigned";

            Object[] row = {
                    student.getStudentId(),
                    student.getStudentCode(),
                    student.getFullName(),
                    student.getDateOfBirth(),
                    student.getGender(),
                    student.getPhoneNumber(),
                    student.getEmail(),
                    student.getHometown(),
                    status
            };
            studentTableModel.addRow(row);
        }
    }

    private void refreshRoomTable() {
        roomTableModel.setRowCount(0);
        for (Room room : rooms) {
            String type = room instanceof ARoom ? "A" : "B";
            BigDecimal additionalFee = room instanceof ARoom ?
                    ((ARoom) room).getAdditionalFee() : BigDecimal.ZERO;

            Object[] row = {
                    room.getRoomId(),
                    room.getRoomNumber(),
                    type,
                    room.getBedCount(),
                    room.getRoomPrice(),
                    additionalFee,
                    room.getTotalPrice(),
                    room.getStatus()
            };
            roomTableModel.addRow(row);
        }
    }

    private void refreshContractTable() {
        contractTableModel.setRowCount(0);
        for (Contract contract : contracts) {
            String studentName = "Unknown";
            String roomNumber = "Unknown";

            // Find student name
            for (Student student : students) {
                if (student.getStudentId() == contract.getStudentId()) {
                    studentName = student.getFullName();
                    break;
                }
            }

            // Find room number
            for (Room room : rooms) {
                if (room.getRoomId() == contract.getRoomId()) {
                    roomNumber = room.getRoomNumber();
                    break;
                }
            }

            Object[] row = {
                    contract.getContractId(),
                    contract.getContractCode(),
                    studentName,
                    roomNumber,
                    contract.getStartDate(),
                    contract.getEndDate(),
                    contract.getRoomPrice(),
                    contract.getContractStatus()
            };
            contractTableModel.addRow(row);
        }
    }

    private void refreshFeeTable() {
        feeTableModel.setRowCount(0);
        for (Fee fee : fees) {
            String studentName = "Unknown";

            // Find student name
            for (Student student : students) {
                if (student.getStudentId() == fee.getStudentId()) {
                    studentName = student.getFullName();
                    break;
                }
            }

            Object[] row = {
                    fee.getFeeId(),
                    fee.getFeeCode(),
                    studentName,
                    fee.getFeeType(),
                    fee.getAmount(),
                    fee.getDueDate(),
                    fee.getPaymentStatus(),
                    fee.getPaymentDate() != null ? fee.getPaymentDate() : "Not paid"
            };
            feeTableModel.addRow(row);
        }
    }

    // Utility methods for data validation
    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\d{10,11}");
    }

    private boolean isValidDate(String dateString) {
        try {
            parse(dateString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Search functionality
    private void searchStudents(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            refreshStudentTable();
            return;
        }

        studentTableModel.setRowCount(0);
        for (Student student : students) {
            if (student.getFullName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    student.getStudentCode().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    student.getEmail().toLowerCase().contains(searchTerm.toLowerCase())) {

                String roomNumber = "Not assigned";
                if (student.getRoomId() > 0) {
                    for (Room room : rooms) {
                        if (room.getRoomId() == student.getRoomId()) {
                            roomNumber = room.getRoomNumber();
                            break;
                        }
                    }
                }

                String status = student.getRoomId() > 0 ? "Assigned" : "Not Assigned";

                Object[] row = {
                        student.getStudentId(),
                        student.getStudentCode(),
                        student.getFullName(),
                        student.getDateOfBirth(),
                        student.getGender(),
                        student.getPhoneNumber(),
                        student.getEmail(),
                        student.getHometown(),
                        status
                };
                studentTableModel.addRow(row);
            }
        }
    }

    // Generate reports
    private void generateOccupancyReport() {
        int totalRooms = rooms.size();
        int occupiedRooms = 0;
        int totalBeds = 0;
        int occupiedBeds = 0;

        for (Room room : rooms) {
            totalBeds += room.getBedCount();
            if ("OCCUPIED".equals(room.getStatus())) {
                occupiedRooms++;
                occupiedBeds += room.getBedCount();
            }
        }

        StringBuilder report = new StringBuilder();
        report.append("Dormitory Occupancy Report\n");
        report.append("Generated on: ").append(LocalDate.now()).append("\n\n");
        report.append("Room Statistics:\n");
        report.append("Total Rooms: ").append(totalRooms).append("\n");
        report.append("Occupied Rooms: ").append(occupiedRooms).append("\n");
        report.append("Available Rooms: ").append(totalRooms - occupiedRooms).append("\n");
        report.append("Occupancy Rate: ").append(String.format("%.1f",
                totalRooms > 0 ? (occupiedRooms * 100.0 / totalRooms) : 0)).append("%\n\n");

        report.append("Bed Statistics:\n");
        report.append("Total Beds: ").append(totalBeds).append("\n");
        report.append("Occupied Beds: ").append(occupiedBeds).append("\n");
        report.append("Available Beds: ").append(totalBeds - occupiedBeds).append("\n");
        report.append("Bed Occupancy Rate: ").append(String.format("%.1f",
                totalBeds > 0 ? (occupiedBeds * 100.0 / totalBeds) : 0)).append("%\n");

        JTextArea textArea = new JTextArea(report.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Occupancy Report",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateFinancialReport() {
        BigDecimal totalPendingFees = BigDecimal.ZERO;
        BigDecimal totalPaidFees = BigDecimal.ZERO;
        int pendingCount = 0;
        int paidCount = 0;

        for (Fee fee : fees) {
            if ("PENDING".equals(fee.getPaymentStatus())) {
                totalPendingFees = totalPendingFees.add(fee.getAmount());
                pendingCount++;
            } else if ("PAID".equals(fee.getPaymentStatus())) {
                totalPaidFees = totalPaidFees.add(fee.getAmount());
                paidCount++;
            }
        }

        StringBuilder report = new StringBuilder();
        report.append("Financial Report\n");
        report.append("Generated on: ").append(LocalDate.now()).append("\n\n");
        report.append("Fee Collection Summary:\n");
        report.append("Total Pending Fees: $").append(totalPendingFees).append(" (").append(pendingCount).append(" fees)\n");
        report.append("Total Collected Fees: $").append(totalPaidFees).append(" (").append(paidCount).append(" fees)\n");
        report.append("Total Outstanding: $").append(totalPendingFees).append("\n");
        report.append("Collection Rate: ").append(String.format("%.1f",
                (paidCount + pendingCount) > 0 ? (paidCount * 100.0 / (paidCount + pendingCount)) : 0)).append("%\n");

        JTextArea textArea = new JTextArea(report.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 200));

        JOptionPane.showMessageDialog(this, scrollPane, "Financial Report",
                JOptionPane.INFORMATION_MESSAGE);
    }

}

