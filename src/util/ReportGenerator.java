package util;

import model.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.text.NumberFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Generates various reports for the dormitory management system
 */
public class ReportGenerator {
    private static final Logger LOGGER = Logger.getLogger(ReportGenerator.class.getName());
    
    // Constants
    private static final int CACHE_DURATION_MINUTES = 30;
    private static final String REPORT_SEPARATOR = "\n===========================================\n\n";
    
    // Cache related
    private static final Map<String, Object> reportCache = new ConcurrentHashMap<>();
    private static final Map<String, LocalDateTime> cacheTimestamps = new ConcurrentHashMap<>();
    
    // Formatters
    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
    private static final NumberFormat percentFormatter = NumberFormat.getPercentInstance();
    // Data storage
    private static final DataStorage dataStorage = DataStorage.getInstance();

    /**
     * Clear expired cache entries
     */
    private static void clearExpiredCache() {
        try {
            LocalDateTime now = LocalDateTime.now();
            cacheTimestamps.entrySet().removeIf(entry -> 
                entry.getValue().plusMinutes(CACHE_DURATION_MINUTES).isBefore(now));
            reportCache.keySet().removeIf(key -> !cacheTimestamps.containsKey(key));
            LOGGER.fine("Cache cleaned successfully");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error clearing cache", e);
        }
    }

