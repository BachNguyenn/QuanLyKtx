package service;

import dao.StudentDAO;
import dao.ContractDAO;
import dao.FeeDAO;
import model.Student;
import model.Contract;
import model.Fee;
import java.time.LocalDate;
import java.util.List;

public class StudentService {
    private final StudentDAO studentDAO;
    private final ContractDAO contractDAO;
    private final FeeDAO feeDAO;

    public StudentService() {
        this.studentDAO = new StudentDAO();
        this.contractDAO = new ContractDAO();
        this.feeDAO = new FeeDAO();
    }

    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }

    public Student getStudentById(int studentId) {
        return studentDAO.getStudentById(studentId);
    }

    public boolean registerStudent(Student student) {
        if (!isValidStudentData(student)) {
            System.err.println("Invalid student data provided");
            return false;
        }

        if (isStudentCodeExists(student.getStudentCode())) {
            System.err.println("Student code already exists: " + student.getStudentCode());
            return false;
        }

        student.setEnrollmentDate(LocalDate.now());
        student.setStatus("ACTIVE");

        return studentDAO.addStudent(student);
    }

    public boolean updateStudent(Student student) {
        if (!isValidStudentData(student)) {
            System.err.println("Invalid student data provided");
            return false;
        }

        Student existingStudent = studentDAO.getStudentById(student.getStudentId());
        if (existingStudent == null) {
            System.err.println("Student not found with ID: " + student.getStudentId());
            return false;
        }

        return studentDAO.updateStudent(student);
    }

    public boolean deleteStudent(int studentId) {
        Student student = studentDAO.getStudentById(studentId);
        if (student == null) {
            System.err.println("Student not found with ID: " + studentId);
            return false;
        }

        List<Contract> activeContracts = contractDAO.getContractsByStudentId(studentId);
        if (!activeContracts.isEmpty()) {
            System.err.println("Cannot delete student with active contracts");
            return false;
        }

        List<Fee> unpaidFees = feeDAO.getFeesByStudentId(studentId);
        long unpaidCount = unpaidFees.stream()
            .filter(fee -> "PENDING".equals(fee.getPaymentStatus()))
            .count();

        if (unpaidCount > 0) {
            System.err.println("Cannot delete student with unpaid fees");
            return false;
        }

        return studentDAO.deleteStudent(studentId);
    }

    public List<Student> searchStudents(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllStudents();
        }
        return studentDAO.searchByName(searchTerm.trim());
    }

    public boolean assignStudentToRoom(int studentId, int roomId) {
        Student student = studentDAO.getStudentById(studentId);
        if (student == null) {
            System.err.println("Student not found with ID: " + studentId);
            return false;
        }

        if (student.getRoomId() > 0) {
            System.err.println("Student is already assigned to a room");
            return false;
        }

        student.setRoomId(roomId);
        return studentDAO.updateStudent(student);
    }

    public boolean removeStudentFromRoom(int studentId) {
        Student student = studentDAO.getStudentById(studentId);
        if (student == null) {
            System.err.println("Student not found with ID: " + studentId);
            return false;
        }

        student.setRoomId(0);
        return studentDAO.updateStudent(student);
    }

    private boolean isValidStudentData(Student student) {
        return student != null &&
            student.getStudentCode() != null && !student.getStudentCode().trim().isEmpty() &&
            student.getFullName() != null && !student.getFullName().trim().isEmpty() &&
            student.getDateOfBirth() != null &&
            student.getGender() != null && !student.getGender().trim().isEmpty() &&
            student.getPhoneNumber() != null && !student.getPhoneNumber().trim().isEmpty() &&
            student.getEmail() != null && !student.getEmail().trim().isEmpty();
    }

    private boolean isStudentCodeExists(String studentCode) {
        List<Student> existingStudents = studentDAO.searchByName(studentCode);
        return existingStudents.stream()
            .anyMatch(s -> studentCode.equals(s.getStudentCode()));
    }
}
