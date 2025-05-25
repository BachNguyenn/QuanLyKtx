package service;

import dao.FeeDAO;
import dao.StudentDAO;
import model.Fee;
import model.FeeType;
import model.Student;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FeeService {
    private final FeeDAO feeDAO;
    private final StudentDAO studentDAO;

    public FeeService() {
        this.feeDAO = new FeeDAO();
        this.studentDAO = new StudentDAO();
    }

    public List<Fee> getAllFees() {
        return feeDAO.getAllFees();
    }

    public Fee getFeeById(int feeId) {
        return feeDAO.getFeeById(feeId);
    }

    public boolean createFee(Fee fee) {
        if (!isValidFeeData(fee)) {
            System.err.println("Invalid fee data provided");
            return false;
        }

        Student student = studentDAO.getStudentById(fee.getStudentId());
        if (student == null) {
            System.err.println("Student not found with ID: " + fee.getStudentId());
            return false;
        }

        fee.setPaymentStatus("PENDING");
        return feeDAO.addFee(fee);
    }

    public boolean updateFee(Fee fee) {
        if (!isValidFeeData(fee)) {
            System.err.println("Invalid fee data provided");
            return false;
        }

        Fee existingFee = feeDAO.getFeeById(fee.getFeeId());
        if (existingFee == null) {
            System.err.println("Fee not found with ID: " + fee.getFeeId());
            return false;
        }

        return feeDAO.updateFee(fee);
    }

    public boolean deleteFee(int feeId) {
        Fee fee = feeDAO.getFeeById(feeId);
        if (fee == null) {
            System.err.println("Fee not found with ID: " + feeId);
            return false;
        }

        if ("PAID".equals(fee.getPaymentStatus())) {
            System.err.println("Cannot delete paid fee");
            return false;
        }

        return feeDAO.deleteFee(feeId);
    }

    public boolean processPayment(int feeId, String paymentMethod) {
        Fee fee = feeDAO.getFeeById(feeId);
        if (fee == null) {
            System.err.println("Fee not found with ID: " + feeId);
            return false;
        }

        if ("PAID".equals(fee.getPaymentStatus())) {
            System.err.println("Fee is already paid");
            return false;
        }

        fee.setPaymentStatus("PAID");
        fee.setPaymentMethod(paymentMethod);
        fee.setPaymentDate(LocalDate.now());

        return feeDAO.updateFee(fee);
    }

    public List<Fee> getFeesByStudentId(int studentId) {
        return feeDAO.getFeesByStudentId(studentId);
    }

    public List<Fee> getUnpaidFees() {
        return feeDAO.getUnpaidFees();
    }

    public List<Fee> getOverdueFees() {
        LocalDate today = LocalDate.now();
        return getUnpaidFees().stream()
                .filter(fee -> fee.getDueDate().isBefore(today))
                .toList();
    }

    public BigDecimal getTotalUnpaidAmount(int studentId) {
        return getFeesByStudentId(studentId).stream()
                .filter(fee -> "PENDING".equals(fee.getPaymentStatus()))
                .map(Fee::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<FeeType, BigDecimal> getFeesSummaryByType() {
        return getAllFees().stream()
                .collect(Collectors.groupingBy(
                        Fee::getFeeType,
                        Collectors.reducing(BigDecimal.ZERO, Fee::getAmount, BigDecimal::add)
                ));
    }

    public boolean generateMonthlyFees(List<Student> students, FeeType feeType,
                                       BigDecimal amount, LocalDate dueDate) {
        boolean allSuccess = true;
        LocalDate billingMonth = LocalDate.now().withDayOfMonth(1);

        for (Student student : students) {
            if (student.getRoomId() <= 0) {
                continue;
            }

            String feeCode = generateFeeCode(feeType, student.getStudentId(), billingMonth);

            Fee fee = new Fee(feeCode, student.getStudentId(), feeType, amount, dueDate);
            fee.setBillingMonth(billingMonth);
            fee.setDescription(String.format("%s fee for %s",
                    feeType.name(), student.getFullName()));

            if (!createFee(fee)) {
                System.err.println("Failed to create fee for student: " + student.getStudentCode());
                allSuccess = false;
            }
        }

        return allSuccess;
    }

    private boolean isValidFeeData(Fee fee) {
        return fee != null &&
                fee.getFeeCode() != null && !fee.getFeeCode().trim().isEmpty() &&
                fee.getStudentId() > 0 &&
                fee.getFeeType() != null &&
                fee.getAmount() != null && fee.getAmount().compareTo(BigDecimal.ZERO) > 0 &&
                fee.getDueDate() != null;
    }

    private String generateFeeCode(FeeType feeType, int studentId, LocalDate billingMonth) {
        return String.format("%s-%d-%04d%02d",
                feeType.name(),
                studentId,
                billingMonth.getYear(),
                billingMonth.getMonthValue());
    }
}