package com.github.mygreen.cellformatter.lang;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Excelの日時表現を処理するためのユーティリティクラス。
 * <ul>
 *  <li>Excel上は、1900年は閏年扱いで、1900年2月29日が存在する。</li>
 *  <li>Javaの形式に変換したときは、1900年3月1日扱いとする。</li>
 * </ul>
 *
 * @since 0.6
 * @author T.TSUCHIE
 *
 */
public class ExcelDateUtils {
    
    /**
     * 24時間の秒数。
     */
    public static final int SECONDS_IN_DAYS = (int) TimeUnit.HOURS.toSeconds(24);
    
    /**
     * {@literal 1900-01-01 00:00:00.000}の時間（単位はミリ秒）。
     * <p>Excelは設定により、1900年始まりか1904年始まりか指定できるため、その基準値として利用する。
     */
    public static final long MILLISECONDS_19000101 = parseDate("1900-01-01 00:00:00.000").getTime();
    
    /**
     * {@literal 1904-01-01 00:00:00.000}の時間（単位はミリ秒）。
     * <p>Excelは設定により、1900年始まりか1904年始まりか指定できるため、その基準値として利用する。
     */
    public static final long MILLISECONDS_19040101 = parseDate("1904-01-01 00:00:00.000").getTime();
    
    /**
     * 1900-03-01の時間（単位はミリ秒）。
     * <p>Excelは1900年始まりの場合、閏日でない1900年2月29日（=3月1日）が存在するため、その基準値として利用する。
     */
    public static final long MILLISECONDS_19000301 = ExcelDateUtils.parseDate("1900-03-01 00:00:00.000").getTime();
    
    /**
     * Javaの基準日(=0)である1970年1月1日に対するExcelの基準日1900年1月0日の日のオフセット（単位は日）。
     * <p>ただし、Excelは1900年は1月0日から始まり、実質1899年12月31日となる。
     */
    public static final int OFFSET_DAYS_1900; 
    
    /**
     * Javaの基準日(=0)である1970年1月1日に対するExcelの基準日1904年1月1日の日のオフセット（単位は日）。
     */
    public static final int OFFSET_DAYS_1904;
    
    static {
        long offsetDay1900 = parseDate("1900-01-01 00:00:00.000").getTime() / ((long)SECONDS_IN_DAYS * 1000);
        OFFSET_DAYS_1900 = (int) offsetDay1900 -1;
        
        long offsetDay1904 = parseDate("1904-01-01 00:00:00.000").getTime() / ((long)SECONDS_IN_DAYS * 1000);
        OFFSET_DAYS_1904 = (int) offsetDay1904;
    }
    
    /**
     * 1900年開始の場合、3月1日の経過日数。
     * <p>1900年は閏年ではないが、Excelの場合は閏年扱いのため、
     *    1900年1月～2月の期間（60日23時59分59秒=60.9999）を表現するための定数として利用する。
     */
    public static final int NON_LEAP_DAY = 61;
    
    /**
     * Excel表現上の数値をJavaの{@link Date}型(UTC形式)に変換する。
     * <p>1900年始まりの場合は以下の注意が必要。</p>
     * <ul>
     *   <li>値{@literal 0.0}は、Excel上は1900年1月0日であるが、Date型へ変換した場合は1899年12月31日となります。</li>
     *   <li>値{@literal 60.0}は、Excel上は1900年2月29日だが、グレゴリオ歴上は閏日ではあにため、1900年3月1日となります。</li>
     * </ul>
     * 
     * @param value 変換対象のExcel表現上の数値。
     * @param startDate1904 基準日が1904年始まりかどうか。
     * @return Java表現上に変換した日時。
     *         ただし、この値はタイムゾーンは考慮されていない（=GMT-00:00）ため、
     *         変換後は独自に処理を行う必要があります。
     */
    public static Date convertJavaDate(final double numValue, final boolean startDate1904) {
        
        double utcDay;
        if(startDate1904) {
            // 1904年始まりの場合
            
            // UTC(1970年基準に戻す)
            utcDay = numValue + OFFSET_DAYS_1904;
            
        } else {
            // 1900年始まりの場合
            
            // UTC(1970年基準に戻す)
            utcDay = numValue + OFFSET_DAYS_1900;
            
            /*
             * 1900年3月1日（Excel上は1900年2月29日）以降の場合の補正。
             * ・Excel上は1900年は、閏年扱いで1900年2月29日の閏年が存在する。
             * ・しかし、グレゴリオ歴上は1900年は閏年ではないため、UTCの表現上から見ると1日多い。
             * ・1900年2月29日は、1900年1月0日から数えて61日目なので、61日以降は、1日前に戻す。
             */
            if(numValue >= NON_LEAP_DAY) {
                utcDay -= 1;
            }
            
        }
        
        /*
         * Javaのミリ秒に直す。
         * ・Excelの日付の形式の場合小数部が時間を示すため、24時間分のミリ秒を考慮する。
         */
        long utcTime = Math.round(utcDay * SECONDS_IN_DAYS) * 1000;
        return new Date(utcTime);
        
    }
    
