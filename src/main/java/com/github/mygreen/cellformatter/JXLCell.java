package com.github.mygreen.cellformatter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.NumberCell;
import jxl.StringFormulaCell;
import jxl.biff.FormatRecord;
import jxl.biff.XFRecord;
import jxl.format.CellFormat;
import jxl.format.Format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mygreen.cellformatter.lang.ArgUtils;
import com.github.mygreen.cellformatter.lang.Utils;


/**
 * JExcel APIのセル
 * @author T.TSUCHIE
 *
 */
public class JXLCell implements CommonCell {
    
    private static Logger logger = LoggerFactory.getLogger(JXLCell.class);
    
    /**
     * 変換対象の組み込みフォーマット
     * ・間違っているものを対象とする。
     * ・環境によって変わるものは、FormattterResolverで変換する
     */
    private static Map<Short, String> BUILT_IN_FORMATS = new ConcurrentHashMap<>();
    static {
        // 通貨（記号あり）
        BUILT_IN_FORMATS.put((short)5, "$#,##0_);($#,##0)");
        BUILT_IN_FORMATS.put((short)6, "$#,##0_);[Red]($#,##0)");
        BUILT_IN_FORMATS.put((short)7, "$#,##0.00);($#,##0.00)");
        BUILT_IN_FORMATS.put((short)8, "$#,##0.00_);[Red]($#,##0.00)");
        
//        BUILT_IN_FORMAT.put((short)5, "¥#,##0_);(¥#,##0)");
//        BUILT_IN_FORMAT.put((short)6, "¥#,##0_);[Red](¥#,##0)");
//        BUILT_IN_FORMAT.put((short)7, "¥#,##0.00);(¥#,##0.00)");
//        BUILT_IN_FORMAT.put((short)8, "¥#,##0.00_);[Red](¥#,##0.00)");
        
        // 日付
        // システムのロケールによって変わる
        BUILT_IN_FORMATS.put((short)14, "m/d/yy");
        
        // 通貨（記号なし）
        BUILT_IN_FORMATS.put((short)37, "#,##0_);(#,##0)");
        BUILT_IN_FORMATS.put((short)38, "#,##0_);[Red](#,##0)");
        BUILT_IN_FORMATS.put((short)39, "#,##0.00_);(#,##0.00)");
        BUILT_IN_FORMATS.put((short)40, "#,##0.00_);[Red](#,##0.00)");
        
        // 会計（記号あり）
        BUILT_IN_FORMATS.put((short)41, "_(* #,##0_);_(* (#,##0);_(* \"-\"_);_(@_)");
        BUILT_IN_FORMATS.put((short)42, "_($* #,##0_);_($* (#,##0);_($* \"-\"_);_(@_)");
        BUILT_IN_FORMATS.put((short)43, "_(* #,##0.00_);_(* (#,##0.00);_(* \"-\"??_);_(@_)");
        BUILT_IN_FORMATS.put((short)44, "_($* #,##0.00_);_($* (#,##0.00);_($* \"-\"??_);_(@_)");
        
    }
    
    private final Cell cell;
    
    public JXLCell(final Cell cell) {
        ArgUtils.notNull(cell, "cell");
        this.cell = cell;
    }
    
    public Cell getCell() {
        return cell;
    }
    
    @Override
    public short getFormatIndex() {
        // セルのスタイル情報の取得
        final CellFormat cellStyle = cell.getCellFormat();
        if(cellStyle == null) {
            return 0;
        }
        
        final short formatIndex = getFormatIndex(cellStyle);
        return formatIndex;
    }
    
