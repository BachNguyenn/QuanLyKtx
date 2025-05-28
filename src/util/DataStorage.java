package util;

import model.*;
import javax.swing.*;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
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

    // Sử dụng ConcurrentHashMap để thread-safe
    private final Map<Integer, Student> students = new ConcurrentHashMap<>();
    private final Map<Integer, Room> rooms = new ConcurrentHashMap<>();
    private final Map<Integer, Contract> contracts = new ConcurrentHashMap<>();
    private final Map<Integer, Fee> fees = new ConcurrentHashMap<>();

    // Atomic counters cho ID
    private final AtomicInteger studentIdCounter = new AtomicInteger(0);
    private final AtomicInteger roomIdCounter = new AtomicInteger(0);
    private final AtomicInteger contractIdCounter = new AtomicInteger(0);
    private final AtomicInteger feeIdCounter = new AtomicInteger(0);

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
    }

    // Khởi tạo thư mục data
    private void initializeDataDirectory() {
        File directory = new File(DATA_DIRECTORY);
        if (!directory.exists()) {
            if (directory.mkdir()) {
                System.out.println("Đã tạo thư mục data");
            } else {
                showError("Khởi tạo", new Exception("Không thể tạo thư mục data"));
            }
        }
    }

    // Khởi tạo dữ liệu mẫu
    private void initializeSampleData() {
        // Thêm phòng mẫu
        addRoom(new ARoom("A101", 2, new BigDecimal("500"), new BigDecimal("50")));
        addRoom(new ARoom("A102", 4, new BigDecimal("800"), new BigDecimal("80")));
        addRoom(new BRoom("B101", 2, new BigDecimal("400")));
        addRoom(new BRoom("B102", 4, new BigDecimal("600")));

        // Thêm sinh viên mẫu
        addStudent(new Student("ST001", "Nguyen Van Anh", LocalDate.of(2005, 5, 15),
                "Female", "0123456789", "anh@gmail.com", "Ho Chi Minh"));
        addStudent(new Student("ST002", "Vu Thu Hang", LocalDate.of(2004, 9, 20),
                "Female", "0987654321", "hang@gmail.com", "Ha Noi"));
        // ... thêm sinh viên mẫu khác

        saveAllData();
    }

    // Phương thức lấy dữ liệu
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

    // Phương thức thêm dữ liệu
    public boolean addStudent(Student student) {
        if (student == null) return false;
        try {
            int id = studentIdCounter.incrementAndGet();
            student.setStudentId(id);
            students.put(id, student);
            saveStudents();
            return true;
        } catch (Exception e) {
            showError("Thêm sinh viên", e);
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
            showError("Thêm phòng", e);
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
            showError("Thêm hợp đồng", e);
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
            showError("Thêm phí", e);
            return false;
        }
    }

    // Phương thức cập nhật dữ liệu
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

    // Phương thức xóa dữ liệu
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
            showError("Xóa sinh viên", e);
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
            showError("Xóa phòng", e);
            return false;
        }
    }

    public boolean deleteContract(int contractId) {
        if (!contracts.containsKey(contractId)) {
            return false;
        }
        contracts.remove(contractId);
        saveContracts();
        return true;
    }

    public boolean deleteFee(int feeId) {
        if (!fees.containsKey(feeId)) {
            return false;
        }
        fees.remove(feeId);
        saveFees();
        return true;
    }

    // Phương thức tìm kiếm
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

    // Phương thức quản lý phòng
    public List<Room> getAvailableRooms() {
        return rooms.values().stream()
                .filter(room -> "AVAILABLE".equals(room.getStatus()))
                .collect(Collectors.toList());
    }

    public boolean assignStudentToRoom(int studentId, int roomId) {
        Student student = students.get(studentId);
        Room room = rooms.get(roomId);

        if (student == null || room == null) {
            return false;
        }

        // Check if student already has a room
        if (student.getRoomId() != 0) {
            return false;
        }

        // Check student and room status
        if (!"ACTIVE".equals(student.getStatus())) {
            return false;
        }

        // Check if room is available for assignment
        String roomStatus = room.getStatus();
        if (!"AVAILABLE".equals(roomStatus) && !"OCCUPIED".equals(roomStatus)) {
            return false;
        }

        // Check if room is full
        if (isRoomFull(roomId)) {
            return false;
        }

        // Assign student to room
        student.setRoomId(roomId);
        
        // Update room status based on new occupancy
        int currentOccupancy = getCurrentOccupancy(roomId);
        if (currentOccupancy >= room.getBedCount()) {
            room.setStatus("FULL");
        } else if (currentOccupancy > 0) {
            room.setStatus("OCCUPIED");
        }

        saveAllData();
        return true;
    }

    public boolean removeStudentFromRoom(int studentId) {
        Student student = getStudentById(studentId);
        if (student != null && student.getRoomId() != 0) {
            int roomId = student.getRoomId();
            Room room = getRoomById(roomId);
            student.setRoomId(0);
            
            // Update room status after removing student
            if (room != null) {
                int newOccupancy = getCurrentOccupancy(roomId);
                if (newOccupancy == 0) {
                    room.setStatus("AVAILABLE");
                } else if (newOccupancy < room.getBedCount()) {
                    room.setStatus("OCCUPIED");
                }
            }
            
            saveAllData(); // Save all changes
            return true;
        }
        return false;
    }

    public int getCurrentOccupancy(int roomId) {
        return (int) students.values().stream()
                .filter(student -> student.getRoomId() == roomId && "ACTIVE".equals(student.getStatus()))
                .count();
    }

    public boolean isRoomFull(int roomId) {
        Room room = rooms.get(roomId);
        if (room == null) return true;
        return getCurrentOccupancy(roomId) >= room.getBedCount();
    }

    // Phương thức lưu và đọc dữ liệu
    public void saveAllData() {
        saveStudents();
        saveRooms();
        saveContracts();
        saveFees();
    }

    private void loadAllData() {
        loadStudents();
        loadRooms();
        loadContracts();
        loadFees();
        
        // Cập nhật các counter
        studentIdCounter.set(students.keySet().stream().mapToInt(Integer::intValue).max().orElse(0));
        roomIdCounter.set(rooms.keySet().stream().mapToInt(Integer::intValue).max().orElse(0));
        contractIdCounter.set(contracts.keySet().stream().mapToInt(Integer::intValue).max().orElse(0));
        feeIdCounter.set(fees.keySet().stream().mapToInt(Integer::intValue).max().orElse(0));
    }

    // Lưu sinh viên
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
                student.getGmail(),
                student.getHometown(),
                student.getRoomId(),
                student.getStatus()
            );
            data.add(line);
        }
        saveToFile(STUDENTS_FILE, data);
    }

    // Đọc sinh viên
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
                        parts[6], // gmail
                        parts[7]  // hometown
                    );
                    student.setStudentId(Integer.parseInt(parts[0]));
                    student.setRoomId(Integer.parseInt(parts[8]));
                    student.setStatus(parts[9]);
                    students.put(student.getStudentId(), student);
                }
            } catch (Exception e) {
                showError("Đọc dữ liệu sinh viên", e);
            }
        }
    }

    // Lưu phòng
    private void saveRooms() {
        List<String> data = new ArrayList<>();
        for (Room room : rooms.values()) {
            String line = String.format("%d,%s,%s,%d,%s,%s,%s",
                room.getRoomId(),
                room.getRoomNumber(),
                room.getRoomType(),
                room.getBedCount(),
                room.getRoomPrice(),
                room.getAdditionalFee(),
                room.getStatus()
            );
            data.add(line);
        }
        saveToFile(ROOMS_FILE, data);
    }

    // Đọc phòng
    private void loadRooms() {
        List<String> lines = loadFromFile(ROOMS_FILE);
        
        for (String line : lines) {
            try {
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    Room room = new Room(
                        parts[1], // roomNumber
                        parts[2], // roomType
                        Integer.parseInt(parts[3]), // bedCount
                        new BigDecimal(parts[4]) // roomPrice
                    );
                    room.setRoomId(Integer.parseInt(parts[0]));
                    room.setAdditionalFee(new BigDecimal(parts[5]));
                    room.setStatus(parts[6]);
                    rooms.put(room.getRoomId(), room);
                }
            } catch (Exception e) {
                showError("Đọc dữ liệu phòng", e);
            }
        }
    }

    // Lưu hợp đồng
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

    // Đọc hợp đồng
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
                showError("Đọc dữ liệu hợp đồng", e);
            }
        }
    }

    // Lưu phí
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

    // Đọc phí
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
                showError("Đọc dữ liệu phí", e);
            }
        }
    }

    // Lưu file
    private void saveToFile(String fileName, List<String> data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_DIRECTORY + "/" + fileName))) {
            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            showError("Lưu file " + fileName, e);
        }
    }

    // Đọc file
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
            showError("Đọc file " + fileName, e);
        }
        
        return data;
    }

    // Xác nhận thoát
    public boolean showExitConfirmation() {
        int result = JOptionPane.showConfirmDialog(
            null,
            "Bạn có muốn lưu dữ liệu trước khi thoát không?",
            "Xác nhận thoát",
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

    private void showError(String operation, Exception e) {
        String message = String.format("Lỗi khi %s: %s", operation, e.getMessage());
        System.err.println(message);
        JOptionPane.showMessageDialog(null, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
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
}
