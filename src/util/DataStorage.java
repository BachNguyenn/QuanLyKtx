package util;

import model.*;
import javax.swing.*;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DataStorage {
    private static final String DATA_DIRECTORY = "data";
    private static final String STUDENTS_FILE = "students.txt";
    private static final String ROOMS_FILE = "rooms.txt";
    private static final String CONTRACTS_FILE = "contracts.txt";
    private static final String FEES_FILE = "fees.txt";

    private static volatile DataStorage instance;

    // Using ConcurrentHashMap for thread-safety
    private final Map<Integer, Student> students = new ConcurrentHashMap<>();
    private final Map<Integer, Room> rooms = new ConcurrentHashMap<>();
    private final Map<Integer, Contract> contracts = new ConcurrentHashMap<>();
    private final Map<Integer, Fee> fees = new ConcurrentHashMap<>();

    // Atomic counters for IDs
    private final AtomicInteger studentIdCounter = new AtomicInteger(0);
    private final AtomicInteger roomIdCounter = new AtomicInteger(0);
    private final AtomicInteger contractIdCounter = new AtomicInteger(0);
    private final AtomicInteger feeIdCounter = new AtomicInteger(0);

    private List<Report> reports;
    private AtomicInteger reportIdCounter;

    public static DataStorage getInstance() {
        if (instance == null) {
            synchronized (DataStorage.class) {
                if (instance == null) {
                    instance = new DataStorage();
                }
            }
        }
        return instance;
    }

    private DataStorage() {
        initializeDataDirectory();
        loadAllData();
        if (getAllStudents().isEmpty()) {
            initializeSampleData();
        }
        reports = Collections.synchronizedList(new ArrayList<>());
        reportIdCounter = new AtomicInteger(1);
        loadReports();
    }

    // Data methods
    public List<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public List<Contract> getAllContracts() {
        return new ArrayList<>(contracts.values());
    }

    public List<Fee> getAllFees() {
        return new ArrayList<>(fees.values());
    }

    // Add methods
    public boolean addStudent(Student student) {
        if (student == null) return false;
        try {
            int id = studentIdCounter.incrementAndGet();
            student.setStudentId(id);
            students.put(id, student);
            saveStudents();
            return true;
        } catch (Exception e) {
            showError("Adding student", e);
            return false;
        }
    }

    public boolean addRoom(Room room) {
        if (room == null) return false;
        try {
            int id = roomIdCounter.incrementAndGet();
            room.setRoomId(id);
            rooms.put(id, room);
            saveRooms();
            return true;
        } catch (Exception e) {
            showError("Adding room", e);
            return false;
        }
    }

    public boolean addContract(Contract contract) {
        if (contract == null) return false;
        try {
            int id = contractIdCounter.incrementAndGet();
            contract.setContractId(id);
            contracts.put(id, contract);
            saveContracts();
            return true;
        } catch (Exception e) {
            showError("Adding contract", e);
            return false;
        }
    }

    public boolean addFee(Fee fee) {
        if (fee == null) return false;
        try {
            int id = feeIdCounter.incrementAndGet();
            fee.setFeeId(id);
            fees.put(id, fee);
            saveFees();
            return true;
        } catch (Exception e) {
            showError("Adding fee", e);
            return false;
        }
    }

    // Delete methods
    public boolean deleteStudent(int studentId) {
        if (!students.containsKey(studentId)) {
            return false;
        }
        try {
            students.remove(studentId);
            contracts.values().removeIf(contract -> contract.getStudentId() == studentId);
            fees.values().removeIf(fee -> fee.getStudentId() == studentId);
            saveAllData();
            return true;
        } catch (Exception e) {
            showError("Deleting student", e);
            return false;
        }
    }

    public boolean deleteRoom(int roomId) {
        if (!rooms.containsKey(roomId)) {
            return false;
        }
        try {
            students.values().stream()
                    .filter(student -> student.getRoomId() == roomId)
                    .forEach(student -> student.setRoomId(0));
            rooms.remove(roomId);
            contracts.values().removeIf(contract -> contract.getRoomId() == roomId);
            saveAllData();
            return true;
        } catch (Exception e) {
            showError("Deleting room", e);
            return false;
        }
    }

    public boolean deleteContract(int contractId) {
        if (!contracts.containsKey(contractId)) {
            return false;
        }
        try {
            contracts.remove(contractId);
            saveContracts();
            return true;
        } catch (Exception e) {
            showError("Deleting contract", e);
            return false;
        }
    }

    public boolean deleteFee(int feeId) {
        if (!fees.containsKey(feeId)) {
            return false;
        }
        try {
            fees.remove(feeId);
            saveFees();
            return true;
        } catch (Exception e) {
            showError("Deleting fee", e);
            return false;
        }
    }

    // Load methods
    private void loadStudents() {
        List<String> lines = loadFromFile(STUDENTS_FILE);
        
        for (String line : lines) {
            try {
                String[] parts = line.split(",");
                if (parts.length == 10) {
                    Student student = new Student(
                        parts[1], // studentCode
                        parts[2], // fullName
                        LocalDate.parse(parts[3]), // dateOfBirth
                        parts[4], // gender
                        parts[5], // phoneNumber
                        parts[6], // email
                        parts[7]  // hometown
                    );
                    student.setStudentId(Integer.parseInt(parts[0]));
                    student.setRoomId(Integer.parseInt(parts[8]));
                    student.setStatus(parts[9]);
                    students.put(student.getStudentId(), student);
                }
            } catch (Exception e) {
                showError("Loading student data", e);
            }
        }
    }

    private void loadRooms() {
        List<String> lines = loadFromFile(ROOMS_FILE);
        rooms.clear();
        
        for (String line : lines) {
            try {
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    int roomId = Integer.parseInt(parts[0]);
                    String roomNumber = parts[1];
                    int bedCount = Integer.parseInt(parts[3]);
                    BigDecimal roomPrice = new BigDecimal(parts[4]);
                    int occupancy = parts.length > 5 ? Integer.parseInt(parts[5]) : 0;
                    String status = parts[parts.length - 1];

                    Room room = new Room(roomNumber, bedCount, roomPrice);
                    room.setRoomId(roomId);
                    room.setCurrentOccupancy(occupancy);
                    room.setStatus(status);
                    rooms.put(roomId, room);
                    
                    if (roomId > roomIdCounter.get()) {
                        roomIdCounter.set(roomId);
                    }
                }
            } catch (Exception e) {
                showError("Loading room data", e);
            }
        }
    }

    private void loadContracts() {
        List<String> lines = loadFromFile(CONTRACTS_FILE);
        
        for (String line : lines) {
            try {
                String[] parts = line.split(",");
                if (parts.length == 10) {
                    Contract contract = new Contract(
                        parts[1], // contractCode
                        Integer.parseInt(parts[2]), // studentId
                        Integer.parseInt(parts[3]), // roomId
                        LocalDate.parse(parts[4]), // startDate
                        LocalDate.parse(parts[5]), // endDate
                        new BigDecimal(parts[6]) // roomPrice
                    );
                    contract.setContractId(Integer.parseInt(parts[0]));
                    contract.setPaymentMethod(parts[7]);
                    contract.setContractStatus(parts[8]);
                    contract.setDepositAmount(new BigDecimal(parts[9]));
                    contracts.put(contract.getContractId(), contract);
                }
            } catch (Exception e) {
                showError("Loading contract data", e);
            }
        }
    }

    private void loadFees() {
        List<String> lines = loadFromFile(FEES_FILE);
        
        for (String line : lines) {
            try {
                String[] parts = line.split(",");
                if (parts.length == 10) {
                    Fee fee = new Fee(
                        parts[1], // feeCode
                        Integer.parseInt(parts[2]), // studentId
                        FeeType.valueOf(parts[3]), // feeType
                        new BigDecimal(parts[4]), // amount
                        LocalDate.parse(parts[7]) // dueDate
                    );
                    fee.setFeeId(Integer.parseInt(parts[0]));
                    fee.setPaymentMethod(parts[5]);
                    fee.setPaymentStatus(parts[6]);
                    
                    if (!"null".equals(parts[8])) {
                        fee.setPaymentDate(LocalDate.parse(parts[8]));
                    }
                    
                    if (!"null".equals(parts[9])) {
                        fee.setDescription(parts[9].replace(";;", ","));
                    }
                    
                    fees.put(fee.getFeeId(), fee);
                }
            } catch (Exception e) {
                showError("Loading fee data", e);
            }
        }
    }

    // File operations
    private void saveToFile(String fileName, List<String> data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_DIRECTORY + "/" + fileName))) {
            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            showError("Saving file " + fileName, e);
        }
    }

    private List<String> loadFromFile(String fileName) {
        List<String> data = new ArrayList<>();
        File file = new File(DATA_DIRECTORY + "/" + fileName);
        
        if (!file.exists()) {
            return data;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                data.add(line);
            }
        } catch (IOException e) {
            showError("Reading file " + fileName, e);
        }
        
        return data;
    }

    // Update counters
    private void updateCounters() {
        studentIdCounter.set(students.keySet().stream().mapToInt(Integer::intValue).max().orElse(0));
        roomIdCounter.set(rooms.keySet().stream().mapToInt(Integer::intValue).max().orElse(0));
        contractIdCounter.set(contracts.keySet().stream().mapToInt(Integer::intValue).max().orElse(0));
        feeIdCounter.set(fees.keySet().stream().mapToInt(Integer::intValue).max().orElse(0));
    }

    private void loadAllData() {
        loadStudents();
        loadRooms();
        loadContracts();
        loadFees();
        updateCounters();
    }

    // Error handling
    private void showError(String operation, Exception e) {
        String message = String.format("Error during %s: %s", operation, e.getMessage());
        System.err.println(message);
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Initialize data directory
    private void initializeDataDirectory() {
        File directory = new File(DATA_DIRECTORY);
        if (!directory.exists()) {
            if (directory.mkdir()) {
                System.out.println("Data directory created successfully");
            } else {
                showError("Initialization", new Exception("Could not create data directory"));
            }
        }
    }

    // Initialize sample data
    private void initializeSampleData() {
        // Clear existing data
        students.clear();
        rooms.clear();
        contracts.clear();
        fees.clear();

        // Reset counters
        studentIdCounter.set(0);
        roomIdCounter.set(0);
        contractIdCounter.set(0);
        feeIdCounter.set(0);

        // Add sample rooms with USD prices
        addRoom(new Room("P401", 4, new BigDecimal("120.00"))); // 4-person room
        addRoom(new Room("P402", 4, new BigDecimal("120.00"))); // 4-person room
        addRoom(new Room("P801", 8, new BigDecimal("80.00"))); // 8-person room
        addRoom(new Room("P802", 8, new BigDecimal("80.00"))); // 8-person room

        // Add sample students
        addStudent(new Student("ST001", "Nguyen Van A", LocalDate.of(2005, 5, 15),
                "Male", "0123456789", "A@email.com", "Ha Noi"));
        addStudent(new Student("ST002", "Nguyen Van B", LocalDate.of(2004, 9, 20),
                "Male", "0987654321", "B@email.com", "Hai Phong"));
        addStudent(new Student("ST003", "Nguyen Thi C", LocalDate.of(2005, 4, 25),
                "Female", "0916627268", "C@email.com", "Ho Chi Minh"));
        addStudent(new Student("ST004", "Nguyen Thi D", LocalDate.of(2004, 8, 12),
                "Female", "0945123456", "D@email.com", "Nghe An"));

        // Add sample contracts
        Contract contract1 = new Contract(
            "C001", 1, 1,
            LocalDate.now(),
            LocalDate.now().plusMonths(6),
            new BigDecimal("120.00")
        );
        contract1.setDepositAmount(new BigDecimal("120.00"));
        contract1.setPaymentMethod("Bank Transfer");
        addContract(contract1);

        Contract contract2 = new Contract(
            "C002", 2, 1,
            LocalDate.now(),
            LocalDate.now().plusMonths(6),
            new BigDecimal("120.00")
        );
        contract2.setDepositAmount(new BigDecimal("120.00"));
        contract2.setPaymentMethod("Cash");
        addContract(contract2);

        // Add sample fees
        Fee fee1 = new Fee(
            "F001", 1,
            FeeType.ROOM_FEE,
            new BigDecimal("120.00"),
            LocalDate.now().plusMonths(1)
        );
        fee1.setPaymentMethod("Bank Transfer");
        addFee(fee1);

        Fee fee2 = new Fee(
            "F002", 2,
            FeeType.ROOM_FEE,
            new BigDecimal("120.00"),
            LocalDate.now().plusMonths(1)
        );
        fee2.setPaymentMethod("Cash");
        addFee(fee2);

        saveAllData();
    }

    // Update methods
    public boolean updateStudent(Student student) {
        if (student == null || !students.containsKey(student.getStudentId())) {
            return false;
        }
        students.put(student.getStudentId(), student);
        saveStudents();
        return true;
    }

    public boolean updateRoom(Room room) {
        if (room == null || !rooms.containsKey(room.getRoomId())) {
            return false;
        }
        rooms.put(room.getRoomId(), room);
        saveRooms();
        return true;
    }

    public boolean updateContract(Contract contract) {
        if (contract == null || !contracts.containsKey(contract.getContractId())) {
            return false;
        }
        contracts.put(contract.getContractId(), contract);
        saveContracts();
        return true;
    }

    public boolean updateFee(Fee fee) {
        if (fee == null || !fees.containsKey(fee.getFeeId())) {
            return false;
        }
        fees.put(fee.getFeeId(), fee);
        saveFees();
        return true;
    }

    // Search methods
    public Student getStudentById(int studentId) {
        return students.get(studentId);
    }

    public Room getRoomById(int roomId) {
        return rooms.get(roomId);
    }

    public Contract getContractById(int contractId) {
        return contracts.get(contractId);
    }

    public Fee getFeeById(int feeId) {
        return fees.get(feeId);
    }

    // Room management methods
    public List<Room> getAvailableRooms() {
        return rooms.values().stream()
                .filter(room -> "AVAILABLE".equals(room.getStatus()))
                .collect(Collectors.toList());
    }

    public boolean assignStudentToRoom(int studentId, int roomId) {
        Room room = getRoomById(roomId);
        Student student = getStudentById(studentId);
        
        if (room == null || student == null) {
            return false;
        }

        // Check if room has available beds
        if (!room.hasAvailableBeds()) {
            return false;
        }

        // If student is already in a room, remove them first
        if (student.getRoomId() != 0) {
            removeStudentFromRoom(studentId);
        }

        // Update student's room assignment
        student.setRoomId(roomId);
        
        // Update room occupancy
        room.incrementOccupancy();
        
        return updateStudent(student) && updateRoom(room);
    }

    public boolean removeStudentFromRoom(int studentId) {
        Student student = getStudentById(studentId);
        if (student == null || student.getRoomId() == 0) {
            return false;
        }

        Room room = getRoomById(student.getRoomId());
        if (room == null) {
            return false;
        }

        // Update room occupancy
        room.decrementOccupancy();
        
        // Clear student's room assignment
        student.setRoomId(0);
        
        return updateStudent(student) && updateRoom(room);
    }

    public int getCurrentOccupancy(int roomId) {
        Room room = getRoomById(roomId);
        return room != null ? room.getCurrentOccupancy() : 0;
    }

    public boolean isRoomFull(int roomId) {
        Room room = getRoomById(roomId);
        return room != null && !room.hasAvailableBeds();
    }

    public int getAvailableBeds(int roomId) {
        Room room = getRoomById(roomId);
        return room != null ? room.getAvailableBeds() : 0;
    }

    // Data saving and loading methods
    public void saveAllData() {
        saveStudents();
        saveRooms();
        saveContracts();
        saveFees();
    }

    // Save students
    private void saveStudents() {
        List<String> data = new ArrayList<>();
        for (Student student : students.values()) {
            String line = String.format("%d,%s,%s,%s,%s,%s,%s,%s,%d,%s",
                student.getStudentId(),
                student.getStudentCode(),
                student.getFullName(),
                student.getDateOfBirth(),
                student.getGender(),
                student.getPhoneNumber(),
                student.getEmail(),
                student.getHometown(),
                student.getRoomId(),
                student.getStatus()
            );
            data.add(line);
        }
        saveToFile(STUDENTS_FILE, data);
    }

    // Save rooms
    private void saveRooms() {
        List<String> data = new ArrayList<>();
        for (Room room : rooms.values()) {
            String line = String.format("%d,%s,%s,%d,%.2f,%d,%s",
                room.getRoomId(),
                room.getRoomNumber(),
                room.getRoomType(),
                room.getBedCount(),
                room.getRoomPrice(),
                room.getCurrentOccupancy(),
                room.getStatus()
            );
            data.add(line);
        }
        saveToFile(ROOMS_FILE, data);
    }

    // Save contracts
    private void saveContracts() {
        List<String> data = new ArrayList<>();
        for (Contract contract : contracts.values()) {
            String line = String.format("%d,%s,%d,%d,%s,%s,%s,%s,%s,%s",
                contract.getContractId(),
                contract.getContractCode(),
                contract.getStudentId(),
                contract.getRoomId(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getRoomPrice(),
                contract.getPaymentMethod(),
                contract.getContractStatus(),
                contract.getDepositAmount()
            );
            data.add(line);
        }
        saveToFile(CONTRACTS_FILE, data);
    }

    // Save fees
    private void saveFees() {
        List<String> data = new ArrayList<>();
        for (Fee fee : fees.values()) {
            String paymentDateStr = fee.getPaymentDate() != null ? fee.getPaymentDate().toString() : "null";
            String line = String.format("%d,%s,%d,%s,%s,%s,%s,%s,%s,%s",
                fee.getFeeId(),
                fee.getFeeCode(),
                fee.getStudentId(),
                fee.getFeeType().name(),
                fee.getAmount().toString(),
                fee.getPaymentMethod(),
                fee.getPaymentStatus(),
                fee.getDueDate().toString(),
                paymentDateStr,
                fee.getDescription() != null ? fee.getDescription().replace(",", ";;") : "null"
            );
            data.add(line);
        }
        saveToFile(FEES_FILE, data);
    }

    // Exit confirmation
    public boolean showExitConfirmation() {
        int result = JOptionPane.showConfirmDialog(
            null,
            "Do you want to save data before exiting?",
            "Exit Confirmation",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            saveAllData();
            return true;
        } else if (result == JOptionPane.NO_OPTION) {
            return true;
        }
        return false;
    }

    public boolean terminateContract(int contractId) {
        Contract contract = getContractById(contractId);
        if (contract != null && "Active".equals(contract.getStatus())) {
            contract.setStatus("Terminated");
            return updateContract(contract);
        }
        return false;
    }

    public boolean recordFeePayment(int feeId) {
        Fee fee = getFeeById(feeId);
        if (fee != null && "Unpaid".equals(fee.getStatus())) {
            fee.setPaymentStatus("Paid");
            fee.setPaymentDate(LocalDate.now());
            return updateFee(fee);
        }
        return false;
    }

    public int getNextReportId() {
        return reportIdCounter.getAndIncrement();
    }

    public void addReport(Report report) {
        reports.add(report);
        saveReports();
    }

    public List<Report> getReports() {
        return new ArrayList<>(reports);
    }

    private void saveReports() {
        try {
            File file = new File("data/reports.txt");
            file.getParentFile().mkdirs();
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (Report report : reports) {
                    writer.println(String.format("%d|%s|%s|%s|%s|%s|%s",
                        report.getId(),
                        report.getTitle(),
                        report.getDescription(),
                        report.getType(),
                        report.getGeneratedDate(),
                        report.getFilePath(),
                        report.getFormat()
                    ));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadReports() {
        try {
            File file = new File("data/reports.txt");
            if (!file.exists()) return;
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 7) {
                        Report report = new Report(
                            Integer.parseInt(parts[0]),
                            parts[1],
                            parts[2],
                            parts[3]
                        );
                        report.setGeneratedDate(LocalDateTime.parse(parts[4]));
                        report.setFilePath(parts[5]);
                        report.setFormat(parts[6]);
                        reports.add(report);
                        
                        // Update counter
                        reportIdCounter.set(Math.max(reportIdCounter.get(), report.getId() + 1));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