    private short getFormatIndex(final CellFormat cellStyle) {
        
        final Format cellFormat = cellStyle.getFormat();
        if(cellFormat == null && cellStyle instanceof XFRecord) {
            final XFRecord record = (XFRecord) cellStyle;
            return (short) record.formatIndex;
        }
        
        if(cellFormat == null) {
            return 0;
        }
        
        final String className = cellFormat.getClass().getName();
        if(className.equals("jxl.biff.BuiltInFormat")) {
            // 非公開のクラスなので、クラス名で比較する
            try {
                final Method method = cellFormat.getClass().getDeclaredMethod("getFormatIndex");
                method.setAccessible(true);
                
                final int formatIndex = (int) method.invoke(cellFormat);
                return (short) formatIndex;
                
            } catch (NoSuchMethodException | SecurityException e) {
                logger.warn("fail access method : jxl.biff.BuiltInFormat#getFormatIndex()", e);
                return 0;
                
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.warn("fail invoke method : jxl.biff.BuiltInFormat#getFormatIndex()", e);
                return 0;
            }
            
        } else if(className.equals("jxl.biff.FormatRecord")) {
            final FormatRecord record = (FormatRecord) cellFormat;
            return (short) record.getFormatIndex();
        }
        
        return 0;
        
    }
    
    
    @Override
    public String getFormatPattern() {
        
        // セルのスタイル情報の取得
        final CellFormat cellStyle = cell.getCellFormat();
        if(cellStyle == null) {
            return "";
        }
        
        // 変換対象のビルトインフォーマットの場合
        final short formatIndex = getFormatIndex(cellStyle);
        if(BUILT_IN_FORMATS.containsKey(formatIndex)) {
            return BUILT_IN_FORMATS.get(formatIndex);
        }
        
        // セルのフォーマットの取得
        final Format cellFormat = cellStyle.getFormat();
        if(cellFormat == null) {
            return "";
        }
        
        final String formatPattern = cellFormat.getFormatString();
        if(Utils.isEmpty(formatPattern)) {
            return "";
        }
        
        return formatPattern;
    }
    
    @Override
    public boolean isString() {
        return cell.getType().equals(CellType.LABEL) || cell.getType().equals(CellType.STRING_FORMULA);
    }
    
    @Override
    public String getStringCellValue() {
        
        if(isString()) {
            return ((StringFormulaCell) cell).getString();
        } else {
            return cell.getContents();
        }
    }
    
    @Override
    public double getNumberCellValue() {
        
        if(cell.getType() == CellType.NUMBER || cell.getType() == CellType.NUMBER_FORMULA) {
            return ((NumberCell) cell).getValue();
        } else {
            return 0;
        }
    }
    
    @Override
    public Calendar getDateCellValue() {
        
        // JExcel APIの場合は、TimeZoneを考慮していないため、TimeZoneはGMT+0にする。
        if(cell.getType() == CellType.DATE || cell.getType() == CellType.DATE_FORMULA) {
            final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            cal.setTime(((DateCell) cell).getDate());
            return cal;
            
        } else if(cell.getType() == CellType.NUMBER || cell.getType() == CellType.NUMBER_FORMULA) {
            final double num = getNumberCellValue();
            final Date date = convertJavaDate(num, false);
            final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            cal.setTime(date);
            return cal;
            
        } else {
            final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            cal.setTimeInMillis(0);;
            return cal;
        }
    }
    
    // The number of days between 1 Jan 1900 and 1 March 1900. Excel thinks
    // the day before this was 29th Feb 1900, but it was 28th Feb 1900.
    // I guess the programmers thought nobody would notice that they
    // couldn't be bothered to program this dating anomaly properly
    private static final int nonLeapDay = 61;
    
    // The number of days between 01 Jan 1900 and 01 Jan 1970 - this gives
    // the UTC offset
    private static final int utcOffsetDays = 25569;

    // The number of days between 01 Jan 1904 and 01 Jan 1970 - this gives
    // the UTC offset using the 1904 date system
    private static final int utcOffsetDays1904 = 24107;
    
    // The number of milliseconds in  a day
    private static final long secondsInADay = 24 * 60 * 60;
    private static final long msInASecond = 1000;
//    private static final long msInADay = secondsInADay * msInASecond;
    
    // jxl.read.biff.DateRecordを参照
    private Date convertJavaDate(final double value, final boolean date1904 ) {
        
        double numValue = value;
        boolean time;
        
        if(Math.abs(numValue) < 1) {
            time = true;
        } else {
            time = false;
        }
        
        if(!date1904 && !time && numValue < nonLeapDay) {
            numValue += 1;
        }
        
        int offsetDays = date1904 ? utcOffsetDays1904 : utcOffsetDays;
        double utcDays = numValue - offsetDays;
        
        long utcValue = Math.round(utcDays * secondsInADay) * msInASecond;
        
        return new Date(utcValue);
        
    }
    
    
}
