package util;

import model.Report;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;

public class PDFExporter {
    private static final String PDF_DIR = "reports/pdf/";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    public static String export(Report report, String content) {
        try {
            String fileName = generateFileName(report);
            String filePath = PDF_DIR + fileName;
            
            // Here we would use a PDF library like iText to generate the PDF
            // For now, we'll just create a placeholder file
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(content.getBytes());
            }
            
            report.setFilePath(filePath);
            report.setFormat("PDF");
            return filePath;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static String generateFileName(Report report) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String sanitizedTitle = report.getTitle().replaceAll("[^a-zA-Z0-9]", "_");
        return String.format("%s_%s.pdf", sanitizedTitle, timestamp);
    }
} 