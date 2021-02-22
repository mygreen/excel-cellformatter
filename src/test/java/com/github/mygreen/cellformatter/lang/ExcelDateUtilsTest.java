package com.github.mygreen.cellformatter.lang;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;

/**
 * {@link ExcelDateUtils}のテスタ
 *
 * @since 0.6
 * @author T.TSUCHIE
 *
 */
public class ExcelDateUtilsTest {
    
    /**
     * {@link ExcelDateUtils#convertJavaDate(double, boolean)}のテスタ
     * ・1900年始まり
     * ・日付部分の処理のテスト
     */
    @Test
    public void testConvertToJavaDate_date_1900() {
        
        assertThat(formatDate(ExcelDateUtils.convertJavaDate(0.0, false)), is("1899-12-31 00:00:00.000"));
        assertThat(formatDate(ExcelDateUtils.convertJavaDate(1.0, false)), is("1900-01-01 00:00:00.000"));
        assertThat(formatDate(ExcelDateUtils.convertJavaDate(2.0, false)), is("1900-01-02 00:00:00.000"));
        
        assertThat(formatDate(ExcelDateUtils.convertJavaDate(31.0, false)), is("1900-01-31 00:00:00.000"));
        assertThat(formatDate(ExcelDateUtils.convertJavaDate(32.0, false)), is("1900-02-01 00:00:00.000"));
        assertThat(formatDate(ExcelDateUtils.convertJavaDate(60.0, false)), is("1900-03-01 00:00:00.000"));
        assertThat(formatDate(ExcelDateUtils.convertJavaDate(61.0, false)), is("1900-03-01 00:00:00.000"));
        
        assertThat(formatDate(ExcelDateUtils.convertJavaDate(1462.0, false)), is("1904-01-01 00:00:00.000"));
        assertThat(formatDate(ExcelDateUtils.convertJavaDate(1521.0, false)), is("1904-02-29 00:00:00.000"));
        
        assertThat(formatDate(ExcelDateUtils.convertJavaDate(25569.0, false)), is("1970-01-01 00:00:00.000"));
        
        assertThat(formatDate(ExcelDateUtils.convertJavaDate(36526.0, false)), is("2000-01-01 00:00:00.000"));
        assertThat(formatDate(ExcelDateUtils.convertJavaDate(36585.0, false)), is("2000-02-29 00:00:00.000"));
        
        assertThat(formatDate(ExcelDateUtils.convertJavaDate(-1.0, false)), is("1899-12-30 00:00:00.000"));
        assertThat(formatDate(ExcelDateUtils.convertJavaDate(-2.0, false)), is("1899-12-29 00:00:00.000"));
        
        
    }
    
    /**
     * {@link ExcelDateUtils#convertJavaDate(double, boolean)}のテスタ
     * ・1904年始まり
     * ・日付部分の処理のテスト
     */
    @Test
    public void testConvertToJavaDate_date_1904() {
        
        assertThat(formatDate(ExcelDateUtils.convertJavaDate(0.0, true)), is("1904-01-01 00:00:00.000"));
        assertThat(formatDate(ExcelDateUtils.convertJavaDate(1.0, true)), is("1904-01-02 00:00:00.000"));
        assertThat(formatDate(ExcelDateUtils.convertJavaDate(2.0, true)), is("1904-01-03 00:00:00.000"));
        
        assertThat(formatDate(ExcelDateUtils.convertJavaDate(59.0, true)), is("1904-02-29 00:00:00.000"));
        
        assertThat(formatDate(ExcelDateUtils.convertJavaDate(24107.0, true)), is("1970-01-01 00:00:00.000"));
        
        assertThat(formatDate(ExcelDateUtils.convertJavaDate(35064.0, true)), is("2000-01-01 00:00:00.000"));
        assertThat(formatDate(ExcelDateUtils.convertJavaDate(35123.0, true)), is("2000-02-29 00:00:00.000"));
        
        assertThat(formatDate(ExcelDateUtils.convertJavaDate(-1.0, true)), is("1903-12-31 00:00:00.000"));
        assertThat(formatDate(ExcelDateUtils.convertJavaDate(-2.0, true)), is("1903-12-30 00:00:00.000"));
        
        
    }
    
