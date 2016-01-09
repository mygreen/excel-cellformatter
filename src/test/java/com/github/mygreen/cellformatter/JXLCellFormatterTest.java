package com.github.mygreen.cellformatter;

import static org.hamcrest.CoreMatchers.*;
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
import org.junit.Test;

import com.github.mygreen.cellformatter.lang.JXLUtils;

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
    
    /**
     * 結合セルのテスト
     * @since 0.4
     */
    @Test
    public void testMergedCell() {
        
        File file = new File("src/test/data/cell_format_2010_custom_compatible.xls");
        JXLCellFormatter cellFormatter = new JXLCellFormatter();
        try {
            final Sheet sheet = loadSheetByName(file, "結合セル");
            final boolean isDateStart1904 = JXLUtils.isDateStart1904(sheet);
            
            Cell cell = null;
            CellFormatResult result = null;
            
            // 全て空の場合
            {
                cell = sheet.getCell("B4");
                result = cellFormatter.format(cell, isDateStart1904);
                assertThat("", is(result.getText()));
            }
            
            // 左上に値（文字列）
            {
                cell = sheet.getCell("B8");
                result = cellFormatter.format(cell, isDateStart1904);
                assertThat("ABC", is(result.getText()));
            }
            
            // 左上に値（日付）
            {
                cell = sheet.getCell("B12");
                result = cellFormatter.format(cell, isDateStart1904);
                assertThat("2014年10月23日", is(result.getText()));
            }
            
            // 右下に値（文字列）
            {
                cell = sheet.getCell("B16");
                result = cellFormatter.format(cell, isDateStart1904);
                assertThat("ABC", is(result.getText()));
            }
            
            // 右下に値（日付）
            {
                cell = sheet.getCell("B20");
                result = cellFormatter.format(cell, isDateStart1904);
                assertThat("2014年10月23日", is(result.getText()));
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
        
    }
    
    /**
     * エラーのテスト
     * @since 0.4
     */
    @Test
    public void testErrorCell() {
        
        File file = new File("src/test/data/cell_format_2010_custom_compatible.xls");
        JXLCellFormatter cellFormatter = new JXLCellFormatter();
        try {
            Sheet sheet = loadSheetByName(file, "エラー");
            assertSheet(sheet, cellFormatter);
            
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
        
    }
    
    /**
     * エラーのテスト
     * ・エラー時に空文字として取得する。
     * @since 0.4
     */
    @Test
    public void testErrorCell_asEmpty() {
        
        File file = new File("src/test/data/cell_format_2010_custom_compatible.xls");
        JXLCellFormatter cellFormatter = new JXLCellFormatter();
        cellFormatter.setErrorCellAsEmpty(true);
        try {
            Sheet sheet = loadSheetByName(file, "エラー (空)");
            assertSheet(sheet, cellFormatter);
            
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
        
    }
    
    @Test
    public void testFormatExcel2010_compatible() {
        
        File file = new File("src/test/data/cell_format_2010_compatible.xls");
        JXLCellFormatter cellFormatter = new JXLCellFormatter();
        try {
            List<Sheet> sheetList = loadSheetForFormat(file);
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
    public void testFormatExcel2010_custom_compatible() {
        
        File file = new File("src/test/data/cell_format_2010_custom_compatible.xls");
        JXLCellFormatter cellFormatter = new JXLCellFormatter();
        try {
            List<Sheet> sheetList = loadSheetForFormat(file);
            for(Sheet sheet : sheetList) {
                assertSheet(sheet, cellFormatter);
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
        
    }
    
    @Test
    public void testFormatExcel2010_custom_compatible_test() {
        
        File file = new File("src/test/data/cell_format_2010_custom_compatible.xls");
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
            List<Sheet> sheetList = loadSheetForFormat(file);
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
    public void testFormatLibre() {
        
        File file = new File("src/test/data/cell_format_libre.xls");
        JXLCellFormatter cellFormatter = new JXLCellFormatter();
        try {
            List<Sheet> sheetList = loadSheetForFormat(file);
            for(Sheet sheet : sheetList) {
                assertSheet(sheet, cellFormatter);
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
        
    }
    
    @Test
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
    
    @Test
    public void testFormat_date1904() {
        
        File file = new File("src/test/data/cell_format_date1904.xls");
        JXLCellFormatter cellFormatter = new JXLCellFormatter();
        try {
            List<Sheet> sheetList = loadSheetForFormat(file);
            for(Sheet sheet : sheetList) {
                assertSheet(sheet, cellFormatter);
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
        
    }
    
    @Test
    public void testFormat_date1904_test() {
        
        File file = new File("src/test/data/cell_format_date1904.xls");
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
    
    private List<Sheet> loadSheetForFormat(final File file) throws IOException, BiffException {
        
        final List<Sheet> list = new ArrayList<>();
        try(InputStream in = new FileInputStream(file)) {
            final WorkbookSettings settings = new WorkbookSettings();
            settings.setSuppressWarnings(true);
            settings.setGCDisabled(true);
            
            // 文字コードを「ISO8859_1」にしないと文字化けする
            settings.setEncoding("ISO8859_1");
            
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
            
//            System.out.println(JXLUtils.isDateStart1904(workbook));
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
    
    /**
     * シート名を指定して取得する
     * @param file
     * @param name
     */
    private Sheet loadSheetByName(final File file, final String name) throws IOException, BiffException {
        
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
                if(sheetName.equals(name)) {
                    return sheet;
                }
                
            }
        }
        
        throw new IllegalStateException("not found sheet : " + name);
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
            
            final boolean startDate1904 = JXLUtils.isDateStart1904(sheet);
            final String testCase = cellFormatter.formatAsString(testCaseCell, Locale.JAPANESE, startDate1904);
            final String testResult = testResultCell.getContents();
            
            final String test = testCase.equals(testResult) ? "○" : "×";
            
            // セルのスタイル情報の取得
            final CommonCell jxlTestCase = new JXLCell(testCaseCell, startDate1904);
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
