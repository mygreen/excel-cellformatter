package com.github.mygreen.cellformatter;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import jxl.BooleanCell;
import jxl.Cell;
import jxl.CellReferenceHelper;
import jxl.CellType;
import jxl.DateCell;
import jxl.LabelCell;
import jxl.NumberCell;
import jxl.biff.DisplayFormat;
import jxl.biff.XFRecord;
import jxl.format.CellFormat;
import jxl.format.Format;

import com.github.mygreen.cellformatter.lang.ArgUtils;
import com.github.mygreen.cellformatter.lang.Utils;


/**
 * JExcel APIのセル
 * @author T.TSUCHIE
 *
 */
public class JXLCell implements CommonCell {
    
    /** 日付の始まりが1904年開始かどうか */
    private final boolean dateStart1904;
    
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
        
        // 経過時間
        BUILT_IN_FORMATS.put((short)46, "[h]:mm:ss");
    }
    
    private final Cell cell;
    
    /**
     * セルを渡してインスタンスを作成する。
     * @param cell フォーマット対象のセルのインスタンス。
     * @param dateStart1904 日付の開始が1904年始まりかどうか。Workbookまたはシートから取得する。
     * @throws IllegalArgumentException cell is null.
     */
    public JXLCell(final Cell cell, final boolean dateStart1904) {
        ArgUtils.notNull(cell, "cell");
        this.cell = cell;
        this.dateStart1904 = dateStart1904;
    }
    
    /**
     * JExcelAPIの元々のセルのインスタンスを取得する。
     * @return
     */
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
            
        } else if(cellFormat == null) {
            return 0;
        }
        
        if(cellFormat instanceof DisplayFormat) {
            final DisplayFormat displayFormat = (DisplayFormat)cellFormat;
            return (short) displayFormat.getFormatIndex();
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
    public boolean isText() {
        return cell.getType() == CellType.LABEL || cell.getType() == CellType.STRING_FORMULA
                || cell.getType() == CellType.BOOLEAN || cell.getType() == CellType.BOOLEAN_FORMULA;
    }
    
    @Override
    public String getTextCellValue() {
        
        final CellType type = cell.getType();
        if(type == CellType.LABEL || type == CellType.STRING_FORMULA) {
            return ((LabelCell) cell).getString();
            
        } else if(type == CellType.BOOLEAN || type == CellType.BOOLEAN_FORMULA) {
            return Boolean.toString(((BooleanCell) cell).getValue()).toUpperCase();
            
        } else {
            return cell.getContents();
        }
        
    }
    
    @Override
    public boolean isNumber() {
        final CellType type = cell.getType();
        return type == CellType.NUMBER || type == CellType.NUMBER_FORMULA
                || type == CellType.DATE || type == CellType.DATE_FORMULA;
    }
    
    @Override
    public double getNumberCellValue() {
        
        final CellType type = cell.getType();
        if(type == CellType.NUMBER || type == CellType.NUMBER_FORMULA) {
            return ((NumberCell) cell).getValue();
            
        } else if(type == CellType.DATE || type == CellType.DATE_FORMULA) {
            //TODO:
            return 0;
            
        } else {
            return 0;
        }
    }
    
    private static final long DATE_19000101 = Timestamp.valueOf("1900-01-01 00:00:00.000").getTime();
    
    @Override
    public Date getDateCellValue() {
        
        if(cell.getType() == CellType.DATE || cell.getType() == CellType.DATE_FORMULA) {
            Date date = ((DateCell) cell).getDate();
            return adjustDate(date);
            
        } else if(cell.getType() == CellType.NUMBER || cell.getType() == CellType.NUMBER_FORMULA) {
            final double num = getNumberCellValue();
            final Date date = convertJavaDate(num, isDateStart1904());
            return adjustDate(date);
            
        } else {
            return new Date(Utils.getExcelZeroDateTime(isDateStart1904()));
        }
    }
    
    private Date adjustDate(final Date date) {
        
        // JExcelAPIは、タイムゾーンの時差が加算されているため補正する。
        Date adjustDate = new Date(date.getTime() - TimeZone.getDefault().getRawOffset());
        
        // 1900年以前の場合は、1899-12-30となるため、１日進めて補正をかける
        if(adjustDate.getTime() < DATE_19000101) {
            adjustDate = new Date(adjustDate.getTime() + TimeUnit.DAYS.toMillis(1));
        }
        
        return adjustDate;
    }
    
    /**
     * 数値を日時に変換する処理。
     * <p>JExcelAPIの「jxl.read.biff.DateRecord」を参照。
     * @param value
     * @param date1904 Excelの基準日が1904年かどうか
     * @return
     */
    private Date convertJavaDate(final double value, final boolean date1904) {
        
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
    
    // convertJavaDate内で使用する定数。
    private static final int nonLeapDay = 61;
    private static final int utcOffsetDays = 25569;
    private static final int utcOffsetDays1904 = 24107;
    private static final long secondsInADay = 24 * 60 * 60;
    private static final long msInASecond = 1000;
    
    @Override
    public boolean isDateStart1904() {
        // JExcelAPIの場合は、Workbookからでないと取得できないため、コンストラクタで渡す。
        return dateStart1904;
    }
    
    @Override
    public String getCellAddress() {
        return CellReferenceHelper.getCellReference(cell.getColumn(), cell.getRow());
    }
    
}