    /**
     * {@link ExcelDateUtils#convertJavaDate(double, boolean)}のテスタ
     * ・1900年始まり
     * ・時間部分の処理のテスト
     */
    @Test
    public void testConvertToJavaDate_time_1900() {
        
        // 時間部のチェック
        assertThat(formatDate(ExcelDateUtils.convertJavaDate((0.0 + toXlsSeconds(1)), false)), is("1899-12-31 00:00:01.000"));
        assertThat(formatDate(ExcelDateUtils.convertJavaDate((0.0 + toXlsSeconds(2)), false)), is("1899-12-31 00:00:02.000"));
        
        assertThat(formatDate(ExcelDateUtils.convertJavaDate((1.0 + toXlsSeconds(1)), false)), is("1900-01-01 00:00:01.000"));
        assertThat(formatDate(ExcelDateUtils.convertJavaDate((1.0 + toXlsSeconds(2)), false)), is("1900-01-01 00:00:02.000"));
        assertThat(formatDate(ExcelDateUtils.convertJavaDate((1.0 + toXlsSeconds(59)), false)), is("1900-01-01 00:00:59.000"));
        assertThat(formatDate(ExcelDateUtils.convertJavaDate((1.0 + toXlsSeconds(60)), false)), is("1900-01-01 00:01:00.000"));
        assertThat(formatDate(ExcelDateUtils.convertJavaDate((1.0 + toXlsSeconds(60*59)), false)), is("1900-01-01 00:59:00.000"));
        assertThat(formatDate(ExcelDateUtils.convertJavaDate((1.0 + toXlsSeconds(60*60)), false)), is("1900-01-01 01:00:00.000"));
        assertThat(formatDate(ExcelDateUtils.convertJavaDate((1.0 + toXlsSeconds(60*60*24 - 1)), false)), is("1900-01-01 23:59:59.000"));

    }
    
    /**
     * {@link ExcelDateUtils#convertJavaDate(double, boolean)}のテスタ
     * ・1904年始まり
     * ・時間部分の処理のテスト
     */
    @Test
    public void testConvertToJavaDate_time_1904() {
        
        // 時間部のチェック
        assertThat(formatDate(ExcelDateUtils.convertJavaDate((0.0 + toXlsSeconds(1)), true)), is("1904-01-01 00:00:01.000"));
        assertThat(formatDate(ExcelDateUtils.convertJavaDate((0.0 + toXlsSeconds(2)), true)), is("1904-01-01 00:00:02.000"));
        
        assertThat(formatDate(ExcelDateUtils.convertJavaDate((1.0 + toXlsSeconds(1)), true)), is("1904-01-02 00:00:01.000"));
        assertThat(formatDate(ExcelDateUtils.convertJavaDate((1.0 + toXlsSeconds(2)), true)), is("1904-01-02 00:00:02.000"));
        assertThat(formatDate(ExcelDateUtils.convertJavaDate((1.0 + toXlsSeconds(59)), true)), is("1904-01-02 00:00:59.000"));
        assertThat(formatDate(ExcelDateUtils.convertJavaDate((1.0 + toXlsSeconds(60)), true)), is("1904-01-02 00:01:00.000"));
        assertThat(formatDate(ExcelDateUtils.convertJavaDate((1.0 + toXlsSeconds(60*59)), true)), is("1904-01-02 00:59:00.000"));
        assertThat(formatDate(ExcelDateUtils.convertJavaDate((1.0 + toXlsSeconds(60*60)), true)), is("1904-01-02 01:00:00.000"));
        assertThat(formatDate(ExcelDateUtils.convertJavaDate((1.0 + toXlsSeconds(60*60*24 - 1)), true)), is("1904-01-02 23:59:59.000"));

    }
    
    /**
     * {@link ExcelDateUtils#convertExcelNumber(java.util.Date, boolean)}のテスタ
     * ・1900年始まり
     * ・日付部分の処理のテスト
     */
    @Test
    public void testConvertToExcelNumber_date_1900() {
        
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1899-12-31 00:00:00.000"), false), is(0.0d));
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1900-01-01 00:00:00.000"), false), is(1.0d));
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1900-01-02 00:00:00.000"), false), is(2.0d));
        
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1900-01-31 00:00:00.000"), false), is(31.0d));
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1900-02-01 00:00:00.000"), false), is(32.0d));
        
        // UTC上は、1900年2月29日と1900年3月31日は同じ。
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1900-02-29 00:00:00.000"), false), is(61.0d));
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1900-03-01 00:00:00.000"), false), is(61.0d));
        
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1904-01-01 00:00:00.000"), false), is(1462.0d));
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1904-02-29 00:00:00.000"), false), is(1521.0d));
        
        
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1970-01-01 00:00:00.000"), false), is(25569.0d));
        
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1899-12-30 00:00:00.000"), false), is(-1.0d));
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1899-12-29 00:00:00.000"), false), is(-2.0d));
        
        
    }
    
