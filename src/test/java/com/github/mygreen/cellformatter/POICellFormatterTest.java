package com.github.mygreen.cellformatter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.github.mygreen.cellformatter.lang.MSColor;

/**
 * POIによるテスト
 * 
 * @version 0.6
 * @since 0.1
 * @author T.TSUCHIE
 *
 */
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
    
    /**
     * 引数がnullの場合のテスト
     * @since 0.4
     */
    @Test
    public void testArg() {
        
        POICellFormatter cellFormatter = new POICellFormatter();
        
        // Cellがnullの場合
        CellFormatResult result = cellFormatter.format(null);
        assertThat(result.getCellType(), is(FormatCellType.Blank));
        assertThat(result.getText(), is(""));
    }
    
    /**
     * 戻り値のテスト
     * ・日付用。
     * @since 0.6
     */
    @Test
    public void testReturnValue_date() {
        
        File file = new File("src/test/data/cell_format_2010_custom_compatible.xls");
        POICellFormatter cellFormatter = new POICellFormatter();
        
        try {
            Sheet sheet = loadSheetByName(file, "書式（日付）");
            Cell cell = getCell(sheet, "C4");
            
            CellFormatResult result = cellFormatter.format(cell);
            
            assertThat(result.getCellType(), is(FormatCellType.Date));
            assertThat(result.getText(), is("2012/2/29 1:58 AM"));
            assertThat(result.getTextColor(), is(nullValue()));
            assertThat(result.getSectionPattern(), is("[$-409]yyyy/m/d\\ h:mm\\ AM/PM"));
            assertThat(result.getValueAsDate(), is(new Date(Timestamp.valueOf("2012-02-29 01:58:00.000").getTime())));
        
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    
    /**
     * 戻り値のテスト
     * ・日付用。
     * @since 0.6
     */
    @Test
    public void testReturnValue_numeric() {
        
        File file = new File("src/test/data/cell_format_2010_custom_compatible.xls");
        POICellFormatter cellFormatter = new POICellFormatter();
        
        try {
            Sheet sheet = loadSheetByName(file, "書式（数値）");
            Cell cell = getCell(sheet, "C4");
            
            CellFormatResult result = cellFormatter.format(cell);
            
            assertThat(result.getCellType(), is(FormatCellType.Number));
            assertThat(result.getText(), is("123.5"));
            assertThat(result.getTextColor(), is(MSColor.BLUE));
            assertThat(result.getSectionPattern(), is("[Blue]#,##0.0"));
            assertThat(result.getValueAsDoulbe(), is(123.456d));
        
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    
    /**
     * 結合セルのテスト
     * @since 0.4
     */
    @Test
    public void testMergedCell() {
        
        File file = new File("src/test/data/cell_format_2010_custom_compatible.xls");
        POICellFormatter cellFormatter = new POICellFormatter();
        try {
            Sheet sheet = loadSheetByName(file, "結合セル");
            Cell cell = null;
            CellFormatResult result = null;
            
            // 全て空の場合
            {
                cell = getCell(sheet, "B4");
                result = cellFormatter.format(cell);
                assertThat("", is(result.getText()));
            }
            
            // 左上に値（文字列）
            {
                cell = getCell(sheet, "B8");
                result = cellFormatter.format(cell);
                assertThat("ABC", is(result.getText()));
            }
            
            // 左上に値（日付）
            {
                cell = getCell(sheet, "B12");
                result = cellFormatter.format(cell);
                assertThat("2014年10月23日", is(result.getText()));
            }
            
            // 右下に値（文字列）
            {
                cell = getCell(sheet, "B16");
                result = cellFormatter.format(cell);
                assertThat("ABC", is(result.getText()));
            }
            
            // 右下に値（日付）
            {
                cell = getCell(sheet, "B20");
                result = cellFormatter.format(cell);
                assertThat("2014年10月23日", is(result.getText()));
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
        
    }
    
    private Cell getCell(final Sheet sheet, final String address) {
        
        final CellReference ref = new CellReference(address.toUpperCase());
        return sheet.getRow(ref.getRow()).getCell(ref.getCol());
        
    }
    
    /**
     * エラーのテスト
     * @since 0.4
     */
    @Test
    public void testErrorCell() {
        
        File file = new File("src/test/data/cell_format_2010_custom.xlsx");
        POICellFormatter cellFormatter = new POICellFormatter();
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
        
        File file = new File("src/test/data/cell_format_2010_custom.xlsx");
        POICellFormatter cellFormatter = new POICellFormatter();
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
    public void testFormatExcel2010() {
        
        File file = new File("src/test/data/cell_format_2010.xlsx");
        POICellFormatter cellFormatter = new POICellFormatter();
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
    
    /**
     * マルチスレッドでのテスト
     * @since 0.5
     */
    @Test
    public void testFormatExcel2010_MultiThread() {
        
        File file = new File("src/test/data/cell_format_2010.xlsx");
        final POICellFormatter cellFormatter = new POICellFormatter();
        cellFormatter.setCache(true);
        
        ExecutorService executor = Executors.newFixedThreadPool(5);
//        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            final List<Sheet> sheetList = loadSheetForFormat(file);
            final CountDownLatch countDown = new CountDownLatch(sheetList.size());
            
            for(Sheet sheet : sheetList) {
                
                final Sheet s = sheet;
                
                executor.submit(new Runnable() {
                    
                    @Override
                    public void run() {
                        try {
                            assertSheet(s, cellFormatter);
                            
                        } finally {
                            countDown.countDown();
                        }
                        
                    }
                });
                
            }
            
            countDown.await();
            
        } catch (Exception e) {
            e.printStackTrace();
            fail();
            
        } finally {
            executor.shutdown();
        }
        
    }
    
    @Test
    public void testFormatExcel2010_compatible() {
        
        File file = new File("src/test/data/cell_format_2010_compatible.xls");
        POICellFormatter cellFormatter = new POICellFormatter();
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
    public void testFormatExcel2010_custom() {
        
        File file = new File("src/test/data/cell_format_2010_custom.xlsx");
        POICellFormatter cellFormatter = new POICellFormatter();
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
    public void testFormatExcel2010_custom_test() {
        
        File file = new File("src/test/data/cell_format_2010_custom.xlsx");
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
    public void testFormatExcel2010_custom_compatible() {
        
        File file = new File("src/test/data/cell_format_2010_custom_compatible.xls");
        POICellFormatter cellFormatter = new POICellFormatter();
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
    public void testFormatLibre() {
        
        File file = new File("src/test/data/cell_format_libre.xls");
        POICellFormatter cellFormatter = new POICellFormatter();
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
    public void testFormat_date1904() {
        
        File file = new File("src/test/data/cell_format_date1904.xls");
        POICellFormatter cellFormatter = new POICellFormatter();
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
    public void testFormatLibre_date1904_test() {
        
        File file = new File("src/test/data/cell_format_date1904.xls");
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
    
    /**
     * 書式確認用のシートの取得
     * @param file
     * @return
     * @throws InvalidFormatException
     * @throws IOException
     */
    private List<Sheet> loadSheetForFormat(final File file) throws InvalidFormatException, IOException {
        
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
    
    /**
     * シート名を指定して取得する
     * @param file
     * @param name
     */
    private Sheet loadSheetByName(final File file, final String name) throws InvalidFormatException, IOException {
        
        try(InputStream in = new FileInputStream(file)) {
            Workbook workbook = WorkbookFactory.create(in);
            
            final int sheetNum = workbook.getNumberOfSheets();
            for(int i=0; i < sheetNum; i++) {
                
                final Sheet sheet = workbook.getSheetAt(i);
                final String sheetName = sheet.getSheetName();
                if(sheetName.equals(name)) {
                    return sheet;
                }
            }
        }
        
        throw new IllegalStateException("not found sheet : " + name);
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
            
            final String testCase = cellFormatter.formatAsString(testCaseCell);
            final String testResult = testResultCell.getRichStringCellValue().getString();
            
            final String test = testCase.equals(testResult) ? "○" : "×";
            
            // セルのスタイル情報の取得
            CommonCell commonTestCase = new POICell(testCaseCell);
            final short formatIndex = commonTestCase.getFormatIndex();
            final String formatPattern = commonTestCase.getFormatPattern();
//            final boolean poiDate = testCaseCell.getCellType() == Cell.CELL_TYPE_NUMERIC && DateUtil.isCellDateFormatted(testCaseCell);
            CellFormatter formatter = cellFormatter.getFormatterResolver().getFormatter(commonTestCase.getFormatPattern());
            boolean isDateFormatter = false;
            boolean isNumberFormatter = false;
            boolean isTextFormatter = false;
            if(formatter instanceof CustomFormatter) {
                isDateFormatter = ((CustomFormatter) formatter).hasDateFormatter();
                isNumberFormatter = ((CustomFormatter) formatter).hasNumberFormatter();
                isTextFormatter = ((CustomFormatter) formatter).hasTextFormatter();
            }
            
            System.out.printf("[%3s] [%s] [%s] : actual=\"%s\" : exprected=\"%s\" \t%d\t%s\tdateFormmat=%b\tnumberFormat=%b\ttextFormat=%b\n",
                    no, description, test, testCase, testResult, formatIndex, formatPattern, isDateFormatter, isNumberFormatter, isTextFormatter);
            
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
