package com.github.mygreen.cellformatter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Border;
import jxl.format.CellFormat;
import jxl.read.biff.BiffException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * JExcelAPI用のフォーマッタのテスタ
 *
 * @author T.TSUCHIE
 *
 */
public class JXLCellFormatterTest {
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testFormatExcel2010_compatible() {
        
        File file = new File("src/test/data/cell_format_2010_compatible.xls");
        JXLCellFormatter cellFormatter = new JXLCellFormatter();
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
    public void testFormatExcel2010_compatible_test() {
        
        File file = new File("src/test/data/cell_format_2010_compatible.xls");
        JXLCellFormatter cellFormatter = new JXLCellFormatter();
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
        JXLCellFormatter cellFormatter = new JXLCellFormatter();
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
        JXLCellFormatter cellFormatter = new JXLCellFormatter();
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
    @Ignore
    public void testFormatLibre() {
        
        File file = new File("src/test/data/cell_format_libre.xls");
        JXLCellFormatter cellFormatter = new JXLCellFormatter();
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
    @Ignore
    public void testFormatLibre_test() {
        
        File file = new File("src/test/data/cell_format_libre.xls");
        JXLCellFormatter cellFormatter = new JXLCellFormatter();
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
    
    private List<Sheet> loadSheet(final File file) throws IOException, BiffException {
        
        final List<Sheet> list = new ArrayList<>();
        try(InputStream in = new FileInputStream(file)) {
            final WorkbookSettings settings = new WorkbookSettings();
            settings.setSuppressWarnings(true);
            settings.setGCDisabled(true);
            
            // 文字コードを「ISO8859_1」にしないと文字化けする
            settings.setEncoding("ISO8859_1");
            settings.setLocale(Locale.JAPANESE);
            
            final Workbook workbook = Workbook.getWorkbook(in, settings);
            
            final int sheetNum = workbook.getNumberOfSheets();
            for(int i=0; i < sheetNum; i++) {
                
                final Sheet sheet = workbook.getSheet(i);
                
                
                
                final String sheetName = sheet.getName();
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
    private List<Sheet> loadSheetForTest(final File file) throws IOException, BiffException {
        
        final List<Sheet> list = new ArrayList<>();
        try(InputStream in = new FileInputStream(file)) {
            final WorkbookSettings settings = new WorkbookSettings();
            settings.setSuppressWarnings(true);
            settings.setGCDisabled(true);
            
            // 文字コードを「ISO8859_1」にしないと文字化けする
            settings.setEncoding("ISO8859_1");
            settings.setLocale(Locale.JAPANESE);
            
            final Workbook workbook = Workbook.getWorkbook(in, settings);
            
            
            final int sheetNum = workbook.getNumberOfSheets();
            for(int i=0; i < sheetNum; i++) {
                
                final Sheet sheet = workbook.getSheet(i);
                final String sheetName = sheet.getName();
                if(!sheetName.startsWith("テスト")) {
                    continue;
                }
                
                list.add(sheet);
                
            }
        }
        
        return list;
    }
    
    private void assertSheet(final Sheet sheet, final JXLCellFormatter cellFormatter) {
        
        System.out.printf("======== START : [%s] =========\n", sheet.getName());
        
        final int maxRow = sheet.getRows();
        for(int r=3; r < maxRow; r++) {
            
            final Cell[] row = sheet.getRow(r);
            if(row == null) {
                break;
            }
            
            final Cell noCell = row[0];
            final Cell desctiptionCell = row[1];
            if(!hasBorderBottom(noCell) || !hasBorderBottom(desctiptionCell)) {
                break;
            }
            
            final Cell testCaseCell = row[2];
            final Cell testResultCell = row[3];
            
            final String no = noCell.getContents();
            final String description = desctiptionCell.getContents();
            if(description.isEmpty()) {
                break;
            }
            
            final String testCase = cellFormatter.format(testCaseCell, Locale.JAPANESE);
            final String testResult = testResultCell.getContents();
            
            final String test = testCase.equals(testResult) ? "○" : "×";
            
            // セルのスタイル情報の取得
            final CommonCell jxlTestCase = new JXLCell(testCaseCell);
            final int formatIndex = jxlTestCase.getFormatIndex();
            final String formatPattern = jxlTestCase.getFormatPattern();
            final boolean poiDate = testCaseCell.getType().equals(CellType.DATE);
            
            System.out.printf("[%3s] [%s] [%s] : actual=\"%s\" : exprected=\"%s\" \t%d\t%s\t%b\n",
                    no, description, test, testCase, testResult, formatIndex, formatPattern, poiDate);
            
            assertThat(String.format("[%s] [%s]", no, description), testCase, is(testResult));
        }
        
        System.out.printf("======== END : [%s] =========\n\n", sheet.getName());
        
    }
    
    private boolean hasBorderBottom(final Cell cell) {
        
        if(cell == null) {
            return false;
        }
        
        final CellFormat style = cell.getCellFormat();
        if(style == null) {
            return false;
        }
        
        if(style.getBorder(Border.BOTTOM).equals(Border.NONE)) {
            return false;
        }
        
        return true;
    }
}
