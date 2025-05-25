package service;

import dao.ContractDAO;
import dao.StudentDAO;
import dao.RoomDAO;
import model.Contract;
import model.Student;
import model.Room;
import java.time.LocalDate;
import java.util.List;

public class ContractService {
    private final ContractDAO contractDAO;
    private final StudentDAO studentDAO;
    private final RoomDAO roomDAO;

    public ContractService() {
        this.contractDAO = new ContractDAO();
        this.studentDAO = new StudentDAO();
        this.roomDAO = new RoomDAO();
    }

    public List<Contract> getAllContracts() {
        return contractDAO.getAllContracts();
    }

    public Contract getContractById(int contractId) {
        return contractDAO.getContractById(contractId);
    }

    public boolean createContract(Contract contract) {
        if (!isValidContractData(contract)) {
            System.err.println("Invalid contract data provided");
            return false;
        }

        if (!canCreateContract(contract)) {
            return false;
        }

        contract.setContractStatus("ACTIVE");

        if (contractDAO.addContract(contract)) {
            updateRoomAndStudentStatus(contract);
            return true;
        }

        return false;
    }

    public boolean updateContract(Contract contract) {
        if (!isValidContractData(contract)) {
            System.err.println("Invalid contract data provided");
            return false;
        }

        Contract existingContract = contractDAO.getContractById(contract.getContractId());
        if (existingContract == null) {
            System.err.println("Contract not found with ID: " + contract.getContractId());
            return false;
        }

        return contractDAO.updateContract(contract);
    }

    public boolean terminateContract(int contractId) {
        Contract contract = contractDAO.getContractById(contractId);
        if (contract == null) {
            System.err.println("Contract not found with ID: " + contractId);
            return false;
        }

        contract.setContractStatus("TERMINATED");
        contract.setEndDate(LocalDate.now());

        if (contractDAO.updateContract(contract)) {
            releaseRoomAndStudent(contract);
            return true;
        }

        return false;
    }

    public boolean deleteContract(int contractId) {
        Contract contract = contractDAO.getContractById(contractId);
        if (contract == null) {
            System.err.println("Contract not found with ID: " + contractId);
            return false;
        }
        
        if ("ACTIVE".equals(contract.getContractStatus())) {
            System.err.println("Cannot delete active contract. Terminate it first.");
            return false;
        }

        return contractDAO.deleteContract(contractId);
    }

    public List<Contract> getContractsByStudentId(int studentId) {
        return contractDAO.getContractsByStudentId(studentId);
    }

    public List<Contract> getActiveContracts() {
        return getAllContracts().stream()
            .filter(contract -> "ACTIVE".equals(contract.getContractStatus()))
            .toList();
    }

    public List<Contract> getExpiringContracts(int daysAhead) {
        LocalDate cutoffDate = LocalDate.now().plusDays(daysAhead);
        return getActiveContracts().stream()
            .filter(contract -> contract.getEndDate().isBefore(cutoffDate) || 
                               contract.getEndDate().isEqual(cutoffDate))
            .toList();
    }

    private boolean isValidContractData(Contract contract) {
        return contract != null &&
               contract.getContractCode() != null && !contract.getContractCode().trim().isEmpty() &&
               contract.getStudentId() > 0 &&
               contract.getRoomId() > 0 &&
               contract.getStartDate() != null &&
               contract.getEndDate() != null &&
               contract.getEndDate().isAfter(contract.getStartDate()) &&
               contract.getRoomPrice() != null && 
               contract.getRoomPrice().compareTo(java.math.BigDecimal.ZERO) > 0;
    }
    
    private boolean canCreateContract(Contract contract) {
        Student student = studentDAO.getStudentById(contract.getStudentId());
        if (student == null) {
            System.err.println("Student not found with ID: " + contract.getStudentId());
            return false;
        }
        
        Room room = roomDAO.getRoomById(contract.getRoomId());
        if (room == null) {
            System.err.println("Room not found with ID: " + contract.getRoomId());
            return false;
        }
        
        if (!"AVAILABLE".equals(room.getStatus())) {
            System.err.println("Room is not available: " + room.getRoomNumber());
            return false;
        }
        
        List<Contract> existingContracts = contractDAO.getContractsByStudentId(contract.getStudentId());
        boolean hasActiveContract = existingContracts.stream()
            .anyMatch(c -> "ACTIVE".equals(c.getContractStatus()));
        
        if (hasActiveContract) {
            System.err.println("Student already has an active contract");
            return false;
        }
        
        return true;
    }
    
    private void updateRoomAndStudentStatus(Contract contract) {
        Student student = studentDAO.getStudentById(contract.getStudentId());
        if (student != null) {
            student.setRoomId(contract.getRoomId());
            studentDAO.updateStudent(student);
        }
        
        Room room = roomDAO.getRoomById(contract.getRoomId());
        if (room != null) {
            room.setStatus("OCCUPIED");
            roomDAO.updateRoom(room);
        }
    }
    
    private void releaseRoomAndStudent(Contract contract) {
        Student student = studentDAO.getStudentById(contract.getStudentId());
        if (student != null) {
            student.setRoomId(0);
            studentDAO.updateStudent(student);
        }
        
        Room room = roomDAO.getRoomById(contract.getRoomId());
        if (room != null) {
            room.setStatus("AVAILABLE");
            roomDAO.updateRoom(room);
        }
    }
}