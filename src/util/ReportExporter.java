package util;

import model.Report;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class ReportExporter {
    private static final String REPORTS_DIR = "reports/";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    public enum Format {
        PDF("pdf"),
        EXCEL("excel");
        
        private final String directory;
        
        Format(String directory) {
            this.directory = directory;
        }
        
        public String getDirectory() {
            return REPORTS_DIR + directory + "/";
        }
        
        public String getExtension() {
            return name().toLowerCase();
        }
    }
    
    public static String exportToExcel(Report report, String[][] data, String[] headers) {
        StringBuilder content = new StringBuilder();
        content.append(String.join(",", headers)).append("\n");
        for (String[] row : data) {
            content.append(String.join(",", row)).append("\n");
        }
        return export(report, Format.EXCEL, content.toString());
    }
    
    public static String exportToPDF(Report report, String content) {
        return export(report, Format.PDF, content);
    }
    
    private static String export(Report report, Format format, String content) {
        try {
            String fileName = generateFileName(report, format);
            String filePath = format.getDirectory() + fileName;
            
            // Create directory if it doesn't exist
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            
            // Write content with UTF-8 encoding
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(file), StandardCharsets.UTF_8))) {
                writer.write(content);
            }
            
            report.setFilePath(filePath);
            report.setFormat(format.name());
            return filePath;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static String generateFileName(Report report, Format format) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String sanitizedTitle = report.getTitle().replaceAll("[^a-zA-Z0-9]", "_");
        return String.format("%s_%s.%s", sanitizedTitle, timestamp, format.getExtension());
    }
}