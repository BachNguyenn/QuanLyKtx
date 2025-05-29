package model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDateTime;

public class ReportTest {
    private Report report;
    private final int TEST_ID = 1;
    private final String TEST_TITLE = "Test Report";
    private final String TEST_DESCRIPTION = "Test Description";
    private final String TEST_TYPE = "Monthly";
    private final String TEST_FILE_PATH = "reports/test.pdf";
    private final String TEST_FORMAT = "PDF";
    private final LocalDateTime TEST_DATE = LocalDateTime.now();

    @Before
    public void setUp() {
        report = new Report(TEST_ID, TEST_TITLE, TEST_DESCRIPTION, TEST_TYPE);
        report.setFilePath(TEST_FILE_PATH);
        report.setFormat(TEST_FORMAT);
        report.setGeneratedDate(TEST_DATE);
    }

    @Test
    public void testGetId() {
        assertEquals(TEST_ID, report.getId());
    }

    @Test
    public void testGetTitle() {
        assertEquals(TEST_TITLE, report.getTitle());
    }

    @Test
    public void testGetDescription() {
        assertEquals(TEST_DESCRIPTION, report.getDescription());
    }

    @Test
    public void testGetType() {
        assertEquals(TEST_TYPE, report.getType());
    }

    @Test
    public void testGetFilePath() {
        assertEquals(TEST_FILE_PATH, report.getFilePath());
    }

    @Test
    public void testGetFormat() {
        assertEquals(TEST_FORMAT, report.getFormat());
    }

    @Test
    public void testGetGeneratedDate() {
        assertEquals(TEST_DATE, report.getGeneratedDate());
    }

    @Test
    public void testSetTitle() {
        String newTitle = "New Title";
        report.setTitle(newTitle);
        assertEquals(newTitle, report.getTitle());
    }

    @Test
    public void testSetDescription() {
        String newDescription = "New Description";
        report.setDescription(newDescription);
        assertEquals(newDescription, report.getDescription());
    }

    @Test
    public void testSetType() {
        String newType = "Annual";
        report.setType(newType);
        assertEquals(newType, report.getType());
    }

    @Test
    public void testSetFilePath() {
        String newPath = "reports/new.pdf";
        report.setFilePath(newPath);
        assertEquals(newPath, report.getFilePath());
    }

    @Test
    public void testSetFormat() {
        String newFormat = "XLSX";
        report.setFormat(newFormat);
        assertEquals(newFormat, report.getFormat());
    }

    @Test
    public void testSetGeneratedDate() {
        LocalDateTime newDate = LocalDateTime.now().plusDays(1);
        report.setGeneratedDate(newDate);
        assertEquals(newDate, report.getGeneratedDate());
    }
} 