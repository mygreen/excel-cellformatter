package com.github.mygreen.cellformatter;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.github.mygreen.cellformatter.lang.ArgUtils;
import com.github.mygreen.cellformatter.lang.ExcelDateUtils;
import com.github.mygreen.cellformatter.lang.JXLUtils;
import com.github.mygreen.cellformatter.lang.Utils;

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


/**
 * JExcel APIのラッパークラス。
 * 
 * @version 0.6
 * @since 0.4
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
        
        // 日付
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
     * @param dateStart1904 日付の開始が1904年始まりかどうか。
     *        {@link JXLUtils#isDateStart1904(jxl.Sheet)}を使用して、Workbookまたはシートから取得する。
     * @throws IllegalArgumentException {@literal cell == null.}
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
        return cell.getType() == CellType.LABEL || cell.getType() == CellType.STRING_FORMULA;
    }
    
    @Override
    public String getTextCellValue() {
        
        final CellType type = cell.getType();
        if(type == CellType.LABEL || type == CellType.STRING_FORMULA) {
            return ((LabelCell) cell).getString();
            
        } else {
            return cell.getContents();
        }
        
    }
    
    @Override
    public boolean isBoolean() {
        return cell.getType() == CellType.BOOLEAN || cell.getType() == CellType.BOOLEAN_FORMULA;
    }
    
    @Override
    public boolean getBooleanCellValue() {
        
        final CellType type = cell.getType();
        if(type == CellType.BOOLEAN || type == CellType.BOOLEAN_FORMULA) {
            return ((BooleanCell) cell).getValue();
            
        } else {
            return false;
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
            final Date  date = ((DateCell) cell).getDate();
            final double num = ExcelDateUtils.convertExcelNumber(date, isDateStart1904());
            return num;
            
        } else {
            return 0;
        }
    }
    
    @Override
    public Date getDateCellValue() {
        
        if(cell.getType() == CellType.DATE || cell.getType() == CellType.DATE_FORMULA) {
            Date date = ((DateCell) cell).getDate();
            return adjustDate(date);
            
        } else if(cell.getType() == CellType.NUMBER || cell.getType() == CellType.NUMBER_FORMULA) {
            final double num = getNumberCellValue();
            final Date date = ExcelDateUtils.convertJavaDate(num, isDateStart1904());
            return date;
            
        } else {
            return new Date(ExcelDateUtils.getExcelZeroDateTime(isDateStart1904()));
        }
    }
    
    /**
     * 時刻の調整を行う。
     * <p>1900年1月0日が、1899-12-30となり、1899年12月31日以降が1日ずれるため、調整を行う。
     * @param date
     * @return
     */
    private Date adjustDate(final Date date) {
        
        if(!isDateStart1904() && date.getTime() < ExcelDateUtils.MILLISECONDS_19000101) {
            return new Date(date.getTime() + TimeUnit.DAYS.toMillis(1));
        }
        
        return date;
    }
    
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
