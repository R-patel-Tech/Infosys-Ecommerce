package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {

    public String readCellData(String filePath, String sheetName, int rowIndex, int columnIndex) {
        Path path = Paths.get(filePath).toAbsolutePath().normalize();
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Excel file not found: " + path);
        }

        try (InputStream inputStream = Files.newInputStream(path); Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet not found: " + sheetName);
            }

            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                return "";
            }

            Cell cell = row.getCell(columnIndex);
            if (cell == null) {
                return "";
            }

            return switch (cell.getCellType()) {
                case STRING -> cell.getStringCellValue();
                case NUMERIC -> String.valueOf(cell.getNumericCellValue());
                case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
                case FORMULA -> cell.getCellFormula();
                case BLANK -> "";
                default -> "";
            };
        } catch (IOException ioException) {
            throw new IllegalStateException("Unable to read Excel data from " + path, ioException);
        }
    }

    public void writeCellData(String filePath, String sheetName, int rowIndex, int columnIndex, String value) {
        Path path = Paths.get(filePath).toAbsolutePath().normalize();
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            Workbook workbook;
            if (Files.exists(path)) {
                try (InputStream inputStream = Files.newInputStream(path)) {
                    workbook = WorkbookFactory.create(inputStream);
                }
            } else {
                workbook = new XSSFWorkbook();
            }

            try (Workbook closeableWorkbook = workbook) {
                Sheet sheet = closeableWorkbook.getSheet(sheetName);
                if (sheet == null) {
                    sheet = closeableWorkbook.createSheet(sheetName);
                }

                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    row = sheet.createRow(rowIndex);
                }

                Cell cell = row.getCell(columnIndex);
                if (cell == null) {
                    cell = row.createCell(columnIndex, CellType.STRING);
                }

                cell.setCellValue(value);

                try (OutputStream outputStream = Files.newOutputStream(path)) {
                    closeableWorkbook.write(outputStream);
                }
            }
        } catch (IOException ioException) {
            throw new IllegalStateException("Unable to write Excel data to " + path, ioException);
        }
    }
}