    /**
     * Javaの{@link Date}型をExcelの内部表現の数値に変換する。
     * <p>小数の桁数に関する注意事項。</p>
     * <ul>
     *    <li>このメソッドは少数は第16位まで保証し、小数第17位は四捨五入して計算します。</li>
     *    <li>Excelの1秒は、UTC上では1/(60x60x24x1000)=0.0000000115741=1.15741e-008であるので、小数13位までの精度が必要。</li>
     *    <li>Excelは秒までだが、Javaはミリ秒まで存在するので、さらに3桁多い、16桁までの精度が必要になる。</li>
     * </ul>
     * 
     * <p>1900年始まりの場合は以下の注意が必要。</p>
     * <ul>
     *   <li>UTC上は1900年2月29日は存在しないため、{@literal 60.0}への変換はできません。</li>
     * </ul>
     * 
     * @param value 変換対象のJava表現上の日時。タイムゾーンを排除した（GMT-00:00）日時。
     * @param startDate1904 基準日が1904年始まりかどうか。
     * @return Excel表現上に変換した数値。
     * @throws IllegalArgumentException {@literal value == nulll.}
     */
    public static double convertExcelNumber(final Date value, final boolean startDate1904) {
        
        ArgUtils.notNull(value, "value");
        
        /*
         * Excelの時間の表現に直す。
         * ・Excelの日付の形式の場合小数部が時間を示すため、24時間分のミリ秒を考慮する。
         */
        long utcDay = value.getTime();
        BigDecimal numValue = new BigDecimal(utcDay);
        numValue = numValue.divide(new BigDecimal(SECONDS_IN_DAYS * 1000), 17, BigDecimal.ROUND_HALF_UP);
        
        if(startDate1904) {
            // 1904年始まりの場合
            numValue = numValue.subtract(new BigDecimal(OFFSET_DAYS_1904));
            
        } else {
            // 1900年始まりの場合
            numValue = numValue.subtract(new BigDecimal(OFFSET_DAYS_1900));
            
            if(numValue.compareTo(new BigDecimal(NON_LEAP_DAY - 1)) >= 0) {
                numValue = numValue.add(new BigDecimal(1));
            }
            
        }
        
        return numValue.doubleValue();
        
    }
    
    /**
     * 日時形式を{@literal yyyy-MM-dd HH:mm:ss.SSS}の書式でフォーマットする。
     * <p>ただし、タイムゾーンは、標準時間の{@literal GMT-00:00}で処理する。
     * @param date フォーマット対象の日時。
     * @return フォーマットした文字列。
     * @throws IllegalArgumentException date is null.
     */
    public static String formatDate(final Date date) {
        
        ArgUtils.notNull(date, "date");
        
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        format.setTimeZone(TimeZone.getTimeZone("GMT-00:00"));
        return format.format(date);
        
    }
    
    /**
     * 文字列を日時形式を{@literal yyyy-MM-dd HH:mm:ss.SSS}のパースする。
     * <p>ただし、タイムゾーンは、標準時間の{@literal GMT-00:00}で処理する。
     * @param str パース対象の文字列
     * @return パースした日付。
     * @throws IllegalArgumentException str is empty.
     * @throws IllegalStateException fail parsing.
     */
    public static Date parseDate(final String str) {
        ArgUtils.notEmpty(str, str);
        
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            format.setTimeZone(TimeZone.getTimeZone("GMT-00:00"));
            return format.parse(str);
        } catch (ParseException e) {
            throw new IllegalStateException(String.format("fail parse to Data from '%s',", str), e);
        }
        
    }
    
    /**
     * Excelの日付となる（=数値では0の値）の時の、時間の取得。（単位はミリ秒）。
     * <p>1900年始まりの場合、Excelでは1月0日から始まるため、{@literal 1899-12-31 00:00:00.000}の値を返す。
     * <p>1904年始まりは、{@literal 1904-01-01 0:00:00}の値を返す。
     * <p>引数により、1904年始まりの場合の値か選択できる。
     * @param isStartDate1904 1904年始まりかどうか。
     * @return Excelの基準日に対するUTC表現上のミリ秒。
     */
    public static long getExcelZeroDateTime(boolean isStartDate1904) {
        if(isStartDate1904) {
            return MILLISECONDS_19040101;
        } else {
            return MILLISECONDS_19000101 - TimeUnit.DAYS.toMillis(1);
        }
    }
    
}
