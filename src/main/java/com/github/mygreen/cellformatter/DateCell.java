package com.github.mygreen.cellformatter;

import java.util.Date;
import java.util.TimeZone;

import com.github.mygreen.cellformatter.lang.ExcelDateUtils;

/**
 * 日時型の値を直接扱うための仮想的なセル。
 * @since 0.6
 * @author T.TSUCHIE
 *
 */
public class DateCell extends ObjectCell<Date> {
    
    /** 日付の始まりが1904年開始かどうか */
    private final boolean dateStart1904;
    
    /**
     * 値と書式のインデックス番号を指定するコンストラクタ。
     * <p>フォーマットの書式は、{@literal null}になります。
     * @param value フォーマット対象の日時。タイムゾーンが考慮された値。
     * @param formatIndex 書式のインデックス番号。
     * @throws IllegalArgumentException {@literal value == null}
     * @throws IllegalArgumentException {@literal formatIndex < 0}
     * 
     */
    public DateCell(final Date value, final short formatIndex) {
        this(value, formatIndex, false);
    }
    
    /**
     * 値とその書式を指定するコンストラクタ。
     * <p>フォーマットのインデックス番号は、{@literal 0}となります。
     * @param value フォーマット対象の日時。タイムゾーンが考慮された値。
     * @param formatPattern Excelの書式。
     * @throws IllegalArgumentException {@literal value == null}
     * @throws IllegalArgumentException fommatPattern is empty.
     */
    public DateCell(final Date value, final String formatPattern) {
        this(value, formatPattern, false);
    }
    
    /**
     * 値と、書式のインデックス番号、書式を指定するコンストラクタ。
     * @param value フォーマット対象の日時。タイムゾーンが考慮された値。
     * @param formatIndex フォーマットのインデックス番号。
     * @param formatPattern Excelの書式。
     * @throws IllegalArgumentException {@literal value == null}
     * @throws IllegalArgumentException {@literal formatIndex < 0}
     * @throws IllegalArgumentException fommatPattern is empty.
     * 
     */
    public DateCell(final Date value, final short formatIndex, final String formatPattern) {
        this(value, formatIndex, formatPattern, false);
    }
    
    /**
     * 値と書式のインデックス番号を指定するコンストラクタ。
     * <p>フォーマットの書式は、{@literal null}になります。
     * @param value フォーマット対象の日時。タイムゾーンが考慮された値。
     * @param formatIndex 書式のインデックス番号。
     * @param dateStart1904 1904年始まりかどうか。
     * @throws IllegalArgumentException {@literal value == null}
     * @throws IllegalArgumentException {@literal formatIndex < 0}
     * 
     */
    public DateCell(final Date value, final short formatIndex, final boolean dateStart1904) {
        super(value, formatIndex);
        this.dateStart1904 = dateStart1904;
    }
    
    /**
     * 値とその書式を指定するコンストラクタ。
     * <p>フォーマットのインデックス番号は、{@literal 0}となります。
     * @param value フォーマット対象の日時。タイムゾーンが考慮された値。
     * @param formatPattern Excelの書式。
     * @param dateStart1904 1904年始まりかどうか。
     * @throws IllegalArgumentException {@literal value == null}
     * @throws IllegalArgumentException fommatPattern is empty.
     */
    public DateCell(final Date value, final String formatPattern, final boolean dateStart1904) {
        super(value, formatPattern);
        this.dateStart1904 = dateStart1904;
    }
    
    /**
     * 値と、書式のインデックス番号、書式を指定するコンストラクタ。
     * @param value フォーマット対象の値。
     * @param formatIndex フォーマットのインデックス番号。
     * @param formatPattern Excelの書式。
     * @param dateStart1904 1904年始まりかどうか。
     * @throws IllegalArgumentException {@literal value == null}
     * @throws IllegalArgumentException {@literal formatIndex < 0}
     * @throws IllegalArgumentException fommatPattern is empty.
     * 
     */
    public DateCell(final Date value, final short formatIndex, final String formatPattern, final boolean dateStart1904) {
        super(value, formatIndex, formatPattern);
        this.dateStart1904 = dateStart1904;
    }
    
    @Override
    public boolean isNumber() {
        return true;
    }
    
    @Override
    public Date getDateCellValue() {
        
        // タイムゾーン分を引いて、標準時にする。
        return new Date(getValue().getTime() + TimeZone.getDefault().getRawOffset());
    }
    
    @Override
    public double getNumberCellValue() {
        return ExcelDateUtils.convertExcelNumber(getValue(), isDateStart1904());
    }
    
    @Override
    public boolean isDateStart1904() {
        return dateStart1904;
    }
}
