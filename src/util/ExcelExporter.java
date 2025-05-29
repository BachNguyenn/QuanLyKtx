package util;

import model.Report;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;

public class ExcelExporter {
    private static final String EXCEL_DIR = "reports/excel/";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    public static String export(Report report, String[][] data, String[] headers) {
        try {
            String fileName = generateFileName(report);
            String filePath = EXCEL_DIR + fileName;
            
            // Here we would use Apache POI to generate the Excel file
            // For now, we'll just create a placeholder file
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            
            // Create a simple CSV format as placeholder
            StringBuilder content = new StringBuilder();
            content.append(String.join(",", headers)).append("\n");
            for (String[] row : data) {
                content.append(String.join(",", row)).append("\n");
            }
            
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(content.toString().getBytes());
            }
            
            report.setFilePath(filePath);
            report.setFormat("EXCEL");
            return filePath;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static String generateFileName(Report report) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String sanitizedTitle = report.getTitle().replaceAll("[^a-zA-Z0-9]", "_");
        return String.format("%s_%s.xlsx", sanitizedTitle, timestamp);
    }
} 