    /**
     * Get cached report if available and not expired
     */
    private static Optional<String> getCachedReport(String cacheKey) {
        try {
            if (reportCache.containsKey(cacheKey)) {
                LocalDateTime timestamp = cacheTimestamps.get(cacheKey);
                if (timestamp != null && 
                    timestamp.plusMinutes(CACHE_DURATION_MINUTES).isAfter(LocalDateTime.now())) {
                    LOGGER.fine("Cache hit for key: " + cacheKey);
                    return Optional.ofNullable((String) reportCache.get(cacheKey));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error accessing cache for key: " + cacheKey, e);
        }
        return Optional.empty();
    }

    /**
     * Cache a report result
     */
    private static void cacheReport(String cacheKey, String report) {
        try {
            reportCache.put(cacheKey, report);
            cacheTimestamps.put(cacheKey, LocalDateTime.now());
            LOGGER.fine("Report cached successfully for key: " + cacheKey);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error caching report for key: " + cacheKey, e);
        }
    }

    /**
     * Format a number as currency
     */
    private static String formatCurrency(BigDecimal amount) {
        return amount != null ? currencyFormatter.format(amount) : "N/A";
    }

    /**
     * Format a number as percentage
     */
    private static String formatPercent(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return "N/A";
        }
        return percentFormatter.format(value);
    }

    /**
     * Calculate occupancy rate safely
     */
    private static double calculateOccupancyRate(int occupied, int total) {
        return total > 0 ? (double) occupied / total : 0.0;
    }

    /**
     * Generate occupancy report showing room and bed utilization
     */
    public static String generateOccupancyReport() {
        final String CACHE_KEY = "occupancy_report";
        
        // Try to get from cache first
        Optional<String> cachedReport = getCachedReport(CACHE_KEY);
        if (cachedReport.isPresent()) {
            return cachedReport.get();
        }

        clearExpiredCache();
        
        try {
            List<Room> rooms = dataStorage.getAllRooms();
            if (rooms.isEmpty()) {
                return "No rooms available in the system.";
            }

            StringBuilder report = new StringBuilder()
                .append("ROOM OCCUPANCY REPORT")
                .append(REPORT_SEPARATOR);

            // Calculate basic statistics using parallel stream for large datasets
            int totalBeds = rooms.parallelStream().mapToInt(Room::getBedCount).sum();
            int occupiedBeds = rooms.parallelStream()
                    .mapToInt(r -> dataStorage.getCurrentOccupancy(r.getRoomId()))
                    .sum();
            double occupancyRate = calculateOccupancyRate(occupiedBeds, totalBeds);

            // Basic statistics
            report.append(String.format("Total Rooms: %d%n", rooms.size()))
                  .append(String.format("Total Beds: %d%n", totalBeds))
                  .append(String.format("Occupied Beds: %d%n", occupiedBeds))
                  .append(String.format("Overall Occupancy Rate: %s%n%n", formatPercent(occupancyRate)));

            // Room type analysis using parallel stream
            Map<String, List<Room>> roomsByType = rooms.parallelStream()
                    .collect(Collectors.groupingBy(Room::getRoomType));

            report.append("ROOM TYPE ANALYSIS:\n------------------\n");
            roomsByType.forEach((type, roomList) -> {
                int typeTotal = roomList.size();
                int typeBeds = roomList.parallelStream().mapToInt(Room::getBedCount).sum();
                int typeOccupied = roomList.parallelStream()
                        .mapToInt(r -> dataStorage.getCurrentOccupancy(r.getRoomId()))
                        .sum();
                double typeOccupancyRate = calculateOccupancyRate(typeOccupied, typeBeds);

                report.append(String.format("%n%s Rooms:%n", type))
                      .append(String.format("- Count: %d (%.1f%% of total)%n", 
                          typeTotal, (double)typeTotal/rooms.size()*100))
                      .append(String.format("- Total Beds: %d%n", typeBeds))
                      .append(String.format("- Occupied Beds: %d%n", typeOccupied))
                      .append(String.format("- Occupancy Rate: %s%n", formatPercent(typeOccupancyRate)));
            });

            String reportStr = report.toString();
            cacheReport(CACHE_KEY, reportStr);
            LOGGER.info("Occupancy report generated successfully");
            return reportStr;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating occupancy report", e);
            return "Error generating occupancy report. Please check the logs.";
        }
    }

    /**
     * Generate a financial report showing fee collection statistics
     */
    public static String generateFinancialReport() {
        clearExpiredCache();
        String cacheKey = "financial_report";
        
        if (reportCache.containsKey(cacheKey)) {
            return (String) reportCache.get(cacheKey);
        }

        List<Fee> fees = dataStorage.getAllFees();
        List<Contract> contracts = dataStorage.getAllContracts();
        
        StringBuilder report = new StringBuilder();
        report.append("FINANCIAL REPORT\n");
        report.append("================\n\n");
        
        // Tổng quan tài chính
        BigDecimal totalFees = fees.stream()
                .map(Fee::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal collectedFees = fees.stream()
                .filter(f -> "PAID".equals(f.getPaymentStatus()))
                .map(Fee::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal pendingFees = fees.stream()
                .filter(f -> "PENDING".equals(f.getPaymentStatus()))
                .map(Fee::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        report.append("FINANCIAL OVERVIEW:\n");
        report.append("-----------------\n");
        report.append(String.format("Total Fees Billed: %s\n", formatCurrency(totalFees)));
        report.append(String.format("Total Collected: %s\n", formatCurrency(collectedFees)));
        report.append(String.format("Total Pending: %s\n", formatCurrency(pendingFees)));
        report.append(String.format("Collection Rate: %s\n\n",
                formatPercent(collectedFees.doubleValue() / totalFees.doubleValue())));
        
        // Phân tích theo loại phí
        report.append("FEE TYPE ANALYSIS:\n");
        report.append("-----------------\n");
        Map<FeeType, List<Fee>> feesByType = fees.stream()
                .collect(Collectors.groupingBy(Fee::getFeeType));
        
        feesByType.forEach((type, typeList) -> {
            BigDecimal typeTotal = typeList.stream()
                    .map(Fee::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal typeCollected = typeList.stream()
                    .filter(f -> "PAID".equals(f.getPaymentStatus()))
                    .map(Fee::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            report.append(String.format("\n%s:\n", type.getDisplayName()));
            report.append(String.format("- Total Billed: %s\n", formatCurrency(typeTotal)));
            report.append(String.format("- Collected: %s\n", formatCurrency(typeCollected)));
            report.append(String.format("- Collection Rate: %s\n",
                    formatPercent(typeCollected.doubleValue() / typeTotal.doubleValue())));
        });
        
        // Phân tích theo tháng
        report.append("\nMONTHLY ANALYSIS:\n");
        report.append("----------------\n");
        Map<YearMonth, List<Fee>> feesByMonth = fees.stream()
                .collect(Collectors.groupingBy(f -> YearMonth.from(f.getDueDate())));
        
        feesByMonth.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    YearMonth month = entry.getKey();
                    List<Fee> monthFees = entry.getValue();
                    BigDecimal monthTotal = monthFees.stream()
                            .map(Fee::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    report.append(String.format("\n%s:\n", month.format(DateTimeFormatter.ofPattern("MM/yyyy"))));
                    report.append(String.format("- Total Fees: %s\n", formatCurrency(monthTotal)));
                    report.append(String.format("- Number of Fees: %d\n", monthFees.size()));
                });
        
        // Phân tích tiền đặt cọc
        BigDecimal totalDeposits = contracts.stream()
                .map(Contract::getDepositAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal activeDeposits = contracts.stream()
                .filter(c -> "ACTIVE".equals(c.getContractStatus()))
                .map(Contract::getDepositAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        report.append("\nDEPOSIT ANALYSIS:\n");
        report.append("----------------\n");
        report.append(String.format("Total Deposits Held: %s\n", formatCurrency(totalDeposits)));
        report.append(String.format("Active Deposits: %s\n", formatCurrency(activeDeposits)));
        
        String reportStr = report.toString();
        reportCache.put(cacheKey, reportStr);
        cacheTimestamps.put(cacheKey, LocalDateTime.now());
        
        return reportStr;
    }

    /**
     * Generate student statistics report
     */
    public static String generateStudentReport() {
        List<Student> students = dataStorage.getAllStudents();
        StringBuilder report = new StringBuilder();
        report.append("STUDENT STATISTICS REPORT\n");
        report.append("=========================\n\n");
        
        // Tổng số sinh viên
        report.append(String.format("Total Students: %d\n", students.size()));
        
        // Phân loại theo giới tính
        Map<String, Long> genderStats = students.stream()
                .collect(Collectors.groupingBy(Student::getGender, Collectors.counting()));
        report.append("\nGender Distribution:\n");
        genderStats.forEach((gender, count) ->
                report.append(String.format("- %s: %d\n", gender, count)));
        
        // Sinh viên theo trạng thái
        Map<String, Long> statusStats = students.stream()
                .collect(Collectors.groupingBy(Student::getStatus, Collectors.counting()));
        report.append("\nStudent Status:\n");
        statusStats.forEach((status, count) ->
                report.append(String.format("- %s: %d\n", status, count)));
        
        // Sinh viên có phòng và chưa có phòng
        long assignedStudents = students.stream()
                .filter(s -> s.getRoomId() != 0).count();
        report.append(String.format("\nRoom Assignment:\n- Assigned: %d\n- Unassigned: %d\n",
                assignedStudents, students.size() - assignedStudents));
        
        return report.toString();
    }

    /**
     * Generate contract statistics report
     */
    public static String generateContractReport() {
        List<Contract> contracts = dataStorage.getAllContracts();
        
        StringBuilder report = new StringBuilder();
        report.append("CONTRACT REPORT\n");
        report.append("===============\n\n");
        
        // Tổng số hợp đồng
        report.append(String.format("Total Contracts: %d\n", contracts.size()));
        
        // Phân loại theo trạng thái
        Map<String, Long> statusStats = contracts.stream()
                .collect(Collectors.groupingBy(Contract::getContractStatus, Collectors.counting()));
        report.append("\nContract Status:\n");
        statusStats.forEach((status, count) ->
                report.append(String.format("- %s: %d\n", status, count)));
        
        // Phân loại theo phương thức thanh toán
        Map<String, Long> paymentMethodStats = contracts.stream()
                .collect(Collectors.groupingBy(Contract::getPaymentMethod, Collectors.counting()));
        report.append("\nPayment Methods:\n");
        paymentMethodStats.forEach((method, count) ->
                report.append(String.format("- %s: %d\n", method, count)));
        
        // Thống kê hợp đồng sắp hết hạn (trong vòng 30 ngày)
        LocalDate now = LocalDate.now();
        LocalDate thirtyDaysLater = now.plusDays(30);
        long expiringContracts = contracts.stream()
                .filter(c -> {
                    LocalDate endDate = c.getEndDate();
                    return endDate.isAfter(now) && endDate.isBefore(thirtyDaysLater);
                })
                .count();
        report.append(String.format("\nContracts Expiring in 30 Days: %d\n", expiringContracts));
        
        return report.toString();
    }

    /**
     * Generate a comprehensive summary report with trends
     */
    public static String generateSummaryReport() {
        clearExpiredCache();
        String cacheKey = "summary_report";
        
        if (reportCache.containsKey(cacheKey)) {
            return (String) reportCache.get(cacheKey);
        }

        StringBuilder report = new StringBuilder();
        report.append("DORMITORY MANAGEMENT SYSTEM - SUMMARY REPORT\n");
        report.append("===========================================\n\n");
        
        // Thống kê sinh viên
        List<Student> students = dataStorage.getAllStudents();
        long activeStudents = students.stream()
                .filter(s -> "ACTIVE".equals(s.getStatus()))
                .count();
        double studentUtilization = (double) activeStudents / students.size();
        
        report.append("STUDENT STATISTICS:\n");
        report.append("-----------------\n");
        report.append(String.format("Total Students: %d\n", students.size()));
        report.append(String.format("Active Students: %d (%s)\n\n",
                activeStudents, formatPercent(studentUtilization)));
        
        // Thống kê phòng và giường
        List<Room> rooms = dataStorage.getAllRooms();
        int totalBeds = rooms.stream().mapToInt(Room::getBedCount).sum();
        int occupiedBeds = rooms.stream()
                .mapToInt(r -> dataStorage.getCurrentOccupancy(r.getRoomId()))
                .sum();
        double occupancyRate = (double) occupiedBeds / totalBeds;
        
        report.append("OCCUPANCY STATISTICS:\n");
        report.append("-------------------\n");
        report.append(String.format("Total Rooms: %d\n", rooms.size()));
        report.append(String.format("Total Beds: %d\n", totalBeds));
        report.append(String.format("Occupied Beds: %d (%s)\n\n",
                occupiedBeds, formatPercent(occupancyRate)));
        
        // Thống kê tài chính
        List<Fee> fees = dataStorage.getAllFees();
        List<Contract> contracts = dataStorage.getAllContracts();
        
        BigDecimal totalFees = fees.stream()
                .map(Fee::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal collectedFees = fees.stream()
                .filter(f -> "PAID".equals(f.getPaymentStatus()))
                .map(Fee::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        double collectionRate = collectedFees.doubleValue() / totalFees.doubleValue();
        
        report.append("FINANCIAL SUMMARY:\n");
        report.append("-----------------\n");
        report.append(String.format("Total Fees Billed: %s\n", formatCurrency(totalFees)));
        report.append(String.format("Total Collected: %s\n", formatCurrency(collectedFees)));
        report.append(String.format("Collection Rate: %s\n", formatPercent(collectionRate)));
        
        BigDecimal totalDeposits = contracts.stream()
                .map(Contract::getDepositAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        report.append(String.format("Total Deposits Held: %s\n\n", formatCurrency(totalDeposits)));
        
        // Cảnh báo và vấn đề cần chú ý
        report.append("ALERTS AND NOTIFICATIONS:\n");
        report.append("----------------------\n");
        
        // Phòng gần đầy
        long nearFullRooms = rooms.stream()
                .filter(r -> {
                    int occupancy = dataStorage.getCurrentOccupancy(r.getRoomId());
                    return occupancy > 0 && occupancy >= r.getBedCount() * 0.9;
                })
                .count();
        if (nearFullRooms > 0) {
            report.append(String.format("- %d rooms are at or above 90%% capacity\n", nearFullRooms));
        }
        
        // Hợp đồng sắp hết hạn
        LocalDate thirtyDaysLater = LocalDate.now().plusDays(30);
        long expiringContracts = contracts.stream()
                .filter(c -> {
                    LocalDate endDate = c.getEndDate();
                    return "ACTIVE".equals(c.getContractStatus()) &&
                           endDate.isBefore(thirtyDaysLater);
                })
                .count();
        if (expiringContracts > 0) {
            report.append(String.format("- %d contracts expiring within 30 days\n", expiringContracts));
        }
        
        // Phí quá hạn
        long overdueFees = fees.stream()
                .filter(f -> "PENDING".equals(f.getPaymentStatus()) &&
                            f.getDueDate().isBefore(LocalDate.now()))
                .count();
        if (overdueFees > 0) {
            report.append(String.format("- %d overdue fee payments\n", overdueFees));
        }
        
        String reportStr = report.toString();
        reportCache.put(cacheKey, reportStr);
        cacheTimestamps.put(cacheKey, LocalDateTime.now());
        
        return reportStr;
    }
}