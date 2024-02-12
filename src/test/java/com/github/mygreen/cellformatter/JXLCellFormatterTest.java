package com.github.mygreen.cellformatter;

import static com.github.mygreen.cellformatter.lang.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.github.mygreen.cellformatter.lang.JXLUtils;
import com.github.mygreen.cellformatter.lang.MSColor;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.read.biff.BiffException;

/**
 * JExcelAPI用のフォーマッタのテスタ
 * @version 0.6
 * @since 0.1
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
     * 戻り値のテスト
     * ・日付用。
     * @since 0.6
     */
    @Test
    public void testReturnValue_date() {

        File file = new File("src/test/data/cell_format_2010_custom_compatible.xls");
        JXLCellFormatter cellFormatter = new JXLCellFormatter();

        try {
            Sheet sheet = loadSheetByName(file, "書式（日付）");
            final boolean isDateStart1904 = JXLUtils.isDateStart1904(sheet);
            Cell cell = sheet.getCell("C4");

            CellFormatResult result = cellFormatter.format(cell, isDateStart1904);

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
        JXLCellFormatter cellFormatter = new JXLCellFormatter();

        try {
            Sheet sheet = loadSheetByName(file, "書式（数値）");
            final boolean isDateStart1904 = JXLUtils.isDateStart1904(sheet);
            Cell cell = sheet.getCell("C4");

            CellFormatResult result = cellFormatter.format(cell, isDateStart1904);

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
            assertSheet(sheet, cellFormatter, null);

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
            assertSheet(sheet, cellFormatter, null);

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
                assertSheet(sheet, cellFormatter, null);
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
                assertSheet(sheet, cellFormatter, null);
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
    public void testFormatExcel2010_compatible_MultiThread() {

        File file = new File("src/test/data/cell_format_2010_compatible.xls");
        final JXLCellFormatter cellFormatter = new JXLCellFormatter();
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
                            assertSheet(s, cellFormatter, null);

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
    public void testFormatExcel2010_custom_compatible() {

        File file = new File("src/test/data/cell_format_2010_custom_compatible.xls");
        JXLCellFormatter cellFormatter = new JXLCellFormatter();
        try {
            List<Sheet> sheetList = loadSheetForFormat(file);
            for(Sheet sheet : sheetList) {
                assertSheet(sheet, cellFormatter, null);
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
                assertSheet(sheet, cellFormatter, null);
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
                assertSheet(sheet, cellFormatter, null);
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
                assertSheet(sheet, cellFormatter, null);
            }

        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }

    }

    @Test
    public void testFormatExcel2000_builtintformat_ja() {

        File file = new File("src/test/data/cell_format_2000_builtinformat_ja.xls");
        JXLCellFormatter cellFormatter = new JXLCellFormatter();
        try {
            List<Sheet> sheetList = loadSheetForFormat(file);
            for(Sheet sheet : sheetList) {
                assertSheet(sheet, cellFormatter, Locale.JAPANESE);
            }

        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }

    }

    @Test
    public void testFormatExcel2000_builtintformat_en() {

        File file = new File("src/test/data/cell_format_2000_builtinformat_en.xls");
        JXLCellFormatter cellFormatter = new JXLCellFormatter();
        try {
            List<Sheet> sheetList = loadSheetForFormat(file);
            for(Sheet sheet : sheetList) {
                assertSheet(sheet, cellFormatter, Locale.ENGLISH);
            }

        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }

    }

    @Ignore
    @Test
    public void testFormatExcel2000_builtintformat_en_test() {

        File file = new File("src/test/data/cell_format_2000_builtinformat_en.xls");
        JXLCellFormatter cellFormatter = new JXLCellFormatter();
        try {
            List<Sheet> sheetList = loadSheetForTest(file);
            for(Sheet sheet : sheetList) {
                assertSheet(sheet, cellFormatter, Locale.ENGLISH);
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
                assertSheet(sheet, cellFormatter, null);
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
                assertSheet(sheet, cellFormatter, null);
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
                assertSheet(sheet, cellFormatter, null);
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
                assertSheet(sheet, cellFormatter, null);
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

    private void assertSheet(final Sheet sheet, final JXLCellFormatter cellFormatter, final Locale locale) {

        System.out.printf("======== START : [%s] =========\n", sheet.getName());

        final int maxRow;
        synchronized (cellFormatter) {
            maxRow = sheet.getRows();
        }

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
            
            final Cell spConditionCell = row.length > 6 ? row[6] : null;

            final String no = noCell.getContents();
            final String description = desctiptionCell.getContents();
            if(description.isEmpty()) {
                break;
            }
            
            // 特殊条件 - java8のときなど
            final String spCondition;
            if(spConditionCell == null || spConditionCell.getType().equals(CellType.EMPTY)) {
                spCondition = "";
            } else {
                spCondition = spConditionCell.getContents();
            }

            final boolean startDate1904 = JXLUtils.isDateStart1904(sheet);
            final String testCase = cellFormatter.formatAsString(testCaseCell, locale, startDate1904);
            String testResult = testResultCell.getContents();
            if(spCondition.startsWith("Java8=") && IS_JAVA_1_8) {
                // Java8の環境で、値が変わる場合
                testResult = spCondition.substring("Java8=".length());
            }
            
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

        if(style.getBorder(Border.BOTTOM).equals(BorderLineStyle.NONE)) {
            return false;
        }

        return true;
    }
}
