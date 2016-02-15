package com.github.mygreen.cellformatter.lang;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Excelの日時表現を処理するためのユーティリティクラス。
 * <ul>
 *  <li>Excel上は、1900年は閏年扱いで、1900年2月29日が存在する。
 *      Javaの形式に変換したときは、1900年3月1日扱いとする。
 *
 * @since 0.6
 * @author T.TSUCHIE
 *
 */
public class ExcelDateUtils2 {
    
    /**
     * 24時間の秒数。
     */
    public static final int SECONDS_IN_DAYS = 24 * 60 * 60;
    
    /**
     * 1900/1/1のミリ秒。
     */
    private static final long MILLISECINDS_19000101 = Timestamp.valueOf("1900-01-01 00:00:00.000").getTime();
    
    /**
     * Javaの基準日(=0)である1970年日に対するExcelの基準日1900年の日のオフセット（単位は日）。
     */
    public static final int OFFSET_DAYS_1900; 
    
    /**
     * Javaの基準日(=0)である1970年日に対するExcelの基準日1904年の日のオフセット（単位は日）。
     */
    public static final int OFFSET_DAYS_1904;
    
    static {
        long offsetDay1900 = Timestamp.valueOf("1900-01-01 00:00:00.000").getTime() / ((long)SECONDS_IN_DAYS * 1000);
        OFFSET_DAYS_1900 = (int) offsetDay1900;
        
        long offsetDay1904 = Timestamp.valueOf("1904-01-01 00:00:00.000").getTime() / ((long)SECONDS_IN_DAYS * 1000);
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
     * 
     * @param value 変換対象のExcel表現上の数値。
     * @param date1904 基準日が1904年始まりかどうか。
     * @return Java表現上に変換した日時。
     */
    public static Date convertToJavaDate(final double value, final boolean date1904) {
        
        double numValue = value;
        
        /*
         * 1900年1月～2月の場合の補正。
         * ・Execl上は開始日は1900年1月0日であるが、1900年2月29日の閏年が存在する。
         * ・しかし、グレゴリオ歴上は1900年は閏年ではないため、Javaの表現上1日のずれが存在してしまう。
         * ・ただし、Excelの経過時間を表現する形式の場合である、時間部分の時のみである1より小さい場合は除外する。
         */
        boolean time = (Math.abs(numValue) < 1);
        if(!date1904 && !time && numValue < NON_LEAP_DAY) {
            numValue += 1;
        }
        
        /*
         * Javaの基準日の1970年基準日に戻す。
         * ・ただし、1900年開始の場合、Excelは、1900年は1月0日から始まるため1日ずらす。
         *   さらに、1900年はExcelは閏年としているが、UTCだと存在しないのでさらに1日ずらす。
         */
        int offsetDays = date1904 ? OFFSET_DAYS_1904 : OFFSET_DAYS_1900 -2;
        numValue += offsetDays;
        
        /*
         * Javaのミリ秒に直す。
         * ・Excelの日付の形式の場合小数部が時間を示すため、24時間分のミリ秒を考慮する。
         */
        long dateTime = Math.round(numValue * SECONDS_IN_DAYS )* 1000;
        
        Date date = new Date(dateTime);
        
        return adjustDate(date);
    }
    
    /**
     * タイムゾーンを考慮して、時差分を調整する。
     * さらに、Excelの場合、1900年1月0日から始まるため、1900年以前は1日進めて調整する。
     * @param date
     * @return
     */
    public static Date adjustDate(final Date date) {
        
        long adjustValue = date.getTime();
        
        // タイムゾーンの時差の加算がされているため補正する。
        adjustValue -= TimeZone.getDefault().getRawOffset();
        
        // 1900年以前の場合は、1899-12-30となるため、１日進めて補正をかける
        if(adjustValue < MILLISECINDS_19000101) {
            adjustValue += TimeUnit.DAYS.toMillis(1);
        }
        
        return new Date(adjustValue);
        
    }
    
    /**
     * タイムゾーンを考慮して、時差分を調整する。
     * さらに、Excelの場合、1900年1月0日から始まるため、1900年以前は1日進めて調整する。
     * @param date
     * @return
     */
    public static Date reAdjustDate(final Date date) {
        
        long adjustValue = date.getTime();
        
        // 1900年以前の場合は、1899-12-30となるため、１日進めて補正をかける
        if(adjustValue < MILLISECINDS_19000101) {
            adjustValue -= TimeUnit.DAYS.toMillis(1);
        }
        
        // タイムゾーンの時差の加算がされているため補正する。
        adjustValue += TimeZone.getDefault().getRawOffset();
        
        return new Date(adjustValue);
        
    }
    
    /**
     * Javaの{@link Date}型をExcelの内部表現の数値に変換する。
     * <p>数値 -> 日付 -> 数値のように、再び数値に戻したした場合、小数第11位以降は切り捨てられるため誤差が発生します。
     * 
     * @param value 変換対象のJava表現上の日時。
     * @param date1904 基準日が1904年始まりかどうか。
     * @return Excel表現上に変換した数値。
     * @throws IllegalArgumentException {@literal value == nulll.}
     */
    public static double convertToExcelNumber(final Date value, final boolean date1904) {
        ArgUtils.notNull(value, "value");
        
        double numValue = reAdjustDate(value).getTime();
//        double numValue = value.getTime();
        
        /*
         * Excelの時間の表現に直す。
         * ・Excelの日付の形式の場合小数部が時間を示すため、24時間分のミリ秒を考慮する。
         */
        numValue = numValue / (SECONDS_IN_DAYS * 1000);
        
        /*
         * Excelの基準日に戻す。
         */
        int offsetDays = date1904 ? OFFSET_DAYS_1904 : OFFSET_DAYS_1900;
        numValue -= offsetDays;
        
        /*
         * 1900年1月～2月の補正
         */
        boolean time = (Math.abs(numValue -1)) < 1;
        if(!date1904 && !time && (numValue -1) < NON_LEAP_DAY) {
            numValue -= 1;
        }
        
        return numValue;
        
    }
    
}
