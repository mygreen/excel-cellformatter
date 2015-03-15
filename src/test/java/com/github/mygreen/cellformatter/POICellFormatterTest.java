package com.github.mygreen.cellformatter;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;


public class POICellFormatterTest {
    
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testFormatExcel2010() {
        
        File file = new File("src/test/data/cell_format_2010.xlsx");
        POICellFormatter cellFormatter = new POICellFormatter();
        try {
            List<Sheet> sheetList = loadSheet(file);
            for(Sheet sheet : sheetList) {
                assertSheet(sheet, cellFormatter);
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
        
    }
    
    @Test
    public void testFormatExcel2010_test() {
        
        File file = new File("src/test/data/cell_format_2010.xlsx");
        POICellFormatter cellFormatter = new POICellFormatter();
        try {
            List<Sheet> sheetList = loadSheetForTest(file);
            for(Sheet sheet : sheetList) {
                assertSheet(sheet, cellFormatter);
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
        
    }
    
    @Test
    public void testFormatExcel2007() {
        
        File file = new File("src/test/data/cell_format_2007.xlsx");
        POICellFormatter cellFormatter = new POICellFormatter();
        try {
            List<Sheet> sheetList = loadSheet(file);
            for(Sheet sheet : sheetList) {
                assertSheet(sheet, cellFormatter);
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
        
    }
    
    @Test
    public void testFormatExcel2007_test() {
        
        File file = new File("src/test/data/cell_format_2007.xlsx");
        POICellFormatter cellFormatter = new POICellFormatter();
        try {
            List<Sheet> sheetList = loadSheetForTest(file);
            for(Sheet sheet : sheetList) {
                assertSheet(sheet, cellFormatter);
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
        
    }
    
    @Test
    public void testFormatExcel2000() {
        
        File file = new File("src/test/data/cell_format_2000.xls");
        POICellFormatter cellFormatter = new POICellFormatter();
        try {
            List<Sheet> sheetList = loadSheet(file);
            for(Sheet sheet : sheetList) {
                assertSheet(sheet, cellFormatter);
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
        
    }
    
    @Test
    public void testFormatExcel2000_test() {
        
        File file = new File("src/test/data/cell_format_2000.xls");
        POICellFormatter cellFormatter = new POICellFormatter();
        try {
            List<Sheet> sheetList = loadSheetForTest(file);
            for(Sheet sheet : sheetList) {
                assertSheet(sheet, cellFormatter);
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
        
    }
    
    private List<Sheet> loadSheet(final File file) throws InvalidFormatException, IOException {
        
        List<Sheet> list = new ArrayList<>();
        try(InputStream in = new FileInputStream(file)) {
            Workbook workbook = WorkbookFactory.create(in);
            
            final int sheetNum = workbook.getNumberOfSheets();
            for(int i=0; i < sheetNum; i++) {
                
                final Sheet sheet = workbook.getSheetAt(i);
                final String sheetName = sheet.getSheetName();
                if(!sheetName.startsWith("書式")) {
                    continue;
                }
                
                list.add(sheet);
                
            }
        }
        
        return list;
    }
    
    /**
     * テスト用のシートの取得
     * <p>修正したりするためのシート
     */
    private List<Sheet> loadSheetForTest(final File file) throws InvalidFormatException, IOException {
        
        List<Sheet> list = new ArrayList<>();
        try(InputStream in = new FileInputStream(file)) {
            Workbook workbook = WorkbookFactory.create(in);
        
            final int sheetNum = workbook.getNumberOfSheets();
            for(int i=0; i < sheetNum; i++) {
                
                final Sheet sheet = workbook.getSheetAt(i);
                final String sheetName = sheet.getSheetName();
                if(!sheetName.startsWith("テスト")) {
                    continue;
                }
                
                list.add(sheet);
                
            }
        }
        
        return list;
    }
    
    private void assertSheet(final Sheet sheet, final POICellFormatter cellFormatter) {
        
        System.out.printf("======== START : [%s] =========\n", sheet.getSheetName());
        
        final int maxRow = sheet.getLastRowNum();
        for(int r=3; r < maxRow; r++) {
            
            final Row row = sheet.getRow(r);
            if(row == null) {
                break;
            }
            
            final Cell noCell = row.getCell(0);
            final Cell desctiptionCell = row.getCell(1);
            if(!hasBorderBottom(noCell) || !hasBorderBottom(desctiptionCell)) {
                break;
            }
            
            final Cell testCaseCell = row.getCell(2);
            final Cell testResultCell = row.getCell(3);
            
            final String no = String.valueOf((int)noCell.getNumericCellValue());
            final String description = desctiptionCell.getRichStringCellValue().getString();
            if(description.isEmpty()) {
                break;
            }
            
            final String testCase = cellFormatter.format(testCaseCell);
            final String testResult = testResultCell.getRichStringCellValue().getString();
            
            final String test = testCase.equals(testResult) ? "○" : "×";
            
            // セルのスタイル情報の取得
            final CellStyle cellStyle = testCaseCell.getCellStyle();
            final short formatIndex = cellStyle.getDataFormat();
            final String formatPattern = cellStyle.getDataFormatString();
            final boolean poiDate = testCaseCell.getCellType() == Cell.CELL_TYPE_NUMERIC && DateUtil.isCellDateFormatted(testCaseCell);
            
            System.out.printf("[%3s] [%s] [%s] : actual=\"%s\" : exprected=\"%s\" \t%d\t%s\t%b\n",
                    no, description, test, testCase, testResult, formatIndex, formatPattern, poiDate);
            
            assertThat(String.format("[%s] [%s]", no, description), testCase, is(testResult));
        }
        
        System.out.printf("======== END : [%s] =========\n\n", sheet.getSheetName());
        
    }
    
    private boolean hasBorderBottom(final Cell cell) {
        
        if(cell == null) {
            return false;
        }
        
        final CellStyle style = cell.getCellStyle();
        if(style == null) {
            return false;
        }
        
        if(style.getBorderBottom() == CellStyle.BORDER_NONE) {
            return false;
        }
        
        return true;
    }
}