    /**
     * {@link ExcelDateUtils#convertExcelNumber(java.util.Date, boolean)}のテスタ
     * ・1904年始まり
     * ・日付部分の処理のテスト
     */
    @Test
    public void testConvertToExcelNumber_date_1904() {
        
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1904-01-01 00:00:00.000"), true), is(0.0d));
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1904-01-02 00:00:00.000"), true), is(1.0d));
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1904-01-03 00:00:00.000"), true), is(2.0d));
        
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1904-01-31 00:00:00.000"), true), is(30.0d));
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1904-02-01 00:00:00.000"), true), is(31.0d));
        
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1904-02-29 00:00:00.000"), true), is(59.0d));
        
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1970-01-01 00:00:00.000"), true), is(24107.0d));
        
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1903-12-31 00:00:00.000"), true), is(-1.0d));
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1903-12-30 00:00:00.000"), true), is(-2.0d));
        
        
    }
    
    /**
     * {@link ExcelDateUtils#convertExcelNumber(java.util.Date, boolean)}のテスタ
     * ・1900年始まり
     * ・時間部分の処理のテスト
     */
    @Test
    public void testConvertToExcelNumber_time_1900() {
        
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1899-12-31 00:00:01.000"), false), is(toXlsSeconds(1)));
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1899-12-31 00:00:02.000"), false), is(toXlsSeconds(2)));
        
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1900-01-01 00:00:01.000"), false), is(1.0 + toXlsSeconds(1)));
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1900-01-01 00:00:02.000"), false), is(1.0 + toXlsSeconds(2)));
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1900-01-01 00:00:59.000"), false), is(1.0 + toXlsSeconds(59)));
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1900-01-01 00:01:00.000"), false), is(1.0 + toXlsSeconds(60)));
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1900-01-01 00:59:00.000"), false), is(1.0 + toXlsSeconds(60*59)));
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1900-01-01 01:00:00.000"), false), is(1.0 + toXlsSeconds(60*60)));
        
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1900-01-01 23:59:59.000"), false), is(1.0 + toXlsSeconds(60*60*24 - 1)));
        
        // 小数の桁数確認
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1900-01-01 23:59:59.999"), false), is(1.0 + toXlsSeconds(60*60*24 - 1)+ toXlsMillSeconds(999)));
    }
    
    /**
     * {@link ExcelDateUtils#convertExcelNumber(java.util.Date, boolean)}のテスタ
     * ・1904年始まり
     * ・時間部分の処理のテスト
     */
    @Test
    public void testConvertToExcelNumber_time_1904() {
        
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1904-01-01 00:00:01.000"), true), is(toXlsSeconds(1)));
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1904-01-01 00:00:02.000"), true), is(toXlsSeconds(2)));
        
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1904-01-02 00:00:01.000"), true), is(1.0 + toXlsSeconds(1)));
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1904-01-02 00:00:02.000"), true), is(1.0 + toXlsSeconds(2)));
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1904-01-02 00:00:59.000"), true), is(1.0 + toXlsSeconds(59)));
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1904-01-02 00:01:00.000"), true), is(1.0 + toXlsSeconds(60)));
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1904-01-02 00:59:00.000"), true), is(1.0 + toXlsSeconds(60*59)));
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1904-01-02 01:00:00.000"), true), is(1.0 + toXlsSeconds(60*60)));
        
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1904-01-02 23:59:59.000"), true), is(1.0 + toXlsSeconds(60*60*24 - 1)));
        
        // 小数の桁数確認
        assertThat(ExcelDateUtils.convertExcelNumber(toDate("1904-01-02 23:59:59.999"), true), is(1.0 + toXlsSeconds(60*60*24 - 1)+ toXlsMillSeconds(999)));
        
    }
    
    /**
     * Excel上の数値の秒に変換する
     * @param value
     * @return
     */
    private double toXlsSeconds(int value) {
        
        BigDecimal num = new BigDecimal(value);
        num = num.divide(new BigDecimal(60*60*24), 17, BigDecimal.ROUND_HALF_UP);
        
        return num.doubleValue();
        
    }
    
    /**
     * Excel上の数値のミリ秒に変換する
     * @param value
     * @return
     */
    private double toXlsMillSeconds(int value) {
        
        BigDecimal num = new BigDecimal(value);
        num = num.divide(new BigDecimal(60*60*24*1000), 17, BigDecimal.ROUND_HALF_UP);
        
        return num.doubleValue();
        
    }
    
    private Date toDate(final String strDateTime) {
        return ExcelDateUtils.parseDate(strDateTime);
    }
    
    private static String formatDate(final Date date) {
        return ExcelDateUtils.formatDate(date);
    }
}
