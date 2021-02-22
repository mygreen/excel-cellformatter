package com.github.mygreen.cellformatter.lang;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import org.junit.Test;

/**
 * 時間表示用のセルのテスト
 *
 *
 * @author T.TSUCHIE
 *
 */
public class TimeCellTest {

    @Test
    public void testTime0800() throws Exception{

        final Sheet sheet = getTestSheet();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Cell cell = sheet.getRow(2).getCell(2);
        String address = formatCellAddress(cell);

        {
            Date date = cell.getDateCellValue();
            String formattedDate = dateFormatter.format(date);

            assertThat(formattedDate, is("1899-12-31 08:00:00"));
            System.out.printf("[%s][読み込み時]date=%s\n", address, formattedDate);
        }

        {
            // 日時を数値に変換
            Date date = cell.getDateCellValue();
            Date date2 = stripTimeZone(date);

            String formattedDate = dateFormatter.format(date);
            String formattedDate2 = dateFormatter.format(date2);

            assertThat(formattedDate2, is("1899-12-31 17:00:00"));

            System.out.printf("[%s]date=%s, GMT=%s\n", address, formattedDate, formattedDate2);

            double num = ExcelDateUtils.convertExcelNumber(date2, false);
            cell.setCellValue(num);
            Date date3 = cell.getDateCellValue();
            String formattedDate3 = dateFormatter.format(date3);
            assertThat(formattedDate3, is("1899-12-31 08:00:00"));

            System.out.printf("[%s][変換時]num=%f,date=%s\n", address, num, formattedDate3);
        }


    }

    private Sheet getTestSheet() throws Exception {

        try (InputStream in = Files.newInputStream(Paths.get("src/test/data", "cell_time.xlsx"))) {
            Workbook book = WorkbookFactory.create(in);

            Sheet sheet = book.getSheet("時間テスト");
            return sheet;

        }

    }

    /**
     * セルのアドレス'A1'を取得する。
     * @param cell セル情報
     * @return IllegalArgumentException cell == null.
     */
    public static String formatCellAddress(final Cell cell) {
        ArgUtils.notNull(cell, "cell");
        return CellReference.convertNumToColString(cell.getColumnIndex()) + String.valueOf(cell.getRowIndex()+1);
    }

    private Date stripTimeZone(Date date) {

        return new Date(date.getTime() + TimeZone.getDefault().getRawOffset());

    }
}
