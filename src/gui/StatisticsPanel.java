package gui;

import model.*;
import util.DataStorage;
import util.ReportExporter;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class StatisticsPanel extends JPanel {
    private final DataStorage dataStorage;
    private JComboBox<String> periodCombo;
    private JTable financialTable;
    private JTable occupancyTable;
    private DefaultTableModel financialModel;
    private DefaultTableModel occupancyModel;
    private JLabel totalStudentsValue;
    private JLabel occupancyRateValue;
    private JLabel activeStudentsValue;
    private JLabel totalRevenueValue;
    private JButton refreshButton;
    private JButton exportButton;
    
    public StatisticsPanel() {
        this.dataStorage = DataStorage.getInstance();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        refreshData();
    }
    
    private void initializeComponents() {
        // Top panel components
        String[] periods = {"Last 7 Days", "Last 30 Days", "Last 3 Months", "Last 6 Months", "Last Year", "All Time"};
        periodCombo = new JComboBox<>(periods);
        refreshButton = new JButton("Refresh");
        exportButton = new JButton("Export");

        // Initialize statistics labels with styled fonts
        Font valueFont = new Font(Font.SANS_SERIF, Font.BOLD, 24);
        totalStudentsValue = new JLabel("0", SwingConstants.CENTER);
        totalStudentsValue.setFont(valueFont);
        
        occupancyRateValue = new JLabel("0%", SwingConstants.CENTER);
        occupancyRateValue.setFont(valueFont);
        
        activeStudentsValue = new JLabel("0", SwingConstants.CENTER);
        activeStudentsValue.setFont(valueFont);
        
        totalRevenueValue = new JLabel("$0.00", SwingConstants.CENTER);
        totalRevenueValue.setFont(valueFont);

        // Initialize tables with custom renderers
        setupFinancialTable();
        setupOccupancyTable();
    }

    private void setupFinancialTable() {
        String[] financialColumns = {"Category", "Amount", "Percentage", "Status"};
        financialModel = new DefaultTableModel(financialColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        financialTable = new JTable(financialModel);
        financialTable.setRowHeight(25);
        financialTable.getTableHeader().setReorderingAllowed(false);
        financialTable.getTableHeader().setBackground(new Color(240, 240, 240));
        financialTable.getTableHeader().setFont(financialTable.getTableHeader().getFont().deriveFont(Font.BOLD));
        
        // Custom renderers
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        financialTable.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        financialTable.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        
        // Status column renderer
        financialTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String status = (String) value;
                    if ("GOOD".equals(status)) {
                        c.setForeground(new Color(0, 150, 0));
                    } else if ("WARNING".equals(status)) {
                        c.setForeground(new Color(200, 130, 0));
                    } else {
                        c.setForeground(new Color(200, 0, 0));
                    }
                }
                return c;
            }
        });
    }

    private void setupOccupancyTable() {
        String[] occupancyColumns = {"Room Type", "Total Rooms", "Occupied", "Available", "Occupancy Rate"};
        occupancyModel = new DefaultTableModel(occupancyColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        occupancyTable = new JTable(occupancyModel);
        occupancyTable.setRowHeight(25);
        occupancyTable.getTableHeader().setReorderingAllowed(false);
        occupancyTable.getTableHeader().setBackground(new Color(240, 240, 240));
        occupancyTable.getTableHeader().setFont(occupancyTable.getTableHeader().getFont().deriveFont(Font.BOLD));
        
        // Custom renderers
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        for (int i = 0; i < occupancyTable.getColumnCount(); i++) {
            occupancyTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel with period selection and buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topPanel.add(new JLabel("Time Period:"));
        topPanel.add(periodCombo);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(refreshButton);
        topPanel.add(exportButton);
        add(topPanel, BorderLayout.NORTH);

        // Statistics cards panel
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        cardsPanel.add(createStatCard("Total Students", totalStudentsValue, new Color(230, 240, 255)));
        cardsPanel.add(createStatCard("Room Occupancy Rate", occupancyRateValue, new Color(230, 255, 230)));
        cardsPanel.add(createStatCard("Active Students", activeStudentsValue, new Color(255, 240, 230)));
        cardsPanel.add(createStatCard("Total Revenue", totalRevenueValue, new Color(255, 255, 230)));
        
        add(cardsPanel, BorderLayout.CENTER);

        // Tables panel
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        
        // Financial table panel
        JPanel financialPanel = new JPanel(new BorderLayout());
        financialPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            "Financial Summary"
        ));
        JScrollPane financialScroll = new JScrollPane(financialTable);
        financialScroll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        financialPanel.add(financialScroll);
        
        // Occupancy table panel
        JPanel occupancyPanel = new JPanel(new BorderLayout());
        occupancyPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            "Room Occupancy Details"
        ));
        JScrollPane occupancyScroll = new JScrollPane(occupancyTable);
        occupancyScroll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        occupancyPanel.add(occupancyScroll);
        
        tablesPanel.add(financialPanel);
        tablesPanel.add(occupancyPanel);
        
        add(tablesPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createStatCard(String title, JLabel valueLabel, Color backgroundColor) {
        JPanel card = new JPanel(new BorderLayout(5, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(backgroundColor);
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        card.add(titleLabel, BorderLayout.NORTH);
        
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void setupEventHandlers() {
        periodCombo.addActionListener(e -> refreshData());
        refreshButton.addActionListener(e -> refreshData());
        exportButton.addActionListener(e -> exportStatistics());
    }
    
    private void exportStatistics() {
        // TODO: Implement export functionality
        JOptionPane.showMessageDialog(this,
            "Statistics export feature will be implemented in the next version.",
            "Coming Soon",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void refreshData() {
        List<Student> students = dataStorage.getAllStudents();
        List<Room> rooms = dataStorage.getAllRooms();
        List<Contract> contracts = dataStorage.getAllContracts();
        List<Fee> fees = dataStorage.getAllFees();
        
        // Update statistics cards
        updateStatisticsCards(students, rooms, fees);
        
        // Update tables
        updateFinancialTable(fees);
        updateOccupancyTable(rooms);
    }
    
    private void updateStatisticsCards(List<Student> students, List<Room> rooms, List<Fee> fees) {
        // Total students
        int totalStudents = students.size();
        totalStudentsValue.setText(String.valueOf(totalStudents));
        
        // Active students
        long activeStudents = students.stream()
            .filter(s -> "ACTIVE".equals(s.getStatus()))
            .count();
        activeStudentsValue.setText(String.valueOf(activeStudents));
        
        // Occupancy rate
        int totalBeds = rooms.stream()
            .mapToInt(Room::getCapacity)
            .sum();
        int occupiedBeds = rooms.stream()
            .mapToInt(r -> dataStorage.getCurrentOccupancy(r.getRoomId()))
            .sum();
        double occupancyRate = totalBeds > 0 ? (double) occupiedBeds / totalBeds * 100 : 0;
        occupancyRateValue.setText(String.format("%.1f%%", occupancyRate));
        
        // Total revenue
        BigDecimal totalRevenue = fees.stream()
            .filter(f -> "PAID".equals(f.getPaymentStatus()))
            .map(Fee::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalRevenueValue.setText(String.format("$%.2f", totalRevenue));
    }
    
    private void updateFinancialTable(List<Fee> fees) {
        financialModel.setRowCount(0);
        
        // Group fees by type and calculate statistics
        fees.stream()
            .collect(Collectors.groupingBy(Fee::getFeeType))
            .forEach((type, typeFees) -> {
                BigDecimal totalAmount = typeFees.stream()
                    .map(Fee::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                BigDecimal paidAmount = typeFees.stream()
                    .filter(f -> "PAID".equals(f.getPaymentStatus()))
                    .map(Fee::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                double paymentRate = totalAmount.doubleValue() > 0 
                    ? paidAmount.doubleValue() / totalAmount.doubleValue() * 100 
                    : 0;
                
                String status = paymentRate >= 90 ? "GOOD" :
                              paymentRate >= 70 ? "WARNING" : "ATTENTION";
                
                financialModel.addRow(new Object[] {
                    type.getDisplayName(),
                    String.format("$%.2f", totalAmount),
                    String.format("%.1f%%", paymentRate),
                    status
                });
            });
    }
    
    private void updateOccupancyTable(List<Room> rooms) {
        occupancyModel.setRowCount(0);
        
        // Group rooms by type and calculate statistics
        rooms.stream()
            .collect(Collectors.groupingBy(Room::getRoomType))
            .forEach((type, typeRooms) -> {
                int totalRooms = typeRooms.size();
                int occupied = (int) typeRooms.stream()
                    .filter(r -> "OCCUPIED".equals(r.getStatus()) || "FULL".equals(r.getStatus()))
                    .count();
                int available = totalRooms - occupied;
                double rate = totalRooms > 0 ? (double) occupied / totalRooms * 100 : 0;
                
                occupancyModel.addRow(new Object[] {
                    type,
                    totalRooms,
                    occupied,
                    available,
                    String.format("%.1f%%", rate)
                });
            });
            
        // Add total row
        int totalRooms = rooms.size();
        int totalOccupied = (int) rooms.stream()
            .filter(r -> "OCCUPIED".equals(r.getStatus()) || "FULL".equals(r.getStatus()))
            .count();
        int totalAvailable = totalRooms - totalOccupied;
        double totalRate = totalRooms > 0 ? (double) totalOccupied / totalRooms * 100 : 0;
        
        occupancyModel.addRow(new Object[] {
            "TOTAL",
            totalRooms,
            totalOccupied,
            totalAvailable,
            String.format("%.1f%%", totalRate)
        });
    }
}